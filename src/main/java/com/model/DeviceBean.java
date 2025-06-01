package com.model;

import java.math.BigDecimal;

public class DeviceBean {
    private Integer deviceID;      // DE-3001: Integer(10)
    private Integer categoryID;    // DE-3003: Integer(10)
    private String deviceName;     // DE-3002: Varchar(100)
    private String description;    // DE-3003: Text(500)
    private BigDecimal price;      // DE-3004: Decimal(10,2)
    private Integer stockQuantity; // DE-3005: Integer(5)
    
    public DeviceBean() {
        this.deviceID = null;
        this.categoryID = null;
        this.deviceName = "";
        this.description = "";
        this.price = BigDecimal.ZERO;
        this.stockQuantity = 0;
    }
    
    public Integer getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(Integer deviceID) {
        this.deviceID = deviceID;
    }
    
    public Integer getCategoryID() {
        return categoryID;
    }
    
    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
} 