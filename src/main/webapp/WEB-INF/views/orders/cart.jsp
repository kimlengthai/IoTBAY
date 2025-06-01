<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.model.OrderItem" %>
<%@ page import="com.model.Device" %>
<%@ page import="com.model.User" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.model.CartBean" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart - IoTBay</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-top: 20px;
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
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
        .btn-update {
            background-color: #2196F3;
        }
        .btn-delete {
            background-color: #F44336;
        }
        .btn-checkout {
            background-color: #FF9800;
            font-weight: bold;
            padding: 10px 20px;
        }
        .navbar {
            background-color: #333 !important;
            padding: 15px 0;
        }
        .navbar a {
            color: white !important;
        }
        .empty-message {
            padding: 20px;
            text-align: center;
            color: #666;
            background-color: #f9f9f9;
            border-radius: 4px;
        }
        .quantity-input {
            width: 60px;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-align: center;
        }
        .error-message {
            background-color: #FFEBEE;
            color: #D32F2F;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .action-buttons {
            margin: 20px 0;
        }
        .checkout-form {
            margin-top: 20px;
            background-color: #f9f9f9;
            padding: 20px;
            border-radius: 4px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            min-height: 80px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <div class="container-fluid">
            <span class="navbar-brand mx-auto">
                <a href="<%= request.getContextPath() %>/user/" class="text-white text-decoration-none">Home</a>
            </span>
        </div>
    </nav>

    <div class="container mt-5">
        <h1>Shopping Cart</h1>
        
        <% 
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
        <div class="error-message">
            <%= errorMessage %>
        </div>
        <% } %>
        
        <% 
        @SuppressWarnings("unchecked")
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        CartBean cartBean = (CartBean) session.getAttribute("cartBean");
        DecimalFormat priceFormat = new DecimalFormat("$#,##0.00");
        User user = (User) session.getAttribute("user");
        Boolean isAnonymousObj = (Boolean) request.getAttribute("isAnonymous");
        boolean isAnonymous = (isAnonymousObj != null && isAnonymousObj.booleanValue());
        %>
        
        <% if (user != null) { %>
        <div class="action-buttons">
            <form method="post" action="<%= request.getContextPath() %>/orders" style="display:inline;">
                <input type="hidden" name="action" value="save_cart">
                <button type="submit" class="btn" style="background-color: #009688;">Save Cart</button>
            </form>
        </div>
        <% } %>
        
        <% 
        if (cart == null || cart.isEmpty()) {
        %>
        <div class="empty-message">
            <p>Your cart is empty.</p>
            <% if (user != null) { %>
            <p>If you previously saved items, they will be loaded next time you log in.</p>
            <% } %>
        </div>
        <% } else { %>
        <form method="post" action="<%= request.getContextPath() %>/orders">
            <input type="hidden" name="action" value="update_cart">
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Device</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        for (OrderItem item : cart) {
                            Device device = item.getDevice();
                            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
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
                            <td>
                                <input type="hidden" name="deviceId" value="<%= item.getDeviceId() %>">
                                <input type="text" name="quantity" value="<%= item.getQuantity() %>" class="quantity-input">
                            </td>
                            <td><%= priceFormat.format(subtotal) %></td>
                            <td>
                                <a href="javascript:void(0)" onclick="document.getElementById('remove_<%= item.getDeviceId() %>').submit();" class="btn btn-delete">Remove</a>
                            </td>
                        </tr>
                        <% } %>
                        <tr class="total-row">
                            <td colspan="3" style="text-align: right;">Total:</td>
                            <td colspan="2"><%= priceFormat.format(cartBean.getCartTotalAmount()) %></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="action-buttons">
                <button type="submit" class="btn btn-update">Update Cart</button>
            </div>
        </form>
        
        <div class="action-buttons">
            <form method="post" action="<%= request.getContextPath() %>/orders" style="display:inline;">
                <input type="hidden" name="action" value="clear_cart">
                <button type="submit" class="btn btn-delete">Clear Cart</button>
            </form>
        </div>
        
        <% 
        if (user != null) { 
        %>
        <div class="checkout-form">
            <h2>Finalise Order</h2>
            <form method="post" action="<%= request.getContextPath() %>/orders">
                <input type="hidden" name="action" value="create_order">
                
                <div class="form-group">
                    <label for="shippingAddress">Shipping Address:</label>
                    <textarea id="shippingAddress" name="shippingAddress" class="form-control" required><%= (user.getAddress() != null) ? user.getAddress() : "" %></textarea>
                </div>
                
                <button type="submit" class="btn btn-checkout" style="background-color: #4CAF50;">Finalise Order</button>
                <p style="margin-top: 15px;">After finalising your order, you can add payment details from your dashboard.</p>
            </form>
        </div>
        <% } else if (isAnonymous) { %>
        <div class="checkout-form">
            <h2>Guest Checkout</h2>
            <form method="post" action="<%= request.getContextPath() %>/orders">
                <input type="hidden" name="action" value="create_order">
                
                <div class="form-group">
                    <label for="name">Your Name:</label>
                    <input type="text" id="name" name="name" class="form-control" required>
                </div>
                
                <div class="form-group">
                    <label for="email">Email Address:</label>
                    <input type="email" id="email" name="email" class="form-control" required>
                </div>

                <div class="form-group">
                    <label for="shippingAddressGuest">Shipping Address:</label>
                    <textarea id="shippingAddressGuest" name="shippingAddress" class="form-control" required></textarea>
                </div>
                
                <button type="submit" class="btn btn-checkout" style="background-color: #4CAF50;">Finalise Order</button>
                <p style="margin-top: 15px;">After finalising your order, you can add payment details.</p>
                
                <div class="form-group">
                    <p>Already have an account? <a href="<%= request.getContextPath() %>/login">Log in</a> to place order with your account.</p>
                </div>
            </form>
        </div>
        <% } else { %>
        <div class="checkout-form">
            <h2>Finalise Order</h2>
            <p>Please <a href="<%= request.getContextPath() %>/login">log in</a> to complete your order.</p>
        </div>
        <% } %>
        <% } %>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <% if (cart != null && !cart.isEmpty()) { %>
    <% for (OrderItem item : cart) { %>
    <form id="remove_<%= item.getDeviceId() %>" method="post" action="<%= request.getContextPath() %>/orders" style="display:none;">
        <input type="hidden" name="action" value="remove_from_cart">
        <input type="hidden" name="deviceId" value="<%= item.getDeviceId() %>">
    </form>
    <% } %>
    <% } %>
</body>
</html>