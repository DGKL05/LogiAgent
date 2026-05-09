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

    public SupervisorAgent(AgentIntentClassifier intentClassifier,
                           WaybillQueryAgent waybillQueryAgent,
                           ExceptionDiagnosisAgent exceptionDiagnosisAgent) {
        this.intentClassifier = intentClassifier;
        this.waybillQueryAgent = waybillQueryAgent;
        this.exceptionDiagnosisAgent = exceptionDiagnosisAgent;
    }

    public ChatResponse handle(String sessionId, String message) {
        if (!StringUtils.hasText(message)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "message is required");
        }
        String waybillNo = intentClassifier.extractWaybillNo(message);
        if (!StringUtils.hasText(waybillNo)) {
            return unknown(sessionId, "请提供以 WB 开头的运单号，我才能查询和分析物流状态。");
        }

        AgentIntent intent = intentClassifier.classify(message);
        if (intent == AgentIntent.WAYBILL_EXCEPTION_DIAGNOSIS) {
            return exceptionDiagnosisAgent.diagnose(sessionId, waybillNo);
        }
        if (intent == AgentIntent.WAYBILL_QUERY) {
            return waybillQueryAgent.query(sessionId, waybillNo);
        }
        return unknown(sessionId, "当前仅支持运单查询和运单异常诊断。");
    }

    private ChatResponse unknown(String sessionId, String answer) {
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.UNKNOWN.name());
        response.setAnswer(answer);
        return response;
    }
}
