package com.model;

import java.util.Date;

public class CartItemBean {
    private Integer cartItemID; // DE-5001: Integer(10)
    private Integer cartID;     // DE-5002: Integer(10)
    private Integer deviceID;   // DE-5003: Integer(10)
    private Integer quantity;   // DE-5004: Integer(5)
    private Date dateAdded;     // DE-5005: Datetime(20)
    
    public CartItemBean() {
        this.cartItemID = null;
        this.cartID = null;
        this.deviceID = null;
        this.quantity = 0;
        this.dateAdded = new Date();
    }
    
    public Integer getCartItemID() {
        return cartItemID;
    }
    
    public void setCartItemID(Integer cartItemID) {
        this.cartItemID = cartItemID;
    }
    
    public Integer getCartID() {
        return cartID;
    }
    
    public void setCartID(Integer cartID) {
        this.cartID = cartID;
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
    
    public Date getDateAdded() {
        return dateAdded;
    }
    
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
} 