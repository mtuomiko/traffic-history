package net.mtuomiko.traffichistory;

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
        this.keyFactory = datastore.newKeyFactory();
    }

    public void tryout() {
        // The kind for the new entity
        String kind = "Task";
        // The name/ID for the new entity
        String name = "sampletask1";
        // The Cloud Datastore key for the new entity
        Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(name);

        // Prepares the new entity
        Entity task = Entity.newBuilder(taskKey).set("description", "Buy milk").build();

        // Saves the entity
        datastore.put(task);

        System.out.printf("Saved %s: %s%n", task.getKey().getName(), task.getString("description"));

        // Retrieve entity
        Entity retrieved = datastore.get(taskKey);

        System.out.printf("Retrieved %s: %s%n", taskKey.getName(), retrieved.getString("description"));
    }

    public String getDescription() {
        var key = keyFactory.setKind("Task").newKey("sampletask1");
        var entity = datastore.get(key);
        return entity.getString("description");
    }
}