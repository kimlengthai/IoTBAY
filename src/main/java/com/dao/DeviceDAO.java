package com.dao;

// Import required classes for DB and model interaction
import com.util.DatabaseConnection;
import com.model.Device;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DeviceDAO
 * --------------------------
 * This class is responsible for interacting with the `Device` table in the database.
 * It implements core CRUD operations (Create, Read, Update, Delete) as well as search and stock updates.
 * 
 * This class acts as the "Data Access Layer" and is called by the Controller.
 */
public class DeviceDAO {

    // Constructor – no setup is needed for this DAO
    public DeviceDAO() {}

    /**
     * Fetch a single device from the database using its primary key (DeviceID).
     *
     * @param deviceId the ID of the device to retrieve
     * @return the Device object if found, null otherwise
     */
    public Device getDeviceById(int deviceId) throws SQLException {
        // SQL statement to select a device by its ID
        String sql = "SELECT * FROM Device WHERE DeviceID = ?";

        // Try-with-resources ensures proper closing of DB resources
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deviceId);  // Set the first parameter (DeviceID)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDevice(rs); // Map DB row to Device object
                }
                return null; // Device not found
            }
        }
    }

    /**
     * Retrieve all devices from the database.
     *
     * @return a list of Device objects
     */
    public List<Device> getAllDevices() throws SQLException {
        String sql = "SELECT * FROM Device WHERE isActive = TRUE"; // Select all columns from Device table

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Device> devices = new ArrayList<>();
            while (rs.next()) {
                devices.add(mapResultSetToDevice(rs)); // Convert each row into Device object
            }
            return devices;
        }
    }

    /**
     * Retrieve all devices that belong to a specific category.
     * Useful for filtering devices by their assigned CategoryID.
     *
     * @param categoryId the foreign key to filter devices
     * @return list of matching Device objects
     */
    public List<Device> getDevicesByCategory(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Device WHERE CategoryID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId); // Bind the categoryId value

            try (ResultSet rs = stmt.executeQuery()) {
                List<Device> devices = new ArrayList<>();
                while (rs.next()) {
                    devices.add(mapResultSetToDevice(rs));
                }
                return devices;
            }
        }
    }

    /**
     * Search for devices using a keyword that matches the name or description.
     * The search is case-insensitive and uses SQL LIKE with wildcards.
     *
     * @param searchTerm the search keyword from the user
     * @return list of devices that match the search criteria
     */
  public List<Device> searchDevices(String searchTerm, Integer categoryId) throws SQLException {
    StringBuilder sql = new StringBuilder("SELECT * FROM Device WHERE isActive = TRUE");
    List<Object> params = new ArrayList<>();

    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        sql.append(" AND (LOWER(DeviceName) LIKE ? OR LOWER(Description) LIKE ?)");
        String wildcard = "%" + searchTerm.toLowerCase() + "%";
        params.add(wildcard);
        params.add(wildcard);
    }

    if (categoryId != null) {
        sql.append(" AND CategoryID = ?");
        params.add(categoryId);
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

        // Bind parameters dynamically
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = stmt.executeQuery()) {
            List<Device> devices = new ArrayList<>();
            while (rs.next()) {
                devices.add(mapResultSetToDevice(rs));
            }
            return devices;
        }
    }
}


    /**
     * Insert a new device record into the database.
     * After execution, the generated primary key (DeviceID) is retrieved and stored in the object.
     *
     * @param device the Device object to be created
     * @return the same device object with an assigned ID
     */
    public Device createDevice(Device device) throws SQLException {
        String sql = "INSERT INTO Device (CategoryID, DeviceName, Description, Price, StockQuantity, isActive) VALUES (?, ?, ?, ?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS allows us to get the new primary key
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Bind device properties to the SQL statement
            stmt.setInt(1, device.getCategoryId());
            stmt.setString(2, device.getDeviceName());
            stmt.setString(3, device.getDescription());
            stmt.setBigDecimal(4, device.getPrice());
            stmt.setInt(5, device.getStockQuantity());
            stmt.setBoolean(6, device.isActive());

            // Execute INSERT
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating device failed, no rows affected.");
            }

            // Get generated key (DeviceID)
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    device.setDeviceId(rs.getInt(1)); // Store it in the device object
                } else {
                    throw new SQLException("Creating device failed, no ID obtained.");
                }
            }

            return device; // Return device object
        }
    }

    /**
     * Update an existing device's data in the database.
     *
     * @param device the device with updated data
     * @return true if the update succeeded, false otherwise
     */
    public boolean updateDevice(Device device) throws SQLException {
        String sql = "UPDATE Device SET CategoryID = ?, DeviceName = ?, Description = ?, Price = ?, StockQuantity = ?, isActive = ? WHERE DeviceID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind new values
            stmt.setInt(1, device.getCategoryId());
            stmt.setString(2, device.getDeviceName());
            stmt.setString(3, device.getDescription());
            stmt.setBigDecimal(4, device.getPrice());
            stmt.setInt(5, device.getStockQuantity());
            stmt.setBoolean(6, device.isActive());
            stmt.setInt(7, device.getDeviceId()); // WHERE clause condition

            return stmt.executeUpdate() > 0; // Returns true if at least one row was affected
        }
    }

    /**
     * Delete a device from the database using its ID.
     *
     * @param deviceId the ID of the device to delete
     * @return true if deletion was successful
     */
     public boolean deleteDevice(int deviceId) throws SQLException {
    String sql = "UPDATE Device SET IsActive = FALSE WHERE DeviceID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, deviceId);
        return stmt.executeUpdate() > 0;
    }
}

    /**
     * Update only the stock quantity of a device.
     * Used after purchases, restocking, etc.
     *
     * @param deviceId the ID of the device
     * @param quantity the new stock quantity
     * @return true if update was successful
     */
   
     public boolean updateStockQuantity(int deviceId, int quantity) throws SQLException {
        String sql = "UPDATE Device SET StockQuantity = ? WHERE DeviceID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);   // New quantity
            stmt.setInt(2, deviceId);   // Target device ID

            return stmt.executeUpdate() > 0; // True if stock was updated
        }
    }
    /**
     * Internal utility method to convert a database row (ResultSet) into a Device object.
     * Ensures the DAO remains decoupled from how SQL columns map to Java fields.
     *
     * @param rs the ResultSet pointing to the current row
     * @return a fully constructed Device object
     */
    private Device mapResultSetToDevice(ResultSet rs) throws SQLException {
        Device device = new Device();

        // Map each DB column to a Device field
        device.setDeviceId(rs.getInt("DeviceID"));
        device.setCategoryId(rs.getInt("CategoryID"));
        device.setDeviceName(rs.getString("DeviceName"));
        device.setDescription(rs.getString("Description"));
        device.setPrice(rs.getBigDecimal("Price"));
        device.setStockQuantity(rs.getInt("StockQuantity"));
        device.setActive(rs.getBoolean("isActive"));

        // Optional: track creation date (if present in schema)
        device.setCreatedAt(rs.getTimestamp("CreatedAt"));

        return device;
    }
}
