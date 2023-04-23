package net.mtuomiko.traffichistory.dao;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.LongValue;

import net.mtuomiko.traffichistory.common.ExternalFailureException;
import net.mtuomiko.traffichistory.common.model.HourlyTraffic;
import net.mtuomiko.traffichistory.common.model.Station;
import net.mtuomiko.traffichistory.datastore.DatastoreOperations;
import net.mtuomiko.traffichistory.datastore.StationEntity;
import net.mtuomiko.traffichistory.datastore.VolumeEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Singleton;

import io.quarkus.logging.Log;

@Singleton
public class StationDao {
    DatastoreOperations operations;

    static final ZoneId ZONE_ID = ZoneId.of("Europe/Helsinki");

    public StationDao(Datastore datastore) {
        try {
            this.operations = new DatastoreOperations(datastore, ZONE_ID);
        } catch (DatastoreException e) {
            throw wrapDatastoreException(e);
        }
    }

    public List<Station> getStations() {
        try {
            var stationEntities = operations.getStationEntities();

            return stationEntities.stream().map(this::toStation).toList();
        } catch (DatastoreException e) {
            throw wrapDatastoreException(e);
        }
    }

    public Station getStation(Integer stationId) {
        try {
            var stationEntity = operations.getStationEntity(stationId);

            return toStation(stationEntity);
        } catch (DatastoreException e) {
            throw wrapDatastoreException(e);
        }
    }

    private Station toStation(StationEntity stationEntity) {
        return stationEntity == null
                ? null
                : new Station(stationEntity.name(), stationEntity.tmsId(),
                stationEntity.tmsNumber(),
                stationEntity.latitude(), stationEntity.longitude());
    }

    /**
     * Returns a sorted map of all dates between firstDate and lastDate (inclusive) as keys and values as found
     * hourly volumes or null if no value was found.
     *
     * @param stationId
     * @param firstDate
     * @param lastDate
     * @return null padded sorted map of all local dates between firstDate and lastDate (inclusive)
     */
    public SortedMap<LocalDate, HourlyTraffic> getHourlyVolumes(
            Integer stationId,
            LocalDate firstDate,
            LocalDate lastDate
    ) {
        try {
            var volumeEntities = operations.getVolumeEntities(stationId, firstDate, lastDate);

            var hourlyTraffics = volumeEntities.stream()
                    .map(this::toHourlyTraffic)
                    .toList();

            return createMap(firstDate, lastDate, hourlyTraffics);
        } catch (DatastoreException e) {
            throw wrapDatastoreException(e);
        }
    }

    private HourlyTraffic toHourlyTraffic(VolumeEntity volumeEntity) {
        var date = Instant.ofEpochSecond(volumeEntity.date().getSeconds(), volumeEntity.date().getNanos())
                .atZone(ZONE_ID)
                .toLocalDate();
        var volumesList = volumeEntity.volumes().stream().map(value -> value.get().intValue()).toList();
        return new HourlyTraffic(date, volumesList);
    }

    public void storeHourlyTraffic(Integer stationId, List<HourlyTraffic> trafficList) {
        try {
            if (trafficList.isEmpty()) {
                Log.debug("Empty traffic volume list, not storing");
                return;
            }
            var volumeEntities = trafficList.stream().map(this::toVolumeEntity).toList();

            operations.upsertVolumeEntities(stationId, volumeEntities);
        } catch (DatastoreException e) {
            wrapDatastoreException(e);
        }
    }

    private VolumeEntity toVolumeEntity(HourlyTraffic hourlyTraffic) {
        var dateInstant = hourlyTraffic.date().atStartOfDay(ZONE_ID).toInstant();
        var date = Timestamp.ofTimeSecondsAndNanos(dateInstant.getEpochSecond(), dateInstant.getNano());
        var longValueList = hourlyTraffic.volumes().stream()
                // list value indices cannot be disabled on the list itself, only on individual values
                .map(num -> LongValue.newBuilder(num.longValue()).setExcludeFromIndexes(true).build())
                .toList();
        return new VolumeEntity(date, longValueList);
    }

    private SortedMap<LocalDate, HourlyTraffic> createMap(
            LocalDate firstDate,
            LocalDate lastDate,
            List<HourlyTraffic> hourlyVolumes
    ) {
        SortedMap<LocalDate, HourlyTraffic> map = new TreeMap<>();
        firstDate.datesUntil(lastDate.plusDays(1)).forEach(date -> map.put(date, null));

        hourlyVolumes.forEach(volumes -> map.put(volumes.date(), volumes));

        return map;
    }

    private ExternalFailureException wrapDatastoreException(DatastoreException e) {
        return new ExternalFailureException(String.format("Issue with Datastore: %s", e.getReason()), e);
    }
}
