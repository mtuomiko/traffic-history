package net.mtuomiko.traffichistory.svc;

import net.mtuomiko.traffichistory.common.ExternalFailureException;
import net.mtuomiko.traffichistory.common.NotFoundException;
import net.mtuomiko.traffichistory.common.model.HourlyTraffic;
import net.mtuomiko.traffichistory.common.model.Station;
import net.mtuomiko.traffichistory.common.model.StationIdentity;
import net.mtuomiko.traffichistory.dao.StationDao;
import net.mtuomiko.traffichistory.svc.tms.TmsService;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * @param stationId
     * @param firstDate
     * @param lastDate
     * @return
     * @throws NotFoundException if station is not found
     */
    public List<HourlyTraffic> getStationTraffic(Integer stationId, LocalDate firstDate, LocalDate lastDate) {
        var station = stationDao.getStation(stationId);
        if (station == null) {
            throw new NotFoundException("Station not found");
        }
        var stationIdentity = station.toStationIdentity();

        var trafficVolumes = stationDao.getHourlyVolumes(stationId, firstDate, lastDate);
        var missingDates = getMissingDates(trafficVolumes);

        if (!missingDates.isEmpty()) {
            Log.debugv("Missing stored traffic volumes for {0} dates: {1}", missingDates.size(),
                    StringUtils.join(missingDates, ", "));
            var fetchedHourlyVolumes = fetchHourlyTraffic(stationIdentity, missingDates);
            Log.debugv("Fetched traffic volume for {0} dates", fetchedHourlyVolumes.size());

            fetchedHourlyVolumes.forEach(hourlyTraffic -> trafficVolumes.put(hourlyTraffic.date(), hourlyTraffic));

            stationDao.storeHourlyTraffic(stationId, fetchedHourlyVolumes);
        }

        return trafficVolumes.entrySet().stream().map(entry -> {
            if (entry.getValue() == null) {
                return new HourlyTraffic(entry.getKey(), Collections.emptyList());
            }
            return entry.getValue();
        }).toList();
    }

    private List<LocalDate> getMissingDates(Map<LocalDate, HourlyTraffic> trafficMap) {
        return trafficMap.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * @param stationIdentity
     * @param dates
     * @return hourly traffic
     */
    private List<HourlyTraffic> fetchHourlyTraffic(StationIdentity stationIdentity, List<LocalDate> dates) {
        return dates.stream().map(date -> {
            try {
                var integerList = tmsService.getHourlyLAMStatsByIdentityAndDate(stationIdentity, date);
                return new HourlyTraffic(date, integerList);
            } catch (ExternalFailureException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }
}
