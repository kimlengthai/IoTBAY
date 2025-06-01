<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.model.User" %>
<%
    // Get the existing user from session (don't create a new one)
    User user = (User) session.getAttribute("user");
    if (user == null) {
        // If no user in session, redirect to login
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Welcome Page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        .welcome-container {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            max-width: 600px;
            margin: 0 auto;
        }
        .welcome-message {
            color: #4CAF50;
            margin-bottom: 20px;
        }
        .user-info {
            margin: 20px 0;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 4px;
        }
        .continue-btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .continue-btn:hover {
            background-color: #45a049;
            color: white;
        }
    </style>
</head>
<body>
    <div class="welcome-container">
        <h1 class="welcome-message">Welcome, <%= user.getName() %>!</h1>
        
        <div class="user-info">
            <h2>Your Information:</h2>
            <p><strong>Name:</strong> <%= user.getName() %></p>
            <p><strong>Email:</strong> <%= user.getEmail() %></p>
            <p><strong>Phone:</strong> <%= user.getPhone() %></p>
            <p><strong>Address:</strong> <%= user.getAddress() %></p>
        </div>
        
        <div class="text-center">
            <a href="Mainpage.jsp" class="continue-btn">Continue to Main Page</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 