package com.logiagent.order.service;

import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.request.CreateOrderRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.api.response.CreateOrderResponse;
import com.logiagent.common.result.PageResult;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    OrderDTO getByOrderNo(String orderNo);

    PageResult<OrderDTO> pageOrders(long page, long size);

    OrderDTO updateStatus(String orderNo, UpdateStatusRequest request);
}
