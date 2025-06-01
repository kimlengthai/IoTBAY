<%-- 
    Document   : CreateDevice
    Created on : 14 May 2025, 10:16:11 am
    Author     : allanahwadey
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Device | IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 600px;
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
            margin-top: 0;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 8px 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
        }
        .form-actions {
            margin-top: 20px;
            text-align: right;
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
            margin-left: 5px;
        }
        .btn:hover {
            background: #45a049;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Create New Device</h1>

        <form action="${pageContext.request.contextPath}/devices/create" method="post">
            <div class="form-group">
                <label for="deviceName">Device Name</label>
                <input type="text" id="deviceName" name="deviceName" required />
            </div>

            <div class="form-group">
                <label for="categoryId">Category</label>
                <select id="categoryId" name="categoryId" required>
                    <option value="">-- choose category --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}">${cat.categoryName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="4" required></textarea>
            </div>

            <div class="form-group">
                <label for="price">Unit Price</label>
                <input type="number" id="price" name="price" step="0.01" min="0" required />
            </div>

            <div class="form-group">
                <label for="stockQuantity">Quantity (Stock)</label>
                <input type="number" id="stockQuantity" name="stockQuantity" min="0" required />
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/devices/" class="btn">Cancel</a>
                <button type="submit" class="btn">Create Device</button>
            </div>
        </form>
    </div>
</body>
</html>
