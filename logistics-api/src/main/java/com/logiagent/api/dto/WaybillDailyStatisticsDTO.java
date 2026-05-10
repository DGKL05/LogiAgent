package com.logiagent.api.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class WaybillDailyStatisticsDTO {

    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalWaybillCount;
    private Long newWaybillCount;
    private Long signedWaybillCount;
    private Long exceptionWaybillCount;
    private Long transportingWaybillCount;
    private Map<String, Long> waybillStatusCountMap = new LinkedHashMap<>();
    private Map<String, Long> exceptionTypeCountMap = new LinkedHashMap<>();

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getTotalWaybillCount() {
        return totalWaybillCount;
    }

    public void setTotalWaybillCount(Long totalWaybillCount) {
        this.totalWaybillCount = totalWaybillCount;
    }

    public Long getNewWaybillCount() {
        return newWaybillCount;
    }

    public void setNewWaybillCount(Long newWaybillCount) {
        this.newWaybillCount = newWaybillCount;
    }

    public Long getSignedWaybillCount() {
        return signedWaybillCount;
    }

    public void setSignedWaybillCount(Long signedWaybillCount) {
        this.signedWaybillCount = signedWaybillCount;
    }

    public Long getExceptionWaybillCount() {
        return exceptionWaybillCount;
    }

    public void setExceptionWaybillCount(Long exceptionWaybillCount) {
        this.exceptionWaybillCount = exceptionWaybillCount;
    }

    public Long getTransportingWaybillCount() {
        return transportingWaybillCount;
    }

    public void setTransportingWaybillCount(Long transportingWaybillCount) {
        this.transportingWaybillCount = transportingWaybillCount;
    }

    public Map<String, Long> getWaybillStatusCountMap() {
        return waybillStatusCountMap;
    }

    public void setWaybillStatusCountMap(Map<String, Long> waybillStatusCountMap) {
        this.waybillStatusCountMap = waybillStatusCountMap;
    }

    public Map<String, Long> getExceptionTypeCountMap() {
        return exceptionTypeCountMap;
    }

    public void setExceptionTypeCountMap(Map<String, Long> exceptionTypeCountMap) {
        this.exceptionTypeCountMap = exceptionTypeCountMap;
    }
}
