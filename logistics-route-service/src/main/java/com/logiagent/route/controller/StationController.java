package com.logiagent.route.controller;

import com.logiagent.api.dto.StationDTO;
import com.logiagent.common.result.Result;
import com.logiagent.route.service.RoutePlanningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final RoutePlanningService routePlanningService;

    public StationController(RoutePlanningService routePlanningService) {
        this.routePlanningService = routePlanningService;
    }

    @GetMapping("/search")
    public Result<List<StationDTO>> searchStations(@RequestParam("keyword") String keyword) {
        return Result.success(routePlanningService.searchStations(keyword));
    }
}
