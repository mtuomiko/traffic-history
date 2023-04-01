package net.mtuomiko.traffichistory.common;

public record Station(
        String name,
        int tmsNumber,
        double latitude,
        double longitude
) {
}
