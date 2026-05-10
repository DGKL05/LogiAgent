package com.logiagent.route.planner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logiagent.api.dto.RouteStepDTO;
import com.logiagent.api.request.RoutePlanRequest;
import com.logiagent.api.response.RoutePlanResponse;
import com.logiagent.common.enums.RouteProviderEnum;
import com.logiagent.common.enums.RouteStrategyEnum;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.route.entity.RouteEntity;
import com.logiagent.route.entity.StationEntity;
import com.logiagent.route.mapper.RouteMapper;
import com.logiagent.route.mapper.StationMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@Component
public class LocalDijkstraRoutePlanner implements RoutePlanner {

    private final StationMapper stationMapper;
    private final RouteMapper routeMapper;

    public LocalDijkstraRoutePlanner(StationMapper stationMapper, RouteMapper routeMapper) {
        this.stationMapper = stationMapper;
        this.routeMapper = routeMapper;
    }

    @Override
    public RoutePlanResponse plan(RoutePlanRequest request) {
        validate(request);
        RouteStrategyEnum strategy = request.getStrategy() == null ? RouteStrategyEnum.BALANCED : request.getStrategy();
        List<Long> checkpoints = new ArrayList<>();
        checkpoints.add(request.getStartStationId());
        if (request.getWaypoints() != null) {
            checkpoints.addAll(request.getWaypoints());
        }
        checkpoints.add(request.getEndStationId());

        loadStations(checkpoints);
        List<RouteEntity> routes = routeMapper.selectList(new LambdaQueryWrapper<RouteEntity>()
                .eq(RouteEntity::getEnabled, true));
        Map<Long, List<RouteEntity>> graph = buildGraph(routes);
        List<Long> wholePath = new ArrayList<>();
        BigDecimal totalDistance = BigDecimal.ZERO;
        BigDecimal totalDuration = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        List<RouteStepDTO> steps = new ArrayList<>();

        for (int i = 0; i < checkpoints.size() - 1; i++) {
            Segment segment = shortestPath(checkpoints.get(i), checkpoints.get(i + 1), strategy, graph);
            if (wholePath.isEmpty()) {
                wholePath.addAll(segment.pathStationIds());
            } else {
                wholePath.addAll(segment.pathStationIds().subList(1, segment.pathStationIds().size()));
            }
            totalDistance = totalDistance.add(segment.distance());
            totalDuration = totalDuration.add(segment.duration());
            totalCost = totalCost.add(segment.cost());
            steps.addAll(toSteps(segment, loadStations(segment.pathStationIds())));
        }
        Map<Long, StationEntity> pathStations = loadStations(wholePath);

        RoutePlanResponse response = new RoutePlanResponse();
        response.setProvider(RouteProviderEnum.LOCAL_DIJKSTRA);
        response.setStrategy(strategy);
        response.setPathStationIds(wholePath);
        response.setPathStationNames(wholePath.stream().map(id -> pathStations.get(id).getStationName()).toList());
        response.setTotalDistance(totalDistance.setScale(2, RoundingMode.HALF_UP));
        response.setTotalDuration(totalDuration.setScale(2, RoundingMode.HALF_UP));
        response.setTotalCost(totalCost.setScale(2, RoundingMode.HALF_UP));
        response.setToll(BigDecimal.ZERO);
        response.setSteps(steps);
        response.setRouteSummary("LOCAL_DIJKSTRA path: " + String.join(" -> ", response.getPathStationNames()));
        return response;
    }

    private void validate(RoutePlanRequest request) {
        if (request == null || request.getStartStationId() == null || request.getEndStationId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "startStationId and endStationId are required");
        }
        if (request.getStartStationId().equals(request.getEndStationId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "startStationId and endStationId cannot be same");
        }
    }

    private Map<Long, StationEntity> loadStations(List<Long> stationIds) {
        List<StationEntity> stations = stationMapper.selectBatchIds(stationIds);
        Map<Long, StationEntity> stationMap = new HashMap<>();
        for (StationEntity station : stations) {
            stationMap.put(station.getId(), station);
        }
        for (Long stationId : stationIds) {
            if (!stationMap.containsKey(stationId)) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "station not found: " + stationId);
            }
        }
        return stationMap;
    }

    private Map<Long, List<RouteEntity>> buildGraph(List<RouteEntity> routes) {
        Map<Long, List<RouteEntity>> graph = new HashMap<>();
        for (RouteEntity route : routes) {
            graph.computeIfAbsent(route.getStartStationId(), ignored -> new ArrayList<>()).add(route);
            RouteEntity reverse = new RouteEntity();
            reverse.setStartStationId(route.getEndStationId());
            reverse.setEndStationId(route.getStartStationId());
            reverse.setDistanceKm(route.getDistanceKm());
            reverse.setDurationMinute(route.getDurationMinute());
            reverse.setCost(route.getCost());
            graph.computeIfAbsent(reverse.getStartStationId(), ignored -> new ArrayList<>()).add(reverse);
        }
        return graph;
    }

    private Segment shortestPath(Long startId,
                                 Long endId,
                                 RouteStrategyEnum strategy,
                                 Map<Long, List<RouteEntity>> graph) {
        Map<Long, BigDecimal> distance = new HashMap<>();
        Map<Long, RouteEntity> previous = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparing(Node::weight));
        distance.put(startId, BigDecimal.ZERO);
        queue.add(new Node(startId, BigDecimal.ZERO));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (!visited.add(node.stationId())) {
                continue;
            }
            if (node.stationId().equals(endId)) {
                break;
            }
            for (RouteEntity route : graph.getOrDefault(node.stationId(), List.of())) {
                BigDecimal nextWeight = node.weight().add(strategy.localWeight(
                        route.getDistanceKm(),
                        route.getDurationMinute(),
                        route.getCost()
                ));
                if (nextWeight.compareTo(distance.getOrDefault(route.getEndStationId(), new BigDecimal("999999999"))) < 0) {
                    distance.put(route.getEndStationId(), nextWeight);
                    previous.put(route.getEndStationId(), route);
                    queue.add(new Node(route.getEndStationId(), nextWeight));
                }
            }
        }

        if (!previous.containsKey(endId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "local route path not found");
        }
        ArrayDeque<Long> path = new ArrayDeque<>();
        List<RouteEntity> pathRoutes = new ArrayList<>();
        Long current = endId;
        path.addFirst(current);
        while (!current.equals(startId)) {
            RouteEntity route = previous.get(current);
            pathRoutes.add(0, route);
            current = route.getStartStationId();
            path.addFirst(current);
        }
        BigDecimal routeDistance = pathRoutes.stream().map(RouteEntity::getDistanceKm).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal routeDuration = pathRoutes.stream().map(RouteEntity::getDurationMinute).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal routeCost = pathRoutes.stream().map(RouteEntity::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Segment(new ArrayList<>(path), pathRoutes, routeDistance, routeDuration, routeCost);
    }

    private List<RouteStepDTO> toSteps(Segment segment, Map<Long, StationEntity> stations) {
        List<RouteStepDTO> steps = new ArrayList<>();
        for (RouteEntity route : segment.routes()) {
            StationEntity start = stations.get(route.getStartStationId());
            StationEntity end = stations.get(route.getEndStationId());
            RouteStepDTO step = new RouteStepDTO();
            step.setInstruction(start.getStationName() + " -> " + end.getStationName());
            step.setDistance(route.getDistanceKm());
            step.setDuration(route.getDurationMinute());
            steps.add(step);
        }
        return steps;
    }

    private record Node(Long stationId, BigDecimal weight) {
    }

    private record Segment(List<Long> pathStationIds,
                           List<RouteEntity> routes,
                           BigDecimal distance,
                           BigDecimal duration,
                           BigDecimal cost) {
    }
}
