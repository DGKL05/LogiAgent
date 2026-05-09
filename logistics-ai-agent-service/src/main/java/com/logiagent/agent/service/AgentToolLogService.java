package com.logiagent.agent.service;

public interface AgentToolLogService {

    void record(String sessionId,
                String toolName,
                String requestParams,
                String responseResult,
                boolean success,
                String errorMsg,
                long costMs);
}
