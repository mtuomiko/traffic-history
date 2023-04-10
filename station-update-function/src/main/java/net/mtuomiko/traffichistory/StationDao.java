package net.mtuomiko.traffichistory;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class StationDao {

    private static final Logger logger = Logger.getLogger(StationDao.class.getName());
    Datastore datastore;
    KeyFactory keyFactory;

    public StationDao(Datastore datastore) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().setKind(StationEntity.KIND);
    }

    public void upsertStations(List<StationEntity> stationEntities) {
        logger.info("Upserting stations");
        stationEntities.forEach(stationEntity -> {
            var stationKey = keyFactory.newKey(String.format("station%d", stationEntity.tmsId()));
            var entityBuilder = Entity.newBuilder(stationKey);
            stationEntity.setPropertiesTo(entityBuilder);
            var entity = entityBuilder.build();
            datastore.put(entity);
        });
    }
}
