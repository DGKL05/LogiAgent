package com.logiagent.route.planner;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BaiduDrivingRoutePlannerTest {

    @Test
    void formatCoordinateUsesLatitudeBeforeLongitude() {
        String coordinate = BaiduDrivingRoutePlanner.formatCoordinate(
                new BigDecimal("23.129110"),
                new BigDecimal("113.264385")
        );

        assertThat(coordinate).isEqualTo("23.129110,113.264385");
    }
}
