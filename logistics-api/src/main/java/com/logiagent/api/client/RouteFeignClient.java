package com.logiagent.api.client;

import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "logistics-route-service", path = "/routes")
public interface RouteFeignClient {

    @PostMapping("/plan")
    Result<RoutePlanResponse> plan(@RequestBody RoutePlanRequest request);

    @GetMapping("/stations/search")
    Result<List<StationDTO>> searchStations(@RequestParam("keyword") String keyword);
}
