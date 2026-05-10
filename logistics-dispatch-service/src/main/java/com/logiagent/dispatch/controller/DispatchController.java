package com.logiagent.dispatch.controller;

import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.DispatchTaskDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.request.CreateDispatchTaskRequest;
import com.logiagent.common.result.Result;
import com.logiagent.dispatch.service.DispatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/tasks")
    public Result<DispatchTaskDTO> createTask(@RequestBody CreateDispatchTaskRequest request) {
        return Result.success(dispatchService.createTask(request));
    }

    @GetMapping("/tasks/{taskNo}")
    public Result<DispatchTaskDTO> getTask(@PathVariable("taskNo") String taskNo) {
        return Result.success(dispatchService.getTask(taskNo));
    }

    @GetMapping("/stations/{stationId}/load")
    public Result<StationLoadDTO> getStationLoad(@PathVariable("stationId") Long stationId) {
        return Result.success(dispatchService.getStationLoad(stationId));
    }

    @GetMapping("/stations/load-ranking")
    public Result<List<StationLoadDTO>> loadRanking() {
        return Result.success(dispatchService.loadRanking());
    }

    @GetMapping("/suggestions")
    public Result<List<DispatchSuggestionDTO>> suggestions() {
        return Result.success(dispatchService.suggestions());
    }
}
