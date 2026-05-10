package com.logiagent.waybill.controller;

import com.logiagent.api.dto.ExceptionStatisticsDTO;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.request.AdminExceptionQueryRequest;
import com.logiagent.api.request.AdminWaybillQueryRequest;
import com.logiagent.api.request.MarkExceptionRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import com.logiagent.waybill.service.WaybillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminWaybillController {

    private final WaybillService waybillService;

    public AdminWaybillController(WaybillService waybillService) {
        this.waybillService = waybillService;
    }

    @GetMapping("/admin/waybills")
    public Result<PageResult<WaybillDTO>> pageWaybills(@ModelAttribute AdminWaybillQueryRequest request) {
        return Result.success(waybillService.pageAdminWaybills(request));
    }

    @GetMapping("/admin/waybills/{waybillNo}")
    public Result<WaybillDTO> getWaybill(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(waybillService.getByWaybillNo(waybillNo));
    }

    @PutMapping("/admin/waybills/{waybillNo}/status")
    public Result<WaybillDTO> updateStatus(@PathVariable("waybillNo") String waybillNo,
                                           @RequestBody UpdateStatusRequest request) {
        return Result.success(waybillService.updateStatus(waybillNo, request));
    }

    @PostMapping("/admin/waybills/{waybillNo}/exception")
    public Result<WaybillDTO> markException(@PathVariable("waybillNo") String waybillNo,
                                            @RequestBody MarkExceptionRequest request) {
        return Result.success(waybillService.markException(waybillNo, request));
    }

    @PutMapping("/admin/waybills/{waybillNo}/exception/resolve")
    public Result<WaybillDTO> resolveWaybillException(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(waybillService.resolveException(waybillNo));
    }

    @GetMapping("/admin/exceptions")
    public Result<PageResult<WaybillDTO>> pageExceptions(@ModelAttribute AdminExceptionQueryRequest request) {
        return Result.success(waybillService.pageAdminExceptions(request));
    }

    @GetMapping("/admin/exceptions/{waybillNo}")
    public Result<WaybillDTO> getException(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(waybillService.getByWaybillNo(waybillNo));
    }

    @PutMapping("/admin/exceptions/{waybillNo}/resolve")
    public Result<WaybillDTO> resolveException(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(waybillService.resolveException(waybillNo));
    }

    @GetMapping("/admin/exceptions/statistics")
    public Result<ExceptionStatisticsDTO> exceptionStatistics() {
        return Result.success(waybillService.exceptionStatistics());
    }
}
