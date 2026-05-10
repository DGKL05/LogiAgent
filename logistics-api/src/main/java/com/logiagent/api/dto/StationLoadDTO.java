package com.logiagent.api.dto;

import java.io.Serializable;

public class StationLoadDTO implements Serializable {

    private Long stationId;
    private String stationName;
    private Long pendingWaybillCount;
    private Long exceptionWaybillCount;
    private Long dispatchTaskCount;
    private Long courierCount;
    private Long driverCount;
    private String loadLevel;

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Long getPendingWaybillCount() {
        return pendingWaybillCount;
    }

    public void setPendingWaybillCount(Long pendingWaybillCount) {
        this.pendingWaybillCount = pendingWaybillCount;
    }

    public Long getExceptionWaybillCount() {
        return exceptionWaybillCount;
    }

    public void setExceptionWaybillCount(Long exceptionWaybillCount) {
        this.exceptionWaybillCount = exceptionWaybillCount;
    }

    public Long getDispatchTaskCount() {
        return dispatchTaskCount;
    }

    public void setDispatchTaskCount(Long dispatchTaskCount) {
        this.dispatchTaskCount = dispatchTaskCount;
    }

    public Long getCourierCount() {
        return courierCount;
    }

    public void setCourierCount(Long courierCount) {
        this.courierCount = courierCount;
    }

    public Long getDriverCount() {
        return driverCount;
    }

    public void setDriverCount(Long driverCount) {
        this.driverCount = driverCount;
    }

    public String getLoadLevel() {
        return loadLevel;
    }

    public void setLoadLevel(String loadLevel) {
        this.loadLevel = loadLevel;
    }
}
