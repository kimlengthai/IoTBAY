package com.model;

import java.util.Date;

public class OrderManagementBean {
    private Integer orderManagementID; // DE-9001: Integer(10)
    private Integer orderID;           // DE-9002: Integer(10)
    private Integer staffID;           // DE-9003: Integer(10)
    private Date updateTimestamp;      // DE-9004: Datetime(20)
    private String trackingNumber;     // DE-9005: Varchar(50)
    
    public OrderManagementBean() {
        this.orderManagementID = null;
        this.orderID = null;
        this.staffID = null;
        this.updateTimestamp = new Date();
        this.trackingNumber = "";
    }
    
    public Integer getOrderManagementID() {
        return orderManagementID;
    }
    
    public void setOrderManagementID(Integer orderManagementID) {
        this.orderManagementID = orderManagementID;
    }
    
    public Integer getOrderID() {
        return orderID;
    }
    
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    
    public Integer getStaffID() {
        return staffID;
    }
    
    public void setStaffID(Integer staffID) {
        this.staffID = staffID;
    }
    
    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }
    
    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
} 