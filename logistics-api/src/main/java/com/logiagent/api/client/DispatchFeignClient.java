package com.logiagent.api.client;

import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.DispatchTaskDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.request.CreateDispatchTaskRequest;
import com.logiagent.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "logistics-dispatch-service", path = "/dispatch")
public interface DispatchFeignClient {

    @PostMapping("/tasks")
    Result<DispatchTaskDTO> createTask(@RequestBody CreateDispatchTaskRequest request);

    @GetMapping("/tasks/{taskNo}")
    Result<DispatchTaskDTO> getTask(@PathVariable("taskNo") String taskNo);

    @GetMapping("/stations/{stationId}/load")
    Result<StationLoadDTO> getStationLoad(@PathVariable("stationId") Long stationId);

    @GetMapping("/stations/load-ranking")
    Result<List<StationLoadDTO>> loadRanking();

    @GetMapping("/suggestions")
    Result<List<DispatchSuggestionDTO>> suggestions();
}
