package com.github.olorini.validators;

import com.github.olorini.db.DboException;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.db.dao.MedicationEntity;

import java.util.List;
import java.util.Map;

public class MedicineForLoadValidator implements IValidator {
    private final List<Long> medicineIds;
    private final Map<Long, MedicationEntity> medicationEntities;
    private final DroneEntity drone;

    public MedicineForLoadValidator(List<Long> medicineIds,
                                    Map<Long, MedicationEntity> medicationEntities,
                                    DroneEntity drone) {
        this.medicineIds = medicineIds;
        this.medicationEntities = medicationEntities;
        this.drone = drone;
    }

    @Override
    public ValidationResult check() throws DboException {
        if (medicationEntities.isEmpty()) {
            return ValidationResult.fail("Medicines are not found");
        }
        int weight = medicineIds.stream()
                .map(e -> medicationEntities.get(e).getWeight())
                .reduce(Integer::sum).orElse(Integer.MAX_VALUE);
        if (weight > drone.getWeightLimit()) {
            return ValidationResult.fail("Weight of medicine is very large");
        }
        return ValidationResult.ok();
    }
}
