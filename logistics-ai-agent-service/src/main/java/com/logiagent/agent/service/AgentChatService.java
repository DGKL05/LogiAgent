package com.logiagent.agent.service;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.request.ChatRequest;

public interface AgentChatService {

    ChatResponse chat(ChatRequest request);
}
