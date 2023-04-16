package net.mtuomiko.traffichistory;

import com.google.cloud.datastore.Datastore;

import net.mtuomiko.datastore.DatastoreOperations;
import net.mtuomiko.datastore.StationEntity;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class StationDao {

    private static final Logger logger = Logger.getLogger(StationDao.class.getName());
    DatastoreOperations operations;

    public StationDao(Datastore datastore) {
        this.operations = new DatastoreOperations(datastore);
    }

    public void upsertStations(List<StationEntity> stationEntities) {
        logger.info(() -> String.format("Upserting %d stations", stationEntities.size()));
        operations.upsertStationEntities(stationEntities);
    }
}
