package net.mtuomiko.traffichistory.svc;

import net.mtuomiko.traffichistory.common.Station;
import net.mtuomiko.traffichistory.dao.StationDao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StationService {

    StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> getStations() {
        return stationDao.getStations();
    }
}
