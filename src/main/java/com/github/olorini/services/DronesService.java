package com.github.olorini.services;

import com.github.olorini.core.AppContext;
import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.db.DboException;
import com.github.olorini.db.DboProcessException;
import com.github.olorini.db.DboRepository;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.db.dao.LoadEntity;
import com.github.olorini.db.dao.MedicationEntity;
import com.github.olorini.endpoints.pojo.Drone;
import com.github.olorini.endpoints.pojo.Load;
import com.github.olorini.endpoints.pojo.Medication;
import com.github.olorini.endpoints.pojo.State;
import com.github.olorini.validators.*;
import org.apache.log4j.Logger;

import javax.xml.ws.WebServiceException;
import java.sql.Connection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.olorini.core.exceptions.WebErrorCode.REQUEST_ERROR;

public class DronesService {

    private static final Logger LOGGER = Logger.getLogger(DronesService.class);
    private final DboRepository dboRepository;
    public DronesService() {
        this(new DboRepository());
    }
    public DronesService(DboRepository dboRepository) {
        this.dboRepository = dboRepository;
    }
    public List<Drone> getDrones() {
        try {
            List<DroneEntity> drones = dboRepository.getDrones();
            return drones.stream()
                    .map(Drone::new)
                    .collect(Collectors.toList());
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }
    public List<Medication> getMedication() {
        try {
            List<MedicationEntity> drones = dboRepository.getMedicine();
            return drones.stream()
                    .map(Medication::new)
                    .collect(Collectors.toList());
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }
    public Long registerNewDrone(Drone request) {
        try {
            String serialNumber = request.getSerialNumber();
            AndValidator validator = new AndValidator();
            validator.add(new DroneWeightValidator(request.getWeightLimit()))
                    .add(new DroneSerialNumberValidator(serialNumber));
            forceCheck(validator);
            DroneEntity entity = new DroneEntity(request);
            request.setState(State.IDLE);
            return dboRepository.saveDrone(entity);
        } catch (DboProcessException e) {
            throw new BadRequestException(REQUEST_ERROR, e.getMessage());
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }
    public void loadDrone(Load request) {
        try {
            forceCheck(new LoadEmptyDataValidator(request.getDroneId(), request.getMedicineIds()));
            Optional<DroneEntity> dbDrone = dboRepository.findDroneById(request.getDroneId());
            if (!dbDrone.isPresent()) {
                throw new BadRequestException(REQUEST_ERROR, "Drone is not found");
            }
            DroneEntity droneEntity = dbDrone.get();
            forceCheck(new DroneForLoadValidator(droneEntity));
            try (Connection conn = AppContext.getConnection()) {
                conn.setAutoCommit(false);
                saveDroneState(State.LOADING, droneEntity);
                Set<Long> medicineIds = new HashSet<>(request.getMedicineIds());
                Map<Long, MedicationEntity> medicationEntities = dboRepository.findMedication(medicineIds)
                        .stream()
                        .collect(Collectors.toMap(MedicationEntity::getId, Function.identity()));
                ValidationResult validationResult =
                        new MedicineForLoadValidator(request.getMedicineIds(), medicationEntities, droneEntity)
                        .check();
                if (!validationResult.isOk()) {
                    conn.rollback();
                    throw new BadRequestException(REQUEST_ERROR, validationResult.getMessage());
                }
                try {
                    for (Long medicationId : request.getMedicineIds()) {
                        dboRepository.saveLoad(new LoadEntity(droneEntity.getId(), medicationId));
                    }
                    saveDroneState(State.LOADED, droneEntity);
                    conn.commit();
                } catch (DboException e) {
                    conn.rollback();
                    LOGGER.error(e);
                    throw e;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new WebServiceException(e);
        }
    }
    public List<Medication> getMedicationForDrone(long droneId) {
        try {
            List<MedicationEntity> loads = dboRepository.findMedicationByDroneId(droneId);
            return loads.stream()
                    .map(Medication::new)
                    .collect(Collectors.toList());
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }
    public List<Drone> getIdleDrones() {
        try {
            List<DroneEntity> drones = dboRepository.findDroneByState(State.IDLE.name());
            return drones.stream()
                    .map(Drone::new)
                    .collect(Collectors.toList());
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }
    public int getBatteryLevel(long droneId) {
        try {
            Optional<DroneEntity> droneEntity = dboRepository.findDroneById(droneId);
            if (!droneEntity.isPresent()) {
                throw new BadRequestException(REQUEST_ERROR, "Drone is not found");
            }
            return droneEntity.get().getBatteryCapacity();
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }
    private void forceCheck(IValidator validator) throws DboException {
        ValidationResult validationResult = validator.check();
        if (!validationResult.isOk()) {
            throw new BadRequestException(REQUEST_ERROR, validationResult.getMessage());
        }
    }
    private void saveDroneState(State state, DroneEntity drone) throws DboException {
        drone.setState(state.name());
        dboRepository.saveDroneState(drone.getId(), drone.getState());
    }
}
