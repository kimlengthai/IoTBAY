package com.model;

import java.util.Date;
import java.math.BigDecimal;

public class PaymentBean {
    private Integer paymentID;     // DE-8001: Integer(10)
    private Integer orderID;       // DE-8002: Integer(10)
    private String paymentMethod;  // DE-8003: Varchar(50)
    private String paymentStatus;  // DE-8004: Varchar(20)
    private Date paymentDate;      // DE-8005: Datetime(20)
    private String creditCardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvc;
    private BigDecimal amount;
    
    public PaymentBean() {
        this.paymentID = null;
        this.orderID = null;
        this.paymentMethod = "";
        this.paymentStatus = "";
        this.paymentDate = new Date();
        this.creditCardNumber = "";
        this.cardHolderName = "";
        this.expiryDate = "";
        this.cvc = "";
        this.amount = new BigDecimal("0.00");
    }
    
    public Integer getPaymentID() {
        return paymentID;
    }
    
    public void setPaymentID(Integer paymentID) {
        this.paymentID = paymentID;
    }
    
    public Integer getOrderID() {
        return orderID;
    }
    
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    
    public String getCardHolderName() {
        return cardHolderName;
    }
    
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getCvc() {
        return cvc;
    }
    
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
} 