package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SupervisorAgent {

    private final AgentIntentClassifier intentClassifier;
    private final WaybillQueryAgent waybillQueryAgent;
    private final ExceptionDiagnosisAgent exceptionDiagnosisAgent;
    private final RoutePlanningAgent routePlanningAgent;
    private final DispatchSuggestAgent dispatchSuggestAgent;
    private final LogisticsReportAgent logisticsReportAgent;

    public SupervisorAgent(AgentIntentClassifier intentClassifier,
                           WaybillQueryAgent waybillQueryAgent,
                           ExceptionDiagnosisAgent exceptionDiagnosisAgent,
                           RoutePlanningAgent routePlanningAgent,
                           DispatchSuggestAgent dispatchSuggestAgent,
                           LogisticsReportAgent logisticsReportAgent) {
        this.intentClassifier = intentClassifier;
        this.waybillQueryAgent = waybillQueryAgent;
        this.exceptionDiagnosisAgent = exceptionDiagnosisAgent;
        this.routePlanningAgent = routePlanningAgent;
        this.dispatchSuggestAgent = dispatchSuggestAgent;
        this.logisticsReportAgent = logisticsReportAgent;
    }

    public ChatResponse handle(String sessionId, String message) {
        if (!StringUtils.hasText(message)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "message is required");
        }
        AgentIntent intent = intentClassifier.classify(message);
        if (intent == AgentIntent.DAILY_REPORT) {
            return logisticsReportAgent.generate(sessionId);
        }
        if (intent == AgentIntent.DISPATCH_SUGGESTION) {
            return dispatchSuggestAgent.suggest(sessionId);
        }
        if (intent == AgentIntent.ROUTE_PLANNING) {
            return routePlanningAgent.plan(sessionId, message);
        }

        String waybillNo = intentClassifier.extractWaybillNo(message);
        if (!StringUtils.hasText(waybillNo)) {
            return unknown(sessionId, "Please provide a waybill number, route planning question, dispatch question, or daily report request.");
        }
        if (intent == AgentIntent.WAYBILL_EXCEPTION_DIAGNOSIS) {
            return exceptionDiagnosisAgent.diagnose(sessionId, waybillNo);
        }
        if (intent == AgentIntent.WAYBILL_QUERY) {
            return waybillQueryAgent.query(sessionId, waybillNo);
        }
        return unknown(sessionId, "Current MVP supports waybill query, exception diagnosis, route planning, dispatch suggestion, and logistics report.");
    }

    private ChatResponse unknown(String sessionId, String answer) {
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.UNKNOWN.name());
        response.setAnswer(answer);
        return response;
    }
}
