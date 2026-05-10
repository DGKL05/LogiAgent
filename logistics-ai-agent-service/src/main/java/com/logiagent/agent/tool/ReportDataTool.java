package com.logiagent.agent.tool;

import com.logiagent.agent.service.AgentToolLogService;
import com.logiagent.api.client.OrderFeignClient;
import com.logiagent.api.client.WaybillFeignClient;
import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Component;

@Component
public class ReportDataTool {

    private final OrderFeignClient orderFeignClient;
    private final WaybillFeignClient waybillFeignClient;
    private final AgentToolLogService agentToolLogService;

    public ReportDataTool(OrderFeignClient orderFeignClient,
                          WaybillFeignClient waybillFeignClient,
                          AgentToolLogService agentToolLogService) {
        this.orderFeignClient = orderFeignClient;
        this.waybillFeignClient = waybillFeignClient;
        this.agentToolLogService = agentToolLogService;
    }

    public Long orderTotal(String sessionId) {
        long start = System.currentTimeMillis();
        try {
            Result<PageResult<OrderDTO>> result = orderFeignClient.pageOrders(1, 1);
            Long total = result == null || result.getData() == null ? 0L : result.getData().getTotal();
            record(sessionId, "ReportDataTool.orderTotal", "page=1,size=1", String.valueOf(total), true, null, start);
            return total;
        } catch (RuntimeException ex) {
            record(sessionId, "ReportDataTool.orderTotal", "page=1,size=1", null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    public Long exceptionWaybillTotal(String sessionId) {
        long start = System.currentTimeMillis();
        try {
            Result<PageResult<WaybillDTO>> result = waybillFeignClient.listExceptions(1, 1);
            Long total = result == null || result.getData() == null ? 0L : result.getData().getTotal();
            record(sessionId, "ReportDataTool.exceptionWaybillTotal", "page=1,size=1", String.valueOf(total), true, null, start);
            return total;
        } catch (RuntimeException ex) {
            record(sessionId, "ReportDataTool.exceptionWaybillTotal", "page=1,size=1", null, false, ex.getMessage(), start);
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
