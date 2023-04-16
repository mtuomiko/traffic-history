package net.mtuomiko.datastore;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;

import java.util.List;

public record StationListEntity(
        List<EntityValue> stations
) {
    public static final String KIND = "StationList";
    public static final String STATIONS = "stations";

    Entity.Builder setPropertiesTo(Entity.Builder builder) {
        return builder.set(STATIONS, stations);
    }
}
