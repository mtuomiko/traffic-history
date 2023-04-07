package net.mtuomiko.traffichistory.svc.tms;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Client declaration for accessing raw LAM CSV files from external API. The API does not provide JSON/REST
 * responses, only raw files, so any mention of REST here does not have a meaning in that sense. RegisterRestClient
 * seems to be a documented way of doing things with Quarkus. It also allows us to potentially use SmallRye Fault
 * Tolerance for throttling access to the external resource.
 * <p>
 * Digitraffic has some varying numbering on the filenames. Some stations can have negative tmsNumbers which won't work
 * as part of the filename, but their data is accessible using the id instead of the tmsNumber value.
 */
@RegisterRestClient(configKey = "tms-csv-api")
public interface TmsCsvClient {

    @GET
    @Produces("binary/octet-stream")
    @Path("{filename}")
    InputStream getByFilename(@PathParam("filename") String filename);

}
