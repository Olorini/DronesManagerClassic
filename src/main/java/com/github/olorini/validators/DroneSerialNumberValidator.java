package com.github.olorini.validators;

import com.github.olorini.db.DboException;

public class DroneSerialNumberValidator implements IValidator {
    private final String serialNumber;
    public DroneSerialNumberValidator(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    @Override
    public ValidationResult check() throws DboException {
        if (serialNumber == null || serialNumber.length() > 100) {
            return ValidationResult.fail("Serial number is too long");
        } else {
            return ValidationResult.ok();
        }
    }
}
