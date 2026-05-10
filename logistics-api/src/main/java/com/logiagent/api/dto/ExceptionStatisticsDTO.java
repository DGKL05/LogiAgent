package com.logiagent.api.dto;

import java.io.Serializable;
import java.util.Map;

public class ExceptionStatisticsDTO implements Serializable {

    private Long totalExceptionCount;
    private Long timeoutExceptionCount;
    private Long rejectedExceptionCount;
    private Long lostExceptionCount;
    private Map<String, Long> exceptionTypeCountMap;

    public Long getTotalExceptionCount() {
        return totalExceptionCount;
    }

    public void setTotalExceptionCount(Long totalExceptionCount) {
        this.totalExceptionCount = totalExceptionCount;
    }

    public Long getTimeoutExceptionCount() {
        return timeoutExceptionCount;
    }

    public void setTimeoutExceptionCount(Long timeoutExceptionCount) {
        this.timeoutExceptionCount = timeoutExceptionCount;
    }

    public Long getRejectedExceptionCount() {
        return rejectedExceptionCount;
    }

    public void setRejectedExceptionCount(Long rejectedExceptionCount) {
        this.rejectedExceptionCount = rejectedExceptionCount;
    }

    public Long getLostExceptionCount() {
        return lostExceptionCount;
    }

    public void setLostExceptionCount(Long lostExceptionCount) {
        this.lostExceptionCount = lostExceptionCount;
    }

    public Map<String, Long> getExceptionTypeCountMap() {
        return exceptionTypeCountMap;
    }

    public void setExceptionTypeCountMap(Map<String, Long> exceptionTypeCountMap) {
        this.exceptionTypeCountMap = exceptionTypeCountMap;
    }
}
