package com.logiagent.agent.tool;

import com.logiagent.agent.service.AgentToolLogService;
import com.logiagent.api.client.WaybillFeignClient;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Component;

@Component
public class WaybillTool {

    private static final String TOOL_NAME = "WaybillTool.getWaybillByNo";

    private final WaybillFeignClient waybillFeignClient;
    private final AgentToolLogService agentToolLogService;

    public WaybillTool(WaybillFeignClient waybillFeignClient, AgentToolLogService agentToolLogService) {
        this.waybillFeignClient = waybillFeignClient;
        this.agentToolLogService = agentToolLogService;
    }

    public WaybillDTO getWaybillByNo(String sessionId, String waybillNo) {
        long start = System.currentTimeMillis();
        try {
            Result<WaybillDTO> result = waybillFeignClient.getByWaybillNo(waybillNo);
            WaybillDTO data = result == null ? null : result.getData();
            if (data == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "waybill not found");
            }
            record(sessionId, waybillNo, data.getCurrentStatus(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, waybillNo, null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    private void record(String sessionId, String request, String response, boolean success, String errorMsg, long start) {
        agentToolLogService.record(
                sessionId,
                TOOL_NAME,
                request,
                response,
                success,
                errorMsg,
                System.currentTimeMillis() - start
        );
    }
}
