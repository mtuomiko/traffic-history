package net.mtuomiko.traffichistory;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DatastoreProducer {

    private static final Logger logger = Logger.getLogger(DatastoreProducer.class.getName());

    GcloudConfig gcloudConfig;

    public DatastoreProducer(GcloudConfig gcloudConfig) {
        this.gcloudConfig = gcloudConfig;
    }


    @Produces
    Datastore datastore() {
        var auth = gcloudConfig.datastore().auth();
        logger.info(() -> String.format("Creating Datastore with %s authentication", auth.name()));
        return switch (gcloudConfig.datastore().auth()) {
            case DEFAULT -> DatastoreOptions.getDefaultInstance().getService();
            case NONE -> DatastoreOptions.newBuilder()
                    .setHost(gcloudConfig.datastore().host().orElseThrow())
                    .setProjectId(gcloudConfig.projectId())
                    .build().getService();
        };
    }
}
