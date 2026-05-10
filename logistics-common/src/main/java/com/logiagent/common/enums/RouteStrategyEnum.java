package com.logiagent.common.enums;

import java.math.BigDecimal;

public enum RouteStrategyEnum {
    DISTANCE_FIRST(3),
    TIME_FIRST(0),
    COST_FIRST(4),
    BALANCED(0),
    AVOID_HIGHWAY(1),
    AVOID_TRAFFIC(2),
    HIGHWAY_FIRST(5),
    AVOID_FERRY(6);

    private final int baiduTactics;

    RouteStrategyEnum(int baiduTactics) {
        this.baiduTactics = baiduTactics;
    }

    public int getBaiduTactics() {
        return baiduTactics;
    }

    public BigDecimal localWeight(BigDecimal distanceKm, BigDecimal durationMinute, BigDecimal cost) {
        BigDecimal safeDistance = distanceKm == null ? BigDecimal.ZERO : distanceKm;
        BigDecimal safeDuration = durationMinute == null ? BigDecimal.ZERO : durationMinute;
        BigDecimal safeCost = cost == null ? BigDecimal.ZERO : cost;
        return switch (this) {
            case DISTANCE_FIRST -> safeDistance;
            case COST_FIRST -> safeCost;
            case BALANCED -> safeDistance.add(safeDuration.divide(BigDecimal.valueOf(60), 4, java.math.RoundingMode.HALF_UP))
                    .add(safeCost.divide(BigDecimal.valueOf(20), 4, java.math.RoundingMode.HALF_UP));
            default -> safeDuration;
        };
    }
}
