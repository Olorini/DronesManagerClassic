package com.github.olorini.services;

import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.db.DboException;
import com.github.olorini.db.DboRepository;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.db.dao.LoadEntity;
import com.github.olorini.db.dao.MedicationEntity;
import com.github.olorini.endpoints.pojo.Drone;
import com.github.olorini.endpoints.pojo.Load;
import com.github.olorini.endpoints.pojo.Medication;
import com.github.olorini.endpoints.pojo.State;
import org.apache.commons.lang3.EnumUtils;

import javax.xml.ws.WebServiceException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.olorini.core.exceptions.WebErrorCode.REQUEST_ERROR;

public class DronesService {

    DboRepository dboRepository;

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
            if (serialNumber == null || serialNumber.length() > 100) {
                throw new BadRequestException(REQUEST_ERROR, "Serial number is too long");
            }
            if (request.getWeightLimit() > 500) {
                throw new BadRequestException(REQUEST_ERROR, "Weight limit is very large");
            }
            if (dboRepository.existsBySerialNumber(serialNumber)) {
                throw new BadRequestException(REQUEST_ERROR, "Drone with this serial number is already exist");
            }
            DroneEntity entity = new DroneEntity(request);
            request.setState(State.IDLE);
            return dboRepository.saveDrone(entity);
        } catch (DboException e) {
            throw new WebServiceException(e);
        }
    }

    public void loadDrone(Load request) {
        try {
            List<Long> result = new ArrayList<>();
            if (request.getDroneId() == null) {
                throw new BadRequestException(REQUEST_ERROR, "Drone identifier is empty");
            }
            if (request.getMedicineIds() == null || request.getMedicineIds().isEmpty()) {
                throw new BadRequestException(REQUEST_ERROR, "List of medicine identifiers are empty");
            }
            Optional<DroneEntity> dbDrone = dboRepository.findDroneById(request.getDroneId());
            if (!dbDrone.isPresent()) {
                throw new BadRequestException(REQUEST_ERROR, "Drone is not found");
            }
            DroneEntity droneEntity = dbDrone.get();
            checkDroneAvailableForLoading(droneEntity);
            saveDroneState(State.LOADING, droneEntity);
            Set<Long> medicineIds = new HashSet<>(request.getMedicineIds());
            Map<Long, MedicationEntity> medicationEntities = dboRepository.findMedication(medicineIds)
                    .stream().collect(Collectors.toMap(MedicationEntity::getId, Function.identity()));
            if (medicationEntities.isEmpty()) {
                saveDroneState(State.IDLE, droneEntity);
                throw new BadRequestException(REQUEST_ERROR, "Medicines are not found");
            }
            int weight = request.getMedicineIds().stream()
                    .map(e -> medicationEntities.get(e).getWeight())
                    .reduce(Integer::sum).orElse(Integer.MAX_VALUE);
            if (weight > droneEntity.getWeightLimit()) {
                saveDroneState(State.IDLE, droneEntity);
                throw new BadRequestException(REQUEST_ERROR, "Weight of medicine is very large");
            }
            try {
                for (Long medicationId : request.getMedicineIds()) {
                    dboRepository.saveLoad(new LoadEntity(droneEntity.getId(), medicationId));
                }
                saveDroneState(State.LOADED, droneEntity);
            } catch (DboException e) {
                saveDroneState(State.IDLE, droneEntity);
                throw e;
            }
        } catch (DboException e) {
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

    private void checkDroneAvailableForLoading(DroneEntity droneEntity) {
        if (EnumUtils.getEnumIgnoreCase(State.class, droneEntity.getState()) != State.IDLE) {
            throw new BadRequestException(REQUEST_ERROR, "Drone is busy");
        }
        if (droneEntity.getBatteryCapacity() < 25) {
            throw new BadRequestException(REQUEST_ERROR, "Drone isn't charged");
        }
    }

    private void saveDroneState(State state, DroneEntity drone) throws DboException {
        drone.setState(state.name());
        dboRepository.saveDroneState(drone.getId(), drone.getState());
    }
}
