package com.logiagent.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class RouteStepDTO implements Serializable {

    private String instruction;
    private BigDecimal distance;
    private BigDecimal duration;
    private String path;

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
