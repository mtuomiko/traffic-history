package net.mtuomiko.traffichistory.common;

import java.time.LocalDate;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;

@ConfigMapping(prefix = "traffichistory.config")
public interface AppConfig {

    @WithConverter(AppConfigDateConverter.class)
    LocalDate earliestDate();

    int currentDateBuffer();
}
