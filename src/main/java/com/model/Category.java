package com.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Category model class representing an IoT device category in the system
 * This is part of the Model in MVC architecture
 */
public class Category {
    // Fields corresponding to columns in the Category table
    private int categoryId;
    private String categoryName;
    private String description;
    private Timestamp createdAt;
    
    // Non-persisted fields for model relationships
    private List<Device> devices;
    
    /**
     * Default constructor
     */
    public Category() {
        this.devices = new ArrayList<>();
    }
    
    /**
     * Constructor with essential fields
     * @param categoryName Category name
     * @param description Category description
     */
    public Category(String categoryName, String description) {
        this();
        this.categoryName = categoryName;
        this.description = description;
    }
    
    /**
     * Full constructor with all fields
     * @param categoryId Category ID
     * @param categoryName Category name
     * @param description Category description
     * @param createdAt Timestamp when the category was created
     */
    public Category(int categoryId, String categoryName, String description, Timestamp createdAt) {
        this(categoryName, description);
        this.categoryId = categoryId;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Device> getDevices() {
        return devices;
    }
    
    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
    
    /**
     * Add a device to this category
     * @param device Device to add
     */
    public void addDevice(Device device) {
        this.devices.add(device);
        device.setCategory(this);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 