package com.logiagent.waybill.service;

import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.api.request.MarkExceptionRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;

public interface WaybillService {

    WaybillDTO createWaybill(CreateWaybillRequest request);

    WaybillDTO getByWaybillNo(String waybillNo);

    WaybillDTO getByOrderNo(String orderNo);

    WaybillDTO updateStatus(String waybillNo, UpdateStatusRequest request);

    WaybillDTO markException(String waybillNo, MarkExceptionRequest request);

    PageResult<WaybillDTO> pageExceptions(long page, long size);
}
