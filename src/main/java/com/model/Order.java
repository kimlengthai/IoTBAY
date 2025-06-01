package com.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Order model class representing an order in the system (corresponds to the Orders table)
 * This is part of the Model in MVC architecture
 */
public class Order {
    // Constants for order status
    public static final String STATUS_DRAFT = "Draft";       // Order is being created/edited
    public static final String STATUS_SAVED = "Saved";       // Order is saved but not submitted
    public static final String STATUS_SUBMITTED = "Submitted"; // Order is submitted for processing
    public static final String STATUS_PROCESSING = "Processing"; // Order is being processed
    public static final String STATUS_SHIPPED = "Shipped";   // Order has been shipped
    public static final String STATUS_DELIVERED = "Delivered"; // Order has been delivered
    public static final String STATUS_CANCELLED = "Cancelled"; // Order has been cancelled
    
    // Fields corresponding to columns in the Orders table
    private int orderId;
    private int userId;
    private Date orderDate;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String orderStatus;
    private Timestamp createdAt;
    
    // Non-persisted fields for model relationships
    private User user;
    private List<OrderItem> orderItems;
    
    /**
     * Default constructor
     */
    public Order() {
        this.orderItems = new ArrayList<>();
        this.orderDate = new Date(System.currentTimeMillis());
        this.totalAmount = BigDecimal.ZERO;
        this.orderStatus = STATUS_DRAFT;
    }
    
    /**
     * Constructor with essential fields
     * @param userId User's ID who placed the order
     * @param shippingAddress Shipping address for the order
     */
    public Order(int userId, String shippingAddress) {
        this();
        this.userId = userId;
        this.shippingAddress = shippingAddress;
    }
    
    /**
     * Full constructor with all fields
     * @param orderId Order ID
     * @param userId User ID
     * @param orderDate Date the order was placed
     * @param totalAmount Total amount of the order
     * @param shippingAddress Shipping address
     * @param orderStatus Status of the order
     * @param createdAt Timestamp when the order was created
     */
    public Order(int orderId, int userId, Date orderDate, BigDecimal totalAmount, 
                String shippingAddress, String orderStatus, Timestamp createdAt) {
        this();
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    /**
     * Calculate the total amount based on the order items
     */
    public void calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }
        this.totalAmount = total;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculateTotalAmount(); // Recalculate total amount when order items change
    }
    
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        calculateTotalAmount(); // Recalculate total amount
    }
    
    public void removeOrderItem(OrderItem item) {
        this.orderItems.remove(item);
        calculateTotalAmount(); // Recalculate total amount
    }
    
    /**
     * Check if the order can be updated or cancelled
     * @return true if the order is in a state that allows updates/cancellations
     */
    public boolean isEditable() {
        return STATUS_DRAFT.equals(orderStatus) || STATUS_SAVED.equals(orderStatus);
    }
    
    /**
     * Submit the order for processing
     * @return true if the order was successfully submitted
     */
    public boolean submit() {
        if (isEditable()) {
            this.orderStatus = STATUS_SUBMITTED;
            return true;
        }
        return false;
    }
    
    /**
     * Save the order without submitting
     */
    public void save() {
        if (STATUS_DRAFT.equals(orderStatus)) {
            this.orderStatus = STATUS_SAVED;
        }
    }
    
    /**
     * Cancel the order if it's editable
     * @return true if the order was successfully cancelled
     */
    public boolean cancel() {
        if (isEditable()) {
            this.orderStatus = STATUS_CANCELLED;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", itemCount=" + (orderItems != null ? orderItems.size() : 0) +
                '}';
    }
} 