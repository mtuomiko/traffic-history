package net.mtuomiko.traffichistory.dao;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import javax.inject.Singleton;

@Singleton
public class StationDao {
    Datastore datastore;
    KeyFactory keyFactory;

    public StationDao(Datastore datastore) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().setKind(Station.KIND);
    }

    public void tryout() {
        // The kind for the new entity
        String kind = "Task";
        String property = "description";
        // The name/ID for the new entity
        String name = "sampletask1";
        // The Cloud Datastore key for the new entity
        Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(name);

        // Prepares the new entity
        Entity task = Entity.newBuilder(taskKey).set(property, "Buy milk").build();

        // Saves the entity
        datastore.put(task);

        // Retrieve entity
        Entity retrieved = datastore.get(taskKey);

        System.out.printf("Retrieved %s: %s%n", taskKey.getName(), retrieved.getString(property));
    }

    public String getDescription() {
        var key = keyFactory.setKind("Task").newKey("sampletask1");
        var entity = datastore.get(key);
        return entity.getString("description");
    }
}
