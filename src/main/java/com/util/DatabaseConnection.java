package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection utility class
 * This class provides methods to connect to and disconnect from the Java DB (Derby)
 */
public class DatabaseConnection {
    // Database connection information
    private static final String DB_URL = "jdbc:derby://localhost:1527/iotbay;create=true";
    private static final String DB_USERNAME = "iotbay";
    private static final String DB_PASSWORD = "iotbay";
    
    /**
     * Establishes a connection to the database
     * @return Connection object representing the connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the Derby JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            // Establish and return the connection
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Derby JDBC driver not found: " + e.getMessage());
            throw new SQLException("Derby JDBC driver not found", e);
        }
    }
    
    /**
     * Closes a database connection safely
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
} 