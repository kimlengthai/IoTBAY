package com.dao;

import com.util.DatabaseConnection;
import com.model.Order;
import com.model.OrderItem;
import com.model.Device;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order entities
 * Provides CRUD operations for the Orders table
 */
public class OrderDAO {
    
    private final DeviceDAO deviceDAO;
    
    /**
     * Constructor
     */
    public OrderDAO() {
        this.deviceDAO = new DeviceDAO();
    }
    
    /**
     * Create a new order
     * @param order The order to create
     * @return The created order with ID
     * @throws SQLException If database operation fails
     */
    public Order createOrder(Order order) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        System.out.println("[OrderDAO DEBUG] Attempting to create order for UserID: " + order.getUserId() + ", Items: " + order.getOrderItems().size());
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insert order
            String sql = "INSERT INTO Orders (UserID, OrderDate, TotalAmount, ShippingAddress, OrderStatus) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUserId());
            stmt.setDate(2, order.getOrderDate());
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setString(4, order.getShippingAddress());
            stmt.setString(5, order.getOrderStatus());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("[OrderDAO DEBUG] Creating order failed, no rows affected during main order insert.");
                throw new SQLException("Creating order failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
                System.out.println("[OrderDAO DEBUG] Generated OrderID: " + order.getOrderId());
            } else {
                System.out.println("[OrderDAO DEBUG] Creating order failed, no ID obtained for order.");
                throw new SQLException("Creating order failed, no ID obtained.");
            }
            
            // 2. Insert order items
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                System.out.println("[OrderDAO DEBUG] Inserting " + order.getOrderItems().size() + " items for OrderID: " + order.getOrderId());
                insertOrderItems(conn, order);
            } else {
                System.out.println("[OrderDAO DEBUG] No order items to insert for OrderID: " + order.getOrderId());
            }
            
            conn.commit();
            System.out.println("[OrderDAO DEBUG] Order committed successfully. OrderID: " + order.getOrderId() + " for UserID: " + order.getUserId());
            return order;
            
        } catch (SQLException e) {
            System.out.println("[OrderDAO DEBUG] SQLException in createOrder for OrderID: " + order.getOrderId() + ", UserID: " + order.getUserId() + ". Error: " + e.getMessage());
            if (conn != null) {
                try {
                    System.out.println("[OrderDAO DEBUG] Rolling back transaction for OrderID: " + order.getOrderId());
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("[OrderDAO DEBUG] Error rolling back transaction for OrderID: " + order.getOrderId() + ". Error: " + ex.getMessage());
                    throw new SQLException("Error rolling back transaction", ex);
                }
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }
    
    /**
     * Get an order by ID
     * @param orderId The order ID
     * @return The order or null if not found
     * @throws SQLException If database operation fails
     */
    public Order getOrderById(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT * FROM Orders WHERE OrderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderItems(getOrderItems(conn, orderId));
                return order;
            }
            
            return null;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Get all orders
     * @return List of all orders
     * @throws SQLException If database operation fails
     */
    public List<Order> getAllOrders() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT * FROM Orders ORDER BY OrderDate DESC";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
            
            // Load order items for each order
            for (Order order : orders) {
                order.setOrderItems(getOrderItems(conn, order.getOrderId()));
            }
            
            return orders;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Get orders by user ID
     * @param userId The user ID
     * @return List of orders for the user
     * @throws SQLException If database operation fails
     */
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        System.out.println("[OrderDAO DEBUG] getOrdersByUserId called for UserID: " + userId);
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT * FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
            System.out.println("[OrderDAO DEBUG] Found " + orders.size() + " orders for UserID: " + userId);
            
            // Load order items for each order
            for (Order order : orders) {
                order.setOrderItems(getOrderItems(conn, order.getOrderId()));
            }
            
            return orders;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Update an order
     * @param order The order to update
     * @return Whether the update was successful
     * @throws SQLException If database operation fails
     */
    public boolean updateOrder(Order order) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Update order
            String sql = "UPDATE Orders SET UserID = ?, OrderDate = ?, TotalAmount = ?, ShippingAddress = ?, OrderStatus = ? WHERE OrderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, order.getUserId());
            stmt.setDate(2, order.getOrderDate());
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setString(4, order.getShippingAddress());
            stmt.setString(5, order.getOrderStatus());
            stmt.setInt(6, order.getOrderId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // 2. Delete existing order items
                deleteOrderItems(conn, order.getOrderId());
                
                // 3. Insert new order items
                if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                    insertOrderItems(conn, order);
                }
                
                conn.commit();
                return true;
            }
            
            conn.rollback();
            return false;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error rolling back transaction", ex);
                }
            }
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }
    
    /**
     * Delete an order
     * @param orderId The order ID
     * @return Whether the deletion was successful
     * @throws SQLException If database operation fails
     */
    public boolean deleteOrder(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Delete order items
            deleteOrderItems(conn, orderId);
            
            // 2. Delete order
            String sql = "DELETE FROM Orders WHERE OrderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                conn.commit();
                return true;
            }
            
            conn.rollback();
            return false;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error rolling back transaction", ex);
                }
            }
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }
    
    /**
     * Update order status
     * @param orderId The order ID
     * @param status The new status
     * @return Whether the update was successful
     * @throws SQLException If database operation fails
     */
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "UPDATE Orders SET OrderStatus = ? WHERE OrderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Search orders by date range and/or order ID
     * @param userId The user ID for filtering orders by user
     * @param orderId The order ID to search for (optional, pass 0 to ignore)
     * @param startDate The start date for the date range (optional, pass null to ignore)
     * @param endDate The end date for the date range (optional, pass null to ignore)
     * @return List of matching orders
     * @throws SQLException If database operation fails
     */
    public List<Order> searchOrders(int userId, int orderId, Date startDate, Date endDate) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM Orders WHERE UserID = ?");
            
            // Add optional filters
            if (orderId > 0) {
                sql.append(" AND OrderID = ?");
            }
            
            if (startDate != null) {
                sql.append(" AND OrderDate >= ?");
            }
            
            if (endDate != null) {
                sql.append(" AND OrderDate <= ?");
            }
            
            sql.append(" ORDER BY OrderDate DESC");
            
            stmt = conn.prepareStatement(sql.toString());
            
            // Set parameters
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            
            if (orderId > 0) {
                stmt.setInt(paramIndex++, orderId);
            }
            
            if (startDate != null) {
                stmt.setDate(paramIndex++, startDate);
            }
            
            if (endDate != null) {
                stmt.setDate(paramIndex++, endDate);
            }
            
            rs = stmt.executeQuery();
            
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
            
            // Load order items for each order
            for (Order order : orders) {
                order.setOrderItems(getOrderItems(conn, order.getOrderId()));
            }
            
            return orders;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Get unpaid orders by user ID
     * @param userId The user ID
     * @return List of unpaid orders for the user
     * @throws SQLException If database operation fails
     */
    public List<Order> getUnpaidOrdersByUserId(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Get orders that are in "Submitted" status (ready for payment)
            String sql = "SELECT * FROM Orders WHERE UserID = ? AND OrderStatus = ? ORDER BY OrderDate DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, Order.STATUS_SUBMITTED);
            
            rs = stmt.executeQuery();
            
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
            
            // Load order items for each order
            for (Order order : orders) {
                order.setOrderItems(getOrderItems(conn, order.getOrderId()));
            }
            
            return orders;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    // Helper methods
    
    private List<OrderItem> getOrderItems(Connection conn, int orderId) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM OrderItem WHERE OrderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            
            rs = stmt.executeQuery();
            
            List<OrderItem> items = new ArrayList<>();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("OrderItemID"));
                item.setOrderId(rs.getInt("OrderID"));
                item.setDeviceId(rs.getInt("DeviceID"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setPrice(rs.getBigDecimal("Price"));
                
                // Load the associated device
                Device device = deviceDAO.getDeviceById(item.getDeviceId());
                item.setDevice(device);
                
                items.add(item);
            }
            
            return items;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }
    
    /**
     * Insert order items for an order
     * @param conn The database connection
     * @param order The order containing items to insert
     * @throws SQLException If database operation fails
     */
    private void insertOrderItems(Connection conn, Order order) throws SQLException {
        PreparedStatement stmt = null;
        System.out.println("[OrderDAO DEBUG] insertOrderItems called for OrderID: " + order.getOrderId() + " with " + order.getOrderItems().size() + " items.");

        try {
            String sql = "INSERT INTO OrderItem (OrderID, DeviceID, Quantity, Price) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            
            for (OrderItem item : order.getOrderItems()) {
                System.out.println("[OrderDAO DEBUG] Adding item to batch: DeviceID " + item.getDeviceId() + ", Qty " + item.getQuantity() + " for OrderID: " + order.getOrderId());
                stmt.setInt(1, order.getOrderId());
                stmt.setInt(2, item.getDeviceId());
                stmt.setInt(3, item.getQuantity());
                stmt.setBigDecimal(4, item.getPrice());
                stmt.addBatch();
                
                // Set the order ID in the item
                item.setOrderId(order.getOrderId());
            }
            
            int[] batchResults = stmt.executeBatch();
            System.out.println("[OrderDAO DEBUG] insertOrderItems batch executed for OrderID: " + order.getOrderId() + ". Results count: " + batchResults.length);
            for (int i = 0; i < batchResults.length; i++) {
                if (batchResults[i] == Statement.EXECUTE_FAILED) {
                    System.out.println("[OrderDAO DEBUG] Batch insert failed for item " + i + " in OrderID: " + order.getOrderId());
                    throw new SQLException("Failed to insert one or more order items in the batch.");
                }
                System.out.println("[OrderDAO DEBUG] Batch result for item " + i + " in OrderID: " + order.getOrderId() + ": " + batchResults[i]);
            }
            System.out.println("[OrderDAO DEBUG] All items inserted successfully for OrderID: " + order.getOrderId());
            
        } catch (SQLException e) {
            System.out.println("[OrderDAO DEBUG] SQLException in insertOrderItems for OrderID: " + order.getOrderId() + ". Error: " + e.getMessage());
            throw e; // Re-throw the exception to ensure transaction rollback
        } finally {
            if (stmt != null) stmt.close();
        }
    }
    
    private void deleteOrderItems(Connection conn, int orderId) throws SQLException {
        PreparedStatement stmt = null;
        
        try {
            String sql = "DELETE FROM OrderItem WHERE OrderID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
        }
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("OrderID"));
        order.setUserId(rs.getInt("UserID"));
        order.setOrderDate(rs.getDate("OrderDate"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setShippingAddress(rs.getString("ShippingAddress"));
        order.setOrderStatus(rs.getString("OrderStatus"));
        return order;
    }
    
    /**
     * Clear all saved cart items for a user
     * @param userId The user ID
     * @throws SQLException If database operation fails
     */
    public void clearSavedCart(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "DELETE FROM SavedCartItems WHERE UserID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Save a cart item for a user
     * @param userId The user ID
     * @param item The cart item to save
     * @throws SQLException If database operation fails
     */
    public void saveCartItem(int userId, OrderItem item) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "INSERT INTO SavedCartItems (UserID, DeviceID, Quantity, Price) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, item.getDeviceId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getPrice());
            
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Get saved cart items for a user
     * @param userId The user ID
     * @return List of saved cart items
     * @throws SQLException If database operation fails
     */
    public List<OrderItem> getSavedCartItems(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT * FROM SavedCartItems WHERE UserID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            List<OrderItem> items = new ArrayList<>();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setDeviceId(rs.getInt("DeviceID"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setPrice(rs.getBigDecimal("Price"));
                
                // Load the device for this item
                Device device = deviceDAO.getDeviceById(item.getDeviceId());
                item.setDevice(device);
                
                items.add(item);
            }
            
            return items;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
} 