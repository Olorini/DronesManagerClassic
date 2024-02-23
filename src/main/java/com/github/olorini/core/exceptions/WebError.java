package com.github.olorini.core.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebError {
    private Long errorCode;
    private String errorText;

    public WebError(Long errorCode, String errorText) {
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    public WebError() {}

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
