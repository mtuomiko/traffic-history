package net.mtuomiko.traffichistory.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.mtuomiko.traffichistory.svc.tms.TmsService;

import java.io.IOException;
import java.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    private final TmsService tmsService;

    public GreetingResource(TmsService TMSService) {
        this.tmsService = TMSService;
    }

    @JsonProperty
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws IOException {
        var result = tmsService.getHourlyLAMStatsByIdAndDate(Integer.toString(704), LocalDate.of(2023, 1, 1));

        var stations = tmsService.fetchStations();

        return result.toString();
    }
}
