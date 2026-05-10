package com.logiagent.waybill.service;

import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.dto.WaybillDailyStatisticsDTO;
import com.logiagent.api.dto.ExceptionStatisticsDTO;
import com.logiagent.api.request.AdminExceptionQueryRequest;
import com.logiagent.api.request.AdminWaybillQueryRequest;
import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.api.request.MarkExceptionRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;

import java.time.LocalDate;

public interface WaybillService {

    WaybillDTO createWaybill(CreateWaybillRequest request);

    WaybillDTO getByWaybillNo(String waybillNo);

    WaybillDTO getByOrderNo(String orderNo);

    WaybillDTO updateStatus(String waybillNo, UpdateStatusRequest request);

    WaybillDTO markException(String waybillNo, MarkExceptionRequest request);

    PageResult<WaybillDTO> pageExceptions(long page, long size);

    PageResult<WaybillDTO> pageAdminWaybills(AdminWaybillQueryRequest request);

    PageResult<WaybillDTO> pageAdminExceptions(AdminExceptionQueryRequest request);

    WaybillDTO resolveException(String waybillNo);

    ExceptionStatisticsDTO exceptionStatistics();

    WaybillDailyStatisticsDTO dailyStatistics(LocalDate date);

    WaybillDailyStatisticsDTO rangeStatistics(LocalDate startDate, LocalDate endDate);
}
