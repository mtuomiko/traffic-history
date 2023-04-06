package net.mtuomiko.traffichistory.svc.tms;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.logging.Log;

@ApplicationScoped
public class TMSService {

    @RestClient
    TMSClient tmsClient;

    private static final String FILENAME_PATTERN = "lamraw_%s_%s_%d.csv";
    private final CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader(TMSHeader.class)
            .build();

    public List<Integer> getHourlyLAMStatsByIdAndDate(String lamStationId, LocalDate date) throws IOException {
        try (
                var responseStream = tmsClient.getByFilename(idAndDateToFilename(lamStationId, date));
                var reader = new BufferedReader(new InputStreamReader(responseStream))
        ) {
            var records = csvFormat.parse(reader);
            Map<Integer, Long> countByHour = records.stream()
                    .filter(csvRecord -> csvRecord.get(TMSHeader.FAULTY).equals("0"))
                    .map(this::extractAndValidateHourValue)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            return hourlyLongMapToIntList(countByHour);
        }
    }

    private String idAndDateToFilename(String lamStationId, LocalDate date) {
        var twoDigitYear = DateTimeFormatter.ofPattern("yy").format(date);
        var dayOfYear = date.getDayOfYear();

        return String.format(FILENAME_PATTERN, lamStationId, twoDigitYear, dayOfYear);
    }

    /**
     * TMS specs say that a set faulty flag guarantees hour value validity but checking that also so our process does
     * not break.
     *
     * @return valid hour Integer, null for non-parseable or faulty hour values
     */
    private Integer extractAndValidateHourValue(CSVRecord csvRecord) {
        var value = csvRecord.get(TMSHeader.HOUR);
        try {
            var hour = Integer.parseInt(value);
            if (hour >= 0 && hour <= 23) {
                return hour;
            }
            return null;
        } catch (NumberFormatException e) {
            Log.infov("Encountered faulty hour value {0}", value);
            return null;
        }
    }

    /**
     * Hours not present in countByHour will have zero values in returned list. Long to Integer conversion should be
     * a non-issue, traffic counts are not even close to the limits.
     *
     * @param countByHour map of found traffic counts by hour, does not need to have each hour present
     * @return filled integer list for hours 0-23
     */
    private List<Integer> hourlyLongMapToIntList(Map<Integer, Long> countByHour) {
        List<Integer> list = new ArrayList<>(Collections.nCopies(24, 0));
        countByHour.forEach((hour, count) -> list.set(hour, count.intValue()));
        return list;
    }
}
