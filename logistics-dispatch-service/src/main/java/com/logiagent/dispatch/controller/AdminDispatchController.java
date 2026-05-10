package com.logiagent.dispatch.controller;

import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.DispatchTaskDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.request.AdminDispatchTaskQueryRequest;
import com.logiagent.api.request.CreateDispatchTaskRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.dispatch.service.DispatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dispatch")
public class AdminDispatchController {

    private final DispatchService dispatchService;

    public AdminDispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/tasks")
    public Result<PageResult<DispatchTaskDTO>> pageTasks(@ModelAttribute AdminDispatchTaskQueryRequest request) {
        return Result.success(dispatchService.pageAdminTasks(request));
    }

    @GetMapping("/tasks/{taskNo}")
    public Result<DispatchTaskDTO> getTask(@PathVariable("taskNo") String taskNo) {
        return Result.success(dispatchService.getTask(taskNo));
    }

    @PostMapping("/tasks")
    public Result<DispatchTaskDTO> createTask(@RequestBody CreateDispatchTaskRequest request) {
        return Result.success(dispatchService.createTask(request));
    }

    @PutMapping("/tasks/{taskNo}/status")
    public Result<DispatchTaskDTO> updateTaskStatus(@PathVariable("taskNo") String taskNo,
                                                    @RequestBody UpdateStatusRequest request) {
        return Result.success(dispatchService.updateTaskStatus(taskNo, request));
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
