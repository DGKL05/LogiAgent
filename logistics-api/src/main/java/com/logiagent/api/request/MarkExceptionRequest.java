package com.logiagent.api.request;

import java.io.Serializable;

public class MarkExceptionRequest implements Serializable {

    private String exceptionType;
    private String exceptionReason;

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }
}
