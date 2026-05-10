package com.logiagent.dispatch.service;

import com.logiagent.common.enums.StationLoadLevelEnum;
import org.springframework.stereotype.Component;

@Component
public class StationLoadLevelPolicy {

    public StationLoadLevelEnum resolve(long pendingTaskCount, long exceptionWaybillCount) {
        if (pendingTaskCount >= 100 || exceptionWaybillCount >= 10) {
            return StationLoadLevelEnum.OVERLOAD;
        }
        if (pendingTaskCount >= 50) {
            return StationLoadLevelEnum.HIGH;
        }
        if (pendingTaskCount >= 20) {
            return StationLoadLevelEnum.MEDIUM;
        }
        return StationLoadLevelEnum.LOW;
    }
}
