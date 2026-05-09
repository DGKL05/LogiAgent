package com.logiagent.api.request;

import java.io.Serializable;

public class CreateWaybillRequest implements Serializable {

    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
