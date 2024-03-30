package com.github.olorini.validators;

public class DroneWeightValidator implements IValidator {
    private final int weight;

    public DroneWeightValidator(int weight) {
        this.weight = weight;
    }

    @Override
    public ValidationResult check() {
        if (weight > 500) {
            return ValidationResult.fail("Weight limit is very large");
        } else {
            return ValidationResult.ok();
        }
    }
}
