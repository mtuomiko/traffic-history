package net.mtuomiko.traffichistory.svc.tms;

import net.mtuomiko.traffichistory.common.ExternalFailureException;
import net.mtuomiko.traffichistory.common.model.StationIdentity;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
public class TmsService {

    @RestClient
    TmsCsvClient tmsCsvClient;

    public TmsService() {
    }

    private static final String FILENAME_PATTERN = "lamraw_%d_%s_%d.csv";
    private final CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader(TmsCsvHeader.class)
            .build();

    public List<Integer> getHourlyLAMStatsByIdentityAndDate(StationIdentity stationId, LocalDate date) {
        try (
                var responseStream = tmsCsvClient.getByFilename(idAndDateToFilename(stationId, date));
                var reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))
        ) {
            var records = csvFormat.parse(reader);
            Map<Integer, Long> countByHour = records.stream()
                    .filter(csvRecord -> csvRecord.get(TmsCsvHeader.FAULTY).equals("0"))
                    .map(this::extractAndValidateHourValue)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            return hourlyLongMapToIntList(countByHour);
        } catch (IOException e) {
            throw new ExternalFailureException("Failed to read external CSV", e);
        }
    }

    private String idAndDateToFilename(StationIdentity stationId, LocalDate date) {
        // stations with negative tmsNumber must have their files accessed using the station id
        var filenameId = (stationId.tmsNumber() < 0) ? stationId.tmsId() : stationId.tmsNumber();
        var twoDigitYear = DateTimeFormatter.ofPattern("yy").format(date);
        var dayOfYear = date.getDayOfYear();

        return String.format(FILENAME_PATTERN, filenameId, twoDigitYear, dayOfYear);
    }

    /**
     * TMS specs say that a set faulty flag guarantees hour value validity but checking that also so our process does
     * not break.
     *
     * @return valid hour Integer, null for non-parseable or faulty hour values
     */
    private Integer extractAndValidateHourValue(CSVRecord csvRecord) {
        var value = csvRecord.get(TmsCsvHeader.HOUR);
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
