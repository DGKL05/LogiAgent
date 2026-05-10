package com.logiagent.api.dto;

import java.time.LocalDate;
import java.util.List;

public class LogisticsDailyReportDTO {

    private LocalDate date;
    private OrderDailyStatisticsDTO orderStatistics;
    private WaybillDailyStatisticsDTO waybillStatistics;
    private TrackDailyStatisticsDTO trackStatistics;
    private List<StationLoadDTO> stationLoadRanking;
    private List<DispatchSuggestionDTO> dispatchSuggestions;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public OrderDailyStatisticsDTO getOrderStatistics() {
        return orderStatistics;
    }

    public void setOrderStatistics(OrderDailyStatisticsDTO orderStatistics) {
        this.orderStatistics = orderStatistics;
    }

    public WaybillDailyStatisticsDTO getWaybillStatistics() {
        return waybillStatistics;
    }

    public void setWaybillStatistics(WaybillDailyStatisticsDTO waybillStatistics) {
        this.waybillStatistics = waybillStatistics;
    }

    public TrackDailyStatisticsDTO getTrackStatistics() {
        return trackStatistics;
    }

    public void setTrackStatistics(TrackDailyStatisticsDTO trackStatistics) {
        this.trackStatistics = trackStatistics;
    }

    public List<StationLoadDTO> getStationLoadRanking() {
        return stationLoadRanking;
    }

    public void setStationLoadRanking(List<StationLoadDTO> stationLoadRanking) {
        this.stationLoadRanking = stationLoadRanking;
    }

    public List<DispatchSuggestionDTO> getDispatchSuggestions() {
        return dispatchSuggestions;
    }

    public void setDispatchSuggestions(List<DispatchSuggestionDTO> dispatchSuggestions) {
        this.dispatchSuggestions = dispatchSuggestions;
    }
}
