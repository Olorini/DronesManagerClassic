package com.github.olorini.core.exceptions;

import javax.xml.ws.WebServiceException;

public class BadRequestException extends WebServiceException {

    private final WebErrorCode errorCode;

    public BadRequestException(WebErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Long getErrorCode() {
        return errorCode.getCode();
    }
}
