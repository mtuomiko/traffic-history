package net.mtuomiko.dao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;

import net.mtuomiko.traffichistory.dao.StationDao;
import net.mtuomiko.traffichistory.dao.StationEntity;
import net.mtuomiko.traffichistory.dao.StationListEntity;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class StationDaoTest {

    @Inject
    private Datastore datastore;
    @Inject
    private StationDao stationDao;

    void emptyDatastore() {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(StationEntity.KIND).build();
        var result = datastore.run(query);
        result.forEachRemaining(entity -> datastore.delete(entity.getKey()));
        datastore.delete(datastore.newKeyFactory().setKind(StationListEntity.KIND).newKey("stationList"));
    }

    void initializeDatastore() {

    }

    @Test
    public void whenNoStationListPresent_getStations_throws() {
        emptyDatastore();

        assertThatThrownBy(() -> {
            stationDao.getStations();

        }).isInstanceOf(IllegalStateException.class);
    }
}
