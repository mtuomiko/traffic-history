package net.mtuomiko.traffichistory;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import net.mtuomiko.traffichistory.tms.TmsStationService;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Main entrypoint to the function. Will be triggered externally.
 */
@Named("updateStationsV1")
@ApplicationScoped
public class UpdateStationsV1 implements BackgroundFunction<PubsubMessage> {

    private TmsStationService tmsStationService;
    private StationDao stationDao;

    public UpdateStationsV1(TmsStationService tmsStationService, StationDao stationDao) {
        this.tmsStationService = tmsStationService;
        this.stationDao = stationDao;
    }

    private static final Logger logger = Logger.getLogger(UpdateStationsV1.class.getName());

    @Override
    public void accept(PubsubMessage pubsubMessage, Context context) {
        logger.info("UpdateStationsV1 triggered");

        var stations = tmsStationService.fetchStations();
        stationDao.upsertStations(stations);

        logger.info("Stations upserted");
    }
}
