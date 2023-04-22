package net.mtuomiko.traffichistory.function.tms;

import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * Client declaration for accessing station information from external API.
 */
public interface TmsStationClient {

    @GZIP
    @GET
    @Produces("application/json")
    TmsStationResponse getStations();
}
