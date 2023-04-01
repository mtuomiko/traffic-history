package net.mtuomiko.dao;

import net.mtuomiko.traffichistory.dao.StationDao;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class StationDaoTest {

    private final StationDao stationDao;

    StationDaoTest(StationDao dao) {
        this.stationDao = dao;
    }

    @Test
    public void testDatastore() {
        stationDao.save();
    }
}
