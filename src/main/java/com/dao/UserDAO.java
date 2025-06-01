package com.dao;

import com.model.User;
import com.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for User entity
 * This class handles all database operations related to the Users table
 */
public class UserDAO {
    
    /**
     * Insert a new user into the database
     * @param user The user to insert
     * @return The generated user ID if successful, -1 otherwise
     */
    public int createUser(User user) {
        String sql = "INSERT INTO Users (Name, Email, Password, DateOfBirth, Address, Phone, UserType) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnection.getConnection();
            // Explicitly disable auto-commit to manage transaction
            connection.setAutoCommit(false);
            
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setDate(4, user.getDateOfBirth());
            statement.setString(5, user.getAddress());
            statement.setString(6, user.getPhone());
            statement.setString(7, user.getUserType());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        
                        // Explicitly commit the transaction
                        connection.commit();
                        return userId;
                    }
                }
            }
            
            // If we get here, something went wrong but no exception was thrown
            connection.rollback();
            return -1;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            
            // Rollback the transaction on error
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            
            return -1;
        } finally {
            // Clean up resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
            
            if (connection != null) {
                try {
                    // Reset auto-commit mode
                    connection.setAutoCommit(true);
                    // Don't close connection here, let DatabaseConnection handle it
                    DatabaseConnection.closeConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Get a user by their ID
     * @param userId The ID of the user to retrieve
     * @return The User object if found, null otherwise
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get a user by their email
     * @param email The email of the user to retrieve
     * @return The User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Authenticate a user by their email and password
     * @param email The user's email
     * @param password The user's password
     * @return The User object if authentication is successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update an existing user in the database
     * @param user The user to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET Name = ?, Email = ?, Password = ?, DateOfBirth = ?, Address = ?, Phone = ?, UserType = ? WHERE UserID = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setDate(4, user.getDateOfBirth());
            statement.setString(5, user.getAddress());
            statement.setString(6, user.getPhone());
            statement.setString(7, user.getUserType());
            statement.setInt(8, user.getUserId());
            
            int affectedRows = statement.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a user from the database
     * @param userId The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        // First, delete related records in AccessLogs table
        String deleteAccessLogsSql = "DELETE FROM AccessLogs WHERE UserID = ?";
        
        // Then delete the user
        String deleteUserSql = "DELETE FROM Users WHERE UserID = ?";
        
        Connection connection = null;
        
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);
            
            // First delete access logs
            try (PreparedStatement statement = connection.prepareStatement(deleteAccessLogsSql)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            }
            
            // Then delete user
            try (PreparedStatement statement = connection.prepareStatement(deleteUserSql)) {
                statement.setInt(1, userId);
                int affectedRows = statement.executeUpdate();
                
                if (affectedRows > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    DatabaseConnection.closeConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Get all users from the database
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM Users";
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            
            return users;
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            return users;
        }
    }
    
    /**
     * Get users of a specific type (Customer or Staff)
     * @param userType The type of users to retrieve
     * @return A list of users of the specified type
     */
    public List<User> getUsersByType(String userType) {
        String sql = "SELECT * FROM Users WHERE UserType = ?";
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, userType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
            
            return users;
        } catch (SQLException e) {
            System.err.println("Error getting users by type: " + e.getMessage());
            return users;
        }
    }
    
    /**
     * Helper method to map a ResultSet to a User object
     * @param resultSet The ResultSet containing user data
     * @return A User object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("UserID"),
                resultSet.getString("Name"),
                resultSet.getString("Email"),
                resultSet.getString("Password"),
                resultSet.getDate("DateOfBirth"),
                resultSet.getString("Address"),
                resultSet.getString("Phone"),
                resultSet.getString("UserType"),
                resultSet.getTimestamp("CreatedAt")
        );
    }
} 