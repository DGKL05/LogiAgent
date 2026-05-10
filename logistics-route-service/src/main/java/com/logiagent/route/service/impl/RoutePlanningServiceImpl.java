package com.logiagent.route.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logiagent.api.dto.AdminRouteDTO;
import com.logiagent.api.dto.StationDTO;
import com.logiagent.api.request.AdminRouteQueryRequest;
import com.logiagent.api.request.AdminStationQueryRequest;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.request.RouteSaveRequest;
import com.logiagent.api.request.StationSaveRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.PageResult;
import com.logiagent.route.entity.RouteEntity;
import com.logiagent.route.entity.StationEntity;
import com.logiagent.route.mapper.RouteMapper;
import com.logiagent.route.mapper.StationMapper;
import com.logiagent.route.planner.RoutePlannerFactory;
import com.logiagent.route.service.RoutePlanningService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoutePlanningServiceImpl implements RoutePlanningService {

    private final RoutePlannerFactory routePlannerFactory;
    private final StationMapper stationMapper;
    private final RouteMapper routeMapper;

    public RoutePlanningServiceImpl(RoutePlannerFactory routePlannerFactory, StationMapper stationMapper, RouteMapper routeMapper) {
        this.routePlannerFactory = routePlannerFactory;
        this.stationMapper = stationMapper;
        this.routeMapper = routeMapper;
    }

    @Override
    public RoutePlanResponse plan(RoutePlanRequest request) {
        return routePlannerFactory.getPlanner(request).plan(request);
    }

    @Override
    public List<StationDTO> searchStations(String keyword) {
        LambdaQueryWrapper<StationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StationEntity::getStatus, "ENABLED");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(item -> item.like(StationEntity::getStationName, keyword)
                    .or()
                    .like(StationEntity::getCity, keyword));
        }
        wrapper.last("limit 10");
        return stationMapper.selectList(wrapper).stream().map(this::toDTO).toList();
    }

    @Override
    public PageResult<StationDTO> pageAdminStations(AdminStationQueryRequest request) {
        AdminStationQueryRequest query = request == null ? new AdminStationQueryRequest() : request;
        Page<StationEntity> pageParam = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<StationEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getStationName())) {
            wrapper.like(StationEntity::getStationName, query.getStationName());
        }
        if (StringUtils.hasText(query.getProvince())) {
            wrapper.like(StationEntity::getProvince, query.getProvince());
        }
        if (StringUtils.hasText(query.getCity())) {
            wrapper.like(StationEntity::getCity, query.getCity());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(StationEntity::getStatus, query.getStatus());
        }
        Page<StationEntity> stationPage = stationMapper.selectPage(pageParam, wrapper.orderByDesc(StationEntity::getUpdateTime));
        return PageResult.of(stationPage.getRecords().stream().map(this::toDTO).toList(),
                stationPage.getTotal(), stationPage.getCurrent(), stationPage.getSize());
    }

    @Override
    public StationDTO getStation(Long stationId) {
        return toDTO(selectStation(stationId));
    }

    @Override
    public StationDTO createStation(StationSaveRequest request) {
        validateStationRequest(request);
        LocalDateTime now = LocalDateTime.now();
        StationEntity station = new StationEntity();
        fillStation(station, request);
        station.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ENABLED");
        station.setCreateTime(now);
        station.setUpdateTime(now);
        stationMapper.insert(station);
        return toDTO(station);
    }

    @Override
    public StationDTO updateStation(Long stationId, StationSaveRequest request) {
        validateStationRequest(request);
        StationEntity station = selectStation(stationId);
        fillStation(station, request);
        if (StringUtils.hasText(request.getStatus())) {
            station.setStatus(request.getStatus());
        }
        station.setUpdateTime(LocalDateTime.now());
        stationMapper.updateById(station);
        return toDTO(station);
    }

    @Override
    public StationDTO disableStation(Long stationId) {
        StationEntity station = selectStation(stationId);
        station.setStatus("DISABLED");
        station.setUpdateTime(LocalDateTime.now());
        stationMapper.updateById(station);
        return toDTO(station);
    }

    @Override
    public PageResult<AdminRouteDTO> pageAdminRoutes(AdminRouteQueryRequest request) {
        AdminRouteQueryRequest query = request == null ? new AdminRouteQueryRequest() : request;
        Page<RouteEntity> pageParam = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        Page<RouteEntity> routePage = routeMapper.selectPage(pageParam, buildRouteWrapper(query));
        Map<Long, StationEntity> stationMap = loadRouteStations(routePage.getRecords());
        return PageResult.of(routePage.getRecords().stream().map(route -> toRouteDTO(route, stationMap)).toList(),
                routePage.getTotal(), routePage.getCurrent(), routePage.getSize());
    }

    @Override
    public AdminRouteDTO getRoute(Long routeId) {
        RouteEntity route = selectRoute(routeId);
        return toRouteDTO(route, loadRouteStations(List.of(route)));
    }

    @Override
    public AdminRouteDTO createRoute(RouteSaveRequest request) {
        validateRouteRequest(request);
        LocalDateTime now = LocalDateTime.now();
        RouteEntity route = new RouteEntity();
        fillRoute(route, request);
        route.setEnabled(!"DISABLED".equalsIgnoreCase(request.getStatus()));
        route.setCreateTime(now);
        route.setUpdateTime(now);
        routeMapper.insert(route);
        return toRouteDTO(route, loadRouteStations(List.of(route)));
    }

    @Override
    public AdminRouteDTO updateRoute(Long routeId, RouteSaveRequest request) {
        validateRouteRequest(request);
        RouteEntity route = selectRoute(routeId);
        fillRoute(route, request);
        if (StringUtils.hasText(request.getStatus())) {
            route.setEnabled(!"DISABLED".equalsIgnoreCase(request.getStatus()));
        }
        route.setUpdateTime(LocalDateTime.now());
        routeMapper.updateById(route);
        return toRouteDTO(route, loadRouteStations(List.of(route)));
    }

    @Override
    public AdminRouteDTO disableRoute(Long routeId) {
        RouteEntity route = selectRoute(routeId);
        route.setEnabled(false);
        route.setUpdateTime(LocalDateTime.now());
        routeMapper.updateById(route);
        return toRouteDTO(route, loadRouteStations(List.of(route)));
    }

    private LambdaQueryWrapper<RouteEntity> buildRouteWrapper(AdminRouteQueryRequest query) {
        LambdaQueryWrapper<RouteEntity> wrapper = new LambdaQueryWrapper<>();
        if (query.getStartStationId() != null) {
            wrapper.eq(RouteEntity::getStartStationId, query.getStartStationId());
        }
        if (query.getEndStationId() != null) {
            wrapper.eq(RouteEntity::getEndStationId, query.getEndStationId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(RouteEntity::getEnabled, !"DISABLED".equalsIgnoreCase(query.getStatus()));
        }
        return wrapper.orderByDesc(RouteEntity::getUpdateTime);
    }

    private void validateStationRequest(StationSaveRequest request) {
        if (request == null || !StringUtils.hasText(request.getStationName()) || !StringUtils.hasText(request.getCity())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "stationName and city are required");
        }
        validateCoordinate(request.getLatitude(), "latitude", new BigDecimal("-90"), new BigDecimal("90"));
        validateCoordinate(request.getLongitude(), "longitude", new BigDecimal("-180"), new BigDecimal("180"));
    }

    private void validateCoordinate(BigDecimal value, String name, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return;
        }
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, name + " is out of range");
        }
    }

    private void fillStation(StationEntity station, StationSaveRequest request) {
        station.setStationName(request.getStationName());
        station.setProvince(request.getProvince());
        station.setCity(request.getCity());
        station.setAddress(request.getAddress());
        station.setLongitude(request.getLongitude());
        station.setLatitude(request.getLatitude());
    }

    private void validateRouteRequest(RouteSaveRequest request) {
        if (request == null || request.getStartStationId() == null || request.getEndStationId() == null
                || request.getDistance() == null || request.getEstimatedHours() == null || request.getCost() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "route request is incomplete");
        }
        if (request.getStartStationId().equals(request.getEndStationId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "startStationId and endStationId cannot be same");
        }
        selectStation(request.getStartStationId());
        selectStation(request.getEndStationId());
    }

    private void fillRoute(RouteEntity route, RouteSaveRequest request) {
        route.setStartStationId(request.getStartStationId());
        route.setEndStationId(request.getEndStationId());
        route.setDistanceKm(request.getDistance());
        route.setDurationMinute(request.getEstimatedHours().multiply(new BigDecimal("60")).setScale(2, RoundingMode.HALF_UP));
        route.setCost(request.getCost());
    }

    private StationEntity selectStation(Long stationId) {
        if (stationId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "stationId is required");
        }
        StationEntity station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "station not found");
        }
        return station;
    }

    private RouteEntity selectRoute(Long routeId) {
        if (routeId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "routeId is required");
        }
        RouteEntity route = routeMapper.selectById(routeId);
        if (route == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "route not found");
        }
        return route;
    }

    private Map<Long, StationEntity> loadRouteStations(List<RouteEntity> routes) {
        List<Long> ids = routes.stream()
                .flatMap(route -> List.of(route.getStartStationId(), route.getEndStationId()).stream())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        return stationMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(StationEntity::getId, station -> station));
    }

    private StationDTO toDTO(StationEntity station) {
        StationDTO dto = new StationDTO();
        dto.setId(station.getId());
        dto.setStationName(station.getStationName());
        dto.setProvince(station.getProvince());
        dto.setCity(station.getCity());
        dto.setAddress(station.getAddress());
        dto.setLongitude(station.getLongitude());
        dto.setLatitude(station.getLatitude());
        dto.setStatus(station.getStatus());
        dto.setCreateTime(station.getCreateTime());
        dto.setUpdateTime(station.getUpdateTime());
        return dto;
    }

    private AdminRouteDTO toRouteDTO(RouteEntity route, Map<Long, StationEntity> stationMap) {
        AdminRouteDTO dto = new AdminRouteDTO();
        dto.setId(route.getId());
        dto.setStartStationId(route.getStartStationId());
        dto.setStartStationName(stationMap.get(route.getStartStationId()) == null ? null : stationMap.get(route.getStartStationId()).getStationName());
        dto.setEndStationId(route.getEndStationId());
        dto.setEndStationName(stationMap.get(route.getEndStationId()) == null ? null : stationMap.get(route.getEndStationId()).getStationName());
        dto.setDistance(route.getDistanceKm());
        dto.setEstimatedHours(route.getDurationMinute() == null ? null : route.getDurationMinute().divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP));
        dto.setCost(route.getCost());
        dto.setStatus(Boolean.FALSE.equals(route.getEnabled()) ? "DISABLED" : "ENABLED");
        dto.setCreateTime(route.getCreateTime());
        dto.setUpdateTime(route.getUpdateTime());
        return dto;
    }

    private long safePage(Long page) {
        return Math.max(page == null ? 1L : page, 1L);
    }

    private long safeSize(Long size) {
        return Math.max(size == null ? 10L : size, 1L);
    }
}
