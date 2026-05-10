package com.logiagent.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardRiskDTO implements Serializable {

    private Map<String, Long> exceptionTypeCountMap;
    private List<StationLoadDTO> highLoadStations = new ArrayList<>();
    private Long failedToolCallCount;
    private List<String> riskSuggestions = new ArrayList<>();
    private List<String> unavailableFields = new ArrayList<>();

    public Map<String, Long> getExceptionTypeCountMap() {
        return exceptionTypeCountMap;
    }

    public void setExceptionTypeCountMap(Map<String, Long> exceptionTypeCountMap) {
        this.exceptionTypeCountMap = exceptionTypeCountMap;
    }

    public List<StationLoadDTO> getHighLoadStations() {
        return highLoadStations;
    }

    public void setHighLoadStations(List<StationLoadDTO> highLoadStations) {
        this.highLoadStations = highLoadStations;
    }

    public Long getFailedToolCallCount() {
        return failedToolCallCount;
    }

    public void setFailedToolCallCount(Long failedToolCallCount) {
        this.failedToolCallCount = failedToolCallCount;
    }

    public List<String> getRiskSuggestions() {
        return riskSuggestions;
    }

    public void setRiskSuggestions(List<String> riskSuggestions) {
        this.riskSuggestions = riskSuggestions;
    }

    public List<String> getUnavailableFields() {
        return unavailableFields;
    }

    public void setUnavailableFields(List<String> unavailableFields) {
        this.unavailableFields = unavailableFields;
    }
}
