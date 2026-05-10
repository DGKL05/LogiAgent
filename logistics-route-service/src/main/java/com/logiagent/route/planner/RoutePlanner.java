package com.logiagent.route.planner;

import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;

public interface RoutePlanner {

    RoutePlanResponse plan(RoutePlanRequest request);
}
