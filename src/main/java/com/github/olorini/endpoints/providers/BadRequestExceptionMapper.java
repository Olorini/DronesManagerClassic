package com.github.olorini.endpoints.providers;

import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.core.exceptions.WebError;
import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new WebError(ex.getErrorCode(), ex.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}