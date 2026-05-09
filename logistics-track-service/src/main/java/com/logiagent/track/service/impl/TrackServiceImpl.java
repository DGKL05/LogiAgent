package com.logiagent.track.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.request.CreateTrackRequest;
import com.logiagent.common.enums.TrackActionEnum;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.track.entity.TrackEntity;
import com.logiagent.track.mapper.TrackMapper;
import com.logiagent.track.service.TrackService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrackServiceImpl implements TrackService {

    private final TrackMapper trackMapper;

    public TrackServiceImpl(TrackMapper trackMapper) {
        this.trackMapper = trackMapper;
    }

    @Override
    public TrackDTO createTrack(CreateTrackRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getWaybillNo())
                || !StringUtils.hasText(request.getAction())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "track request is incomplete");
        }
        TrackActionEnum.valueOf(request.getAction());
        LocalDateTime now = LocalDateTime.now();
        TrackEntity track = new TrackEntity();
        track.setWaybillNo(request.getWaybillNo());
        track.setAction(request.getAction());
        track.setDescription(request.getDescription());
        track.setOperatorId(request.getOperatorId());
        track.setEventTime(request.getEventTime() == null ? now : request.getEventTime());
        track.setCreateTime(now);
        trackMapper.insert(track);
        return toDTO(track);
    }

    @Override
    public List<TrackDTO> listByWaybillNo(String waybillNo) {
        if (!StringUtils.hasText(waybillNo)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "waybillNo is required");
        }
        return trackMapper.selectList(
                new LambdaQueryWrapper<TrackEntity>()
                        .eq(TrackEntity::getWaybillNo, waybillNo)
                        .orderByDesc(TrackEntity::getEventTime)
        ).stream().map(this::toDTO).toList();
    }

    @Override
    public TrackDTO getLatestByWaybillNo(String waybillNo) {
        if (!StringUtils.hasText(waybillNo)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "waybillNo is required");
        }
        List<TrackEntity> records = trackMapper.selectList(
                new LambdaQueryWrapper<TrackEntity>()
                        .eq(TrackEntity::getWaybillNo, waybillNo)
                        .orderByDesc(TrackEntity::getEventTime)
                        .last("limit 1")
        );
        if (records.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "track not found");
        }
        return toDTO(records.get(0));
    }

    private TrackDTO toDTO(TrackEntity track) {
        TrackDTO dto = new TrackDTO();
        dto.setId(track.getId());
        dto.setWaybillNo(track.getWaybillNo());
        dto.setAction(track.getAction());
        dto.setDescription(track.getDescription());
        dto.setOperatorId(track.getOperatorId());
        dto.setEventTime(track.getEventTime());
        dto.setCreateTime(track.getCreateTime());
        return dto;
    }
}
