package net.mtuomiko.traffichistory;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import net.mtuomiko.traffichistory.tms.TmsService;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named("updateStationsV1")
@ApplicationScoped
public class UpdateStationsV1 implements BackgroundFunction<PubsubMessage> {

    private TmsService tmsService;
    private StationDao stationDao;

    public UpdateStationsV1(TmsService tmsService, StationDao stationDao) {
        this.tmsService = tmsService;
        this.stationDao = stationDao;
    }

    private static final Logger logger = Logger.getLogger(UpdateStationsV1.class.getName());

    @Override
    public void accept(PubsubMessage pubsubMessage, Context context) {
        logger.info("UpdateStationsV1 triggered");

        var stations = tmsService.fetchStations();
        stationDao.upsertStations(stations);

        logger.info("Stations upserted");
    }
}
