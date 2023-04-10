package net.mtuomiko.traffichistory.tms;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "traffichistory.tms")
public interface TmsConfig {
    // Base URL for station JSON API
    String stationApiUrl();
}
