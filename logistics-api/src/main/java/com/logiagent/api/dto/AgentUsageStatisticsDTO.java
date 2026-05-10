package com.logiagent.api.dto;

import java.io.Serializable;
import java.util.Map;

public class AgentUsageStatisticsDTO implements Serializable {

    private Long totalSessionCount;
    private Long todaySessionCount;
    private Map<String, Long> intentCountMap;
    private Long totalToolCallCount;
    private Long failedToolCallCount;

    public Long getTotalSessionCount() {
        return totalSessionCount;
    }

    public void setTotalSessionCount(Long totalSessionCount) {
        this.totalSessionCount = totalSessionCount;
    }

    public Long getTodaySessionCount() {
        return todaySessionCount;
    }

    public void setTodaySessionCount(Long todaySessionCount) {
        this.todaySessionCount = todaySessionCount;
    }

    public Map<String, Long> getIntentCountMap() {
        return intentCountMap;
    }

    public void setIntentCountMap(Map<String, Long> intentCountMap) {
        this.intentCountMap = intentCountMap;
    }

    public Long getTotalToolCallCount() {
        return totalToolCallCount;
    }

    public void setTotalToolCallCount(Long totalToolCallCount) {
        this.totalToolCallCount = totalToolCallCount;
    }

    public Long getFailedToolCallCount() {
        return failedToolCallCount;
    }

    public void setFailedToolCallCount(Long failedToolCallCount) {
        this.failedToolCallCount = failedToolCallCount;
    }
}
