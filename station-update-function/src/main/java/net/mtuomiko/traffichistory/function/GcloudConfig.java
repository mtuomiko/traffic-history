package net.mtuomiko.traffichistory.function;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "traffichistory.gcloud")
public interface GcloudConfig {
    String projectId();

    DatastoreConfig datastore();

    interface DatastoreConfig {
        enum DatastoreAuth {
            DEFAULT,
            NONE
        }

        DatastoreAuth auth();

        Optional<String> host();
    }
}
