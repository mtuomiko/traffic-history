package net.mtuomiko.traffichistory.function;

import com.google.cloud.datastore.Datastore;

import net.mtuomiko.traffichistory.datastore.DatastoreOperations;
import net.mtuomiko.traffichistory.datastore.StationEntity;

import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class StationDao {

    private static final Logger logger = Logger.getLogger(StationDao.class.getName());
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Helsinki");
    private final DatastoreOperations operations;

    public StationDao(Datastore datastore) {
        this.operations = new DatastoreOperations(datastore, ZONE_ID);
    }

    public void upsertStations(List<StationEntity> stationEntities) {
        logger.info(() -> String.format("Upserting %d stations", stationEntities.size()));
        operations.upsertStationEntities(stationEntities);
    }
}
