package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.common.model.HourlyTraffic;
import net.mtuomiko.traffichistory.gen.api.StationApi;
import net.mtuomiko.traffichistory.gen.model.SingleDayTrafficVolume;
import net.mtuomiko.traffichistory.gen.model.Station;
import net.mtuomiko.traffichistory.gen.model.StationsResponse;
import net.mtuomiko.traffichistory.gen.model.TrafficVolumeResponse;
import net.mtuomiko.traffichistory.svc.StationService;

import java.time.LocalDate;

public class StationResource implements StationApi {

    private final StationService stationService;
    private final RequestDatesValidator validator;

    public StationResource(StationService stationDao, RequestDatesValidator validator) {
        this.stationService = stationDao;
        this.validator = validator;
    }

    @Override
    public StationsResponse getAllStations() {
        var stations = stationService.getStations();
        var apiStations = stations.stream().map(this::toApiStation).toList();

        return new StationsResponse().stations(apiStations);
    }

    @Override
    public TrafficVolumeResponse getStationTraffic(Integer stationId, LocalDate firstDate, LocalDate lastDate) {
        validator.validate(firstDate, lastDate);

        var traffic = stationService.getStationTraffic(stationId, firstDate, lastDate);

        return new TrafficVolumeResponse().trafficVolumes(traffic.stream().map(this::toSingleDayTrafficVolume)
                .toList());
    }

    private Station toApiStation(net.mtuomiko.traffichistory.common.model.Station station) {
        return new Station().name(station.name())
                .tmsId(station.tmsId())
                .tmsNumber(station.tmsNumber())
                .latitude(station.latitude())
                .longitude(station.longitude());
    }

    private SingleDayTrafficVolume toSingleDayTrafficVolume(HourlyTraffic volumes) {
        return new SingleDayTrafficVolume().date(volumes.date())
                .hourlyVolumes(volumes.volumes());
    }
}
