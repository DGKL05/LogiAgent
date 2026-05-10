package com.logiagent.route.planner;

import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.common.enums.RouteProviderEnum;
import org.springframework.stereotype.Component;

@Component
public class RoutePlannerFactory {

    private final LocalDijkstraRoutePlanner localDijkstraRoutePlanner;
    private final BaiduDrivingRoutePlanner baiduDrivingRoutePlanner;

    public RoutePlannerFactory(LocalDijkstraRoutePlanner localDijkstraRoutePlanner,
                               BaiduDrivingRoutePlanner baiduDrivingRoutePlanner) {
        this.localDijkstraRoutePlanner = localDijkstraRoutePlanner;
        this.baiduDrivingRoutePlanner = baiduDrivingRoutePlanner;
    }

    public RoutePlanner getPlanner(RoutePlanRequest request) {
        RouteProviderEnum provider = request == null ? null : request.getProvider();
        if (provider == RouteProviderEnum.BAIDU_DRIVING) {
            return baiduDrivingRoutePlanner;
        }
        return localDijkstraRoutePlanner;
    }
}
