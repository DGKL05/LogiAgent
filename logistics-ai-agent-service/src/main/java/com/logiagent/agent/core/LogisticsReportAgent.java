package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.tool.DispatchTool;
import com.logiagent.agent.tool.ReportDataTool;
import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.dto.TrackDailyStatisticsDTO;
import com.logiagent.api.dto.WaybillDailyStatisticsDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LogisticsReportAgent {

    private static final String UNAVAILABLE = "该项数据暂不可用";

    private final ReportDataTool reportDataTool;
    private final DispatchTool dispatchTool;

    public LogisticsReportAgent(ReportDataTool reportDataTool, DispatchTool dispatchTool) {
        this.reportDataTool = reportDataTool;
        this.dispatchTool = dispatchTool;
    }

    public ChatResponse generate(String sessionId) {
        LocalDate date = LocalDate.now();
        List<String> unavailableItems = new ArrayList<>();
        OrderDailyStatisticsDTO orderStats = fetchOrderStats(sessionId, date, unavailableItems);
        WaybillDailyStatisticsDTO waybillStats = fetchWaybillStats(sessionId, date, unavailableItems);
        TrackDailyStatisticsDTO trackStats = fetchTrackStats(sessionId, date, unavailableItems);
        List<StationLoadDTO> ranking = fetchLoadRanking(sessionId, unavailableItems);
        List<DispatchSuggestionDTO> suggestions = fetchSuggestions(sessionId, unavailableItems);

        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.DAILY_REPORT.name());
        response.setAnswer(markdown(date, orderStats, waybillStats, trackStats, ranking, suggestions, unavailableItems));
        response.setToolCalls(List.of(
                "ReportDataTool.orderDailyStatistics",
                "ReportDataTool.waybillDailyStatistics",
                "ReportDataTool.trackDailyStatistics",
                "DispatchTool.loadRanking",
                "DispatchTool.suggestions"
        ));
        return response;
    }

    private OrderDailyStatisticsDTO fetchOrderStats(String sessionId, LocalDate date, List<String> unavailableItems) {
        try {
            return reportDataTool.orderDailyStatistics(sessionId, date);
        } catch (RuntimeException ex) {
            unavailableItems.add("订单统计");
            return null;
        }
    }

    private WaybillDailyStatisticsDTO fetchWaybillStats(String sessionId, LocalDate date, List<String> unavailableItems) {
        try {
            return reportDataTool.waybillDailyStatistics(sessionId, date);
        } catch (RuntimeException ex) {
            unavailableItems.add("运单统计");
            return null;
        }
    }

    private TrackDailyStatisticsDTO fetchTrackStats(String sessionId, LocalDate date, List<String> unavailableItems) {
        try {
            return reportDataTool.trackDailyStatistics(sessionId, date);
        } catch (RuntimeException ex) {
            unavailableItems.add("轨迹统计");
            return null;
        }
    }

    private List<StationLoadDTO> fetchLoadRanking(String sessionId, List<String> unavailableItems) {
        try {
            return dispatchTool.loadRanking(sessionId);
        } catch (RuntimeException ex) {
            unavailableItems.add("网点负载");
            return List.of();
        }
    }

    private List<DispatchSuggestionDTO> fetchSuggestions(String sessionId, List<String> unavailableItems) {
        try {
            return dispatchTool.suggestions(sessionId);
        } catch (RuntimeException ex) {
            unavailableItems.add("调度建议");
            return List.of();
        }
    }

    private String markdown(LocalDate date,
                            OrderDailyStatisticsDTO orderStats,
                            WaybillDailyStatisticsDTO waybillStats,
                            TrackDailyStatisticsDTO trackStats,
                            List<StationLoadDTO> ranking,
                            List<DispatchSuggestionDTO> suggestions,
                            List<String> unavailableItems) {
        StringBuilder builder = new StringBuilder();
        builder.append("# 物流运营日报\n\n");
        appendOverview(builder, date, orderStats, waybillStats, trackStats);
        appendWaybillStatus(builder, waybillStats);
        appendExceptions(builder, waybillStats);
        appendStationLoad(builder, ranking);
        appendDispatchSuggestions(builder, suggestions);
        appendAnalysis(builder, orderStats, waybillStats, trackStats, ranking, unavailableItems);
        return builder.toString();
    }

    private void appendOverview(StringBuilder builder,
                                LocalDate date,
                                OrderDailyStatisticsDTO orderStats,
                                WaybillDailyStatisticsDTO waybillStats,
                                TrackDailyStatisticsDTO trackStats) {
        builder.append("## 一、基础概览\n");
        builder.append("- 日期：").append(date).append("\n");
        builder.append("- 新增订单数：").append(value(orderStats == null ? null : orderStats.getNewOrderCount())).append("\n");
        builder.append("- 新增运单数：").append(value(waybillStats == null ? null : waybillStats.getNewWaybillCount())).append("\n");
        builder.append("- 轨迹更新数量：").append(value(trackStats == null ? null : trackStats.getTrackUpdateCount())).append("\n");
        builder.append("- 活跃运单数量：").append(value(trackStats == null ? null : trackStats.getActiveWaybillCount())).append("\n\n");
    }

    private void appendWaybillStatus(StringBuilder builder, WaybillDailyStatisticsDTO waybillStats) {
        builder.append("## 二、运单状态\n");
        builder.append("- 已签收数量：").append(value(waybillStats == null ? null : waybillStats.getSignedWaybillCount())).append("\n");
        builder.append("- 运输中数量：").append(value(waybillStats == null ? null : waybillStats.getTransportingWaybillCount())).append("\n");
        builder.append("- 异常件数量：").append(value(waybillStats == null ? null : waybillStats.getExceptionWaybillCount())).append("\n");
        builder.append("- 各状态数量分布：").append(mapValue(waybillStats == null ? null : waybillStats.getWaybillStatusCountMap())).append("\n\n");
    }

    private void appendExceptions(StringBuilder builder, WaybillDailyStatisticsDTO waybillStats) {
        builder.append("## 三、异常情况\n");
        builder.append("- 异常件总数：").append(value(waybillStats == null ? null : waybillStats.getExceptionWaybillCount())).append("\n");
        builder.append("- 异常类型分布：").append(mapValue(waybillStats == null ? null : waybillStats.getExceptionTypeCountMap())).append("\n");
        Long exceptionCount = waybillStats == null ? null : waybillStats.getExceptionWaybillCount();
        if (exceptionCount == null) {
            builder.append("- 异常风险说明：").append(UNAVAILABLE).append("\n\n");
        } else if (exceptionCount > 0) {
            builder.append("- 异常风险说明：当前存在异常件，建议优先排查超时、破损、丢失和地址错误类型。\n\n");
        } else {
            builder.append("- 异常风险说明：当前未统计到异常件，保持常规监控。\n\n");
        }
    }

    private void appendStationLoad(StringBuilder builder, List<StationLoadDTO> ranking) {
        builder.append("## 四、网点负载\n");
        if (ranking.isEmpty()) {
            builder.append("- 网点负载排行 Top 5：").append(UNAVAILABLE).append("\n");
            builder.append("- 高负载 / 超负载网点说明：").append(UNAVAILABLE).append("\n\n");
            return;
        }
        builder.append("- 网点负载排行 Top 5：\n");
        ranking.stream().limit(5).forEach(load -> builder.append("  - ")
                .append(load.getStationName())
                .append("：待处理 ")
                .append(load.getPendingWaybillCount())
                .append("，等级 ")
                .append(load.getLoadLevel())
                .append("\n"));
        List<String> riskyStations = ranking.stream()
                .filter(load -> "HIGH".equals(load.getLoadLevel()) || "OVERLOAD".equals(load.getLoadLevel()))
                .map(StationLoadDTO::getStationName)
                .toList();
        builder.append("- 高负载 / 超负载网点说明：")
                .append(riskyStations.isEmpty() ? "当前未发现高负载或超负载网点。" : String.join("、", riskyStations))
                .append("\n\n");
    }

    private void appendDispatchSuggestions(StringBuilder builder, List<DispatchSuggestionDTO> suggestions) {
        builder.append("## 五、调度建议\n");
        if (suggestions.isEmpty()) {
            builder.append("- ").append(UNAVAILABLE).append("\n\n");
            return;
        }
        suggestions.stream().limit(5).forEach(suggestion -> builder.append("- ")
                .append(suggestion.getStationName())
                .append("：")
                .append(suggestion.getSuggestion())
                .append("\n"));
        builder.append("\n");
    }

    private void appendAnalysis(StringBuilder builder,
                                OrderDailyStatisticsDTO orderStats,
                                WaybillDailyStatisticsDTO waybillStats,
                                TrackDailyStatisticsDTO trackStats,
                                List<StationLoadDTO> ranking,
                                List<String> unavailableItems) {
        builder.append("## 六、Agent 分析结论\n");
        builder.append("- 今日整体运营情况：")
                .append(summary(orderStats, waybillStats, trackStats))
                .append("\n");
        builder.append("- 主要风险：")
                .append(riskSummary(waybillStats, ranking, unavailableItems))
                .append("\n");
        builder.append("- 下一步建议：优先处理异常件和高负载网点，持续关注轨迹更新量较低或长时间未更新的运单。\n");
    }

    private String summary(OrderDailyStatisticsDTO orderStats,
                           WaybillDailyStatisticsDTO waybillStats,
                           TrackDailyStatisticsDTO trackStats) {
        if (orderStats == null && waybillStats == null && trackStats == null) {
            return UNAVAILABLE;
        }
        return "新增订单 " + value(orderStats == null ? null : orderStats.getNewOrderCount())
                + "，新增运单 " + value(waybillStats == null ? null : waybillStats.getNewWaybillCount())
                + "，轨迹更新 " + value(trackStats == null ? null : trackStats.getTrackUpdateCount()) + "。";
    }

    private String riskSummary(WaybillDailyStatisticsDTO waybillStats,
                               List<StationLoadDTO> ranking,
                               List<String> unavailableItems) {
        if (!unavailableItems.isEmpty()) {
            return "部分数据获取失败：" + String.join("、", unavailableItems) + "，对应项已标记为“" + UNAVAILABLE + "”。";
        }
        boolean hasException = waybillStats != null && waybillStats.getExceptionWaybillCount() != null
                && waybillStats.getExceptionWaybillCount() > 0;
        boolean hasHighLoad = ranking.stream().anyMatch(load -> "HIGH".equals(load.getLoadLevel()) || "OVERLOAD".equals(load.getLoadLevel()));
        if (hasException || hasHighLoad) {
            return "存在异常件或高负载网点，需要调度侧优先跟进。";
        }
        return "当前未发现明显异常风险。";
    }

    private String value(Long value) {
        return value == null ? UNAVAILABLE : String.valueOf(value);
    }

    private String mapValue(Map<String, Long> map) {
        if (map == null) {
            return UNAVAILABLE;
        }
        if (map.isEmpty()) {
            return "{}";
        }
        return map.toString();
    }
}
