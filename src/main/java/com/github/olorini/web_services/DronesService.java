package com.github.olorini.web_services;

import com.github.olorini.db.DroneEntity;
import com.github.olorini.db.DronesDboRepository;
import com.github.olorini.pojo.Drone;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DronesService {

    DronesDboRepository dronesRepository = new DronesDboRepository();

    public List<Drone> getDrones() throws SQLException, IOException, ClassNotFoundException {
        List<DroneEntity> drones = dronesRepository.findDrones();
        return drones.stream()
                .map(Drone::new)
                .collect(Collectors.toList());
    }

}
