package net.mtuomiko.traffichistory;

public record Station(
        String name,
        Integer tmsNumber
) {
    public static final String KIND = "Station";

}
