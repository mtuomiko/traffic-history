package net.mtuomiko.traffichistory.svc;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "traffichistory")
public interface AppConfig {
    // Base URL for raw CSV LAM data files
    String lamUrl();
}
