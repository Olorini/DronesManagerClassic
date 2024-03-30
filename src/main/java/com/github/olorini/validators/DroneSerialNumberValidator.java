package com.github.olorini.validators;

import com.github.olorini.db.DboException;
import com.github.olorini.db.DboRepository;

public class DroneSerialNumberValidator implements IValidator {
    private final String serialNumber;
    private final DboRepository dboRepository;

    public DroneSerialNumberValidator(String serialNumber, DboRepository dboRepository) {
        this.serialNumber = serialNumber;
        this.dboRepository = dboRepository;
    }

    @Override
    public ValidationResult check() throws DboException {
        if (serialNumber == null || serialNumber.length() > 100) {
            return ValidationResult.fail("Serial number is too long");
        } if (dboRepository.existsBySerialNumber(serialNumber)) {
            return ValidationResult.fail("Drone with this serial number is already exist");
        } else {
            return ValidationResult.ok();
        }
    }
}
