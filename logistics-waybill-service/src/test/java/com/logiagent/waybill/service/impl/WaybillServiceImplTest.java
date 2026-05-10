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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void dailyStatisticsUsesRealWaybillCounts() {
        when(waybillMapper.selectCount(any())).thenReturn(
                20L, 4L, 2L, 1L, 5L,
                1L, 2L, 3L, 5L, 4L, 3L, 2L, 1L, 0L,
                0L, 0L, 0L, 1L, 0L, 0L
        );

        var statistics = waybillService.dailyStatistics(LocalDate.of(2026, 5, 10));

        assertThat(statistics.getDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(statistics.getTotalWaybillCount()).isEqualTo(20L);
        assertThat(statistics.getNewWaybillCount()).isEqualTo(4L);
        assertThat(statistics.getSignedWaybillCount()).isEqualTo(2L);
        assertThat(statistics.getExceptionWaybillCount()).isEqualTo(1L);
        assertThat(statistics.getTransportingWaybillCount()).isEqualTo(5L);
        assertThat(statistics.getWaybillStatusCountMap()).containsEntry(WaybillStatusEnum.TRANSPORTING.name(), 5L);
    }
}
