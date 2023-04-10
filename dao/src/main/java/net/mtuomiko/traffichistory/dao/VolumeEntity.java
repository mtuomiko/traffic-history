package net.mtuomiko.traffichistory.dao;

import static net.mtuomiko.traffichistory.dao.StationDao.ZONE_ID;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Value;

import net.mtuomiko.traffichistory.common.HourlyVolumes;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public record VolumeEntity(
        Timestamp date,
        List<Value<Integer>> volumes
) {
    public static final String KIND = "HourlyVolumes";
    public static final String DATE = "date";
    public static final String VOLUMES = "volumes";

    static VolumeEntity createFrom(Entity entity) {
        var dateTimestamp = entity.getTimestamp(DATE);
        var volumes = entity.<Value<Integer>>getList(VOLUMES);
        return new VolumeEntity(dateTimestamp, volumes);
    }

//    Entity.Builder setPropertiesTo(Entity.Builder builder) {
//        return builder.set(DATE, name)
//                .set(TMS_ID, tmsId)
//                .set(TMS_NUMBER, tmsNumber)
//                .set(LOCATION, LatLng.of(latitude, longitude));
//    }

    HourlyVolumes toHourlyVolumes() {
        var date = Instant.ofEpochSecond(this.date.getSeconds(), this.date.getNanos())
                .atZone(ZONE_ID)
                .toLocalDate();
        var volumesList = volumes.stream().map(Value::get).toList();
        return new HourlyVolumes(date, volumesList);
    }
}
