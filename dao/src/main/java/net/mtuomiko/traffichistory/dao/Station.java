package net.mtuomiko.traffichistory.dao;

public record Station(
        String name,
        int tmsNumber,
        double latitude,
        double longitude
) {
    public static final String KIND = "Station";

}
