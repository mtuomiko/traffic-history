package net.mtuomiko.traffichistory.common;

import java.time.InstantSource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class InstantSourceProducer {

    // Could help testing
    @Produces
    InstantSource instantSource() {
        return InstantSource.system();
    }
}

