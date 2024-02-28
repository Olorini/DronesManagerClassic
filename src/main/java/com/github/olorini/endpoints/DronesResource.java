package com.github.olorini.endpoints;

import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.endpoints.pojo.Drone;
import com.github.olorini.endpoints.pojo.Load;
import com.github.olorini.endpoints.pojo.Medication;
import com.github.olorini.services.DronesService;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("/d")
public class DronesResource {
    DronesService service = new DronesService();
    @javax.ws.rs.core.Context
    ServletContext servletContext;

    @GET
    @Produces("application/json")
    @Path("/fleet")
    public List<Drone> getDrones() {
        return service.getDrones();
    }

    @GET
    @Produces("application/json")
    @Path("/medication")
    public List<Medication> getMedication() {
        return service.getMedication();
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/register")
    public Map<String, Long> registerDrone(Drone request) throws BadRequestException {
        Long id = service.registerNewDrone(request);
        return Collections.singletonMap("id", id);
    }

    @POST
    @Consumes("application/json")
    @Path("/load")
    public Response loadDrone(Load request) {
        service.loadDrone(request);
        return Response.ok().build();
    }

}
