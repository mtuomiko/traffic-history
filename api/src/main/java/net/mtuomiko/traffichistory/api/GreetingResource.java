package net.mtuomiko.traffichistory.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.mtuomiko.traffichistory.svc.LAMService;

import java.io.IOException;
import java.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    private final LAMService lamService;

    public GreetingResource(LAMService lamService) {
        this.lamService = lamService;
    }

    @JsonProperty
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws IOException {
        System.out.println(lamService.getLAMStatisticsByIdAndDate(Integer.toString(704), LocalDate.of(2023, 1, 1)));

        return "Hello from RESTEasy Reactive";
    }
}
