package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.gen.api.StationApi;
import net.mtuomiko.traffichistory.gen.model.Station;
import net.mtuomiko.traffichistory.gen.model.StationsResponse;
import net.mtuomiko.traffichistory.svc.StationService;

import java.time.LocalDate;

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

    @Override
    public StationsResponse getStationTraffic(Integer stationId, LocalDate firstDate, LocalDate lastDate) {
        return null;
    }

    private Station toApiStation(net.mtuomiko.traffichistory.common.Station station) {
        return new Station().name(station.name())
                .tmsId(station.tmsId())
                .tmsNumber(station.tmsNumber())
                .latitude(station.latitude())
                .longitude(station.longitude());
    }
}
