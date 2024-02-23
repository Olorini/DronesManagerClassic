package com.github.olorini.core.exceptions;

public enum WebErrorCode {
    REQUEST_ERROR(1001L);
    private final Long code;

    WebErrorCode(Long code) {
        this.code = code;
    }

    public Long getCode() {
        return code;
    }
}
