package com.logiagent.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logiagent.agent.entity.AgentSessionEntity;
import com.logiagent.agent.entity.AgentToolLogEntity;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.mapper.AgentSessionMapper;
import com.logiagent.agent.mapper.AgentToolLogMapper;
import com.logiagent.agent.service.AdminAgentService;
import com.logiagent.api.client.DispatchFeignClient;
import com.logiagent.api.client.OrderFeignClient;
import com.logiagent.api.client.TrackFeignClient;
import com.logiagent.api.client.WaybillFeignClient;
import com.logiagent.api.dto.AgentToolLogDTO;
import com.logiagent.api.dto.AgentUsageStatisticsDTO;
import com.logiagent.api.dto.DashboardOverviewDTO;
import com.logiagent.api.dto.DashboardRiskDTO;
import com.logiagent.api.dto.DashboardTrendDTO;
import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.dto.TrackDailyStatisticsDTO;
import com.logiagent.api.dto.WaybillDailyStatisticsDTO;
import com.logiagent.api.request.AdminAgentSessionQueryRequest;
import com.logiagent.common.enums.StationLoadLevelEnum;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminAgentServiceImpl implements AdminAgentService {

    private final AgentSessionMapper agentSessionMapper;
    private final AgentToolLogMapper agentToolLogMapper;
    private final OrderFeignClient orderFeignClient;
    private final WaybillFeignClient waybillFeignClient;
    private final TrackFeignClient trackFeignClient;
    private final DispatchFeignClient dispatchFeignClient;

    public AdminAgentServiceImpl(AgentSessionMapper agentSessionMapper,
                                 AgentToolLogMapper agentToolLogMapper,
                                 OrderFeignClient orderFeignClient,
                                 WaybillFeignClient waybillFeignClient,
                                 TrackFeignClient trackFeignClient,
                                 DispatchFeignClient dispatchFeignClient) {
        this.agentSessionMapper = agentSessionMapper;
        this.agentToolLogMapper = agentToolLogMapper;
        this.orderFeignClient = orderFeignClient;
        this.waybillFeignClient = waybillFeignClient;
        this.trackFeignClient = trackFeignClient;
        this.dispatchFeignClient = dispatchFeignClient;
    }

    @Override
    public PageResult<com.logiagent.api.dto.AgentSessionDTO> pageSessions(AdminAgentSessionQueryRequest request) {
        AdminAgentSessionQueryRequest query = request == null ? new AdminAgentSessionQueryRequest() : request;
        Page<AgentSessionEntity> pageParam = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        Page<AgentSessionEntity> sessionPage = agentSessionMapper.selectPage(pageParam, buildSessionWrapper(query));
        return PageResult.of(sessionPage.getRecords().stream().map(this::toSessionDTO).toList(),
                sessionPage.getTotal(), sessionPage.getCurrent(), sessionPage.getSize());
    }

    @Override
    public com.logiagent.api.dto.AgentSessionDTO getSession(String sessionId) {
        return toSessionDTO(selectSession(sessionId));
    }

    @Override
    public List<AgentToolLogDTO> toolLogs(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "sessionId is required");
        }
        return agentToolLogMapper.selectList(new LambdaQueryWrapper<AgentToolLogEntity>()
                        .eq(AgentToolLogEntity::getSessionId, sessionId)
                        .orderByDesc(AgentToolLogEntity::getCreateTime))
                .stream()
                .map(this::toToolLogDTO)
                .toList();
    }

    @Override
    public AgentUsageStatisticsDTO statistics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        AgentUsageStatisticsDTO dto = new AgentUsageStatisticsDTO();
        dto.setTotalSessionCount(countSessions(new LambdaQueryWrapper<>()));
        dto.setTodaySessionCount(countSessions(new LambdaQueryWrapper<AgentSessionEntity>()
                .ge(AgentSessionEntity::getCreateTime, todayStart)));
        dto.setIntentCountMap(intentCountMap());
        dto.setTotalToolCallCount(countToolLogs(new LambdaQueryWrapper<>()));
        dto.setFailedToolCallCount(countToolLogs(new LambdaQueryWrapper<AgentToolLogEntity>()
                .eq(AgentToolLogEntity::getSuccess, false)));
        return dto;
    }

    @Override
    public DashboardOverviewDTO dashboardOverview() {
        DashboardOverviewDTO dto = new DashboardOverviewDTO();
        LocalDate today = LocalDate.now();
        try {
            OrderDailyStatisticsDTO order = requiredData(orderFeignClient.dailyStatistics(today), "order statistics");
            dto.setTotalOrderCount(order.getTotalOrderCount());
            dto.setTodayOrderCount(order.getNewOrderCount());
        } catch (RuntimeException ex) {
            dto.getUnavailableFields().add("order");
        }
        try {
            WaybillDailyStatisticsDTO waybill = requiredData(waybillFeignClient.dailyStatistics(today), "waybill statistics");
            dto.setTotalWaybillCount(waybill.getTotalWaybillCount());
            dto.setTodayWaybillCount(waybill.getNewWaybillCount());
            dto.setExceptionWaybillCount(waybill.getExceptionWaybillCount());
            dto.setSignedWaybillCount(waybill.getSignedWaybillCount());
        } catch (RuntimeException ex) {
            dto.getUnavailableFields().add("waybill");
        }
        try {
            TrackDailyStatisticsDTO track = requiredData(trackFeignClient.dailyStatistics(today), "track statistics");
            dto.setTrackUpdateCount(track.getTrackUpdateCount());
            dto.setActiveWaybillCount(track.getActiveWaybillCount());
        } catch (RuntimeException ex) {
            dto.getUnavailableFields().add("track");
        }
        try {
            List<StationLoadDTO> ranking = requiredData(dispatchFeignClient.loadRanking(), "station load");
            dto.setOverloadedStationCount(ranking.stream()
                    .filter(load -> StationLoadLevelEnum.OVERLOAD.name().equals(load.getLoadLevel()))
                    .count());
        } catch (RuntimeException ex) {
            dto.getUnavailableFields().add("dispatch");
        }
        dto.setAgentSessionCount(countSessions(new LambdaQueryWrapper<>()));
        return dto;
    }

    @Override
    public List<DashboardTrendDTO> dashboardTrends(LocalDate startDate, LocalDate endDate) {
        LocalDate end = endDate == null ? LocalDate.now() : endDate;
        LocalDate start = startDate == null ? end.minusDays(6) : startDate;
        if (start.isAfter(end)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "startDate must not be after endDate");
        }
        return start.datesUntil(end.plusDays(1)).map(this::trendOfDate).toList();
    }

    @Override
    public DashboardRiskDTO dashboardRisks() {
        DashboardRiskDTO dto = new DashboardRiskDTO();
        LocalDate today = LocalDate.now();
        try {
            WaybillDailyStatisticsDTO waybill = requiredData(waybillFeignClient.dailyStatistics(today), "waybill statistics");
            dto.setExceptionTypeCountMap(waybill.getExceptionTypeCountMap());
            if (waybill.getExceptionWaybillCount() != null && waybill.getExceptionWaybillCount() > 0) {
                dto.getRiskSuggestions().add("当前存在异常件，建议优先处理超时、拒收、丢失等异常类型。");
            }
        } catch (RuntimeException ex) {
            dto.getUnavailableFields().add("waybill");
        }
        try {
            List<StationLoadDTO> ranking = requiredData(dispatchFeignClient.loadRanking(), "station load");
            dto.setHighLoadStations(ranking.stream()
                    .filter(load -> StationLoadLevelEnum.HIGH.name().equals(load.getLoadLevel())
                            || StationLoadLevelEnum.OVERLOAD.name().equals(load.getLoadLevel()))
                    .toList());
            if (!dto.getHighLoadStations().isEmpty()) {
                dto.getRiskSuggestions().add("存在高负载网点，建议调整派件任务或临时增加人员。");
            }
            List<DispatchSuggestionDTO> suggestions = requiredData(dispatchFeignClient.suggestions(), "dispatch suggestions");
            suggestions.stream().map(DispatchSuggestionDTO::getSuggestion).forEach(dto.getRiskSuggestions()::add);
        } catch (RuntimeException ex) {
            dto.getUnavailableFields().add("dispatch");
        }
        dto.setFailedToolCallCount(countToolLogs(new LambdaQueryWrapper<AgentToolLogEntity>()
                .eq(AgentToolLogEntity::getSuccess, false)));
        if (dto.getRiskSuggestions().isEmpty()) {
            dto.getRiskSuggestions().add("当前未发现明显运营风险，保持常规监控。");
        }
        return dto;
    }

    private DashboardTrendDTO trendOfDate(LocalDate date) {
        DashboardTrendDTO dto = new DashboardTrendDTO();
        dto.setDate(date);
        try {
            dto.setOrderCount(requiredData(orderFeignClient.dailyStatistics(date), "order statistics").getNewOrderCount());
        } catch (RuntimeException ignored) {
            dto.setOrderCount(null);
        }
        try {
            dto.setWaybillCount(requiredData(waybillFeignClient.dailyStatistics(date), "waybill statistics").getNewWaybillCount());
        } catch (RuntimeException ignored) {
            dto.setWaybillCount(null);
        }
        try {
            dto.setTrackUpdateCount(requiredData(trackFeignClient.dailyStatistics(date), "track statistics").getTrackUpdateCount());
        } catch (RuntimeException ignored) {
            dto.setTrackUpdateCount(null);
        }
        return dto;
    }

    private LambdaQueryWrapper<AgentSessionEntity> buildSessionWrapper(AdminAgentSessionQueryRequest query) {
        LambdaQueryWrapper<AgentSessionEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getSessionId())) {
            wrapper.like(AgentSessionEntity::getSessionId, query.getSessionId());
        }
        if (query.getUserId() != null) {
            wrapper.eq(AgentSessionEntity::getUserId, query.getUserId());
        }
        if (StringUtils.hasText(query.getIntent())) {
            wrapper.eq(AgentSessionEntity::getIntent, query.getIntent());
        }
        if (query.getStartTime() != null) {
            wrapper.ge(AgentSessionEntity::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(AgentSessionEntity::getCreateTime, query.getEndTime());
        }
        return wrapper.orderByDesc(AgentSessionEntity::getCreateTime);
    }

    private AgentSessionEntity selectSession(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "sessionId is required");
        }
        AgentSessionEntity session = agentSessionMapper.selectOne(new LambdaQueryWrapper<AgentSessionEntity>()
                .eq(AgentSessionEntity::getSessionId, sessionId));
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "agent session not found");
        }
        return session;
    }

    private Map<String, Long> intentCountMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (AgentIntent intent : AgentIntent.values()) {
            map.put(intent.name(), countSessions(new LambdaQueryWrapper<AgentSessionEntity>()
                    .eq(AgentSessionEntity::getIntent, intent.name())));
        }
        return map;
    }

    private Long countSessions(LambdaQueryWrapper<AgentSessionEntity> wrapper) {
        Long count = agentSessionMapper.selectCount(wrapper);
        return count == null ? 0L : count;
    }

    private Long countToolLogs(LambdaQueryWrapper<AgentToolLogEntity> wrapper) {
        Long count = agentToolLogMapper.selectCount(wrapper);
        return count == null ? 0L : count;
    }

    private <T> T requiredData(Result<T> result, String name) {
        if (result == null || result.getData() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, name + " unavailable");
        }
        return result.getData();
    }

    private com.logiagent.api.dto.AgentSessionDTO toSessionDTO(AgentSessionEntity entity) {
        com.logiagent.api.dto.AgentSessionDTO dto = new com.logiagent.api.dto.AgentSessionDTO();
        dto.setId(entity.getId());
        dto.setSessionId(entity.getSessionId());
        dto.setUserId(entity.getUserId());
        dto.setQuestion(entity.getQuestion());
        dto.setIntent(entity.getIntent());
        dto.setAnswer(entity.getAnswer());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    private AgentToolLogDTO toToolLogDTO(AgentToolLogEntity entity) {
        AgentToolLogDTO dto = new AgentToolLogDTO();
        dto.setId(entity.getId());
        dto.setSessionId(entity.getSessionId());
        dto.setToolName(entity.getToolName());
        dto.setRequestParams(entity.getRequestParams());
        dto.setResponseResult(entity.getResponseResult());
        dto.setSuccess(entity.getSuccess());
        dto.setErrorMsg(entity.getErrorMsg());
        dto.setCostMs(entity.getCostMs());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    private long safePage(Long page) {
        return Math.max(page == null ? 1L : page, 1L);
    }

    private long safeSize(Long size) {
        return Math.max(size == null ? 10L : size, 1L);
    }
}
