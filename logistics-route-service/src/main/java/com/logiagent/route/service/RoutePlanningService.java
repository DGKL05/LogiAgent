package com.logiagent.route.service;

import com.logiagent.api.dto.AdminRouteDTO;
import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.AdminRouteQueryRequest;
import com.logiagent.api.request.AdminStationQueryRequest;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.request.RouteSaveRequest;
import com.logiagent.api.request.StationSaveRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.result.PageResult;

import java.util.List;

public interface RoutePlanningService {

    RoutePlanResponse plan(RoutePlanRequest request);

    List<StationDTO> searchStations(String keyword);

    PageResult<StationDTO> pageAdminStations(AdminStationQueryRequest request);

    StationDTO getStation(Long stationId);

    StationDTO createStation(StationSaveRequest request);

    StationDTO updateStation(Long stationId, StationSaveRequest request);

    StationDTO disableStation(Long stationId);

    PageResult<AdminRouteDTO> pageAdminRoutes(AdminRouteQueryRequest request);

    AdminRouteDTO getRoute(Long routeId);

    AdminRouteDTO createRoute(RouteSaveRequest request);

    AdminRouteDTO updateRoute(Long routeId, RouteSaveRequest request);

    AdminRouteDTO disableRoute(Long routeId);
}
