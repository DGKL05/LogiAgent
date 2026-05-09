package com.logiagent.agent.service.impl;

import com.logiagent.agent.entity.AgentToolLogEntity;
import com.logiagent.agent.mapper.AgentToolLogMapper;
import com.logiagent.agent.service.AgentToolLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgentToolLogServiceImpl implements AgentToolLogService {

    private final AgentToolLogMapper agentToolLogMapper;

    public AgentToolLogServiceImpl(AgentToolLogMapper agentToolLogMapper) {
        this.agentToolLogMapper = agentToolLogMapper;
    }

    @Override
    public void record(String sessionId,
                       String toolName,
                       String requestParams,
                       String responseResult,
                       boolean success,
                       String errorMsg,
                       long costMs) {
        AgentToolLogEntity entity = new AgentToolLogEntity();
        entity.setSessionId(sessionId);
        entity.setToolName(toolName);
        entity.setRequestParams(requestParams);
        entity.setResponseResult(responseResult);
        entity.setSuccess(success);
        entity.setErrorMsg(errorMsg);
        entity.setCostMs(costMs);
        entity.setCreateTime(LocalDateTime.now());
        agentToolLogMapper.insert(entity);
    }
}
