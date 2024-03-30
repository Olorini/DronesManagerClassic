package com.github.olorini.validators;

public class ValidationResult {
    private final boolean result;
    private final String message;
    private ValidationResult(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
    public static ValidationResult fail(String message) {
        return new ValidationResult(false, message);
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, "");
    }

    public boolean isOk() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
