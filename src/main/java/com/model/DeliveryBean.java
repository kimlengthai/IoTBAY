package com.model;

import java.util.Date;

public class DeliveryBean {
    private Integer deliveryID;           // DE-11001: Integer(10)
    private Integer orderID;              // DE-11002: Integer(10)
    private Date deliveryDate;            // DE-11003: Date(10)
    private Date estimatedDeliveryDate;   // DE-11004: Date(10)
    private String deliveryStatus;        // DE-11005: Varchar(20)
    private String deliveryMethod;        // DE-11006: Varchar(50)
    
    public DeliveryBean() {
        this.deliveryID = null;
        this.orderID = null;
        this.deliveryDate = null;
        this.estimatedDeliveryDate = null;
        this.deliveryStatus = "";
        this.deliveryMethod = "";
    }
    
    public Integer getDeliveryID() {
        return deliveryID;
    }
    
    public void setDeliveryID(Integer deliveryID) {
        this.deliveryID = deliveryID;
    }
    
    public Integer getOrderID() {
        return orderID;
    }
    
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }
    
    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
    
    public String getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
} 