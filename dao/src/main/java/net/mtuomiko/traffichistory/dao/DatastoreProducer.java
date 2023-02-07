package net.mtuomiko.traffichistory.dao;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DatastoreProducer {

    GcloudConfig gcloudConfig;

    public DatastoreProducer(GcloudConfig gcloudConfig) {
        this.gcloudConfig = gcloudConfig;
    }


    @Produces
    Datastore datastore() {
        return switch (gcloudConfig.datastore().auth()) {
            case DEFAULT -> DatastoreOptions.getDefaultInstance().getService();
            case NONE -> DatastoreOptions.newBuilder()
                    .setHost(gcloudConfig.datastore().host().orElseThrow())
                    .setProjectId(gcloudConfig.projectId())
                    .build().getService();
        };
    }
}
