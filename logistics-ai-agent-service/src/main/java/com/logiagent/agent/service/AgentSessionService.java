package com.logiagent.agent.service;

import com.logiagent.agent.dto.AgentSessionDTO;
import com.logiagent.common.result.PageResult;

public interface AgentSessionService {

    void saveSession(String sessionId, Long userId, String question, String intent, String answer);

    AgentSessionDTO getBySessionId(String sessionId);

    PageResult<AgentSessionDTO> pageSessions(long page, long size);
}
