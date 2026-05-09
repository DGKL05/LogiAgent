package com.logiagent.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.logiagent.agent.entity.AgentSessionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AgentSessionMapper extends BaseMapper<AgentSessionEntity> {
}
