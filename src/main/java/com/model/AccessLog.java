package com.model;

import java.sql.Timestamp;

/**
 * AccessLog model class representing a user access log in the system
 * This is part of the Model in MVC architecture
 */
public class AccessLog {
    private int logId;
    private int userId;
    private Timestamp loginTime;
    private Timestamp logoutTime;
    private User user; // Reference to the associated user (optional)
    
    /**
     * Default constructor
     */
    public AccessLog() {
    }
    
    /**
     * Constructor with essential fields
     * @param userId User's ID
     * @param loginTime Time when the user logged in
     */
    public AccessLog(int userId, Timestamp loginTime) {
        this.userId = userId;
        this.loginTime = loginTime;
    }
    
    /**
     * Constructor with all fields except logId and user
     * @param userId User's ID
     * @param loginTime Time when the user logged in
     * @param logoutTime Time when the user logged out
     */
    public AccessLog(int userId, Timestamp loginTime, Timestamp logoutTime) {
        this.userId = userId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }
    
    /**
     * Constructor with all fields except user
     * @param logId Access log's ID
     * @param userId User's ID
     * @param loginTime Time when the user logged in
     * @param logoutTime Time when the user logged out
     */
    public AccessLog(int logId, int userId, Timestamp loginTime, Timestamp logoutTime) {
        this.logId = logId;
        this.userId = userId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }
    
    /**
     * Full constructor with all fields
     * @param logId Access log's ID
     * @param userId User's ID
     * @param loginTime Time when the user logged in
     * @param logoutTime Time when the user logged out
     * @param user Reference to the associated user object
     */
    public AccessLog(int logId, int userId, Timestamp loginTime, Timestamp logoutTime, User user) {
        this.logId = logId;
        this.userId = userId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.user = user;
    }
    
    // Getters and Setters
    
    public int getLogId() {
        return logId;
    }
    
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Timestamp getLoginTime() {
        return loginTime;
    }
    
    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }
    
    public Timestamp getLogoutTime() {
        return logoutTime;
    }
    
    public void setLogoutTime(Timestamp logoutTime) {
        this.logoutTime = logoutTime;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Calculate the session duration in minutes
     * @return Duration in minutes, or -1 if logout time is not set
     */
    public long getSessionDurationMinutes() {
        if (loginTime != null && logoutTime != null) {
            long durationMillis = logoutTime.getTime() - loginTime.getTime();
            return durationMillis / (60 * 1000); // Convert milliseconds to minutes
        }
        return -1; // Session not completed
    }
    
    @Override
    public String toString() {
        return "AccessLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                ", userName='" + (user != null ? user.getName() : "null") + '\'' +
                '}';
    }
} 