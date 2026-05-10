package com.logiagent.api.request;

import com.logiagent.common.enums.RouteProviderEnum;
import com.logiagent.common.enums.RouteStrategyEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoutePlanRequest implements Serializable {

    private Long startStationId;
    private Long endStationId;
    private RouteProviderEnum provider = RouteProviderEnum.LOCAL_DIJKSTRA;
    private RouteStrategyEnum strategy = RouteStrategyEnum.BALANCED;
    private List<Long> waypoints = new ArrayList<>();
    private String coordType = "bd09ll";
    private String retCoordType = "bd09ll";

    public Long getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(Long startStationId) {
        this.startStationId = startStationId;
    }

    public Long getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(Long endStationId) {
        this.endStationId = endStationId;
    }

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

    public List<Long> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Long> waypoints) {
        this.waypoints = waypoints;
    }

    public String getCoordType() {
        return coordType;
    }

    public void setCoordType(String coordType) {
        this.coordType = coordType;
    }

    public String getRetCoordType() {
        return retCoordType;
    }

    public void setRetCoordType(String retCoordType) {
        this.retCoordType = retCoordType;
    }
}
