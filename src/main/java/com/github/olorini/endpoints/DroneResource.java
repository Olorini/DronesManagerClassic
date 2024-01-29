package com.github.olorini.endpoints;

import com.github.olorini.pojo.Drone;
import com.github.olorini.web_services.DronesService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Path("/drones")
public class DroneResource {

    DronesService service = new DronesService();

    @GET
    @Produces("application/json")
    public List<Drone> getEmployee() {
        try {
            return service.getDrones();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
