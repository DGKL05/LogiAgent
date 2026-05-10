package com.logiagent.route.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.route.entity.StationEntity;
import com.logiagent.route.mapper.StationMapper;
import com.logiagent.route.planner.RoutePlannerFactory;
import com.logiagent.route.service.RoutePlanningService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RoutePlanningServiceImpl implements RoutePlanningService {

    private final RoutePlannerFactory routePlannerFactory;
    private final StationMapper stationMapper;

    public RoutePlanningServiceImpl(RoutePlannerFactory routePlannerFactory, StationMapper stationMapper) {
        this.routePlannerFactory = routePlannerFactory;
        this.stationMapper = stationMapper;
    }

    @Override
    public RoutePlanResponse plan(RoutePlanRequest request) {
        return routePlannerFactory.getPlanner(request).plan(request);
    }

    @Override
    public List<StationDTO> searchStations(String keyword) {
        LambdaQueryWrapper<StationEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(StationEntity::getStationName, keyword)
                    .or()
                    .like(StationEntity::getCity, keyword);
        }
        wrapper.last("limit 10");
        return stationMapper.selectList(wrapper).stream().map(this::toDTO).toList();
    }

    private StationDTO toDTO(StationEntity station) {
        StationDTO dto = new StationDTO();
        dto.setId(station.getId());
        dto.setStationName(station.getStationName());
        dto.setCity(station.getCity());
        dto.setAddress(station.getAddress());
        dto.setLongitude(station.getLongitude());
        dto.setLatitude(station.getLatitude());
        return dto;
    }
}
