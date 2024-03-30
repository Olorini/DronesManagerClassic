package com.github.olorini.validators;

import com.github.olorini.db.DboException;

import java.util.ArrayList;
import java.util.Collection;

public class AndValidator implements IValidator {

    private final Collection<IValidator> validators = new ArrayList<>();

    public AndValidator() {}

    public AndValidator add(IValidator validator) {
        validators.add(validator);
        return this;
    }

    @Override
    public ValidationResult check() throws DboException {
       ValidationResult result = ValidationResult.ok();
        for (IValidator validator : validators) {
            result = validator.check();
            if (!result.isOk()) {
                break;
            }
        }
        return result;
    }
}
