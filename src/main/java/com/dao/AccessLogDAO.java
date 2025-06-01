package com.dao;

import com.model.AccessLog;
import com.model.User;
import com.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for AccessLog entity
 * This class handles all database operations related to the AccessLogs table
 */
public class AccessLogDAO {
    
    private final UserDAO userDAO;
    
    /**
     * Constructor that initializes the UserDAO for getting user details when needed
     */
    public AccessLogDAO() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Records a user login event
     * @param userId User's ID
     * @return The generated log ID if successful, -1 otherwise
     */
    public int recordLogin(int userId) {
        String sql = "INSERT INTO AccessLogs (UserID, LoginTime) VALUES (?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, userId);
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
            return -1;
        } catch (SQLException e) {
            System.err.println("Error recording login: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Records a user logout event
     * @param logId Log ID of the login event
     * @return true if the logout was recorded successfully, false otherwise
     */
    public boolean recordLogout(int logId) {
        String sql = "UPDATE AccessLogs SET LogoutTime = ? WHERE LogID = ? AND LogoutTime IS NULL";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setInt(2, logId);
            
            int affectedRows = statement.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error recording logout: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get an access log by its ID
     * @param logId The ID of the access log to retrieve
     * @return The AccessLog object if found, null otherwise
     */
    public AccessLog getAccessLogById(int logId) {
        String sql = "SELECT * FROM AccessLogs WHERE LogID = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, logId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAccessLog(resultSet, false);
                }
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting access log by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get all access logs for a specific user
     * @param userId The ID of the user
     * @return A list of access logs for the user
     */
    public List<AccessLog> getAccessLogsByUserId(int userId) {
        String sql = "SELECT * FROM AccessLogs WHERE UserID = ? ORDER BY LoginTime DESC";
        List<AccessLog> accessLogs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    accessLogs.add(mapResultSetToAccessLog(resultSet, false));
                }
            }
            
            return accessLogs;
        } catch (SQLException e) {
            System.err.println("Error getting access logs by user ID: " + e.getMessage());
            return accessLogs;
        }
    }
    
    /**
     * Get all access logs with optional user data
     * @param includeUserData Whether to include user data
     * @return A list of all access logs
     */
    public List<AccessLog> getAllAccessLogs(boolean includeUserData) {
        String sql = "SELECT * FROM AccessLogs ORDER BY LoginTime DESC";
        List<AccessLog> accessLogs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                accessLogs.add(mapResultSetToAccessLog(resultSet, includeUserData));
            }
            
            return accessLogs;
        } catch (SQLException e) {
            System.err.println("Error getting all access logs: " + e.getMessage());
            return accessLogs;
        }
    }
    
    /**
     * Search access logs by date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @param includeUserData Whether to include user data
     * @return A list of access logs within the date range
     */
    public List<AccessLog> searchAccessLogsByDateRange(Date startDate, Date endDate, boolean includeUserData) {
        String sql = "SELECT * FROM AccessLogs WHERE DATE(LoginTime) BETWEEN ? AND ? ORDER BY LoginTime DESC";
        List<AccessLog> accessLogs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    accessLogs.add(mapResultSetToAccessLog(resultSet, includeUserData));
                }
            }
            
            return accessLogs;
        } catch (SQLException e) {
            System.err.println("Error searching access logs by date range: " + e.getMessage());
            return accessLogs;
        }
    }
    
    /**
     * Delete an access log by its ID
     * @param logId The ID of the access log to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteAccessLog(int logId) {
        String sql = "DELETE FROM AccessLogs WHERE LogID = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, logId);
            
            int affectedRows = statement.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting access log: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper method to map a ResultSet to an AccessLog object
     * @param resultSet The ResultSet containing access log data
     * @param includeUserData Whether to include user data
     * @return An AccessLog object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private AccessLog mapResultSetToAccessLog(ResultSet resultSet, boolean includeUserData) throws SQLException {
        AccessLog accessLog = new AccessLog(
                resultSet.getInt("LogID"),
                resultSet.getInt("UserID"),
                resultSet.getTimestamp("LoginTime"),
                resultSet.getTimestamp("LogoutTime")
        );
        
        if (includeUserData) {
            User user = userDAO.getUserById(accessLog.getUserId());
            accessLog.setUser(user);
        }
        
        return accessLog;
    }
} 