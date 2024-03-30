package com.github.olorini.validators;

import com.github.olorini.db.DboException;

public class OrValidator implements IValidator {

    private final Iterable<IValidator> validators;

    public OrValidator(Iterable<IValidator> validators) {
        this.validators = validators;
    }

    @Override
    public ValidationResult check() throws DboException {
       ValidationResult result = ValidationResult.ok();
        for (IValidator validator : validators) {
            result = validator.check();
            if (result.isOk()) {
                return result;
            }
        }
        return result;
    }
}
