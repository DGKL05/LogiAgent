package com.logiagent.waybill.controller;

import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.dto.WaybillDailyStatisticsDTO;
import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.api.request.MarkExceptionRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.waybill.service.WaybillService;
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
@RequestMapping("/waybills")
public class WaybillController {

    private final WaybillService waybillService;

    public WaybillController(WaybillService waybillService) {
        this.waybillService = waybillService;
    }

    @PostMapping
    public Result<WaybillDTO> createWaybill(@RequestBody CreateWaybillRequest request) {
        return Result.success(waybillService.createWaybill(request));
    }

    @GetMapping("/{waybillNo}")
    public Result<WaybillDTO> getByWaybillNo(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(waybillService.getByWaybillNo(waybillNo));
    }

    @GetMapping("/order/{orderNo}")
    public Result<WaybillDTO> getByOrderNo(@PathVariable("orderNo") String orderNo) {
        return Result.success(waybillService.getByOrderNo(orderNo));
    }

    @PutMapping("/{waybillNo}/status")
    public Result<WaybillDTO> updateStatus(@PathVariable("waybillNo") String waybillNo,
                                           @RequestBody UpdateStatusRequest request) {
        return Result.success(waybillService.updateStatus(waybillNo, request));
    }

    @PostMapping("/{waybillNo}/exception")
    public Result<WaybillDTO> markException(@PathVariable("waybillNo") String waybillNo,
                                            @RequestBody MarkExceptionRequest request) {
        return Result.success(waybillService.markException(waybillNo, request));
    }

    @GetMapping("/exceptions")
    public Result<PageResult<WaybillDTO>> pageExceptions(@RequestParam(name = "page", defaultValue = "1") long page,
                                                         @RequestParam(name = "size", defaultValue = "10") long size) {
        return Result.success(waybillService.pageExceptions(page, size));
    }

    @GetMapping("/statistics/daily")
    public Result<WaybillDailyStatisticsDTO> dailyStatistics(@RequestParam("date") LocalDate date) {
        return Result.success(waybillService.dailyStatistics(date));
    }

    @GetMapping("/statistics/range")
    public Result<WaybillDailyStatisticsDTO> rangeStatistics(@RequestParam("startDate") LocalDate startDate,
                                                            @RequestParam("endDate") LocalDate endDate) {
        return Result.success(waybillService.rangeStatistics(startDate, endDate));
    }
}
