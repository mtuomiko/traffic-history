package net.mtuomiko.traffichistory.datastore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.Query;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatastoreOperationsTest {

    private final ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    private final Datastore datastore = DatastoreOptions.newBuilder()
            .setHost("http://host.docker.internal:8000")
            .setProjectId("traffic-history-376700")
            .build().getService();

    private final DatastoreOperations operations = new DatastoreOperations(datastore, zoneId);

    void emptyDatastore() {
        clearEntities(VolumeEntity.KIND);
        clearEntities(StationEntity.KIND);

        datastore.delete(
                datastore.newKeyFactory().setKind(StationListEntity.KIND).newKey(StationListEntity.KEY_STRING)
        );
    }

    void clearEntities(String kind) {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(kind).build();
        var existingEntities = IteratorUtils.toList(datastore.run(query));

        var chunks = ListUtils.partition(existingEntities, 100);
        chunks.forEach(chunk -> {
            var batch = datastore.newBatch();
            chunk.forEach(entity -> batch.delete(entity.getKey()));
            batch.submit();
        });
    }

    @Test
    void stationEntitiesCanBeUpsertedAndFetched() {
        emptyDatastore();

        var stations = List.of(
                new StationEntity("test1", 10001, 1, 25.0, 60.0),
                new StationEntity("test2", 10002, 2, 26.0, 61.0)
        );
        operations.upsertStationEntities(stations);

        var result = operations.getStationEntities();

        assertThat(result).containsExactlyInAnyOrderElementsOf(stations);
    }

    @Test
    void noStationList_getStationEntities_throws() {
        emptyDatastore();

        Throwable thrown = catchThrowable(() -> {
            operations.getStationEntities();
        });

        assertThat(thrown).isInstanceOf(IllegalStateException.class);
        assertThat(thrown).hasMessageContaining("Station list entity not found");
    }

    @Test
    void givenConflictingIds_upsertStationEntities_replacesStationEntities() {
        emptyDatastore();

        var initialStations = List.of(
                new StationEntity("test1", 10001, 1, 25.0, 60.0),
                new StationEntity("test2", 10002, 2, 26.0, 61.0)
        );
        var updatedStations = List.of(
                new StationEntity("test3", 10001, 3, 27.0, 62.0),
                new StationEntity("test4", 10002, 4, 28.0, 63.0)
        );
        operations.upsertStationEntities(initialStations);
        operations.upsertStationEntities(updatedStations);

        var result = operations.getStationEntities();
        assertThat(result).containsExactlyInAnyOrderElementsOf(updatedStations);
    }

    @Test
    void givenExistingStation_getStationEntity_returnsStation() {
        emptyDatastore();

        var stations = List.of(
                new StationEntity("test1", 10001, 1, 25.0, 60.0),
                new StationEntity("test2", 10002, 2, 26.0, 61.0),
                new StationEntity("test3", 10003, 3, 27.0, 62.0),
                new StationEntity("test4", 10004, 4, 28.0, 63.0)
        );
        operations.upsertStationEntities(stations);

        var result = operations.getStationEntity(10003);
        assertThat(result).isEqualTo(stations.get(2));
    }

    @Test
    void givenNonExistingStation_getStationEntity_returnsNull() {
        emptyDatastore();

        var stations = List.of(
                new StationEntity("test1", 10001, 1, 25.0, 60.0),
                new StationEntity("test2", 10002, 2, 26.0, 61.0),
                new StationEntity("test3", 10003, 3, 27.0, 62.0)
        );
        operations.upsertStationEntities(stations);

        var result = operations.getStationEntity(10004);
        assertThat(result).isNull();
    }

    @Test
    void upsertVolumeEntities_replacesExistingEntitiesIfDateAndParentStationMatch() {
        emptyDatastore();

        var trafficList1 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of).toList();
        var trafficList2 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of)
                .collect(Collectors.toList());
        Collections.reverse(trafficList2);
        var localDate = LocalDate.parse("2000-01-01");

        var volumeEntity1 = new VolumeEntity(localDateToTimeStamp(localDate), trafficList1);
        operations.upsertVolumeEntities(10001, List.of(volumeEntity1));

        var volumeEntity2 = new VolumeEntity(localDateToTimeStamp(localDate), trafficList2);
        operations.upsertVolumeEntities(10001, List.of(volumeEntity2));

        var result = operations.getVolumeEntities(10001, localDate, localDate);

        assertThat(result).containsExactly(volumeEntity2);
    }

    @Test
    void getVolumeEntities_returnsEntitiesOnlyForSelectedStation() {
        emptyDatastore();

        var trafficList1 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of).toList();
        var trafficList2 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of)
                .collect(Collectors.toList());
        Collections.reverse(trafficList2);
        var localDate = LocalDate.parse("2000-01-01");
        var volumeEntity1 = new VolumeEntity(localDateToTimeStamp(localDate), trafficList1);
        var volumeEntity2 = new VolumeEntity(localDateToTimeStamp(localDate), trafficList2);
        operations.upsertVolumeEntities(10001, List.of(volumeEntity1));
        operations.upsertVolumeEntities(10002, List.of(volumeEntity2));

        var result = operations.getVolumeEntities(10001, localDate, localDate);

        assertThat(result).containsExactly(volumeEntity1);
    }

    @Test
    void getVolumeEntities_returnsEntitiesOnlyForSelectedDate() {
        emptyDatastore();

        var trafficList1 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of).toList();
        var trafficList2 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of)
                .collect(Collectors.toList());
        Collections.reverse(trafficList2);
        var localDate1 = LocalDate.parse("2000-01-01");
        var localDate2 = LocalDate.parse("2000-01-02");
        var volumeEntity1 = new VolumeEntity(localDateToTimeStamp(localDate1), trafficList1);
        var volumeEntity2 = new VolumeEntity(localDateToTimeStamp(localDate2), trafficList2);
        operations.upsertVolumeEntities(10001, List.of(volumeEntity1));
        operations.upsertVolumeEntities(10001, List.of(volumeEntity2));

        var result = operations.getVolumeEntities(10001, localDate2, localDate2);
        assertThat(result).containsExactly(volumeEntity2);
    }

    @Test
    void noExistingEntities_getVolumeEntities_returnsEmptyList() {
        emptyDatastore();

        var trafficList1 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of).toList();
        var localDate1 = LocalDate.parse("2000-01-01");
        var volumeEntity1 = new VolumeEntity(localDateToTimeStamp(localDate1), trafficList1);
        operations.upsertVolumeEntities(10001, List.of(volumeEntity1));

        var result = operations.getVolumeEntities(10002, localDate1, localDate1);
        assertThat(result).isEmpty();
    }

    @Test
    void noExistingEntitiesInBetweenDates_getVolumeEntities_returnsExistingEntities() {
        emptyDatastore();

        var trafficList1 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of).toList();
        var trafficList2 = IntStream.rangeClosed(1, 24).asLongStream().mapToObj(LongValue::of)
                .collect(Collectors.toList());
        Collections.reverse(trafficList2);
        var localDate1 = LocalDate.parse("2000-01-01");
        var localDate2 = LocalDate.parse("2000-01-03");
        var volumeEntity1 = new VolumeEntity(localDateToTimeStamp(localDate1), trafficList1);
        var volumeEntity2 = new VolumeEntity(localDateToTimeStamp(localDate2), trafficList2);
        operations.upsertVolumeEntities(10001, List.of(volumeEntity1));
        operations.upsertVolumeEntities(10001, List.of(volumeEntity2));

        var result = operations.getVolumeEntities(10001, localDate1, localDate2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(volumeEntity1, volumeEntity2));
    }

    private Timestamp localDateToTimeStamp(LocalDate localDate) {
        var instant = localDate.atStartOfDay(zoneId).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }
}
