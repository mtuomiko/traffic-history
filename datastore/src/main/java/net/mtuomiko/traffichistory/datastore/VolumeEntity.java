package net.mtuomiko.traffichistory.datastore;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LongValue;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public record VolumeEntity(
        Timestamp date,
        List<LongValue> volumes
) {
    public static final String KIND = "HourlyVolumes";
    public static final String DATE_PROPERTY = "date";
    public static final String VOLUMES_PROPERTY = "volumes";

    static VolumeEntity createFrom(Entity entity) {
        var dateTimestamp = entity.getTimestamp(DATE_PROPERTY);
        var volumes = entity.<LongValue>getList(VOLUMES_PROPERTY);
        return new VolumeEntity(dateTimestamp, volumes);
    }

    Entity.Builder setPropertiesTo(Entity.Builder builder) {
        return builder.set(DATE_PROPERTY, date)
                .set(VOLUMES_PROPERTY, volumes);
    }

    /**
     * Zone needed for Timestamp conversion to a string formatted local date. Using date string as key, so we can
     * guarantee that there won't exist multiple entries for a single date using the same station ancestor.
     *
     * @param zoneId
     * @return
     */
    String getKeyString(ZoneId zoneId) {
        return Instant.ofEpochSecond(date.getSeconds(), date().getNanos()).atZone(zoneId).toLocalDate().toString();
    }
}
