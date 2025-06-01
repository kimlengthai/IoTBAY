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
    <title>My Orders - IoTBay</title>
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
            margin: 20px auto;
            background-color: #fff;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 25px;
            font-weight: 600;
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
            padding: 14px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #495057;
        }
        tr:hover {
            background-color: #f9f9f9;
        }
        .btn {
            display: inline-block;
            padding: 10px 16px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-right: 8px;
            border: none;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.2s;
        }
        .btn:hover {
            opacity: 0.9;
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
            padding: 6px 12px;
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
            padding: 15px 20px;
            margin-bottom: 0;
        }
        .navbar a {
            color: white;
            text-decoration: none;
            padding: 14px 16px;
            font-size: 16px;
        }
        .navbar a:hover {
            background-color: #555;
            border-radius: 4px;
        }
        .empty-message {
            padding: 25px;
            text-align: center;
            color: #666;
            background-color: #f9f9f9;
            border-radius: 8px;
            margin: 20px 0;
        }
        .action-button {
            margin: 20px 0;
        }
        .search-container {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 25px;
            border: 1px solid #e9ecef;
        }
        .search-container h3 {
            color: #495057;
            font-size: 18px;
            margin-top: 0;
            margin-bottom: 15px;
        }
        .search-form {
            display: flex;
            flex-wrap: wrap;
            gap: 18px;
            align-items: flex-end;
        }
        .form-group {
            flex: 1;
            min-width: 200px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #495057;
        }
        .form-control {
            width: 100%;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .search-results {
            background-color: #E3F2FD;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #0D47A1;
            border-left: 4px solid #2196F3;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <div class="mx-auto">
                <a class="nav-link btn btn-outline-light" href="${pageContext.request.contextPath}/user/">Home</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h1>My Orders</h1>
        
        <div class="action-button">
            <a href="${pageContext.request.contextPath}/devices" class="btn">Continue Shopping</a>
        </div>
        
        <div class="search-container">
            <h3>Search Orders</h3>
            
            <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= request.getAttribute("errorMessage") %>
            </div>
            <% } %>
            
            <form method="get" action="${pageContext.request.contextPath}/orders/search" class="search-form">
                <div class="form-group">
                    <label for="orderId">Order ID:</label>
                    <input type="text" id="orderId" name="orderId" value="${searchOrderId}" class="form-control">
                </div>
                <div class="form-group">
                    <label for="startDate">From Date:</label>
                    <input type="date" id="startDate" name="startDate" value="${searchStartDate}" class="form-control">
                </div>
                <div class="form-group">
                    <label for="endDate">To Date:</label>
                    <input type="date" id="endDate" name="endDate" value="${searchEndDate}" class="form-control">
                </div>
                <div class="form-group" style="flex: 0 0 auto;">
                    <button type="submit" class="btn">Search</button>
                    <a href="${pageContext.request.contextPath}/orders/user" class="btn" style="background-color: #6c757d;">Reset</a>
                </div>
            </form>
        </div>
        
        <% 
        List<Order> orders = (List<Order>) request.getAttribute("orders");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat priceFormat = new DecimalFormat("$#,##0.00");
        
        if (orders == null || orders.isEmpty()) {
        %>
        <div class="empty-message">
            <p>You haven't placed any orders yet.</p>
        </div>
        <% } else { %>
        <% if (request.getAttribute("searchResults") != null) { %>
        <div class="search-results">
            <%= request.getAttribute("searchResults") %>
        </div>
        <% } %>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
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
                        <td><%= dateFormat.format(order.getOrderDate()) %></td>
                        <td><%= priceFormat.format(order.getTotalAmount()) %></td>
                        <td>
                            <span class="status status-<%= order.getOrderStatus().toLowerCase() %>">
                                <%= order.getOrderStatus() %>
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/orders/view/<%= order.getOrderId() %>" class="btn btn-view">View</a>
                            <% if (order.isEditable()) { %>
                            <a href="${pageContext.request.contextPath}/orders/edit/<%= order.getOrderId() %>" class="btn btn-edit">Edit</a>
                            <form method="post" action="${pageContext.request.contextPath}/orders" style="display:inline;">
                                <input type="hidden" name="action" value="delete_order">
                                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                                <button type="submit" class="btn btn-delete">Cancel</button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 