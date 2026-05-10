package com.logiagent.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logiagent.api.client.WaybillFeignClient;
import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.request.CreateOrderRequest;
import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.api.response.CreateOrderResponse;
import com.logiagent.common.enums.OrderStatusEnum;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.order.entity.OrderEntity;
import com.logiagent.order.mapper.OrderMapper;
import com.logiagent.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter NUMBER_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final OrderMapper orderMapper;
    private final WaybillFeignClient waybillFeignClient;

    public OrderServiceImpl(OrderMapper orderMapper, WaybillFeignClient waybillFeignClient) {
        this.orderMapper = orderMapper;
        this.waybillFeignClient = waybillFeignClient;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        validateCreateRequest(request);
        LocalDateTime now = LocalDateTime.now();
        OrderEntity order = new OrderEntity();
        order.setOrderNo(generateOrderNo(now));
        order.setSenderId(request.getSenderId());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setGoodsName(request.getGoodsName());
        order.setWeight(request.getWeight());
        order.setStatus(OrderStatusEnum.CREATED.name());
        order.setCreateTime(now);
        order.setUpdateTime(now);
        orderMapper.insert(order);

        CreateWaybillRequest waybillRequest = new CreateWaybillRequest();
        waybillRequest.setOrderNo(order.getOrderNo());
        Result<WaybillDTO> waybillResult = waybillFeignClient.createWaybill(waybillRequest);
        if (waybillResult == null || waybillResult.getData() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "create waybill failed");
        }

        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderNo(order.getOrderNo());
        response.setWaybillNo(waybillResult.getData().getWaybillNo());
        return response;
    }

    @Override
    public OrderDTO getByOrderNo(String orderNo) {
        OrderEntity order = selectByOrderNo(orderNo);
        return toDTO(order);
    }

    @Override
    public PageResult<OrderDTO> pageOrders(long page, long size) {
        Page<OrderEntity> pageParam = new Page<>(Math.max(page, 1), Math.max(size, 1));
        Page<OrderEntity> orderPage = orderMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<OrderEntity>().orderByDesc(OrderEntity::getCreateTime)
        );
        List<OrderDTO> records = orderPage.getRecords().stream().map(this::toDTO).toList();
        return PageResult.of(records, orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize());
    }

    @Override
    public OrderDTO updateStatus(String orderNo, UpdateStatusRequest request) {
        if (request == null || !StringUtils.hasText(request.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "status is required");
        }
        OrderStatusEnum.valueOf(request.getStatus());
        OrderEntity order = selectByOrderNo(orderNo);
        order.setStatus(request.getStatus());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return toDTO(order);
    }

    @Override
    public OrderDailyStatisticsDTO dailyStatistics(LocalDate date) {
        LocalDate target = date == null ? LocalDate.now() : date;
        return statistics(target, target);
    }

    @Override
    public OrderDailyStatisticsDTO rangeStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDate end = endDate == null ? LocalDate.now() : endDate;
        LocalDate start = startDate == null ? end : startDate;
        if (start.isAfter(end)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "startDate must not be after endDate");
        }
        return statistics(start, end);
    }

    private OrderDailyStatisticsDTO statistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();
        OrderDailyStatisticsDTO dto = new OrderDailyStatisticsDTO();
        if (startDate.equals(endDate)) {
            dto.setDate(startDate);
        }
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setTotalOrderCount(count(new LambdaQueryWrapper<>()));
        dto.setNewOrderCount(count(new LambdaQueryWrapper<OrderEntity>()
                .ge(OrderEntity::getCreateTime, startTime)
                .lt(OrderEntity::getCreateTime, endExclusive)));
        dto.setCancelledOrderCount(count(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getStatus, OrderStatusEnum.CANCELLED.name())
                .ge(OrderEntity::getUpdateTime, startTime)
                .lt(OrderEntity::getUpdateTime, endExclusive)));
        dto.setOrderStatusCountMap(orderStatusCountMap());
        return dto;
    }

    private Map<String, Long> orderStatusCountMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            Long count = count(new LambdaQueryWrapper<OrderEntity>()
                    .eq(OrderEntity::getStatus, status.name()));
            map.put(status.name(), count);
        }
        return map;
    }

    private Long count(LambdaQueryWrapper<OrderEntity> wrapper) {
        Long count = orderMapper.selectCount(wrapper);
        return count == null ? 0L : count;
    }

    private OrderEntity selectByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "orderNo is required");
        }
        OrderEntity order = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "order not found");
        }
        return order;
    }

    private void validateCreateRequest(CreateOrderRequest request) {
        if (request == null
                || request.getSenderId() == null
                || !StringUtils.hasText(request.getReceiverName())
                || !StringUtils.hasText(request.getReceiverPhone())
                || !StringUtils.hasText(request.getReceiverAddress())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "order request is incomplete");
        }
    }

    private String generateOrderNo(LocalDateTime now) {
        return "OD" + now.format(NUMBER_TIME_FORMATTER);
    }

    private OrderDTO toDTO(OrderEntity order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setSenderId(order.getSenderId());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setGoodsName(order.getGoodsName());
        dto.setWeight(order.getWeight());
        dto.setStatus(order.getStatus());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        return dto;
    }
}
