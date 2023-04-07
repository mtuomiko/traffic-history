package net.mtuomiko.traffichistory.dao;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import net.mtuomiko.traffichistory.common.Station;

public record StationEntity(
        String name,
        int tmsId,
        int tmsNumber,
        double latitude,
        double longitude
) {
    public static final String KIND = "Station";
    public static final String NAME = "name";
    public static final String TMS_ID = "tmsId";
    public static final String TMS_NUMBER = "tmsNumber";
    public static final String LOCATION = "location";

    Entity.Builder setPropertiesTo(Entity.Builder builder) {
        return builder.set(NAME, name)
                .set(TMS_ID, tmsId)
                .set(TMS_NUMBER, tmsNumber)
                .set(LOCATION, LatLng.of(latitude, longitude));
    }

    Station toStation() {
        return new Station(name, tmsId, tmsNumber, latitude, longitude);
    }

    static StationEntity createFrom(Entity entity) {
        var location = entity.getLatLng(LOCATION);
        return new StationEntity(
                entity.getString(NAME),
                (int) entity.getLong(TMS_ID),
                (int) entity.getLong(TMS_NUMBER),
                location.getLatitude(),
                location.getLongitude()
        );
    }
}
