package net.mtuomiko.traffichistory;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DatastoreProducer {

    @Produces
    Datastore datastore() {
        return DatastoreOptions.newBuilder()
                .setHost("http://host.docker.internal:8000")
                .setProjectId("traffic-history-376700")
                .build().getService();
    }

}
