package net.mtuomiko.traffichistory.dao;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import net.mtuomiko.traffichistory.common.Station;

public record StationEntity(
        String name,
        int tmsNumber,
        double latitude,
        double longitude
) {
    public static final String KIND = "Station";
    public static final String NAME = "name";
    public static final String TMS_NUMBER = "tmsNumber";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    Entity.Builder setPropertiesTo(Entity.Builder builder) {
        return builder.set("name", name)
                .set("tmsNumber", tmsNumber)
                .set("location", LatLng.of(latitude, longitude))
                .set("latitude", latitude)
                .set("longitude", longitude);
    }

    Station toStation() {
        return new Station(name, tmsNumber, latitude, longitude);
    }

    static StationEntity createFrom(Entity entity) {
        return new StationEntity(
                entity.getString(NAME),
                (int) entity.getLong(TMS_NUMBER),
                entity.getDouble(LATITUDE),
                entity.getDouble(LONGITUDE)
        );
    }
}
