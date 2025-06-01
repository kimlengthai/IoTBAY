package com.model;

import java.math.BigDecimal;

/**
 * OrderItem model class representing an item in an order (corresponds to the OrderItem table)
 * This is part of the Model in MVC architecture
 */
public class OrderItem {
    // Fields corresponding to columns in the OrderItem table
    private int orderItemId;
    private int orderId;
    private int deviceId;
    private int quantity;
    private BigDecimal price;
    
    // Non-persisted fields for model relationships
    private Device device;
    
    /**
     * Default constructor
     */
    public OrderItem() {
        this.quantity = 1;
        this.price = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with essential fields for creating a new order item
     * @param deviceId The device ID
     * @param quantity The quantity of the device
     * @param price The price of the device
     */
    public OrderItem(int deviceId, int quantity, BigDecimal price) {
        this();
        this.deviceId = deviceId;
        this.quantity = quantity;
        this.price = price;
    }
    
    /**
     * Full constructor with all fields
     * @param orderItemId The order item ID
     * @param orderId The order ID
     * @param deviceId The device ID
     * @param quantity The quantity of the device
     * @param price The price of the device
     */
    public OrderItem(int orderItemId, int orderId, int deviceId, int quantity, BigDecimal price) {
        this(deviceId, quantity, price);
        this.orderItemId = orderItemId;
        this.orderId = orderId;
    }
    
    // Getters and Setters
    
    public int getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
    
    /**
     * Calculate the total price for this order item (price * quantity)
     * @return The total price
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", deviceId=" + deviceId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
} 