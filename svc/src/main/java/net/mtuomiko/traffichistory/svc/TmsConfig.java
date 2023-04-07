package net.mtuomiko.traffichistory.svc;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "traffichistory.tms")
public interface TmsConfig {
    // Base URL for raw CSV TMS data files
    String csvFilesUrl();

    // Base URL for station JSON API
    String stationApiUrl();
}
