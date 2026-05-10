package com.logiagent.order.controller;

import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.request.AdminOrderQueryRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<PageResult<OrderDTO>> pageOrders(@ModelAttribute AdminOrderQueryRequest request) {
        return Result.success(orderService.pageAdminOrders(request));
    }

    @GetMapping("/{orderNo}")
    public Result<OrderDTO> getOrder(@PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    @PutMapping("/{orderNo}/status")
    public Result<OrderDTO> updateStatus(@PathVariable("orderNo") String orderNo,
                                         @RequestBody UpdateStatusRequest request) {
        return Result.success(orderService.updateStatus(orderNo, request));
    }

    @GetMapping("/statistics")
    public Result<OrderDailyStatisticsDTO> statistics() {
        return Result.success(orderService.adminStatistics());
    }
}
