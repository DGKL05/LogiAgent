package com.logiagent.api.response;

import java.io.Serializable;

public class CreateOrderResponse implements Serializable {

    private String orderNo;
    private String waybillNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }
}
