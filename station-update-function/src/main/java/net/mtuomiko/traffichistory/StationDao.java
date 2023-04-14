package net.mtuomiko.traffichistory;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.KeyFactory;

import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class StationDao {

    private static final Logger logger = Logger.getLogger(StationDao.class.getName());
    Datastore datastore;
    KeyFactory keyFactory;

    private static final String stationListKeyString = "stationList";

    public StationDao(Datastore datastore) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().setKind(StationEntity.KIND);
    }

    public void upsertStations(List<StationEntity> stationEntities) {
        logger.info(() -> String.format("Upserting %d stations", stationEntities.size()));
        var entities = stationEntities.stream().map(stationEntity -> {
            var stationKey = keyFactory.newKey(String.format("station%d", stationEntity.tmsId()));
            var entityBuilder = Entity.newBuilder(stationKey);
            stationEntity.setPropertiesTo(entityBuilder);
            return entityBuilder.build();
        }).toList();

        var chunks = ListUtils.partition(entities, 100);
        chunks.forEach(chunk -> {
            var batch = datastore.newBatch();
            chunk.forEach(batch::put);
            batch.submit();
        });

        var stationListKey = datastore.newKeyFactory().setKind(StationListEntity.KIND).newKey(stationListKeyString);
        var stationListEntity = new StationListEntity(entities.stream().map(EntityValue::of).toList());
        var stationListEntityBuilder = Entity.newBuilder(stationListKey);
        stationListEntity.setPropertiesTo(stationListEntityBuilder);
        datastore.put(stationListEntityBuilder.build());
    }
}
