<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Browse Devices | IoTBay</title>
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
        .user-info {
            margin-bottom: 15px;
            font-size: 16px;
        }
        .filter-bar {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            justify-content: space-between;
            gap: 10px;
            margin-bottom: 20px;
        }
        .filter-form {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
            flex-grow: 1;
        }
        .filter-form input[type="text"],
        .filter-form select {
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 14px;
            min-width: 200px;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
        }
        .filter-form input[type="text"]:focus,
        .filter-form select:focus {
            border-color: #4CAF50;
            box-shadow: 0 0 5px rgba(76, 175, 80, 0.3);
            outline: none;
        }
        .filter-form label {
            font-weight: bold;
            font-size: 14px;
            color: #444;
        }
        .filter-bar .btn {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 10px 16px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s ease;
            white-space: nowrap;
        }
        .filter-bar .btn:hover {
            background-color: #45a049;
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
        .highlight {
            background-color: #fffde7;
        }
        .btn-back {
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
        .btn-back:hover {
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
        /* Add styles for popup */
        .popup {
            display: none;
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 5px;
            color: white;
            z-index: 1000;
            animation: slideIn 0.5s ease-out;
        }
        
        .popup.success {
            background-color: #4CAF50;
        }
        
        .popup.error {
            background-color: #f44336;
        }
        
        @keyframes slideIn {
            from { transform: translateX(100%); }
            to { transform: translateX(0); }
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #dff0d8;
            color: #3c763d;
            border-color: #d6e9c6;
        }
        .alert-error {
            background-color: #f2dede;
            color: #a94442;
            border-color: #ebccd1;
        }
        
        /* Popup message styles */
        .popup-message {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 5px;
            color: white;
            z-index: 1000;
            display: none;
            animation: slideIn 0.5s ease-out;
        }
        
        .popup-success {
            background-color: #4CAF50;
        }
        
        .popup-error {
            background-color: #f44336;
        }
        .btn-addcart {
            background-color: #ff6f61;
            color: white;
            border: none;
            /* ensure the Add to Cart text is aligned centered */
            padding: 6px 6px;
            border-radius: 20px;
            cursor: pointer;
            font-weight: bold;
            font-size: 12px;
            box-shadow: 0 3px 5px rgba(255, 111, 97, 0.4);
            transition: background-color 0.3s ease, box-shadow 0.3s ease;
            white-space: nowrap;

            text-align: center;
            display: inline-block;
            width: 100%;
        }


        .btn-addcart:hover {
            background-color: #ff4a3d; /* darker coral on hover */
            box-shadow: 0 6px 10px rgba(255, 74, 61, 0.5);
        }

    </style>
    <script>
        // Function to show popup message
        function showPopupMessage(message, type) {
            const popup = document.createElement('div');
            popup.className = 'popup-message popup-' + type;
            popup.innerText = message;
            document.body.appendChild(popup);
            
            // Show the popup
            popup.style.display = 'block';
            
            // Hide after 3 seconds
            setTimeout(function() {
                popup.style.display = 'none';
                setTimeout(function() {
                    document.body.removeChild(popup);
                }, 500);
            }, 3000);
        }
        
        // Check for success or error messages when the page loads
        window.onload = function() {
            const successMessage = "${successMessage}";
            const errorMessage = "${errorMessage}";
            
            if (successMessage && successMessage.trim() !== "") {
                showPopupMessage(successMessage, 'success');
            }
            
            if (errorMessage && errorMessage.trim() !== "") {
                showPopupMessage(errorMessage, 'error');
            }
        };
    </script>
</head>
<body>
<div class="container">
    <h1>Browse Devices</h1>

    <!-- Get messages from session and store them in page variables -->
    <c:set var="successMessage" value="${sessionScope.successMessage}" />
    <c:set var="errorMessage" value="${sessionScope.errorMessage}" />
    
    <!-- Clear session messages after retrieving them -->
    <c:remove var="successMessage" scope="session" />
    <c:remove var="errorMessage" scope="session" />

    <!-- Message Display -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">
            ${successMessage}
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            ${errorMessage}
        </div>
    </c:if>

    <!-- Welcome Message -->
    <c:if test="${not empty user}">
        <div class="user-info">
            Welcome <strong>${user.name}</strong>, ${user.email}
            <c:if test="${user.userType == 'Staff'}">(<em>staff</em>)</c:if>
        </div>
    </c:if>

    <!-- Search + Create Bar -->
    <div class="filter-bar">
        <!-- Search Form -->
        <form class="filter-form" action="${pageContext.request.contextPath}/devices/search" method="get">
            <label for="search">Search:</label>
            <input type="text" id="search" name="search" value="${param.search}" placeholder="Search by name" />

            <label for="categoryId">Category:</label>
            <select id="categoryId" name="categoryId">
                <option value="">-- All Categories --</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.categoryId}" <c:if test="${param.categoryId == cat.categoryId}">selected</c:if>>
                        ${cat.categoryName}
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="btn">Search</button>
        </form>

        <!-- Staff-only: Create Device button -->
        <c:if test="${user.staff}">
            <a href="${pageContext.request.contextPath}/devices/create" class="btn">Create Device</a>
        </c:if>
    </div>

    <!-- Device Table -->
    <c:choose>
        <c:when test="${empty devices}">
            <div class="empty-message">No devices found.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Category ID</th>
                        <th>Device Name</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Stock Quantity</th>
                        <th>Quantity</th>
                        <th>Action</th>
                        <c:if test="${user.staff}">
                            <th>Staff Actions</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${devices}" var="device">
                        <tr class="${device.stockQuantity == 0 ? 'highlight' : ''}">
                            <td>${device.categoryId}</td>
                            <td>${device.deviceName}</td>
                            <td>${device.description}</td>
                            <td><fmt:formatNumber value="${device.price}" type="currency" /></td>
                            <td>${device.stockQuantity}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/orders/" method="post" style="display: inline-block;">
                                    <input type="hidden" name="action" value="add_to_cart">
                                    <input type="hidden" name="deviceId" value="${device.deviceId}">

                                    <div style="display: flex; flex-direction: column; align-items: flex-start; gap: 6px; max-width: 80px;">
                                        <input type="number" name="quantity" value="1" min="1" style="width: 100%; box-sizing: border-box;">
                                        <button type="submit" class="btn-addcart" style="width: 100%;">Add to Cart</button>
                                    </div>
                                </form>
                            </td>

                            <c:if test="${user.staff}">
                                <td>
                                    <a href="${pageContext.request.contextPath}/devices/edit?id=${device.deviceId}">Edit</a> |
                                    <a href="${pageContext.request.contextPath}/devices/remove?id=${device.deviceId}" onclick="return confirm('Really remove this device?');">Remove</a>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/user/" class="btn-back">Back</a>
</div>
</body>
</html>
