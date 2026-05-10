package com.logiagent.agent.tool;

import com.logiagent.agent.service.AgentToolLogService;
import com.logiagent.api.client.RouteFeignClient;
import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteTool {

    private final RouteFeignClient routeFeignClient;
    private final AgentToolLogService agentToolLogService;

    public RouteTool(RouteFeignClient routeFeignClient, AgentToolLogService agentToolLogService) {
        this.routeFeignClient = routeFeignClient;
        this.agentToolLogService = agentToolLogService;
    }

    public List<StationDTO> searchStations(String sessionId, String keyword) {
        long start = System.currentTimeMillis();
        try {
            Result<List<StationDTO>> result = routeFeignClient.searchStations(keyword);
            List<StationDTO> data = result == null ? List.of() : result.getData();
            record(sessionId, "RouteTool.searchStations", keyword, String.valueOf(data.size()), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "RouteTool.searchStations", keyword, null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    public RoutePlanResponse planRoute(String sessionId, RoutePlanRequest request) {
        long start = System.currentTimeMillis();
        try {
            Result<RoutePlanResponse> result = routeFeignClient.plan(request);
            RoutePlanResponse data = result == null ? null : result.getData();
            if (data == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "route plan not found");
            }
            record(sessionId, "RouteTool.planRoute", request.getProvider() + ":" + request.getStrategy(),
                    data.getProvider() + ":" + data.getPathStationNames(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "RouteTool.planRoute", request.getProvider() + ":" + request.getStrategy(),
                    null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    private void record(String sessionId,
                        String toolName,
                        String request,
                        String response,
                        boolean success,
                        String errorMsg,
                        long start) {
        agentToolLogService.record(
                sessionId,
                toolName,
                request,
                response,
                success,
                errorMsg,
                System.currentTimeMillis() - start
        );
    }
}
