package com.logiagent.route.controller;

import com.logiagent.api.dto.AdminRouteDTO;
import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.AdminRouteQueryRequest;
import com.logiagent.api.request.AdminStationQueryRequest;
import com.logiagent.api.request.RouteSaveRequest;
import com.logiagent.api.request.StationSaveRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.route.service.RoutePlanningService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminRouteController {

    private final RoutePlanningService routePlanningService;

    public AdminRouteController(RoutePlanningService routePlanningService) {
        this.routePlanningService = routePlanningService;
    }

    @GetMapping("/admin/stations")
    public Result<PageResult<StationDTO>> pageStations(@ModelAttribute AdminStationQueryRequest request) {
        return Result.success(routePlanningService.pageAdminStations(request));
    }

    @GetMapping("/admin/stations/{stationId}")
    public Result<StationDTO> getStation(@PathVariable("stationId") Long stationId) {
        return Result.success(routePlanningService.getStation(stationId));
    }

    @PostMapping("/admin/stations")
    public Result<StationDTO> createStation(@RequestBody StationSaveRequest request) {
        return Result.success(routePlanningService.createStation(request));
    }

    @PutMapping("/admin/stations/{stationId}")
    public Result<StationDTO> updateStation(@PathVariable("stationId") Long stationId,
                                            @RequestBody StationSaveRequest request) {
        return Result.success(routePlanningService.updateStation(stationId, request));
    }

    @DeleteMapping("/admin/stations/{stationId}")
    public Result<StationDTO> disableStation(@PathVariable("stationId") Long stationId) {
        return Result.success(routePlanningService.disableStation(stationId));
    }

    @GetMapping("/admin/routes")
    public Result<PageResult<AdminRouteDTO>> pageRoutes(@ModelAttribute AdminRouteQueryRequest request) {
        return Result.success(routePlanningService.pageAdminRoutes(request));
    }

    @GetMapping("/admin/routes/{routeId}")
    public Result<AdminRouteDTO> getRoute(@PathVariable("routeId") Long routeId) {
        return Result.success(routePlanningService.getRoute(routeId));
    }

    @PostMapping("/admin/routes")
    public Result<AdminRouteDTO> createRoute(@RequestBody RouteSaveRequest request) {
        return Result.success(routePlanningService.createRoute(request));
    }

    @PutMapping("/admin/routes/{routeId}")
    public Result<AdminRouteDTO> updateRoute(@PathVariable("routeId") Long routeId,
                                             @RequestBody RouteSaveRequest request) {
        return Result.success(routePlanningService.updateRoute(routeId, request));
    }

    @DeleteMapping("/admin/routes/{routeId}")
    public Result<AdminRouteDTO> disableRoute(@PathVariable("routeId") Long routeId) {
        return Result.success(routePlanningService.disableRoute(routeId));
    }
}
