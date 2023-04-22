package net.mtuomiko.traffichistory.function;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import net.mtuomiko.traffichistory.function.tms.TmsStationService;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Main entrypoint to the function. Will be triggered externally.
 */
@Named("updateStations")
@ApplicationScoped
public class UpdateStations implements BackgroundFunction<PubsubMessage> {

    private TmsStationService tmsStationService;
    private StationDao stationDao;

    public UpdateStations(TmsStationService tmsStationService, StationDao stationDao) {
        this.tmsStationService = tmsStationService;
        this.stationDao = stationDao;
    }

    private static final Logger logger = Logger.getLogger(UpdateStations.class.getName());

    @Override
    public void accept(PubsubMessage pubsubMessage, Context context) {
        logger.info("UpdateStations triggered");

        var stations = tmsStationService.fetchStations();
        stationDao.upsertStations(stations);

        logger.info("Stations upserted");
    }
}
