package net.mtuomiko.traffichistory.api;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

class ExceptionMappers {
    @ServerExceptionMapper
    public Response mapException(BadRequestException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new CustomError(e.getMessage())).build();
    }
}
