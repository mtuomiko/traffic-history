package net.mtuomiko.traffichistory.common;

import java.time.LocalDate;
import java.util.List;

public record HourlyVolumes(
        LocalDate date,
        List<Integer> volumes
) {
}
