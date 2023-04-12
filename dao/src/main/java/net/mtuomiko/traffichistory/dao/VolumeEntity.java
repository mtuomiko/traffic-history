package net.mtuomiko.traffichistory.dao;

import static net.mtuomiko.traffichistory.dao.StationDao.ZONE_ID;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LongValue;

import net.mtuomiko.traffichistory.common.HourlyTraffic;

import java.time.Instant;
import java.util.List;

public record VolumeEntity(
        Timestamp date,
        List<LongValue> volumes
) {
    public static final String KIND = "HourlyVolumes";
    public static final String DATE = "date";
    public static final String VOLUMES = "volumes";

    static VolumeEntity createFrom(Entity entity) {
        var dateTimestamp = entity.getTimestamp(DATE);
        var volumes = entity.<LongValue>getList(VOLUMES);
        return new VolumeEntity(dateTimestamp, volumes);
    }

    Entity.Builder setPropertiesTo(Entity.Builder builder) {
        return builder.set(DATE, date)
                .set(VOLUMES, volumes);
    }

    HourlyTraffic toHourlyVolumes() {
        var date = Instant.ofEpochSecond(this.date.getSeconds(), this.date.getNanos())
                .atZone(ZONE_ID)
                .toLocalDate();
        var volumesList = volumes.stream().map(value -> value.get().intValue()).toList();
        return new HourlyTraffic(date, volumesList);
    }

    static VolumeEntity createFrom(HourlyTraffic traffic) {
        var dateInstant = traffic.date().atStartOfDay(ZONE_ID).toInstant();
        var date = Timestamp.ofTimeSecondsAndNanos(dateInstant.getEpochSecond(), dateInstant.getNano());
        var longValueList = traffic.volumes().stream()
                // list value indices cannot be disabled on the list itself, only on individual values
                .map(num -> LongValue.newBuilder(num.longValue()).setExcludeFromIndexes(true).build())
                .toList();
        return new VolumeEntity(date, longValueList);
    }
}
