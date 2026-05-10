package com.logiagent.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardOverviewDTO implements Serializable {

    private Long totalOrderCount;
    private Long todayOrderCount;
    private Long totalWaybillCount;
    private Long todayWaybillCount;
    private Long exceptionWaybillCount;
    private Long signedWaybillCount;
    private Long trackUpdateCount;
    private Long activeWaybillCount;
    private Long overloadedStationCount;
    private Long agentSessionCount;
    private List<String> unavailableFields = new ArrayList<>();

    public Long getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Long totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public Long getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Long todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public Long getTotalWaybillCount() {
        return totalWaybillCount;
    }

    public void setTotalWaybillCount(Long totalWaybillCount) {
        this.totalWaybillCount = totalWaybillCount;
    }

    public Long getTodayWaybillCount() {
        return todayWaybillCount;
    }

    public void setTodayWaybillCount(Long todayWaybillCount) {
        this.todayWaybillCount = todayWaybillCount;
    }

    public Long getExceptionWaybillCount() {
        return exceptionWaybillCount;
    }

    public void setExceptionWaybillCount(Long exceptionWaybillCount) {
        this.exceptionWaybillCount = exceptionWaybillCount;
    }

    public Long getSignedWaybillCount() {
        return signedWaybillCount;
    }

    public void setSignedWaybillCount(Long signedWaybillCount) {
        this.signedWaybillCount = signedWaybillCount;
    }

    public Long getTrackUpdateCount() {
        return trackUpdateCount;
    }

    public void setTrackUpdateCount(Long trackUpdateCount) {
        this.trackUpdateCount = trackUpdateCount;
    }

    public Long getActiveWaybillCount() {
        return activeWaybillCount;
    }

    public void setActiveWaybillCount(Long activeWaybillCount) {
        this.activeWaybillCount = activeWaybillCount;
    }

    public Long getOverloadedStationCount() {
        return overloadedStationCount;
    }

    public void setOverloadedStationCount(Long overloadedStationCount) {
        this.overloadedStationCount = overloadedStationCount;
    }

    public Long getAgentSessionCount() {
        return agentSessionCount;
    }

    public void setAgentSessionCount(Long agentSessionCount) {
        this.agentSessionCount = agentSessionCount;
    }

    public List<String> getUnavailableFields() {
        return unavailableFields;
    }

    public void setUnavailableFields(List<String> unavailableFields) {
        this.unavailableFields = unavailableFields;
    }
}
