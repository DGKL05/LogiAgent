package com.logiagent.route.planner;

import com.logiagent.api.dto.RouteStepDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.enums.RouteProviderEnum;
import com.logiagent.common.enums.RouteStrategyEnum;
import com.logiagent.route.client.BaiduMapRouteClient;
import com.logiagent.route.config.BaiduMapProperties;
import com.logiagent.route.dto.BaiduDrivingResponse;
import com.logiagent.route.entity.StationEntity;
import com.logiagent.route.mapper.StationMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BaiduDrivingRoutePlanner implements RoutePlanner {

    private static final String DEFAULT_COORD_TYPE = "bd09ll";
    private static final String DEFAULT_RET_COORD_TYPE = "bd09ll";

    private final BaiduMapProperties properties;
    private final BaiduMapRouteClient baiduMapRouteClient;
    private final LocalDijkstraRoutePlanner localDijkstraRoutePlanner;
    private final StationMapper stationMapper;

    public BaiduDrivingRoutePlanner(BaiduMapProperties properties,
                                    BaiduMapRouteClient baiduMapRouteClient,
                                    LocalDijkstraRoutePlanner localDijkstraRoutePlanner,
                                    StationMapper stationMapper) {
        this.properties = properties;
        this.baiduMapRouteClient = baiduMapRouteClient;
        this.localDijkstraRoutePlanner = localDijkstraRoutePlanner;
        this.stationMapper = stationMapper;
    }

    @Override
    public RoutePlanResponse plan(RoutePlanRequest request) {
        if (!properties.isEnabled()) {
            return fallback(request, "Baidu Map API is disabled, fallback to local Dijkstra route planning");
        }
        if (!StringUtils.hasText(properties.getAk())) {
            return fallback(request, "Baidu Map AK is not configured, fallback to local Dijkstra route planning");
        }
        if (request.getWaypoints() != null && request.getWaypoints().size() > 10) {
            return fallback(request, "Baidu Map supports at most 10 waypoints, fallback to local Dijkstra route planning");
        }

        List<Long> stationIds = stationIds(request);
        List<StationEntity> stations = stationMapper.selectBatchIds(stationIds);
        if (stations.size() != stationIds.size()) {
            return fallback(request, "station not found, fallback to local Dijkstra route planning");
        }
        if (stations.stream().anyMatch(this::missingCoordinate)) {
            return fallback(request, "station coordinate is missing, fallback to local Dijkstra route planning");
        }

        try {
            StationEntity origin = findStation(stations, request.getStartStationId());
            StationEntity destination = findStation(stations, request.getEndStationId());
            String waypoints = waypointText(stations, request.getWaypoints());
            RouteStrategyEnum strategy = request.getStrategy() == null ? RouteStrategyEnum.BALANCED : request.getStrategy();
            BaiduDrivingResponse response = baiduMapRouteClient.driving(
                    coordinate(origin),
                    coordinate(destination),
                    waypoints,
                    strategy.getBaiduTactics(),
                    defaultText(request.getCoordType(), DEFAULT_COORD_TYPE),
                    defaultText(request.getRetCoordType(), DEFAULT_RET_COORD_TYPE)
            );
            return toResponse(request, response, stations, strategy);
        } catch (RuntimeException ex) {
            return fallback(request, "Baidu Map API request failed: " + ex.getMessage());
        }
    }

    private RoutePlanResponse toResponse(RoutePlanRequest request,
                                         BaiduDrivingResponse response,
                                         List<StationEntity> stations,
                                         RouteStrategyEnum strategy) {
        if (response == null) {
            return fallback(request, "Baidu Map API returned empty response, fallback to local Dijkstra route planning");
        }
        if (!Objects.equals(response.getStatus(), 0)) {
            return fallback(request, "Baidu Map API status is " + response.getStatus() + ", " + response.getMessage());
        }
        if (response.getResult() == null || response.getResult().getRoutes() == null || response.getResult().getRoutes().isEmpty()) {
            return fallback(request, "Baidu Map API returned no routes, fallback to local Dijkstra route planning");
        }

        BaiduDrivingResponse.Route route = response.getResult().getRoutes().get(0);
        RoutePlanResponse result = new RoutePlanResponse();
        result.setProvider(RouteProviderEnum.BAIDU_DRIVING);
        result.setStrategy(strategy);
        result.setPathStationIds(stationIds(request));
        result.setPathStationNames(result.getPathStationIds().stream()
                .map(id -> findStation(stations, id).getStationName())
                .toList());
        result.setTotalDistance(scale(route.getDistance(), BigDecimal.valueOf(1000)));
        result.setTotalDuration(scale(route.getDuration(), BigDecimal.valueOf(60)));
        result.setTotalCost(route.getToll() == null ? BigDecimal.ZERO : route.getToll());
        result.setToll(route.getToll());
        result.setTrafficCondition(route.getTrafficCondition());
        result.setSteps(toSteps(route.getSteps()));
        result.setRouteSummary("BAIDU_DRIVING path: " + String.join(" -> ", result.getPathStationNames()));
        return result;
    }

    private RoutePlanResponse fallback(RoutePlanRequest request, String reason) {
        RoutePlanRequest localRequest = new RoutePlanRequest();
        localRequest.setStartStationId(request.getStartStationId());
        localRequest.setEndStationId(request.getEndStationId());
        localRequest.setWaypoints(request.getWaypoints());
        localRequest.setStrategy(request.getStrategy());
        localRequest.setProvider(RouteProviderEnum.LOCAL_DIJKSTRA);
        RoutePlanResponse response = localDijkstraRoutePlanner.plan(localRequest);
        response.setFallbackUsed(true);
        response.setFallbackReason(reason);
        return response;
    }

    private List<Long> stationIds(RoutePlanRequest request) {
        List<Long> ids = new ArrayList<>();
        ids.add(request.getStartStationId());
        if (request.getWaypoints() != null) {
            ids.addAll(request.getWaypoints());
        }
        ids.add(request.getEndStationId());
        return ids;
    }

    private String waypointText(List<StationEntity> stations, List<Long> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            return "";
        }
        return waypoints.stream()
                .map(id -> coordinate(findStation(stations, id)))
                .collect(Collectors.joining("|"));
    }

    private List<RouteStepDTO> toSteps(List<BaiduDrivingResponse.Step> steps) {
        if (steps == null) {
            return List.of();
        }
        return steps.stream().map(step -> {
            RouteStepDTO dto = new RouteStepDTO();
            dto.setInstruction(step.getInstruction());
            dto.setDistance(scale(step.getDistance(), BigDecimal.valueOf(1000)));
            dto.setDuration(scale(step.getDuration(), BigDecimal.valueOf(60)));
            dto.setPath(step.getPath());
            return dto;
        }).toList();
    }

    private StationEntity findStation(List<StationEntity> stations, Long stationId) {
        return stations.stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow();
    }

    private boolean missingCoordinate(StationEntity station) {
        return station.getLatitude() == null || station.getLongitude() == null;
    }

    private String coordinate(StationEntity station) {
        return formatCoordinate(station.getLatitude(), station.getLongitude());
    }

    static String formatCoordinate(BigDecimal latitude, BigDecimal longitude) {
        return latitude.toPlainString() + "," + longitude.toPlainString();
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private BigDecimal scale(BigDecimal value, BigDecimal divisor) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.divide(divisor, 2, RoundingMode.HALF_UP);
    }
}
