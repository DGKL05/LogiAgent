package com.logiagent.order.controller;

import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.api.request.CreateOrderRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.api.response.CreateOrderResponse;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @GetMapping("/{orderNo}")
    public Result<OrderDTO> getOrder(@PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    @GetMapping
    public Result<PageResult<OrderDTO>> pageOrders(@RequestParam(name = "page", defaultValue = "1") long page,
                                                   @RequestParam(name = "size", defaultValue = "10") long size) {
        return Result.success(orderService.pageOrders(page, size));
    }

    @PutMapping("/{orderNo}/status")
    public Result<OrderDTO> updateStatus(@PathVariable("orderNo") String orderNo,
                                         @RequestBody UpdateStatusRequest request) {
        return Result.success(orderService.updateStatus(orderNo, request));
    }

    @GetMapping("/statistics/daily")
    public Result<OrderDailyStatisticsDTO> dailyStatistics(@RequestParam("date") LocalDate date) {
        return Result.success(orderService.dailyStatistics(date));
    }

    @GetMapping("/statistics/range")
    public Result<OrderDailyStatisticsDTO> rangeStatistics(@RequestParam("startDate") LocalDate startDate,
                                                          @RequestParam("endDate") LocalDate endDate) {
        return Result.success(orderService.rangeStatistics(startDate, endDate));
    }
}
