package com.logiagent.api.client;

import com.logiagent.api.dto.OrderDTO;
import com.logiagent.api.dto.OrderDailyStatisticsDTO;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "logistics-order-service", path = "/orders")
public interface OrderFeignClient {

    @GetMapping
    Result<PageResult<OrderDTO>> pageOrders(@RequestParam("page") long page, @RequestParam("size") long size);

    @GetMapping("/statistics/daily")
    Result<OrderDailyStatisticsDTO> dailyStatistics(@RequestParam("date") LocalDate date);

    @GetMapping("/statistics/range")
    Result<OrderDailyStatisticsDTO> rangeStatistics(@RequestParam("startDate") LocalDate startDate,
                                                    @RequestParam("endDate") LocalDate endDate);
}
