package net.mtuomiko.traffichistory.svc;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LAMService {

    @RestClient
    LAMClient lamClient;

    private final String filenamePattern = "lamraw_%s_%s_%d.csv";

    public String getLAMStatisticsByIdAndDate(String lamStationId, LocalDate date) throws IOException {
        try (
                var responseStream = lamClient.getByFilename(idAndDateToFilename(lamStationId, date));
                var reader = new BufferedReader(new InputStreamReader(responseStream))
        ) {
            reader.lines().forEach(System.out::println);
        }
        return "done";
    }

    private String idAndDateToFilename(String lamStationId, LocalDate date) {
        var twoDigitYear = DateTimeFormatter.ofPattern("yy").format(date);
        var dayOfYear = date.getDayOfYear();

        return String.format(filenamePattern, lamStationId, twoDigitYear, dayOfYear);
    }
}
