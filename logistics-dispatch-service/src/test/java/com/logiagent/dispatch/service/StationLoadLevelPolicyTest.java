package com.logiagent.dispatch.service;

import com.logiagent.common.enums.StationLoadLevelEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationLoadLevelPolicyTest {

    private final StationLoadLevelPolicy policy = new StationLoadLevelPolicy();

    @Test
    void resolveLoadLevelByPendingTaskCount() {
        assertThat(policy.resolve(10, 0)).isEqualTo(StationLoadLevelEnum.LOW);
        assertThat(policy.resolve(20, 0)).isEqualTo(StationLoadLevelEnum.MEDIUM);
        assertThat(policy.resolve(50, 0)).isEqualTo(StationLoadLevelEnum.HIGH);
        assertThat(policy.resolve(100, 0)).isEqualTo(StationLoadLevelEnum.OVERLOAD);
    }

    @Test
    void resolveOverloadWhenExceptionCountIsHigh() {
        assertThat(policy.resolve(10, 10)).isEqualTo(StationLoadLevelEnum.OVERLOAD);
    }
}
