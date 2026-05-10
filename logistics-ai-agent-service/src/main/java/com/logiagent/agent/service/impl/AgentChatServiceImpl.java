package com.logiagent.agent.service.impl;

import com.logiagent.agent.core.SupervisorAgent;
import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.request.ChatRequest;
import com.logiagent.agent.service.AgentChatService;
import com.logiagent.agent.service.AgentSessionService;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AgentChatServiceImpl implements AgentChatService {

    private static final DateTimeFormatter SESSION_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final SupervisorAgent supervisorAgent;
    private final AgentSessionService agentSessionService;

    public AgentChatServiceImpl(SupervisorAgent supervisorAgent, AgentSessionService agentSessionService) {
        this.supervisorAgent = supervisorAgent;
        this.agentSessionService = agentSessionService;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "message is required");
        }
        String sessionId = generateSessionId();
        ChatResponse response = supervisorAgent.handle(sessionId, request.getMessage());
        agentSessionService.saveSession(
                sessionId,
                request.getUserId(),
                request.getMessage(),
                response.getIntent(),
                response.getAnswer()
        );
        return response;
    }

    private String generateSessionId() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "AGENT" + LocalDateTime.now().format(SESSION_TIME_FORMATTER) + suffix;
    }
}
