package net.mtuomiko.traffichistory.dao;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import net.mtuomiko.traffichistory.common.HourlyVolumes;
import net.mtuomiko.traffichistory.common.Station;

import org.apache.commons.collections4.IteratorUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class StationDao {
    Datastore datastore;
    KeyFactory stationKeyFactory;

    static final ZoneId ZONE_ID = ZoneId.of("Europe/Helsinki");

    public StationDao(Datastore datastore) {
        this.datastore = datastore;
        this.stationKeyFactory = datastore.newKeyFactory().setKind(StationEntity.KIND);
    }

    public List<Station> getStations() {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(StationEntity.KIND).build();
        QueryResults<Entity> stationResults = datastore.run(query);

        var entities = IteratorUtils.toList(stationResults);
        var stationEntities = entities.stream()
                .map(StationEntity::createFrom)
                .toList();

        return stationEntities.stream().map(StationEntity::toStation).toList();
    }

    public List<HourlyVolumes> getHourlyVolumes(Integer stationId, LocalDate firstDate, LocalDate lastDate) {
        var firstTimestamp = localDateToTimeStamp(firstDate);
        var lastTimestamp = localDateToTimeStamp(lastDate);
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(VolumeEntity.KIND)
                .setFilter(CompositeFilter.and(
                        PropertyFilter.ge(VolumeEntity.DATE, firstTimestamp),
                        PropertyFilter.le(VolumeEntity.DATE, lastTimestamp),
                        PropertyFilter.hasAncestor(
                                stationKeyFactory.newKey(String.format("station%d", stationId))
                        )
                ))
                .setOrderBy(OrderBy.asc(VolumeEntity.DATE))
                .build();
        QueryResults<Entity> volumeResults = datastore.run(query);

        var entities = IteratorUtils.toList(volumeResults);
        var volumeEntities = entities.stream().map(VolumeEntity::createFrom).toList();

        return volumeEntities.stream().map(VolumeEntity::toHourlyVolumes).toList();
    }

    private Timestamp localDateToTimeStamp(LocalDate localDate) {
        var instant = localDate.atStartOfDay(ZONE_ID).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }
}
