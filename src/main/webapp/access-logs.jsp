<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Access Logs | IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            border-radius: 5px;
        }
        h1 {
            color: #333;
            border-bottom: 1px solid #ddd;
            padding-bottom: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .btn {
            display: inline-block;
            background: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 16px;
            margin-top: 20px;
        }
        .btn:hover {
            background: #45a049;
        }
        .empty-message {
            padding: 20px;
            background-color: #f8f8f8;
            border-radius: 4px;
            text-align: center;
            color: #666;
            margin-top: 20px;
        }
        .highlight {
            background-color: #fffde7;
        }
        .filter-form {
            margin-bottom: 20px;
            display: flex;
            gap: 10px;
            align-items: center;
        }
        .filter-form input[type="date"] {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .status-active {
            color: green;
        }
        .status-completed {
            color: blue;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Access Logs</h1>
        
        <div class="filter-form">
            <form action="${pageContext.request.contextPath}/user/accesslogs" method="get">
                <label for="startDate">From:</label>
                <input type="date" id="startDate" name="startDate" value="${param.startDate}">
                
                <label for="endDate">To:</label>
                <input type="date" id="endDate" name="endDate" value="${param.endDate}">
                
                <button type="submit" class="btn">Filter</button>
            </form>
        </div>
        
        <c:choose>
            <c:when test="${empty accessLogs}">
                <div class="empty-message">No access logs found.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Log ID</th>
                            <c:if test="${user.isStaff()}">
                                <th>User</th>
                            </c:if>
                            <th>Login Time</th>
                            <th>Logout Time</th>
                            <th>Duration</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${accessLogs}" var="log">
                            <tr>
                                <td>${log.logId}</td>
                                <c:if test="${user.isStaff()}">
                                    <td>${log.user != null ? log.user.name : 'Unknown'} (ID: ${log.userId})</td>
                                </c:if>
                                <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${log.loginTime}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${log.logoutTime != null}">
                                            <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${log.logoutTime}" />
                                        </c:when>
                                        <c:otherwise>
                                            <em>Still active</em>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${log.sessionDurationMinutes >= 0}">
                                            ${log.sessionDurationMinutes} minutes
                                        </c:when>
                                        <c:otherwise>
                                            <em>In progress</em>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${log.logoutTime == null}">
                                            <span class="status-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-completed">Completed</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        
        <a href="${pageContext.request.contextPath}/user/" class="btn">Home</a>
    </div>
</body>
</html> 