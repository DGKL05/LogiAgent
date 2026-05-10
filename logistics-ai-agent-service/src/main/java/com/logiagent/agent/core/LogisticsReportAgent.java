package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.tool.DispatchTool;
import com.logiagent.agent.tool.ReportDataTool;
import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.StationLoadDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LogisticsReportAgent {

    private final ReportDataTool reportDataTool;
    private final DispatchTool dispatchTool;

    public LogisticsReportAgent(ReportDataTool reportDataTool, DispatchTool dispatchTool) {
        this.reportDataTool = reportDataTool;
        this.dispatchTool = dispatchTool;
    }

    public ChatResponse generate(String sessionId) {
        Long orderTotal = reportDataTool.orderTotal(sessionId);
        Long exceptionTotal = reportDataTool.exceptionWaybillTotal(sessionId);
        List<StationLoadDTO> ranking = dispatchTool.loadRanking(sessionId);
        List<DispatchSuggestionDTO> suggestions = dispatchTool.suggestions(sessionId);
        String answer = markdown(orderTotal, exceptionTotal, ranking, suggestions);

        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.DAILY_REPORT.name());
        response.setAnswer(answer);
        response.setToolCalls(List.of(
                "ReportDataTool.orderTotal",
                "ReportDataTool.exceptionWaybillTotal",
                "DispatchTool.loadRanking",
                "DispatchTool.suggestions"
        ));
        return response;
    }

    private String markdown(Long orderTotal,
                            Long exceptionTotal,
                            List<StationLoadDTO> ranking,
                            List<DispatchSuggestionDTO> suggestions) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Logistics Daily Report\n\n");
        builder.append("- Date: ").append(LocalDate.now()).append("\n");
        builder.append("- New orders: ").append(orderTotal).append(" (MVP uses current order total)\n");
        builder.append("- New waybills: MVP not counted yet, because waybill service has no date statistics API\n");
        builder.append("- Signed waybills: MVP not counted yet, because waybill service has no status statistics API\n");
        builder.append("- Exception waybills: ").append(exceptionTotal).append("\n");
        builder.append("- Track updates: MVP not counted yet, because track service has no date statistics API\n\n");
        builder.append("## Station Load Ranking\n");
        if (ranking.isEmpty()) {
            builder.append("- No dispatch task data is available.\n");
        } else {
            ranking.stream().limit(5).forEach(load -> builder.append("- ")
                    .append(load.getStationName())
                    .append(": ")
                    .append(load.getPendingWaybillCount())
                    .append(" pending, level ")
                    .append(load.getLoadLevel())
                    .append("\n"));
        }
        builder.append("\n## Agent Analysis\n");
        if (!ranking.isEmpty()) {
            StationLoadDTO top = ranking.get(0);
            builder.append("The current busiest station is ")
                    .append(top.getStationName())
                    .append(". Dispatch priority should focus on high pending task stations.\n");
        } else {
            builder.append("No station load data is available for analysis.\n");
        }
        builder.append("\n## Dispatch Suggestions\n");
        if (suggestions.isEmpty()) {
            builder.append("- No dispatch suggestions are available.\n");
        } else {
            suggestions.stream().limit(5).forEach(suggestion -> builder.append("- ")
                    .append(suggestion.getStationName())
                    .append(": ")
                    .append(suggestion.getSuggestion())
                    .append("\n"));
        }
        return builder.toString();
    }
}
