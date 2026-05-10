package com.logiagent.api.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class DashboardTrendDTO implements Serializable {

    private LocalDate date;
    private Long orderCount;
    private Long waybillCount;
    private Long trackUpdateCount;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getWaybillCount() {
        return waybillCount;
    }

    public void setWaybillCount(Long waybillCount) {
        this.waybillCount = waybillCount;
    }

    public Long getTrackUpdateCount() {
        return trackUpdateCount;
    }

    public void setTrackUpdateCount(Long trackUpdateCount) {
        this.trackUpdateCount = trackUpdateCount;
    }
}
