package com.github.olorini.services;

import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.db.DboException;
import com.github.olorini.db.DboProcessException;
import com.github.olorini.db.DboRepository;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.db.dao.MedicationEntity;
import com.github.olorini.endpoints.pojo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@RunWith(MockitoJUnitRunner.class)
public class DroneServiceTest {
    DronesService service;
    DboRepository dboRepository;
    @BeforeEach
    public void setUp() throws DboException {
        dboRepository = Mockito.mock(DboRepository.class);
        Mockito.when(dboRepository.getDrones()).thenReturn(getDroneData());
        Mockito.when(dboRepository.getMedicine()).thenReturn(getMedicationData());
    }
    @Test
    public void getDronesTestSuccess() {
        this.service = new DronesService(dboRepository);
        List<Drone> droneList = service.getDrones();
        assertNotNull(droneList);
        assertEquals(droneList.size(), 1);
        assertEquals(droneList.get(0).getId(), 1);
        assertEquals(droneList.get(0).getBatteryCapacity(), 65);
        assertEquals(droneList.get(0).getModel(), Model.CRUISERWEIGHT);
        assertEquals(droneList.get(0).getSerialNumber(), "088002C");
        assertEquals(droneList.get(0).getState(), State.IDLE);
        assertEquals(droneList.get(0).getWeightLimit(), 300);
    }
    @Test
    public void getMedicationSuccess() {
        this.service = new DronesService(dboRepository);
        List<Medication> medicationList = service.getMedication();
        assertNotNull(medicationList);
        assertEquals(medicationList.size(), 1);
        assertEquals(medicationList.get(0).getId(), 1);
        assertEquals(medicationList.get(0).getCode(), "ME001");
        assertEquals(medicationList.get(0).getName(), "aspirin");
        assertEquals(medicationList.get(0).getWeight(), 10);
    }
    @Test
    public void registerDroneFail() throws DboException, DboProcessException {
        this.service = new DronesService(dboRepository);
        Drone reqDrone = new Drone();
        Exception ex;
        ex = assertThrowsExactly(BadRequestException.class, () -> service.registerNewDrone(reqDrone));
        assertEquals("Serial number is too long", ex.getMessage());
        reqDrone.setSerialNumber(getLongSerialNumber());
        ex = assertThrowsExactly(BadRequestException.class, () -> service.registerNewDrone(reqDrone));
        assertEquals("Serial number is too long", ex.getMessage());
        reqDrone.setSerialNumber("123");
        reqDrone.setWeightLimit(501);
        ex = assertThrowsExactly(BadRequestException.class, () -> service.registerNewDrone(reqDrone));
        assertEquals("Weight limit is very large", ex.getMessage());
        reqDrone.setWeightLimit(400);
        Mockito.when(dboRepository.saveDrone(any(DroneEntity.class)))
                .thenThrow(new DboProcessException("Drone with this serial number is already exist"));
        ex = assertThrowsExactly(BadRequestException.class, () -> service.registerNewDrone(reqDrone));
        assertEquals("Drone with this serial number is already exist", ex.getMessage());
    }
    @Test
    public void registerDroneSuccess() throws DboException, DboProcessException {
        Mockito.when(dboRepository.saveDrone(any(DroneEntity.class))).thenReturn(1L);
        Mockito.when(dboRepository.existsBySerialNumber("123")).thenReturn(false);
        this.service = new DronesService(dboRepository);
        Drone reqDrone = new Drone();
        reqDrone.setSerialNumber("123");
        reqDrone.setWeightLimit(400);
        assertEquals(service.registerNewDrone(reqDrone), 1L);
    }
    @Test
    public void loadDroneFail() throws DboException {
        this.service = new DronesService(dboRepository);
        Load reqLoad = new Load();
        Exception ex;
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("Drone identifier is empty", ex.getMessage());
        reqLoad.setDroneId(1L);
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("List of medicine identifiers are empty", ex.getMessage());
    }
    @Test
    public void loadDroneFindDroneFail() throws DboException {
        this.service = new DronesService(dboRepository);
        Load reqLoad = new Load();
        reqLoad.setDroneId(1L);
        reqLoad.setMedicineIds(Arrays.asList(21L, 22L));
        Exception ex;
        Mockito.when(dboRepository.findDroneById(1L)).thenReturn(Optional.empty());
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("Drone is not found", ex.getMessage());
        DroneEntity foundDrone = getDroneData().get(0);
        foundDrone.setState(State.LOADED.name());
        Mockito.when(dboRepository.findDroneById(1L)).thenReturn(Optional.of(foundDrone));
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("Drone is busy", ex.getMessage());
        foundDrone.setState(State.IDLE.name());
        foundDrone.setBatteryCapacity(10);
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("Drone isn't charged", ex.getMessage());
    }
    @Test
    public void loadDroneFindMedicineFail() throws DboException {
        this.service = new DronesService(dboRepository);
        Load reqLoad = new Load();
        reqLoad.setDroneId(1L);
        reqLoad.setMedicineIds(Collections.singletonList(1L));
        Exception ex;
        DroneEntity foundDrone = getDroneData().get(0);
        Mockito.when(dboRepository.findDroneById(1L)).thenReturn(Optional.of(foundDrone));
        Mockito.when(dboRepository.findMedication(new HashSet<>(reqLoad.getMedicineIds())))
                .thenReturn(new ArrayList<>());
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("Medicines are not found", ex.getMessage());
        List<MedicationEntity> medicationList = getMedicationData();
        medicationList.get(0).setWeight(400);
        Mockito.when(dboRepository.findMedication(new HashSet<>(reqLoad.getMedicineIds())))
                .thenReturn(medicationList);
        this.service = new DronesService(dboRepository);
        ex = assertThrowsExactly(BadRequestException.class, () -> service.loadDrone(reqLoad));
        assertEquals("Weight of medicine is very large", ex.getMessage());
    }
    @Test
    public void loadDroneFindMedicineSuccess() throws DboException {
        this.service = new DronesService(dboRepository);
        Load reqLoad = new Load();
        reqLoad.setDroneId(1L);
        reqLoad.setMedicineIds(Collections.singletonList(1L));
        DroneEntity foundDrone = getDroneData().get(0);
        Mockito.when(dboRepository.findDroneById(1L)).thenReturn(Optional.of(foundDrone));
        Mockito.when(dboRepository.findMedication(new HashSet<>(reqLoad.getMedicineIds())))
                .thenReturn(getMedicationData());
        service.loadDrone(reqLoad);
        Mockito.verify(dboRepository, Mockito.times(1)).saveLoad(any());
        Mockito.verify(dboRepository, Mockito.times(1))
                .saveDroneState(anyLong(), ArgumentMatchers.eq(State.LOADED.name()));
    }
    @Test
    public void getMedicationForDroneSuccess() throws DboException {
        this.service = new DronesService(dboRepository);
        Mockito.when(dboRepository.findMedicationByDroneId(1L))
                .thenReturn(getMedicationData());
        List<Medication> medicationList = service.getMedicationForDrone(1L);
        assertNotNull(medicationList);
        assertEquals(medicationList.size(), 1);
        assertEquals(medicationList.get(0).getId(), 1);
        assertEquals(medicationList.get(0).getCode(), "ME001");
        assertEquals(medicationList.get(0).getName(), "aspirin");
        assertEquals(medicationList.get(0).getWeight(), 10);
    }
    @Test
    public void getBatteryLevelFail() throws DboException {
        this.service = new DronesService(dboRepository);
        Exception ex;
        Mockito.when(dboRepository.findDroneById(1L)).thenReturn(Optional.empty());
        ex = assertThrowsExactly(BadRequestException.class, () -> service.getBatteryLevel(1L));
        assertEquals("Drone is not found", ex.getMessage());
    }
    @Test
    public void getBatteryLevelSuccess() throws DboException {
        this.service = new DronesService(dboRepository);
        Mockito.when(dboRepository.findDroneById(1L)).thenReturn(Optional.of(getDroneData().get(0)));
        int result = service.getBatteryLevel(1L);
        assertEquals(result, 65);
    }
    private List<DroneEntity> getDroneData() {
        List<DroneEntity> drones = new ArrayList<>();
        DroneEntity drone = new DroneEntity();
        drone.setId(1L);
        drone.setBatteryCapacity(65);
        drone.setModel("CRUISERWEIGHT");
        drone.setSerialNumber("088002C");
        drone.setState("IDLE");
        drone.setWeightLimit(300);
        drones.add(drone);
        return drones;
    }
    private List<MedicationEntity> getMedicationData() {
        List<MedicationEntity> medication = new ArrayList<>();
        MedicationEntity medicationEntity = new MedicationEntity();
        medicationEntity.setId(1L);
        medicationEntity.setCode("ME001");
        medicationEntity.setImage("ffff".getBytes(StandardCharsets.UTF_8));
        medicationEntity.setName("aspirin");
        medicationEntity.setWeight(10);
        medication.add(medicationEntity);
        return medication;
    }

    private String getLongSerialNumber() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            builder.append("1");
        }
        return builder.toString();
    }
}
