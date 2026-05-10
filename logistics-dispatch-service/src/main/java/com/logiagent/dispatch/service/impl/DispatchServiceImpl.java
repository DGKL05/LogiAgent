package com.logiagent.dispatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.DispatchTaskDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.request.CreateDispatchTaskRequest;
import com.logiagent.common.enums.DispatchTaskStatusEnum;
import com.logiagent.common.enums.StationLoadLevelEnum;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.dispatch.entity.DispatchTaskEntity;
import com.logiagent.dispatch.mapper.DispatchTaskMapper;
import com.logiagent.dispatch.service.DispatchService;
import com.logiagent.dispatch.service.StationLoadLevelPolicy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DispatchServiceImpl implements DispatchService {

    private static final DateTimeFormatter NUMBER_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final List<String> PENDING_STATUSES = List.of(
            DispatchTaskStatusEnum.CREATED.name(),
            DispatchTaskStatusEnum.ASSIGNED.name(),
            DispatchTaskStatusEnum.DELIVERING.name()
    );

    private final DispatchTaskMapper dispatchTaskMapper;
    private final StationLoadLevelPolicy stationLoadLevelPolicy;

    public DispatchServiceImpl(DispatchTaskMapper dispatchTaskMapper, StationLoadLevelPolicy stationLoadLevelPolicy) {
        this.dispatchTaskMapper = dispatchTaskMapper;
        this.stationLoadLevelPolicy = stationLoadLevelPolicy;
    }

    @Override
    public DispatchTaskDTO createTask(CreateDispatchTaskRequest request) {
        if (request == null || !StringUtils.hasText(request.getWaybillNo()) || request.getStationId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "waybillNo and stationId are required");
        }
        LocalDateTime now = LocalDateTime.now();
        DispatchTaskEntity entity = new DispatchTaskEntity();
        entity.setTaskNo(generateTaskNo(now));
        entity.setWaybillNo(request.getWaybillNo());
        entity.setCourierId(request.getCourierId());
        entity.setStationId(request.getStationId());
        entity.setTaskStatus(DispatchTaskStatusEnum.CREATED.name());
        entity.setAssignTime(now);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        dispatchTaskMapper.insert(entity);
        return toDTO(entity);
    }

    @Override
    public DispatchTaskDTO getTask(String taskNo) {
        if (!StringUtils.hasText(taskNo)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "taskNo is required");
        }
        DispatchTaskEntity task = dispatchTaskMapper.selectOne(
                new LambdaQueryWrapper<DispatchTaskEntity>().eq(DispatchTaskEntity::getTaskNo, taskNo)
        );
        if (task == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "dispatch task not found");
        }
        return toDTO(task);
    }

    @Override
    public StationLoadDTO getStationLoad(Long stationId) {
        if (stationId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "stationId is required");
        }
        List<DispatchTaskEntity> tasks = dispatchTaskMapper.selectList(
                new LambdaQueryWrapper<DispatchTaskEntity>().eq(DispatchTaskEntity::getStationId, stationId)
        );
        return toLoad(stationId, tasks);
    }

    @Override
    public List<StationLoadDTO> loadRanking() {
        List<DispatchTaskEntity> tasks = dispatchTaskMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, List<DispatchTaskEntity>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(DispatchTaskEntity::getStationId));
        return grouped.entrySet().stream()
                .map(entry -> toLoad(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StationLoadDTO::getPendingWaybillCount).reversed())
                .toList();
    }

    @Override
    public List<DispatchSuggestionDTO> suggestions() {
        return loadRanking().stream().map(this::toSuggestion).toList();
    }

    private StationLoadDTO toLoad(Long stationId, List<DispatchTaskEntity> tasks) {
        long pendingCount = tasks.stream()
                .filter(task -> PENDING_STATUSES.contains(task.getTaskStatus()))
                .count();
        long exceptionCount = 0L;
        StationLoadLevelEnum level = stationLoadLevelPolicy.resolve(pendingCount, exceptionCount);
        StationLoadDTO dto = new StationLoadDTO();
        dto.setStationId(stationId);
        dto.setStationName("Station-" + stationId);
        dto.setPendingWaybillCount(pendingCount);
        dto.setExceptionWaybillCount(exceptionCount);
        dto.setDispatchTaskCount((long) tasks.size());
        dto.setCourierCount(tasks.stream().map(DispatchTaskEntity::getCourierId).filter(id -> id != null).distinct().count());
        dto.setDriverCount(0L);
        dto.setLoadLevel(level.name());
        return dto;
    }

    private DispatchSuggestionDTO toSuggestion(StationLoadDTO load) {
        DispatchSuggestionDTO dto = new DispatchSuggestionDTO();
        dto.setStationId(load.getStationId());
        dto.setStationName(load.getStationName());
        dto.setLoadLevel(load.getLoadLevel());
        dto.setSuggestion(suggestionText(load));
        return dto;
    }

    private String suggestionText(StationLoadDTO load) {
        StationLoadLevelEnum level = StationLoadLevelEnum.valueOf(load.getLoadLevel());
        if (level == StationLoadLevelEnum.OVERLOAD) {
            return "当前网点待处理任务较多，建议临时增加快递员或将部分派件任务转移到附近网点。";
        }
        if (level == StationLoadLevelEnum.HIGH) {
            return "当前网点派件压力较高，建议优先分配空闲快递员处理待派任务。";
        }
        if (level == StationLoadLevelEnum.LOW) {
            return "当前网点负载较低，可承接附近高负载网点的部分派件任务。";
        }
        return "当前网点负载适中，保持正常派件节奏。";
    }

    private String generateTaskNo(LocalDateTime now) {
        return "DT" + now.format(NUMBER_TIME_FORMATTER);
    }

    private DispatchTaskDTO toDTO(DispatchTaskEntity entity) {
        DispatchTaskDTO dto = new DispatchTaskDTO();
        dto.setId(entity.getId());
        dto.setTaskNo(entity.getTaskNo());
        dto.setWaybillNo(entity.getWaybillNo());
        dto.setCourierId(entity.getCourierId());
        dto.setStationId(entity.getStationId());
        dto.setTaskStatus(entity.getTaskStatus());
        dto.setAssignTime(entity.getAssignTime());
        dto.setFinishTime(entity.getFinishTime());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
