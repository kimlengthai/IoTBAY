<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Details | IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        header {
            background-color: #333;
            color: white;
            padding: 20px;
            text-align: center;
            margin-bottom: 20px;
        }
        h1 {
            margin: 0;
        }
        .card {
            background: white;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        .details-group {
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }
        .details-group:last-child {
            border-bottom: none;
        }
        .details-group h3 {
            margin-top: 0;
            color: #333;
        }
        .details-row {
            display: flex;
            margin-bottom: 10px;
        }
        .details-label {
            width: 200px;
            font-weight: bold;
        }
        .details-value {
            flex: 1;
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
        .btn-link {
            background: none;
            color: #337ab7;
            text-decoration: underline;
            padding: 0;
        }
        .btn-link:hover {
            background: none;
            color: #23527c;
        }
        .actions {
            margin-top: 20px;
            display: flex;
            gap: 10px;
        }
    </style>
</head>
<body>
    <header>
        <h1>Payment Details</h1>
    </header>
    
    <div class="container">
        <div class="card">
            <div class="details-group">
                <h3>Payment Information</h3>
                <div class="details-row">
                    <div class="details-label">Payment ID:</div>
                    <div class="details-value">${payment.paymentID}</div>
                </div>
                <div class="details-row">
                    <div class="details-label">Order ID:</div>
                    <div class="details-value">
                        ${payment.orderID}
                        <a href="${pageContext.request.contextPath}/orders/view/${payment.orderID}" class="btn-link">View Order</a>
                    </div>
                </div>
                <div class="details-row">
                    <div class="details-label">Payment Date:</div>
                    <div class="details-value"><fmt:formatDate value="${payment.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss" /></div>
                </div>
                <div class="details-row">
                    <div class="details-label">Payment Method:</div>
                    <div class="details-value">${payment.paymentMethod}</div>
                </div>
                <div class="details-row">
                    <div class="details-label">Amount:</div>
                    <div class="details-value">$<fmt:formatNumber value="${payment.amount}" pattern="#,##0.00" /></div>
                </div>
                <div class="details-row">
                    <div class="details-label">Status:</div>
                    <div class="details-value">${payment.paymentStatus}</div>
                </div>
            </div>
            
            <div class="details-group">
                <h3>Card Information</h3>
                <div class="details-row">
                    <div class="details-label">Card Holder:</div>
                    <div class="details-value">${payment.cardHolderName}</div>
                </div>
                <div class="details-row">
                    <div class="details-label">Card Number:</div>
                    <div class="details-value">
                        **** **** **** ${payment.creditCardNumber.substring(Math.max(0, payment.creditCardNumber.length() - 4))}
                    </div>
                </div>
                <div class="details-row">
                    <div class="details-label">Expiry Date:</div>
                    <div class="details-value">${payment.expiryDate}</div>
                </div>
            </div>
            
            <div class="details-group">
                <h3>Order Summary</h3>
                <div class="details-row">
                    <div class="details-label">Order Date:</div>
                    <div class="details-value"><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd" /></div>
                </div>
                <div class="details-row">
                    <div class="details-label">Total Amount:</div>
                    <div class="details-value">$<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /></div>
                </div>
                <div class="details-row">
                    <div class="details-label">Shipping Address:</div>
                    <div class="details-value">${order.shippingAddress}</div>
                </div>
                <div class="details-row">
                    <div class="details-label">Order Status:</div>
                    <div class="details-value">${order.orderStatus}</div>
                </div>
            </div>
            
            <div class="actions">
                <a href="${pageContext.request.contextPath}/payments/" class="btn">Back to Payments</a>
                <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-secondary">Dashboard</a>
            </div>
        </div>
    </div>
</body>
</html> 