package net.mtuomiko.traffichistory.dao;

import com.google.cloud.datastore.EntityValue;

import java.util.List;

public record StationListEntity(
        List<EntityValue> stations
) {
    public static final String KIND = "StationList";
    public static final String STATIONS = "stations";
}
