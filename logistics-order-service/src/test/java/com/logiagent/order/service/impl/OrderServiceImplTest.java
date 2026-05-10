package com.logiagent.order.service.impl;

import com.logiagent.api.client.WaybillFeignClient;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.request.AdminOrderQueryRequest;
import com.logiagent.api.request.CreateOrderRequest;
import com.logiagent.common.enums.OrderStatusEnum;
import com.logiagent.common.enums.WaybillStatusEnum;
import com.logiagent.common.result.Result;
import com.logiagent.order.entity.OrderEntity;
import com.logiagent.order.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private WaybillFeignClient waybillFeignClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrderInsertsCreatedOrderAndGeneratesWaybill() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSenderId(1L);
        request.setReceiverName("Zhang San");
        request.setReceiverPhone("13800000000");
        request.setReceiverAddress("Shenzhen Nanshan");
        request.setGoodsName("electronics");
        request.setWeight(new BigDecimal("2.50"));

        WaybillDTO waybillDTO = new WaybillDTO();
        waybillDTO.setWaybillNo("WB202605090001");
        waybillDTO.setOrderNo("OD202605090001");
        waybillDTO.setCurrentStatus(WaybillStatusEnum.CREATED.name());
        when(waybillFeignClient.createWaybill(any())).thenReturn(Result.success(waybillDTO));

        var response = orderService.createOrder(request);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderMapper).insert(orderCaptor.capture());
        OrderEntity savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getOrderNo()).startsWith("OD");
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatusEnum.CREATED.name());
        assertThat(savedOrder.getReceiverName()).isEqualTo("Zhang San");
        assertThat(response.getOrderNo()).isEqualTo(savedOrder.getOrderNo());
        assertThat(response.getWaybillNo()).isEqualTo("WB202605090001");
    }

    @Test
    void dailyStatisticsUsesRealOrderCounts() {
        when(orderMapper.selectCount(any())).thenReturn(10L, 3L, 1L, 2L, 1L, 0L);

        var statistics = orderService.dailyStatistics(LocalDate.of(2026, 5, 10));

        assertThat(statistics.getDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(statistics.getTotalOrderCount()).isEqualTo(10L);
        assertThat(statistics.getNewOrderCount()).isEqualTo(3L);
        assertThat(statistics.getCancelledOrderCount()).isEqualTo(1L);
        assertThat(statistics.getOrderStatusCountMap()).containsEntry(OrderStatusEnum.CREATED.name(), 2L);
    }

    @Test
    void pageAdminOrdersReturnsFilteredPageDtos() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderNo("OD202605100001");
        order.setSenderId(10L);
        order.setReceiverName("Li Si");
        order.setReceiverPhone("13900000000");
        order.setReceiverAddress("Guangzhou");
        order.setStatus(OrderStatusEnum.CREATED.name());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrderEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10, 1);
        pageResult.setRecords(List.of(order));
        when(orderMapper.selectPage(any(), any())).thenReturn(pageResult);

        AdminOrderQueryRequest request = new AdminOrderQueryRequest();
        request.setOrderNo("OD202605100001");
        var result = orderService.pageAdminOrders(request);

        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getOrderNo()).isEqualTo("OD202605100001");
    }
}
