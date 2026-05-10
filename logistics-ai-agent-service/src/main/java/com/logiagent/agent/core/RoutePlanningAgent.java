package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.tool.RouteTool;
import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.enums.RouteProviderEnum;
import com.logiagent.common.enums.RouteStrategyEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RoutePlanningAgent {

    private final RouteTool routeTool;

    public RoutePlanningAgent(RouteTool routeTool) {
        this.routeTool = routeTool;
    }

    public ChatResponse plan(String sessionId, String message) {
        StationDTO start = resolveStation(sessionId, message, "广州", "Guangzhou");
        StationDTO end = resolveStation(sessionId, message, "深圳", "Shenzhen");
        if (start == null || end == null) {
            return response(sessionId, "Please include supported station names such as Guangzhou station and Shenzhen station.", List.of());
        }

        RoutePlanRequest request = new RoutePlanRequest();
        request.setStartStationId(start.getId());
        request.setEndStationId(end.getId());
        request.setProvider(resolveProvider(message));
        request.setStrategy(resolveStrategy(message));
        RoutePlanResponse plan = routeTool.planRoute(sessionId, request);
        String answer = answer(plan);
        return response(sessionId, answer, List.of("RouteTool.searchStations", "RouteTool.planRoute"));
    }

    private StationDTO resolveStation(String sessionId, String message, String chineseName, String englishName) {
        if (!message.contains(chineseName) && !message.toLowerCase().contains(englishName.toLowerCase())) {
            return null;
        }
        String keyword = message.contains(chineseName) ? chineseName : englishName;
        List<StationDTO> stations = routeTool.searchStations(sessionId, keyword);
        return stations.isEmpty() ? null : stations.get(0);
    }

    private RouteProviderEnum resolveProvider(String message) {
        String text = message.toLowerCase();
        if (text.contains("百度地图") || text.contains("真实道路") || text.contains("实际路线") || text.contains("baidu") || text.contains("real route")) {
            return RouteProviderEnum.BAIDU_DRIVING;
        }
        return RouteProviderEnum.LOCAL_DIJKSTRA;
    }

    private RouteStrategyEnum resolveStrategy(String message) {
        String text = message.toLowerCase();
        if (text.contains("最短") || text.contains("shortest")) {
            return RouteStrategyEnum.DISTANCE_FIRST;
        }
        if (text.contains("成本最低") || text.contains("花费最少") || text.contains("cost")) {
            return RouteStrategyEnum.COST_FIRST;
        }
        if (text.contains("躲避拥堵") || text.contains("avoid traffic")) {
            return RouteStrategyEnum.AVOID_TRAFFIC;
        }
        if (text.contains("最快") || text.contains("fastest")) {
            return RouteStrategyEnum.TIME_FIRST;
        }
        return RouteStrategyEnum.BALANCED;
    }

    private String answer(RoutePlanResponse plan) {
        String providerText = plan.getProvider() == RouteProviderEnum.BAIDU_DRIVING ? "Baidu driving route planning" : "local Dijkstra route planning";
        String fallback = Boolean.TRUE.equals(plan.getFallbackUsed())
                ? "Baidu Map API is unavailable, so the system automatically used local Dijkstra. Reason: " + plan.getFallbackReason() + ". "
                : "";
        return fallback + "Planned route by " + providerText
                + ". Path: " + String.join(" -> ", plan.getPathStationNames())
                + ". Distance: " + text(plan.getTotalDistance()) + " km"
                + ", duration: " + text(plan.getTotalDuration()) + " minutes"
                + ", cost: " + text(plan.getTotalCost()) + " yuan"
                + (plan.getToll() == null ? "." : ", toll: " + text(plan.getToll()) + " yuan.");
    }

    private String text(BigDecimal value) {
        return value == null ? "0" : value.stripTrailingZeros().toPlainString();
    }

    private ChatResponse response(String sessionId, String answer, List<String> toolCalls) {
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.ROUTE_PLANNING.name());
        response.setAnswer(answer);
        response.setToolCalls(toolCalls);
        return response;
    }
}
