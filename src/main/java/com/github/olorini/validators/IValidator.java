package com.github.olorini.validators;

import com.github.olorini.db.DboException;

public interface IValidator {

    ValidationResult check() throws DboException;
}
