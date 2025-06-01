<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.model.Order" %>
<%@ page import="com.model.OrderItem" %>
<%@ page import="com.model.User" %>
<%@ page import="com.model.Device" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Details - IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h1, h2 {
            color: #333;
            margin-bottom: 20px;
        }
        .order-details {
            margin-bottom: 30px;
            display: flex;
            flex-wrap: wrap;
        }
        .detail-column {
            flex: 1;
            min-width: 250px;
            margin-bottom: 20px;
        }
        .detail-item {
            margin-bottom: 15px;
        }
        .detail-label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
            color: #666;
        }
        .detail-value {
            font-size: 16px;
        }
        .table-container {
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f9f9f9;
        }
        .total-row {
            font-weight: bold;
            background-color: #f9f9f9;
        }
        .btn {
            display: inline-block;
            padding: 8px 12px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-right: 5px;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-back {
            background-color: #607D8B;
        }
        .btn-edit {
            background-color: #FFC107;
        }
        .btn-delete {
            background-color: #F44336;
        }
        .btn-status {
            background-color: #2196F3;
        }
        .btn-pay {
            background-color: #4CAF50;
            margin-left: 10px;
        }
        .status {
            display: inline-block;
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: bold;
            text-transform: uppercase;
        }
        .status-pending {
            background-color: #FFF9C4;
            color: #FFA000;
        }
        .status-draft {
            background-color: #E0E0E0;
            color: #616161;
        }
        .status-saved {
            background-color: #E1F5FE;
            color: #0288D1;
        }
        .status-submitted {
            background-color: #FFF9C4;
            color: #FFA000;
        }
        .status-processing {
            background-color: #C8E6C9;
            color: #388E3C;
        }
        .status-shipped {
            background-color: #BBDEFB;
            color: #1976D2;
        }
        .status-delivered {
            background-color: #DCEDC8;
            color: #33691E;
        }
        .status-cancelled {
            background-color: #FFCDD2;
            color: #D32F2F;
        }
        .navbar {
            background-color: #333;
            overflow: hidden;
            margin-bottom: 20px;
        }
        .navbar a {
            float: left;
            display: block;
            color: white;
            text-align: center;
            padding: 14px 16px;
            text-decoration: none;
        }
        .navbar a:hover {
            background-color: #ddd;
            color: black;
        }
        .navbar .right {
            float: right;
        }
        .action-buttons {
            margin: 20px 0;
        }
        .error-message {
            background-color: #FFEBEE;
            color: #D32F2F;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .status-form {
            margin-top: 20px;
            padding: 15px;
            background-color: #f9f9f9;
            border-radius: 4px;
        }
        .status-form select {
            padding: 8px;
            margin-right: 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="<%= request.getContextPath() %>/devices">Devices</a>
        <a href="<%= request.getContextPath() %>/orders/user">My Orders</a>
        <a href="<%= request.getContextPath() %>/orders/cart">Cart</a>
        <a href="<%= request.getContextPath() %>/payments/">Payments</a>
        <div class="right">
            <%-- <a href="<%= request.getContextPath() %>/profile">Profile</a> --%>
            <%-- <a href="<%= request.getContextPath() %>/logout">Logout</a> --%>
        </div>
    </div>

    <div class="container">
        <% 
        Order order = (Order) request.getAttribute("order");
        String errorMessage = (String) request.getAttribute("errorMessage");
        User user = (User) session.getAttribute("user");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat priceFormat = new DecimalFormat("$#,##0.00");
        
        if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
        <div class="error-message">
            <%= errorMessage %>
        </div>
        <% } %>
        
        <h1>Order Details</h1>
        
        <% if (order.getOrderStatus().equals(Order.STATUS_SUBMITTED)) { %>
        <div class="notification" style="background-color: #fff3cd; border: 1px solid #ffeeba; color: #856404; padding: 15px; margin-bottom: 20px; border-radius: 4px;">
            <strong>Action Required:</strong> Your order has been submitted, but payment is still needed. Please click the "Pay for this Order" button to complete your purchase.
        </div>
        <% } %>
        
        <div class="action-buttons">
            <a href="<%= request.getContextPath() %>/orders/user" class="btn btn-back">Back to Orders</a>
            <% if (order.isEditable()) { %>
            <a href="<%= request.getContextPath() %>/orders/edit/<%= order.getOrderId() %>" class="btn btn-edit">Edit Order</a>
            <% } %>
            
            <% if (order.getOrderStatus().equals(Order.STATUS_SUBMITTED)) { %>
            <a href="<%= request.getContextPath() %>/payments/order/<%= order.getOrderId() %>" class="btn btn-pay" style="background-color: #4CAF50; margin-left: 10px;">Pay for this Order</a>
            <% } %>
            
            <% if (user != null && user.isStaff()) { %>
            <a href="<%= request.getContextPath() %>/orders" class="btn btn-back">All Orders</a>
            <% } %>
            
            <% if ((user != null && user.isStaff()) || 
                  (order.isEditable() && user != null && user.getUserId() == order.getUserId())) { %>
            <form method="post" action="<%= request.getContextPath() %>/orders" style="display:inline;">
                <input type="hidden" name="action" value="delete_order">
                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                <button type="submit" class="btn btn-delete">Delete Order</button>
            </form>
            <% } %>
        </div>
        
        <div class="order-details">
            <div class="detail-column">
                <div class="detail-item">
                    <span class="detail-label">Order ID</span>
                    <span class="detail-value">#<%= order.getOrderId() %></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Order Date</span>
                    <span class="detail-value"><%= dateFormat.format(order.getOrderDate()) %></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Total Amount</span>
                    <span class="detail-value"><%= priceFormat.format(order.getTotalAmount()) %></span>
                </div>
            </div>
            
            <div class="detail-column">
                <div class="detail-item">
                    <span class="detail-label">Status</span>
                    <span class="detail-value">
                        <span class="status status-<%= order.getOrderStatus().toLowerCase() %>">
                            <%= order.getOrderStatus() %>
                        </span>
                    </span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Shipping Address</span>
                    <span class="detail-value"><%= order.getShippingAddress() %></span>
                </div>
            </div>
        </div>
        
        <% if (user != null && user.isStaff()) { %>
        <div class="status-form">
            <form method="post" action="<%= request.getContextPath() %>/orders">
                <input type="hidden" name="action" value="update_status">
                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                <label for="status"><strong>Update Order Status:</strong></label>
                <select name="status" id="status">
                    <option value="<%= Order.STATUS_DRAFT %>" <%= order.getOrderStatus().equals(Order.STATUS_DRAFT) ? "selected" : "" %>>Draft</option>
                    <option value="<%= Order.STATUS_SAVED %>" <%= order.getOrderStatus().equals(Order.STATUS_SAVED) ? "selected" : "" %>>Saved</option>
                    <option value="<%= Order.STATUS_SUBMITTED %>" <%= order.getOrderStatus().equals(Order.STATUS_SUBMITTED) ? "selected" : "" %>>Submitted</option>
                    <option value="<%= Order.STATUS_PROCESSING %>" <%= order.getOrderStatus().equals(Order.STATUS_PROCESSING) ? "selected" : "" %>>Processing</option>
                    <option value="<%= Order.STATUS_SHIPPED %>" <%= order.getOrderStatus().equals(Order.STATUS_SHIPPED) ? "selected" : "" %>>Shipped</option>
                    <option value="<%= Order.STATUS_DELIVERED %>" <%= order.getOrderStatus().equals(Order.STATUS_DELIVERED) ? "selected" : "" %>>Delivered</option>
                    <option value="<%= Order.STATUS_CANCELLED %>" <%= order.getOrderStatus().equals(Order.STATUS_CANCELLED) ? "selected" : "" %>>Cancelled</option>
                </select>
                <button type="submit" class="btn btn-status">Update Status</button>
            </form>
        </div>
        <% } %>
        
        <h2>Order Items</h2>
        
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Device</th>
                        <th>Unit Price</th>
                        <th>Quantity</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    for (OrderItem item : order.getOrderItems()) {
                        Device device = item.getDevice();
                    %>
                    <tr>
                        <td>
                            <% if (device != null) { %>
                            <%= device.getDeviceName() %>
                            <% } else { %>
                            Unknown Device (ID: <%= item.getDeviceId() %>)
                            <% } %>
                        </td>
                        <td><%= priceFormat.format(item.getPrice()) %></td>
                        <td><%= item.getQuantity() %></td>
                        <td><%= priceFormat.format(item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity()))) %></td>
                    </tr>
                    <% } %>
                    <tr class="total-row">
                        <td colspan="3" style="text-align: right;">Total:</td>
                        <td><%= priceFormat.format(order.getTotalAmount()) %></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html> 