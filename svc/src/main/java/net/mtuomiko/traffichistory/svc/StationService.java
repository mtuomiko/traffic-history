package net.mtuomiko.traffichistory.svc;

import net.mtuomiko.traffichistory.dao.StationDao;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StationService {

    StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public String getDescription() {
        return stationDao.getDescription();
    }
}
