<%-- 
    Document   : Mainpage
    Created on : 28 Mar 2025, 11:32:08 am
    Author     : yadupillai
--%>
<%-- <%@ page import="model.User" %> --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.model.User" %>
<%
    // Retrieve the user object from the session
    User user = (User) session.getAttribute("user");
    
    // Redirect to login page if user is not logged in
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>IoTBay - Main Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                padding: 20px;
            }
            .main-container {
                max-width: 800px;
                margin: 0 auto;
                background-color: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }
            .user-info {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 5px;
                margin-bottom: 20px;
            }
            .logout-btn {
                background-color: #dc3545;
                color: white;
                padding: 10px 20px;
                text-decoration: none;
                border-radius: 5px;
                display: inline-block;
                transition: background-color 0.3s ease;
            }
            .logout-btn:hover {
                background-color: #c82333;
                color: white;
            }
        </style>
    </head>
    <body>
        <div class="main-container">
            <h1 class="text-center mb-4">Welcome to IoTBay</h1>
            
            <div class="user-info">
                <h2>User Information</h2>
                <p><strong>Name:</strong> <%= user.getName() %></p>
                <p><strong>Email:</strong> <%= user.getEmail() %></p>
                <p><strong>Phone:</strong> <%= user.getPhone() %></p>
                <p><strong>Address:</strong> <%= user.getAddress() %></p>
            </div>
            
            <div class="text-center">
                <a href="${pageContext.request.contextPath}/user/accesslogs" class="btn btn-primary me-2">View Access Logs</a>
                <a href="${pageContext.request.contextPath}/devices" class="btn btn-success me-2">Browse Devices</a>
                <a href="${pageContext.request.contextPath}/orders/user" class="btn btn-info me-2">My Orders</a>
                <a href="${pageContext.request.contextPath}/orders/cart" class="btn btn-warning me-2">Shopping Cart</a>
                <a href="${pageContext.request.contextPath}/payments/" class="btn btn-primary me-2">Payment Management</a>
                <a href="logoutpage.jsp" class="logout-btn">Logout</a>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
