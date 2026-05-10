package com.logiagent.api.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderDailyStatisticsDTO {

    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalOrderCount;
    private Long newOrderCount;
    private Long cancelledOrderCount;
    private Map<String, Long> orderStatusCountMap = new LinkedHashMap<>();

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

    public Long getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Long totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public Long getNewOrderCount() {
        return newOrderCount;
    }

    public void setNewOrderCount(Long newOrderCount) {
        this.newOrderCount = newOrderCount;
    }

    public Long getCancelledOrderCount() {
        return cancelledOrderCount;
    }

    public void setCancelledOrderCount(Long cancelledOrderCount) {
        this.cancelledOrderCount = cancelledOrderCount;
    }

    public Map<String, Long> getOrderStatusCountMap() {
        return orderStatusCountMap;
    }

    public void setOrderStatusCountMap(Map<String, Long> orderStatusCountMap) {
        this.orderStatusCountMap = orderStatusCountMap;
    }
}
