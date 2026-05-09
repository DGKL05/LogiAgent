package com.logiagent.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logiagent.agent.dto.AgentSessionDTO;
import com.logiagent.agent.entity.AgentSessionEntity;
import com.logiagent.agent.mapper.AgentSessionMapper;
import com.logiagent.agent.service.AgentSessionService;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentSessionServiceImpl implements AgentSessionService {

    private final AgentSessionMapper agentSessionMapper;

    public AgentSessionServiceImpl(AgentSessionMapper agentSessionMapper) {
        this.agentSessionMapper = agentSessionMapper;
    }

    @Override
    public void saveSession(String sessionId, Long userId, String question, String intent, String answer) {
        AgentSessionEntity entity = new AgentSessionEntity();
        entity.setSessionId(sessionId);
        entity.setUserId(userId);
        entity.setQuestion(question);
        entity.setIntent(intent);
        entity.setAnswer(answer);
        entity.setCreateTime(LocalDateTime.now());
        agentSessionMapper.insert(entity);
    }

    @Override
    public AgentSessionDTO getBySessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "sessionId is required");
        }
        AgentSessionEntity entity = agentSessionMapper.selectOne(
                new LambdaQueryWrapper<AgentSessionEntity>().eq(AgentSessionEntity::getSessionId, sessionId)
        );
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "agent session not found");
        }
        return toDTO(entity);
    }

    @Override
    public PageResult<AgentSessionDTO> pageSessions(long page, long size) {
        Page<AgentSessionEntity> pageParam = new Page<>(Math.max(page, 1), Math.max(size, 1));
        Page<AgentSessionEntity> sessionPage = agentSessionMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<AgentSessionEntity>().orderByDesc(AgentSessionEntity::getCreateTime)
        );
        List<AgentSessionDTO> records = sessionPage.getRecords().stream().map(this::toDTO).toList();
        return PageResult.of(records, sessionPage.getTotal(), sessionPage.getCurrent(), sessionPage.getSize());
    }

    private AgentSessionDTO toDTO(AgentSessionEntity entity) {
        AgentSessionDTO dto = new AgentSessionDTO();
        dto.setId(entity.getId());
        dto.setSessionId(entity.getSessionId());
        dto.setUserId(entity.getUserId());
        dto.setQuestion(entity.getQuestion());
        dto.setIntent(entity.getIntent());
        dto.setAnswer(entity.getAnswer());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }
}
