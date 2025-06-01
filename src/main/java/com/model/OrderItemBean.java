package com.model;

import java.math.BigDecimal;

public class OrderItemBean {
    private Integer orderItemID; // DE-7001: Integer(10)
    private Integer orderID;     // DE-7002: Integer(10)
    private Integer deviceID;    // DE-7003: Integer(10)
    private Integer quantity;    // DE-7004: Integer(5)
    private BigDecimal price;    // DE-7005: Decimal(10,2)
    
    public OrderItemBean() {
        this.orderItemID = null;
        this.orderID = null;
        this.deviceID = null;
        this.quantity = 0;
        this.price = BigDecimal.ZERO;
    }
    
    public Integer getOrderItemID() {
        return orderItemID;
    }
    
    public void setOrderItemID(Integer orderItemID) {
        this.orderItemID = orderItemID;
    }
    
    public Integer getOrderID() {
        return orderID;
    }
    
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    
    public Integer getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(Integer deviceID) {
        this.deviceID = deviceID;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
} 