package com.logiagent.agent.tool;

import com.logiagent.agent.service.AgentToolLogService;
import com.logiagent.api.client.TrackFeignClient;
import com.logiagent.api.dto.TrackDTO;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrackTool {

    private final TrackFeignClient trackFeignClient;
    private final AgentToolLogService agentToolLogService;

    public TrackTool(TrackFeignClient trackFeignClient, AgentToolLogService agentToolLogService) {
        this.trackFeignClient = trackFeignClient;
        this.agentToolLogService = agentToolLogService;
    }

    public List<TrackDTO> getTracksByWaybillNo(String sessionId, String waybillNo) {
        String toolName = "TrackTool.getTracksByWaybillNo";
        long start = System.currentTimeMillis();
        try {
            Result<List<TrackDTO>> result = trackFeignClient.listByWaybillNo(waybillNo);
            List<TrackDTO> data = result == null || result.getData() == null ? List.of() : result.getData();
            record(sessionId, toolName, waybillNo, "trackCount=" + data.size(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, toolName, waybillNo, null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    public TrackDTO getLatestTrackByWaybillNo(String sessionId, String waybillNo) {
        String toolName = "TrackTool.getLatestTrackByWaybillNo";
        long start = System.currentTimeMillis();
        try {
            Result<TrackDTO> result = trackFeignClient.getLatestByWaybillNo(waybillNo);
            TrackDTO data = result == null ? null : result.getData();
            if (data == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "latest track not found");
            }
            record(sessionId, toolName, waybillNo, data.getAction(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, toolName, waybillNo, null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    private void record(String sessionId, String toolName, String request, String response, boolean success, String errorMsg, long start) {
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
