package com.logiagent.api.request;

import java.io.Serializable;

public class UpdateStatusRequest implements Serializable {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
