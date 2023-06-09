package net.mtuomiko.traffichistory.datastore;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.Value;

import org.apache.commons.collections4.ListUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class DatastoreOperations {
    private final Datastore datastore;
    private final KeyFactory stationKeyFactory;
    private final Key stationListKey;
    private final ZoneId zoneId;

    private static final int STATION_BATCH_SIZE = 100;

    public DatastoreOperations(Datastore datastore, ZoneId zoneId) {
        this.datastore = datastore;
        this.stationKeyFactory = datastore.newKeyFactory().setKind(StationEntity.KIND);
        this.stationListKey = datastore.newKeyFactory().setKind(StationListEntity.KIND)
                .newKey(StationListEntity.KEY_STRING);
        this.zoneId = zoneId;
    }

    public List<StationEntity> getStationEntities() {
        var entity = datastore.get(stationListKey);

        if (entity == null) {
            throw new IllegalStateException("Station list entity not found");
        }

        var embeddedEntities = entity.<EntityValue>getList(StationListEntity.STATIONS);
        var stationEntities = embeddedEntities.stream()
                .map(Value::get)
                .map(StationEntity::createFrom)
                .toList();

        return stationEntities;
    }

    public StationEntity getStationEntity(Integer stationId) {
        var entity = datastore.get(stationKey(stationId));
        if (entity == null) {
            return null;
        }
        return StationEntity.createFrom(entity);
    }

    public List<VolumeEntity> getVolumeEntities(Integer stationId, LocalDate firstDate, LocalDate lastDate) {
        var firstTimestamp = localDateToTimeStamp(firstDate);
        var lastTimestamp = localDateToTimeStamp(lastDate);

        Query<Entity> query = Query.newEntityQueryBuilder().setKind(VolumeEntity.KIND)
                .setFilter(StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.ge(VolumeEntity.DATE_PROPERTY, firstTimestamp),
                        StructuredQuery.PropertyFilter.le(VolumeEntity.DATE_PROPERTY, lastTimestamp),
                        StructuredQuery.PropertyFilter.hasAncestor(stationKey(stationId))
                ))
                .setOrderBy(StructuredQuery.OrderBy.asc(VolumeEntity.DATE_PROPERTY))
                .build();
        QueryResults<Entity> volumeResults = datastore.run(query);

        var volumeEntities = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(volumeResults, Spliterator.ORDERED), false)
                .map(VolumeEntity::createFrom)
                .toList();

        return volumeEntities;
    }

    public void upsertVolumeEntities(Integer stationId, List<VolumeEntity> volumeEntities) {
        var keyFactory = datastore.newKeyFactory().setKind(VolumeEntity.KIND)
                .addAncestor(StationEntity.getAsAncestor(stationId));

        var batch = datastore.newBatch();
        volumeEntities.forEach(volumeEntity -> {
            var key = keyFactory.newKey(volumeEntity.getKeyString(zoneId));
            var entityBuilder = Entity.newBuilder(key);
            volumeEntity.setPropertiesTo(entityBuilder);
            var entity = entityBuilder.build();
            batch.put(entity);
        });

        batch.submit();
    }

    public void upsertStationEntities(List<StationEntity> stationEntities) {
        var entities = stationEntities.stream().map(stationEntity -> {
            var stationKey = stationKey(stationEntity);
            var entityBuilder = Entity.newBuilder(stationKey);
            stationEntity.setPropertiesTo(entityBuilder);
            return entityBuilder.build();
        }).toList();

        var chunks = ListUtils.partition(entities, STATION_BATCH_SIZE);
        chunks.forEach(chunk -> {
            var batch = datastore.newBatch();
            chunk.forEach(batch::put);
            batch.submit();
        });

        var stationListEntity = new StationListEntity(entities.stream().map(EntityValue::of).toList());
        var stationListEntityBuilder = Entity.newBuilder(stationListKey);
        stationListEntity.setPropertiesTo(stationListEntityBuilder);
        datastore.put(stationListEntityBuilder.build());
    }

    private Key stationKey(Integer stationId) {
        return stationKeyFactory.newKey(StationEntity.getKeyString(stationId));
    }

    private Key stationKey(StationEntity stationEntity) {
        return stationKeyFactory.newKey(stationEntity.getKeyString());
    }

    private Timestamp localDateToTimeStamp(LocalDate localDate) {
        var instant = localDate.atStartOfDay(zoneId).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }
}
