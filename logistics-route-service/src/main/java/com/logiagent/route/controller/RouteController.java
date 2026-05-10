package com.logiagent.route.controller;

import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.result.Result;
import com.logiagent.route.service.RoutePlanningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RoutePlanningService routePlanningService;

    public RouteController(RoutePlanningService routePlanningService) {
        this.routePlanningService = routePlanningService;
    }

    @PostMapping("/plan")
    public Result<RoutePlanResponse> plan(@RequestBody RoutePlanRequest request) {
        return Result.success(routePlanningService.plan(request));
    }

    @GetMapping("/stations/search")
    public Result<List<StationDTO>> searchStations(@RequestParam("keyword") String keyword) {
        return Result.success(routePlanningService.searchStations(keyword));
    }
}
