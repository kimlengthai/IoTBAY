<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.model.Order" %>
<%@ page import="com.model.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Orders - IoTBay</title>
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
        .btn-view {
            background-color: #2196F3;
        }
        .btn-edit {
            background-color: #FFC107;
        }
        .btn-delete {
            background-color: #F44336;
        }
        .status {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
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
        .empty-message {
            padding: 20px;
            text-align: center;
            color: #666;
            background-color: #f9f9f9;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="<%= request.getContextPath() %>/">Home</a>
        <a href="<%= request.getContextPath() %>/devices">Devices</a>
        <a href="<%= request.getContextPath() %>/orders">Orders</a>
        <a href="<%= request.getContextPath() %>/orders/cart">Cart</a>
        <div class="right">
            <a href="<%= request.getContextPath() %>/profile">Profile</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <h1>All Orders</h1>
        
        <% 
        List<Order> orders = (List<Order>) request.getAttribute("orders");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat priceFormat = new DecimalFormat("$#,##0.00");
        
        if (orders == null || orders.isEmpty()) {
        %>
        <div class="empty-message">
            <p>No orders found.</p>
        </div>
        <% } else { %>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>User ID</th>
                        <th>Date</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Order order : orders) { %>
                    <tr>
                        <td><%= order.getOrderId() %></td>
                        <td><%= order.getUserId() %></td>
                        <td><%= dateFormat.format(order.getOrderDate()) %></td>
                        <td><%= priceFormat.format(order.getTotalAmount()) %></td>
                        <td>
                            <span class="status status-<%= order.getOrderStatus().toLowerCase() %>">
                                <%= order.getOrderStatus() %>
                            </span>
                        </td>
                        <td>
                            <a href="<%= request.getContextPath() %>/orders/view/<%= order.getOrderId() %>" class="btn btn-view">View</a>
                            <% if (order.isEditable()) { %>
                            <a href="<%= request.getContextPath() %>/orders/edit/<%= order.getOrderId() %>" class="btn btn-edit">Edit</a>
                            <% } %>
                            <form method="post" action="<%= request.getContextPath() %>/orders" style="display:inline;">
                                <input type="hidden" name="action" value="delete_order">
                                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                                <button type="submit" class="btn btn-delete" onclick="return confirm('Are you sure you want to delete this order?')">Delete</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</body>
</html> 