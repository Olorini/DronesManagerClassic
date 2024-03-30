package com.github.olorini.validators;

import com.github.olorini.db.DboException;

import java.util.List;

public class LoadEmptyDataValidator implements IValidator {
    private final Long droneId;
    private final List<Long> medicineIds;

    public LoadEmptyDataValidator(Long droneId, List<Long> medicineIds) {
        this.droneId = droneId;
        this.medicineIds = medicineIds;
    }

    @Override
    public ValidationResult check() throws DboException {
        if (droneId == null) {
            return ValidationResult.fail("Drone identifier is empty");
        }
        if (medicineIds == null || medicineIds.isEmpty()) {
            return ValidationResult.fail("List of medicine identifiers are empty");
        }
        return ValidationResult.ok();
    }
}
