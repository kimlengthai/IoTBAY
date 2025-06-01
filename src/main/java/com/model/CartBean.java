package com.model;

import java.math.BigDecimal;
import java.util.List;

public class CartBean {
    private Integer cartID;  // DE-4001: Integer(10)
    private Integer userID;  // DE-4002: Integer(10)
    private BigDecimal cartTotalAmount;  // Total amount of all items in cart
    
    public CartBean() {
        this.cartID = null;
        this.userID = null;
        this.cartTotalAmount = BigDecimal.ZERO;
    }
    
    public Integer getCartID() {
        return cartID;
    }
    
    public void setCartID(Integer cartID) {
        this.cartID = cartID;
    }
    
    public Integer getUserID() {
        return userID;
    }
    
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
    
    public BigDecimal getCartTotalAmount() {
        return cartTotalAmount;
    }
    
    public void setCartTotalAmount(BigDecimal cartTotalAmount) {
        this.cartTotalAmount = cartTotalAmount;
    }
    
    /**
     * Calculate the total amount based on cart items
     * @param cartItems List of items in the cart
     */
    public void calculateTotalAmount(List<OrderItem> cartItems) {
        this.cartTotalAmount = BigDecimal.ZERO;
        if (cartItems != null) {
            for (OrderItem item : cartItems) {
                BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                this.cartTotalAmount = this.cartTotalAmount.add(itemTotal);
            }
        }
    }
} 