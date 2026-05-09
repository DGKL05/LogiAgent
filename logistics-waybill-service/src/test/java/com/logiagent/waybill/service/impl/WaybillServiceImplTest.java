package com.logiagent.waybill.service.impl;

import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.common.enums.WaybillStatusEnum;
import com.logiagent.waybill.entity.WaybillEntity;
import com.logiagent.waybill.mapper.WaybillMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WaybillServiceImplTest {

    @Mock
    private WaybillMapper waybillMapper;

    @InjectMocks
    private WaybillServiceImpl waybillService;

    @Test
    void createWaybillInsertsCreatedWaybillForOrder() {
        CreateWaybillRequest request = new CreateWaybillRequest();
        request.setOrderNo("OD202605090001");

        var response = waybillService.createWaybill(request);

        ArgumentCaptor<WaybillEntity> captor = ArgumentCaptor.forClass(WaybillEntity.class);
        verify(waybillMapper).insert(captor.capture());
        WaybillEntity savedWaybill = captor.getValue();
        assertThat(savedWaybill.getWaybillNo()).startsWith("WB");
        assertThat(savedWaybill.getOrderNo()).isEqualTo("OD202605090001");
        assertThat(savedWaybill.getCurrentStatus()).isEqualTo(WaybillStatusEnum.CREATED.name());
        assertThat(response.getWaybillNo()).isEqualTo(savedWaybill.getWaybillNo());
    }
}
