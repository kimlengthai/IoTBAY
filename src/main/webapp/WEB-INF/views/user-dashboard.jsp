<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Dashboard | IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        header {
            background-color: #333;
            color: white;
            padding: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .welcome {
            font-size: 24px;
        }
        .actions {
            display: flex;
            gap: 10px;
        }
        .btn {
            display: inline-block;
            background: #4CAF50;
            color: white;
            padding: 8px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
        }
        .btn:hover {
            background: #45a049;
        }
        .btn-secondary {
            background: #f0ad4e;
        }
        .btn-secondary:hover {
            background: #ec971f;
        }
        .dashboard-content {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .card {
            background: white;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            padding: 20px;
            transition: transform 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .card h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        .card-content {
            margin-top: 15px;
        }
        .recent-activity {
            grid-column: 1 / -1;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
    </style>
</head>
<body>
    <header>
        <div class="welcome">Welcome, ${user.name}</div>
        <div class="actions">
            <a href="${pageContext.request.contextPath}/user/profile" class="btn">My Profile</a>
            <a href="${pageContext.request.contextPath}/devices/" class="btn">Browse Devices</a>
            <a href="${pageContext.request.contextPath}/user/logout" class="btn btn-secondary">Logout</a>
        </div>
    </header>
    
    <div class="container">
        <div class="dashboard-content">
            <div class="card">
                <h2>Account Summary</h2>
                <div class="card-content">
                    <p><strong>Name:</strong> ${user.name}</p>
                    <p><strong>Email:</strong> ${user.email}</p>
                    <p><strong>Account Type:</strong> ${user.userType}</p>
                    <p><strong>Member Since:</strong> ${user.createdAt}</p>
                    <a href="${pageContext.request.contextPath}/user/profile" class="btn">View Full Profile</a>
                </div>
            </div>
            
            <div class="card">
                <h2>My Orders</h2>
                <div class="card-content">
                    <p>View and manage your orders</p>
                    <a href="${pageContext.request.contextPath}/orders" class="btn">View Orders</a>
                </div>
            </div>
            
            <div class="card">
                <h2>My Payments</h2>
                <div class="card-content">
                    <p>Manage payment methods and view payment history</p>
                    <div style="display: flex; gap: 10px; margin-top: 10px;">
                        <a href="${pageContext.request.contextPath}/payments/" class="btn">Payment Center</a>
                    </div>
                </div>
            </div>
            
            <div class="card">
                <h2>My Cart</h2>
                <div class="card-content">
                    <p>Items in your shopping cart</p>
                    <a href="${pageContext.request.contextPath}/orders/cart" class="btn">Go to Cart</a>
                </div>
            </div>
            
            <div class="card recent-activity">
                <h2>Recent Activity</h2>
                <div class="card-content">
                    <c:choose>
                        <c:when test="${empty recentActivities}">
                            <p>No recent activities found.</p>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Activity</th>
                                        <th>Details</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentActivities}" var="activity">
                                        <tr>
                                            <td>${activity.date}</td>
                                            <td>${activity.type}</td>
                                            <td>${activity.details}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                    <p style="margin-top: 15px;">
                        <a href="${pageContext.request.contextPath}/user/accesslogs" class="btn">View Access Logs</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 