package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.gen.api.StationApi;
import net.mtuomiko.traffichistory.gen.model.Station;
import net.mtuomiko.traffichistory.gen.model.StationsResponse;
import net.mtuomiko.traffichistory.svc.StationService;

public class StationResource implements StationApi {

    private final StationService stationService;

    public StationResource(StationService stationDao) {
        this.stationService = stationDao;
    }

    @Override
    public StationsResponse getAllStations() {
        var stations = stationService.getStations();
        var apiStations = stations.stream().map(this::toApiStation).toList();

        return new StationsResponse().stations(apiStations);
    }

    private Station toApiStation(net.mtuomiko.traffichistory.common.Station station) {
        return new Station().name(station.name())
                .tmsNumber(station.tmsNumber())
                .latitude(station.latitude())
                .longitude(station.longitude());
    }
}
