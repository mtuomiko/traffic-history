package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.common.BadRequestException;
import net.mtuomiko.traffichistory.common.CustomException;
import net.mtuomiko.traffichistory.common.NotFoundException;
import net.mtuomiko.traffichistory.common.ValidationException;
import net.mtuomiko.traffichistory.gen.model.ErrorWrapper;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

class ExceptionMappers {
    @ServerExceptionMapper
    public Response mapException(CustomException e) {
        var status = getStatus(e);
        var entity = new ErrorWrapper().status(status.getStatusCode())
                .message(e.getMessage())
                .errors(e.getErrors());

        return Response.status(status).entity(entity).build();
    }

    private Status getStatus(CustomException e) {
        if (e instanceof ValidationException) {
            return Status.BAD_REQUEST;
        }
        if (e instanceof BadRequestException) {
            return Status.BAD_REQUEST;
        }
        if (e instanceof NotFoundException) {
            return Status.NOT_FOUND;
        }
        return Status.INTERNAL_SERVER_ERROR;
    }
}
