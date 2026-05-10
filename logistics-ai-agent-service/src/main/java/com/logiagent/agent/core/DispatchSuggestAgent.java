package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.tool.DispatchTool;
import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.StationLoadDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DispatchSuggestAgent {

    private final DispatchTool dispatchTool;

    public DispatchSuggestAgent(DispatchTool dispatchTool) {
        this.dispatchTool = dispatchTool;
    }

    public ChatResponse suggest(String sessionId) {
        List<StationLoadDTO> ranking = dispatchTool.loadRanking(sessionId);
        List<DispatchSuggestionDTO> suggestions = dispatchTool.suggestions(sessionId);
        StringBuilder answer = new StringBuilder();
        answer.append("## Dispatch Suggestion\n\n");
        if (ranking.isEmpty()) {
            answer.append("No dispatch task data is available yet.");
        } else {
            StationLoadDTO top = ranking.get(0);
            answer.append("The busiest station is ")
                    .append(top.getStationName())
                    .append(", pending tasks: ")
                    .append(top.getPendingWaybillCount())
                    .append(", load level: ")
                    .append(top.getLoadLevel())
                    .append(".\n\n");
            answer.append("### Suggestions\n");
            suggestions.stream().limit(3).forEach(item -> answer.append("- ")
                    .append(item.getStationName())
                    .append(": ")
                    .append(item.getSuggestion())
                    .append("\n"));
        }
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.DISPATCH_SUGGESTION.name());
        response.setAnswer(answer.toString());
        response.setToolCalls(List.of("DispatchTool.loadRanking", "DispatchTool.suggestions"));
        return response;
    }
}
