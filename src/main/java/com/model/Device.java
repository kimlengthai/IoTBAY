package com.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Device model class representing an IoT device in the system (corresponds to the Device table)
 * This is part of the Model in MVC architecture
 */
public class Device {
    // Fields corresponding to columns in the Device table
    private int deviceId;
    private int categoryId;
    private String deviceName;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private Timestamp createdAt;
    private boolean isActive = true; // Default to true for new devices

    // Non-persisted fields for model relationships
    private Category category;

    /**
     * Default constructor
     */
    public Device() {
        this.price = BigDecimal.ZERO;
        this.stockQuantity = 0;
    }

    /**
     * Constructor for creating a new device (no ID yet)
     */
    public Device(int categoryId, String deviceName, String description, BigDecimal price, int stockQuantity) {
        this(); // call default constructor
        this.categoryId = categoryId;
        this.deviceName = deviceName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Constructor for editing/updating an existing device (includes ID and category)
     */
    public Device(int deviceId, int categoryId, String deviceName, String description, BigDecimal price, int stockQuantity) {
        this(categoryId, deviceName, description, price, stockQuantity);
        this.deviceId = deviceId;
    }

    /**
     * Full constructor with all fields (used when reading from DB)
     */
    public Device(int deviceId, int categoryId, String deviceName, String description,
                  BigDecimal price, int stockQuantity, Timestamp createdAt) {
        this(deviceId, categoryId, deviceName, description, price, stockQuantity);
        this.createdAt = createdAt;
    }

    // === Getters and Setters ===

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Check if the device is in stock
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Check if a given quantity is available
     */
    public boolean isQuantityAvailable(int quantity) {
        return stockQuantity >= quantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId=" + deviceId +
                ", categoryId=" + categoryId +
                ", deviceName='" + deviceName + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
