package com.logiagent.order.service;

import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.request.CreateOrderRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.api.response.CreateOrderResponse;
import com.logiagent.common.result.PageResult;

import java.time.LocalDate;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    OrderDTO getByOrderNo(String orderNo);

    PageResult<OrderDTO> pageOrders(long page, long size);

    OrderDTO updateStatus(String orderNo, UpdateStatusRequest request);

    OrderDailyStatisticsDTO dailyStatistics(LocalDate date);

    OrderDailyStatisticsDTO rangeStatistics(LocalDate startDate, LocalDate endDate);
}
