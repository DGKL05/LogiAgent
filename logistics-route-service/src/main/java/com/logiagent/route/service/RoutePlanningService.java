package com.logiagent.route.service;

import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;

import java.util.List;

public interface RoutePlanningService {

    RoutePlanResponse plan(RoutePlanRequest request);

    List<StationDTO> searchStations(String keyword);
}
