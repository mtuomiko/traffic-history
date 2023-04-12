package net.mtuomiko.traffichistory.dao;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Value;

import net.mtuomiko.traffichistory.common.HourlyTraffic;
import net.mtuomiko.traffichistory.common.Station;

import org.apache.commons.collections4.IteratorUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Singleton;

@Singleton
public class StationDao {
    Datastore datastore;
    KeyFactory stationKeyFactory;
    Key stationListKey;

    static final ZoneId ZONE_ID = ZoneId.of("Europe/Helsinki");
    private static final String stationListKeyString = "stationList";

    public StationDao(Datastore datastore) {
        this.datastore = datastore;
        this.stationKeyFactory = datastore.newKeyFactory().setKind(StationEntity.KIND);
        this.stationListKey = datastore.newKeyFactory().setKind(StationListEntity.KIND).newKey(stationListKeyString);
    }

    public List<Station> getStations() {
        var entity = datastore.get(stationListKey);

        var embeddedEntities = entity.<EntityValue>getList(StationListEntity.STATIONS);
        var stationEntities = embeddedEntities.stream()
                .map(Value::get)
                .map(StationEntity::createFrom)
                .toList();

        return stationEntities.stream().map(StationEntity::toStation).toList();
    }

    public Station getStation(Integer stationId) {
        var entity = datastore.get(stationKey(stationId));
        if (entity == null) {
            return null;
        }
        return StationEntity.createFrom(entity).toStation();
    }

    /**
     * Returns a sorted map of all dates between firstDate and lastDate (inclusive) as keys and values as found
     * hourly volumes or null if no value was found.
     *
     * @param stationId
     * @param firstDate
     * @param lastDate
     * @return null padded sorted map of all local dates between firstDate and lastDate
     */
    public SortedMap<LocalDate, HourlyTraffic> getHourlyVolumes(Integer stationId, LocalDate firstDate,
                                                                LocalDate lastDate) {
        var firstTimestamp = localDateToTimeStamp(firstDate);
        var lastTimestamp = localDateToTimeStamp(lastDate);
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(VolumeEntity.KIND)
                .setFilter(CompositeFilter.and(
                        PropertyFilter.ge(VolumeEntity.DATE, firstTimestamp),
                        PropertyFilter.le(VolumeEntity.DATE, lastTimestamp),
                        PropertyFilter.hasAncestor(stationKey(stationId))
                ))
                .setOrderBy(OrderBy.asc(VolumeEntity.DATE))
                .build();
        QueryResults<Entity> volumeResults = datastore.run(query);

        var entities = IteratorUtils.toList(volumeResults);
        var volumes = entities.stream()
                .map(VolumeEntity::createFrom)
                .map(VolumeEntity::toHourlyVolumes)
                .toList();

        return createMap(firstDate, lastDate, volumes);
    }

    public void storeHourlyVolumes(Integer stationId, List<HourlyTraffic> volumesList) {
        var volumeEntities = volumesList.stream().map(VolumeEntity::createFrom).toList();

        var keyFactory = datastore.newKeyFactory().setKind(VolumeEntity.KIND)
                .addAncestor(PathElement.of(StationEntity.KIND, String.format("station%d", stationId)));

        volumeEntities.forEach(volumeEntity -> {
            var incompleteKey = keyFactory.newKey();
            var key = datastore.allocateId(incompleteKey);
            var entityBuilder = Entity.newBuilder(key);
            volumeEntity.setPropertiesTo(entityBuilder);
            var entity = entityBuilder.build();
            datastore.put(entity);
        });
    }

    private Timestamp localDateToTimeStamp(LocalDate localDate) {
        var instant = localDate.atStartOfDay(ZONE_ID).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }

    private SortedMap<LocalDate, HourlyTraffic> createMap(
            LocalDate firstDate,
            LocalDate lastDate,
            List<HourlyTraffic> hourlyVolumes
    ) {
        SortedMap<LocalDate, HourlyTraffic> map = new TreeMap<>();
        firstDate.datesUntil(lastDate.plusDays(1)).forEach(date -> map.put(date, null));

        hourlyVolumes.forEach(volumes -> map.put(volumes.date(), volumes));

        return map;
    }

    private Key stationKey(Integer stationId) {
        return stationKeyFactory.newKey(String.format("station%d", stationId));
    }
}
