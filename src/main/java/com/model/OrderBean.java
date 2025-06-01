package com.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderBean {
    private Integer orderID;         // DE-6001: Integer(10)
    private Integer userID;          // DE-6002: Integer(10)
    private Date orderDate;          // DE-6003: Date(10)
    private String shippingAddress;  // DE-6005: Text(255)
    private String orderStatus;      // DE-6006: Varchar(20)
    private List<OrderItem> orderItems; // List of items in the order

    
    public OrderBean() {
        this.orderID = null;
        this.userID = null;
        this.orderDate = new Date();
        this.shippingAddress = "";
        this.orderStatus = "";
        this.orderItems = null;
        this.shippingAddress = "";
        this.orderStatus = "";
    }
    
    public Integer getOrderID() {
        return orderID;
    }
    
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    
    public Integer getUserID() {
        return userID;
    }
    
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    /**
     * Calculate the total amount based on order items
     * @return The total amount of the order
     */
    public BigDecimal calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }
} 