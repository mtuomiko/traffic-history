package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.gen.api.StationApi;
import net.mtuomiko.traffichistory.gen.model.Station;
import net.mtuomiko.traffichistory.gen.model.StationsResponse;
import net.mtuomiko.traffichistory.svc.StationService;

import java.util.stream.IntStream;

public class StationResource implements StationApi {

    private final StationService stationService;

    public StationResource(StationService stationDao) {
        this.stationService = stationDao;
    }

    @Override
    public StationsResponse getAllStations() {
        var message = stationService.getDescription();
        var stations = IntStream.range(1, 5)
                .mapToObj(num -> createStation(message, num, 25d, 60d))
                .toList();
        return new StationsResponse().stations(stations);
    }

    private Station createStation(String name, int tmsNumber, double latitude, double longitude) {
        return new Station().name(name).tmsNumber(tmsNumber).latitude(latitude).longitude(longitude);
    }
}
