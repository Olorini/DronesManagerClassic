package com.github.olorini.services;

import com.github.olorini.core.exceptions.BadRequestException;
import com.github.olorini.core.exceptions.WebErrorCode;
import com.github.olorini.db.DboException;
import com.github.olorini.db.DroneEntity;
import com.github.olorini.db.DboRepository;
import com.github.olorini.db.MedicationEntity;
import com.github.olorini.endpoints.pojo.Drone;
import com.github.olorini.endpoints.pojo.Medication;
import com.github.olorini.endpoints.pojo.State;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.olorini.core.exceptions.WebErrorCode.REQUEST_ERROR;

public class DronesService {
    @Context
    ServletContext servletContext;
    DboRepository dboRepository = new DboRepository();
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

    public Long register(Drone request) {
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
}
