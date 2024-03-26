package com.github.olorini.services;

import com.github.olorini.db.DboException;
import com.github.olorini.db.DboRepository;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.endpoints.pojo.Drone;

import com.github.olorini.endpoints.pojo.Model;
import com.github.olorini.endpoints.pojo.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class DroneServiceTest {

    DronesService service;

    @BeforeEach
    public void setUp() throws DboException {
        DboRepository dboRepository = Mockito.mock(DboRepository.class);
        List<DroneEntity> drones = new ArrayList<>();
        DroneEntity drone = new DroneEntity();
        drone.setId(1L);
        drone.setBatteryCapacity(65);
        drone.setModel("CRUISERWEIGHT");
        drone.setSerialNumber("088002C");
        drone.setState("LOADED");
        drone.setWeightLimit(300);
        drones.add(drone);
        Mockito.when(dboRepository.getDrones()).thenReturn(drones);
        this.service = new DronesService(dboRepository);
    }

    @Test
    public void getDronesTestSuccess() {
        List<Drone> droneList = service.getDrones();
        assertNotNull(droneList);
        assertEquals(droneList.size(), 1);
        assertEquals(droneList.get(0).getId(), 1);
        assertEquals(droneList.get(0).getBatteryCapacity(), 65);
        assertEquals(droneList.get(0).getModel(), Model.CRUISERWEIGHT);
        assertEquals(droneList.get(0).getSerialNumber(), "088002C");
        assertEquals(droneList.get(0).getState(), State.LOADED);
        assertEquals(droneList.get(0).getWeightLimit(), 300);
    }
}
