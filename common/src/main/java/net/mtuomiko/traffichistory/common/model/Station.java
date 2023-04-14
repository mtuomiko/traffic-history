package net.mtuomiko.traffichistory.common.model;

public record Station(
        String name,
        int tmsId,
        int tmsNumber,
        double latitude,
        double longitude
) {
    public StationIdentity toStationIdentity() {
        return new StationIdentity(tmsId, tmsNumber);
    }
}
