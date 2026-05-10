package com.logiagent.agent.tool;

import com.logiagent.agent.service.AgentToolLogService;
import com.logiagent.api.client.DispatchFeignClient;
import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DispatchTool {

    private final DispatchFeignClient dispatchFeignClient;
    private final AgentToolLogService agentToolLogService;

    public DispatchTool(DispatchFeignClient dispatchFeignClient, AgentToolLogService agentToolLogService) {
        this.dispatchFeignClient = dispatchFeignClient;
        this.agentToolLogService = agentToolLogService;
    }

    public List<StationLoadDTO> loadRanking(String sessionId) {
        long start = System.currentTimeMillis();
        try {
            Result<List<StationLoadDTO>> result = dispatchFeignClient.loadRanking();
            List<StationLoadDTO> data = result == null || result.getData() == null ? List.of() : result.getData();
            record(sessionId, "DispatchTool.loadRanking", "load-ranking", String.valueOf(data.size()), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "DispatchTool.loadRanking", "load-ranking", null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    public List<DispatchSuggestionDTO> suggestions(String sessionId) {
        long start = System.currentTimeMillis();
        try {
            Result<List<DispatchSuggestionDTO>> result = dispatchFeignClient.suggestions();
            List<DispatchSuggestionDTO> data = result == null || result.getData() == null ? List.of() : result.getData();
            record(sessionId, "DispatchTool.suggestions", "suggestions", String.valueOf(data.size()), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "DispatchTool.suggestions", "suggestions", null, false, ex.getMessage(), start);
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
        agentToolLogService.record(sessionId, toolName, request, response, success, errorMsg, System.currentTimeMillis() - start);
    }
}
