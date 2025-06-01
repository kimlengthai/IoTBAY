<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Device</title>
    <style>
        body { font-family: Arial; padding: 20px; background: #f4f4f4; }
        .container { max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        input, select, textarea { width: 100%; padding: 8px; margin-top: 5px; margin-bottom: 15px; }
        .btn { background: #4CAF50; color: white; padding: 10px 15px; border: none; cursor: pointer; }
        .btn:hover { background: #45a049; }
    </style>
</head>
<body>
<div class="container">
    <h1>Edit Device</h1>
    <form action="${pageContext.request.contextPath}/devices/update" method="post">
        <input type="hidden" name="id" value="${device.deviceId}" />

        <label>Device Name:</label>
        <input type="text" name="name" value="${device.deviceName}" required />

        <label>Category:</label>
        <select name="categoryId" required>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.categoryId}" <c:if test="${cat.categoryId == device.categoryId}">selected</c:if>>
                    ${cat.categoryName}
                </option>
            </c:forEach>
        </select>

        <label>Description:</label>
        <textarea name="description" rows="4" required>${device.description}</textarea>

        <label>Price:</label>
        <input type="number" step="0.01" name="price" min="0" value="${device.price}" required />

        <label>Stock Quantity:</label>
        <input type="number" name="stock" min="0" value="${device.stockQuantity}" required />

        <button type="submit" class="btn">Update Device</button>
    </form>
</div>
</body>
</html>
