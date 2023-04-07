package net.mtuomiko.traffichistory.svc.tms;

import net.mtuomiko.traffichistory.svc.tms.model.StationResponse;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * Client declaration for accessing station information from external API.
 */
@RegisterRestClient(configKey = "tms-station-api")
public interface TmsStationClient {

    @GZIP
    @GET
    @Produces("application/json")
    StationResponse getStations();
}
