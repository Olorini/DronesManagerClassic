package com.github.olorini.endpoints;

import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.endpoints.pojo.Drone;
import com.github.olorini.endpoints.pojo.Load;
import com.github.olorini.endpoints.pojo.Medication;
import com.github.olorini.services.DronesService;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @Produces("application/json")
    @Path("/register")
    public Map<String, Long> registerDrone(Drone request) throws BadRequestException {
        Long id = service.register(request);
        return Collections.singletonMap("id", id);
    }

    @POST
    @Path("/load")
    public Response loadDrone(Load request) {
        //service.load(request);
        return Response.ok().build();
    }



}
