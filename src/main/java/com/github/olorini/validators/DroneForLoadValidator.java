package com.github.olorini.validators;

import com.github.olorini.db.DboException;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.endpoints.pojo.State;
import org.apache.commons.lang3.EnumUtils;

public class DroneForLoadValidator implements IValidator {
    private final DroneEntity drone;

    public DroneForLoadValidator(DroneEntity drone) {
        this.drone = drone;
    }

    @Override
    public ValidationResult check() throws DboException {
        if (drone == null) {
            return ValidationResult.fail("Drone is not found");
        }
        if (EnumUtils.getEnumIgnoreCase(State.class, drone.getState()) != State.IDLE) {
            return ValidationResult.fail("Drone is busy");
        }
        if (drone.getBatteryCapacity() < 25) {
            return ValidationResult.fail("Drone isn't charged");
        }
        return ValidationResult.ok();
    }
}
