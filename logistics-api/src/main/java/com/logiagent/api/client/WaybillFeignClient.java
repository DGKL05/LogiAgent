package com.logiagent.api.client;

import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.api.request.MarkExceptionRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "logistics-waybill-service", path = "/waybills")
public interface WaybillFeignClient {

    @PostMapping
    Result<WaybillDTO> createWaybill(@RequestBody CreateWaybillRequest request);

    @GetMapping("/{waybillNo}")
    Result<WaybillDTO> getByWaybillNo(@PathVariable("waybillNo") String waybillNo);

    @GetMapping("/order/{orderNo}")
    Result<WaybillDTO> getByOrderNo(@PathVariable("orderNo") String orderNo);

    @PutMapping("/{waybillNo}/status")
    Result<WaybillDTO> updateStatus(@PathVariable("waybillNo") String waybillNo,
                                    @RequestBody UpdateStatusRequest request);

    @PostMapping("/{waybillNo}/exception")
    Result<WaybillDTO> markException(@PathVariable("waybillNo") String waybillNo,
                                     @RequestBody MarkExceptionRequest request);

    @GetMapping("/exceptions")
    Result<PageResult<WaybillDTO>> listExceptions(@RequestParam("page") long page,
                                                  @RequestParam("size") long size);
}
