package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.StationDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/station")
public class StationResource {

    private final StationDao stationDao;

    public StationResource(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @GET
    public StationResponse getMessage() {
        var message = stationDao.getDescription();
        return new StationResponse(message);
    }
}
