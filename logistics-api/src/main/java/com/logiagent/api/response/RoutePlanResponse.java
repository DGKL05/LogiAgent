package com.logiagent.api.response;

import com.logiagent.api.dto.RouteStepDTO;
import com.logiagent.common.enums.RouteProviderEnum;
import com.logiagent.common.enums.RouteStrategyEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoutePlanResponse implements Serializable {

    private RouteProviderEnum provider;
    private RouteStrategyEnum strategy;
    private List<Long> pathStationIds = new ArrayList<>();
    private List<String> pathStationNames = new ArrayList<>();
    private BigDecimal totalDistance;
    private BigDecimal totalDuration;
    private BigDecimal totalCost;
    private BigDecimal toll;
    private String trafficCondition;
    private String routeSummary;
    private Boolean fallbackUsed = false;
    private String fallbackReason;
    private List<RouteStepDTO> steps = new ArrayList<>();

    public RouteProviderEnum getProvider() {
        return provider;
    }

    public void setProvider(RouteProviderEnum provider) {
        this.provider = provider;
    }

    public RouteStrategyEnum getStrategy() {
        return strategy;
    }

    public void setStrategy(RouteStrategyEnum strategy) {
        this.strategy = strategy;
    }

    public List<Long> getPathStationIds() {
        return pathStationIds;
    }

    public void setPathStationIds(List<Long> pathStationIds) {
        this.pathStationIds = pathStationIds;
    }

    public List<String> getPathStationNames() {
        return pathStationNames;
    }

    public void setPathStationNames(List<String> pathStationNames) {
        this.pathStationNames = pathStationNames;
    }

    public BigDecimal getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(BigDecimal totalDistance) {
        this.totalDistance = totalDistance;
    }

    public BigDecimal getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigDecimal totalDuration) {
        this.totalDuration = totalDuration;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getToll() {
        return toll;
    }

    public void setToll(BigDecimal toll) {
        this.toll = toll;
    }

    public String getTrafficCondition() {
        return trafficCondition;
    }

    public void setTrafficCondition(String trafficCondition) {
        this.trafficCondition = trafficCondition;
    }

    public String getRouteSummary() {
        return routeSummary;
    }

    public void setRouteSummary(String routeSummary) {
        this.routeSummary = routeSummary;
    }

    public Boolean getFallbackUsed() {
        return fallbackUsed;
    }

    public void setFallbackUsed(Boolean fallbackUsed) {
        this.fallbackUsed = fallbackUsed;
    }

    public String getFallbackReason() {
        return fallbackReason;
    }

    public void setFallbackReason(String fallbackReason) {
        this.fallbackReason = fallbackReason;
    }

    public List<RouteStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStepDTO> steps) {
        this.steps = steps;
    }
}
