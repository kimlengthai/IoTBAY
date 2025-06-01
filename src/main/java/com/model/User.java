package com.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * User model class representing a user in the system (corresponds to the Users table)
 * This is part of the Model in MVC architecture
 */
public class User {
    // Fields corresponding to columns in the Users table
    private int userId;
    private String name;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String address;
    private String phone;
    private String userType;
    private Timestamp createdAt;
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Constructor with essential fields for user registration
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param userType User's type (Customer or Staff)
     */
    public User(String name, String email, String password, String userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    
    /**
     * Constructor with all fields except userId and createdAt
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param dateOfBirth User's date of birth
     * @param address User's address
     * @param phone User's phone number
     * @param userType User's type (Customer or Staff)
     */
    public User(String name, String email, String password, Date dateOfBirth, String address, String phone, String userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.userType = userType;
    }
    
    /**
     * Constructor with all fields
     * @param userId User's ID
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param dateOfBirth User's date of birth
     * @param address User's address
     * @param phone User's phone number
     * @param userType User's type (Customer or Staff)
     * @param createdAt Timestamp when the user was created
     */
    public User(int userId, String name, String email, String password, Date dateOfBirth, String address, String phone, String userType, Timestamp createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Checks if the user is a staff member
     * @return true if the user is a staff member, false otherwise
     */
    public boolean isStaff() {
        return "Staff".equals(userType);
    }
    
    /**
     * Checks if the user is a customer
     * @return true if the user is a customer, false otherwise
     */
    public boolean isCustomer() {
        return "Customer".equals(userType);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", userType='" + userType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 
