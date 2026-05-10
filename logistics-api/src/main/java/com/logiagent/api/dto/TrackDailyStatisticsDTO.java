package com.logiagent.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class TrackDailyStatisticsDTO {

    private LocalDate date;
    private String waybillNo;
    private Long trackUpdateCount;
    private Long activeWaybillCount;
    private Map<String, Long> actionCountMap = new LinkedHashMap<>();
    private LocalDateTime latestTrackUpdateTime;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
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

    public Map<String, Long> getActionCountMap() {
        return actionCountMap;
    }

    public void setActionCountMap(Map<String, Long> actionCountMap) {
        this.actionCountMap = actionCountMap;
    }

    public LocalDateTime getLatestTrackUpdateTime() {
        return latestTrackUpdateTime;
    }

    public void setLatestTrackUpdateTime(LocalDateTime latestTrackUpdateTime) {
        this.latestTrackUpdateTime = latestTrackUpdateTime;
    }
}
