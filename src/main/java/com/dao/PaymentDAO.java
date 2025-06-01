package com.dao;

import com.model.PaymentBean;
import com.util.DatabaseConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    
    public PaymentDAO() {
        // No need to initialize connection here, we'll get it for each method
    }
    
    // Create a new payment
    public int createPayment(PaymentBean payment) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "INSERT INTO Payment (OrderID, PaymentMethod, PaymentStatus, PaymentDate, " +
                         "CreditCardNumber, CardHolderName, ExpiryDate, CVC, Amount) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, payment.getOrderID());
            pstmt.setString(2, payment.getPaymentMethod());
            pstmt.setString(3, payment.getPaymentStatus());
            pstmt.setTimestamp(4, new Timestamp(payment.getPaymentDate().getTime()));
            pstmt.setString(5, payment.getCreditCardNumber());
            pstmt.setString(6, payment.getCardHolderName());
            pstmt.setString(7, payment.getExpiryDate());
            pstmt.setString(8, payment.getCvc());
            pstmt.setBigDecimal(9, payment.getAmount());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating payment failed, no ID obtained.");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    // Get payment by ID
    public PaymentBean getPaymentById(int paymentId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT * FROM Payment WHERE PaymentID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, paymentId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }
            
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    // Get all payments for a user (via their orders)
    public List<PaymentBean> getPaymentsByUserId(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT p.* FROM Payment p " +
                         "JOIN Orders o ON p.OrderID = o.OrderID " +
                         "WHERE o.UserID = ? " +
                         "ORDER BY p.PaymentDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            List<PaymentBean> payments = new ArrayList<>();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            
            return payments;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    // Search payments by ID and date range for a specific user
    public List<PaymentBean> searchPayments(int userId, Integer paymentId, java.util.Date startDate, java.util.Date endDate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            StringBuilder sqlBuilder = new StringBuilder(
                "SELECT p.* FROM Payment p " +
                "JOIN Orders o ON p.OrderID = o.OrderID " +
                "WHERE o.UserID = ?");
            
            List<Object> params = new ArrayList<>();
            params.add(userId);
            
            if (paymentId != null) {
                sqlBuilder.append(" AND p.PaymentID = ?");
                params.add(paymentId);
            }
            
            if (startDate != null) {
                sqlBuilder.append(" AND p.PaymentDate >= ?");
                params.add(new Timestamp(startDate.getTime()));
            }
            
            if (endDate != null) {
                sqlBuilder.append(" AND p.PaymentDate <= ?");
                params.add(new Timestamp(endDate.getTime()));
            }
            
            sqlBuilder.append(" ORDER BY p.PaymentDate DESC");
            
            pstmt = conn.prepareStatement(sqlBuilder.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            rs = pstmt.executeQuery();
            
            List<PaymentBean> payments = new ArrayList<>();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            
            return payments;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
    
    // Get payments by order ID
    public List<PaymentBean> getPaymentsByOrderId(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT * FROM Payment WHERE OrderID = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            
            rs = pstmt.executeQuery();
            
            List<PaymentBean> payments = new ArrayList<>();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            
            return payments;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DatabaseConnection.closeConnection(conn);
        }
    }
     public void deletePaymentMethodById(int paymentID) throws SQLException {
        String sql = "DELETE FROM Payment WHERE paymentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentID);
            ps.executeUpdate();
        }
    }
     
      public void updatePaymentMethod(PaymentBean payment) throws SQLException {
        String sql = "UPDATE Payment "
                   + "SET cardHolderName=?, creditCardNumber=?, expiryDate=?, cvc=? "
                   + "WHERE paymentID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, payment.getCardHolderName());
            ps.setString(2, payment.getCreditCardNumber());
            ps.setString(3, payment.getExpiryDate());
            ps.setString(4, payment.getCvc());
            ps.setInt   (5, payment.getPaymentID());
            ps.executeUpdate();
        }
    }
    
    
    // Helper method to map ResultSet to PaymentBean
    private PaymentBean mapResultSetToPayment(ResultSet rs) throws SQLException {
        PaymentBean payment = new PaymentBean();
        payment.setPaymentID(rs.getInt("PaymentID"));
        payment.setOrderID(rs.getInt("OrderID"));
        payment.setPaymentMethod(rs.getString("PaymentMethod"));
        payment.setPaymentStatus(rs.getString("PaymentStatus"));
        payment.setPaymentDate(new Date(rs.getTimestamp("PaymentDate").getTime()));
        payment.setCreditCardNumber(rs.getString("CreditCardNumber"));
        payment.setCardHolderName(rs.getString("CardHolderName"));
        payment.setExpiryDate(rs.getString("ExpiryDate"));
        payment.setCvc(rs.getString("CVC"));
        payment.setAmount(rs.getBigDecimal("Amount"));
        return payment;
    }
} 
