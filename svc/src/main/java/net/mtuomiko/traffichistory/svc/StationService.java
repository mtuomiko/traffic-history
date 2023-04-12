package net.mtuomiko.traffichistory.svc;

import net.mtuomiko.traffichistory.common.HourlyTraffic;
import net.mtuomiko.traffichistory.common.Station;
import net.mtuomiko.traffichistory.dao.StationDao;
import net.mtuomiko.traffichistory.svc.tms.TmsService;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.logging.Log;

@ApplicationScoped
public class StationService {

    StationDao stationDao;

    TmsService tmsService;

    public StationService(StationDao stationDao, TmsService tmsService) {
        this.stationDao = stationDao;
        this.tmsService = tmsService;
    }

    public List<Station> getStations() {
        return stationDao.getStations();
    }

    public List<HourlyTraffic> getStationTraffic(Integer stationId, LocalDate firstDate, LocalDate lastDate) {
        var station = stationDao.getStation(stationId);
        if (station == null) {
            return null;
        }

        var trafficVolumes = stationDao.getHourlyVolumes(stationId, firstDate, lastDate);

        var missingDates = trafficVolumes.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();

        if (!missingDates.isEmpty()) {
            Log.infov("Missing stored traffic volumes for {0} dates: {1}", missingDates.size(),
                    StringUtils.join(missingDates, ", "));
            var fetchedHourlyVolumes = getMissingHourlyVolumes(station.tmsNumber(), missingDates);

            fetchedHourlyVolumes.forEach(hourlyTraffic -> trafficVolumes.put(hourlyTraffic.date(), hourlyTraffic));

            stationDao.storeHourlyVolumes(stationId, fetchedHourlyVolumes);
        }

        return trafficVolumes.values().stream().toList();
    }

    private List<HourlyTraffic> getMissingHourlyVolumes(Integer tmsNumber, List<LocalDate> dates) {
        return dates.stream().map(date -> {
            var integerList = tmsService.getHourlyLAMStatsByIdAndDate(tmsNumber, date);
            return new HourlyTraffic(date, integerList);
        }).toList();
    }
}
