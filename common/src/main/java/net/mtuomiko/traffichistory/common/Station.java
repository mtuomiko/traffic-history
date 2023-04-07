package net.mtuomiko.traffichistory.common;

public record Station(
        String name,
        int tmsId,
        int tmsNumber,
        double latitude,
        double longitude
) {
}
