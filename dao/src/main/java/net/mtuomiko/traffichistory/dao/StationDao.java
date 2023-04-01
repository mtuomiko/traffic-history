package net.mtuomiko.traffichistory.dao;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import net.mtuomiko.traffichistory.common.Station;

import org.apache.commons.collections4.IteratorUtils;

import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Singleton;

@Singleton
public class StationDao {
    Datastore datastore;
    KeyFactory keyFactory;

    public StationDao(Datastore datastore) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().setKind(StationEntity.KIND);
    }

    public void save() {
        var stations = IntStream.range(0, 4)
                .mapToObj(num ->
                        new StationEntity(String.format("station_%d", num), num, 24.0 + num, 61.0 + num)
                ).toList();

        stations.forEach(stationEntity -> {
            var stationKey = datastore.allocateId(keyFactory.newKey());
            var entityBuilder = Entity.newBuilder(stationKey);
            stationEntity.setPropertiesTo(entityBuilder);
            var entity = entityBuilder.build();
            datastore.put(entity);
        });
    }

    public List<Station> getStations() {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(StationEntity.KIND).build();
        QueryResults<Entity> stationResults = datastore.run(query);

        var entities = IteratorUtils.toList(stationResults);
        var stationEntities = entities.stream()
                .map(StationEntity::createFrom)
                .toList();

        return stationEntities.stream().map(StationEntity::toStation).toList();
    }
}
