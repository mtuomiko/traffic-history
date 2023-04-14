package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.common.AppConfig;
import net.mtuomiko.traffichistory.common.ValidationException;

import java.time.InstantSource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RequestDatesValidator {

    private AppConfig appConfig;
    private InstantSource instantSource;

    public RequestDatesValidator(AppConfig appConfig, InstantSource instantSource) {
        this.appConfig = appConfig;
        this.instantSource = instantSource;
    }

    public void validate(LocalDate firstDate, LocalDate lastDate) {
        List<String> errorList = new ArrayList<>();
        if (firstDate.isAfter(lastDate)) {
            errorList.add("First date cannot be after last date");
        }
        if (ChronoUnit.DAYS.between(firstDate, lastDate) > 6) {
            errorList.add("Last date cannot be more than 6 days away from first date");
        }
        if (firstDate.isBefore(appConfig.earliestDate())) {
            errorList.add(String.format("First possible date is %s", appConfig.earliestDate().toString()));
        }
        if (lastDate.isAfter(instantSource.instant().atZone(ZoneId.of("Europe/Helsinki")).toLocalDate()
                .minusDays(appConfig.currentDateBuffer()))) {
            errorList.add(String.format("Last date cannot be less than %d days before current date",
                    appConfig.currentDateBuffer()));
        }

        if (!errorList.isEmpty()) {
            throw new ValidationException("Invalid date query params", null, errorList);
        }
    }
}
