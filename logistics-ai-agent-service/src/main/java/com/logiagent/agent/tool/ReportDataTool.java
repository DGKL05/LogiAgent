package com.logiagent.agent.tool;

import com.logiagent.agent.service.AgentToolLogService;
import com.logiagent.api.client.OrderFeignClient;
import com.logiagent.api.client.TrackFeignClient;
import com.logiagent.api.client.WaybillFeignClient;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.TrackDailyStatisticsDTO;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.dto.WaybillDailyStatisticsDTO;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReportDataTool {

    private final OrderFeignClient orderFeignClient;
    private final WaybillFeignClient waybillFeignClient;
    private final TrackFeignClient trackFeignClient;
    private final AgentToolLogService agentToolLogService;

    public ReportDataTool(OrderFeignClient orderFeignClient,
                          WaybillFeignClient waybillFeignClient,
                          TrackFeignClient trackFeignClient,
                          AgentToolLogService agentToolLogService) {
        this.orderFeignClient = orderFeignClient;
        this.waybillFeignClient = waybillFeignClient;
        this.trackFeignClient = trackFeignClient;
        this.agentToolLogService = agentToolLogService;
    }

    public OrderDailyStatisticsDTO orderDailyStatistics(String sessionId, LocalDate date) {
        long start = System.currentTimeMillis();
        String request = "date=" + date;
        try {
            Result<OrderDailyStatisticsDTO> result = orderFeignClient.dailyStatistics(date);
            OrderDailyStatisticsDTO data = requireData(result, "order statistics unavailable");
            record(sessionId, "ReportDataTool.orderDailyStatistics", request, "newOrderCount=" + data.getNewOrderCount(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "ReportDataTool.orderDailyStatistics", request, null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    public WaybillDailyStatisticsDTO waybillDailyStatistics(String sessionId, LocalDate date) {
        long start = System.currentTimeMillis();
        String request = "date=" + date;
        try {
            Result<WaybillDailyStatisticsDTO> result = waybillFeignClient.dailyStatistics(date);
            WaybillDailyStatisticsDTO data = requireData(result, "waybill statistics unavailable");
            record(sessionId, "ReportDataTool.waybillDailyStatistics", request, "newWaybillCount=" + data.getNewWaybillCount(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "ReportDataTool.waybillDailyStatistics", request, null, false, ex.getMessage(), start);
            throw ex;
        }
    }

    public TrackDailyStatisticsDTO trackDailyStatistics(String sessionId, LocalDate date) {
        long start = System.currentTimeMillis();
        String request = "date=" + date;
        try {
            Result<TrackDailyStatisticsDTO> result = trackFeignClient.dailyStatistics(date);
            TrackDailyStatisticsDTO data = requireData(result, "track statistics unavailable");
            record(sessionId, "ReportDataTool.trackDailyStatistics", request, "trackUpdateCount=" + data.getTrackUpdateCount(), true, null, start);
            return data;
        } catch (RuntimeException ex) {
            record(sessionId, "ReportDataTool.trackDailyStatistics", request, null, false, ex.getMessage(), start);
            throw ex;
        }
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

    private <T> T requireData(Result<T> result, String message) {
        if (result == null || result.getData() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, message);
        }
        return result.getData();
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
