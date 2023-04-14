package net.mtuomiko.traffichistory.common;

import org.eclipse.microprofile.config.spi.Converter;

import java.time.LocalDate;

public class AppConfigDateConverter implements Converter<LocalDate> {
    @Override
    public LocalDate convert(String s) {
        return LocalDate.parse(s);
    }
}
