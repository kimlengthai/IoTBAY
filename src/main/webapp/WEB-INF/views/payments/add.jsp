<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Make Payment | IoTBay</title>
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
        }
        h1 {
            margin: 0;
        }
        .card {
            background: white;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            padding: 20px;
            margin-top: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"],
        input[type="number"],
        input[type="date"],
        select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
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
        .order-summary {
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .payment-options {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }
        .payment-option {
            flex: 1;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
        }
        .payment-option.active {
            border-color: #4CAF50;
            background-color: rgba(76, 175, 80, 0.1);
        }
        .payment-option:hover {
            border-color: #4CAF50;
        }
        .hidden {
            display: none;
        }
    </style>
    <script>
        function togglePaymentFields() {
            var savedPayment = document.getElementById('useSavedPayment');
            var newPaymentFields = document.getElementById('newPaymentFields');
            
            if (savedPayment && savedPayment.value !== 'none') {
                newPaymentFields.style.display = 'none';
                
                // Remove validation attributes
                document.getElementById('cardHolderName').removeAttribute('required');
                document.getElementById('creditCardNumber').removeAttribute('required');
                document.getElementById('expiryDate').removeAttribute('required');
                document.getElementById('cvc').removeAttribute('required');
            } else {
                newPaymentFields.style.display = 'block';
                
                // No need to re-add required attributes for server-side only validation
            }
        }
        
        function selectPaymentOption(option) {
            // Remove active class from all options
            var options = document.getElementsByClassName('payment-option');
            for (var i = 0; i < options.length; i++) {
                options[i].classList.remove('active');
            }
            
            // Add active class to selected option
            document.getElementById(option + '-option').classList.add('active');
            
            // Show the selected form section and hide the other
            if (option === 'saved') {
                document.getElementById('saved-payment-section').classList.remove('hidden');
                document.getElementById('new-payment-section').classList.add('hidden');
                
                // Set the useSavedPayment to the first option if available
                var selectElement = document.getElementById('useSavedPayment');
                if (selectElement && selectElement.options.length > 0 && selectElement.value === 'none') {
                    selectElement.selectedIndex = 1;
                }
                togglePaymentFields();
            } else {
                document.getElementById('saved-payment-section').classList.add('hidden');
                document.getElementById('new-payment-section').classList.remove('hidden');
                
                // Reset the useSavedPayment to none
                var selectElement = document.getElementById('useSavedPayment');
                if (selectElement) {
                    selectElement.value = 'none';
                }
                togglePaymentFields();
            }
        }
        
        // Function to update amount based on selected order
        function updateAmount() {
            var orderSelect = document.getElementById('orderId');
            var amountInput = document.getElementById('amount');
            
            if (orderSelect && amountInput) {
                var selectedOption = orderSelect.options[orderSelect.selectedIndex];
                if (selectedOption && selectedOption.value) {
                    // Extract the amount from the option text
                    var optionText = selectedOption.text;
                    var amountMatch = optionText.match(/\$\s*([\d,]+\.\d+)/);
                    if (amountMatch && amountMatch[1]) {
                        // Remove commas and convert to number
                        var amount = amountMatch[1].replace(/,/g, '');
                        amountInput.value = amount;
                    }
                }
            }
        }
        
        // Initialize on page load
        window.onload = function() {
            // Initialize saved payment toggle
            if (document.getElementById('useSavedPayment')) {
                togglePaymentFields();
            }
            
            // Automatically select payment option based on available saved payments
            var hasSavedPayments = document.getElementById('saved-payment-section') && 
                                   document.getElementById('useSavedPayment') && 
                                   document.getElementById('useSavedPayment').options.length > 1;
            
            if (hasSavedPayments) {
                selectPaymentOption('saved');
            } else {
                selectPaymentOption('new');
            }
            
            // Add event listener to order select for auto-filling amount
            var orderSelect = document.getElementById('orderId');
            if (orderSelect) {
                orderSelect.addEventListener('change', updateAmount);
            }
        };
    </script>
</head>
<body>
    <header>
        <h1>Add Payment</h1>
    </header>
    
    <div class="container">
        <div class="card">
            <c:if test="${order != null}">
                <div class="order-summary">
                    <h2>Order Summary</h2>
                    <p><strong>Order ID:</strong> ${order.orderId}</p>
                    <p><strong>Order Date:</strong> <fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd" /></p>
                    <p><strong>Total Amount:</strong> $<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /></p>
                    <p><strong>Shipping Address:</strong> ${order.shippingAddress}</p>
                    <p><strong>Status:</strong> ${order.orderStatus}</p>
                </div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/payments/" method="post">
                <input type="hidden" name="action" value="add_payment">
                
                <c:choose>
                    <c:when test="${order != null}">
                        <input type="hidden" name="orderId" value="${order.orderId}">
                        <input type="hidden" name="amount" value="${order.totalAmount}">
                        
                        <div class="form-group">
                            <label>Amount:</label>
                            <p>$<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="form-group">
                            <label for="orderId">Select Order:</label>
                            <select id="orderId" name="orderId" onchange="updateAmount()">
                                <option value="">-- Select an Order --</option>
                                <c:forEach items="${orders}" var="order">
                                    <option value="${order.orderId}">
                                        Order #${order.orderId} - $<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /> 
                                        (<fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd" />)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="amount">Amount:</label>
                            <input type="number" id="amount" name="amount" step="0.01" readonly>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <c:if test="${not empty previousPayments}">
                    <div class="payment-options">
                        <div id="saved-option" class="payment-option" onclick="selectPaymentOption('saved')">
                            <h3>Use Saved Payment Method</h3>
                            <p>Select one of your previously used payment methods</p>
                        </div>
                        <div id="new-option" class="payment-option" onclick="selectPaymentOption('new')">
                            <h3>Enter New Payment Details</h3>
                            <p>Add a new payment method</p>
                        </div>
                    </div>
                    
                    <div id="saved-payment-section">
                        <div class="form-group">
                            <label for="useSavedPayment">Select Payment Method:</label>
                            <select id="useSavedPayment" name="useSavedPayment" onchange="togglePaymentFields()">
                                <option value="none">-- Select a saved payment method --</option>
                                <c:forEach items="${previousPayments}" var="savedPayment">
                                    <option value="${savedPayment.paymentID}">
                                        **** **** **** ${savedPayment.creditCardNumber.substring(Math.max(0, savedPayment.creditCardNumber.length() - 4))} - 
                                        ${savedPayment.cardHolderName} (Exp: ${savedPayment.expiryDate})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    
                    <div id="new-payment-section" class="hidden">
                </c:if>
                
                <div id="newPaymentFields">
                    <div class="form-group">
                        <label for="cardHolderName">Card Holder Name:</label>
                        <input type="text" id="cardHolderName" name="cardHolderName">
                    </div>
                    
                    <div class="form-group">
                        <label for="creditCardNumber">Credit Card Number:</label>
                        <input type="text" id="creditCardNumber" name="creditCardNumber">
                    </div>
                    
                    <div class="form-group">
                        <label for="expiryDate">Expiry Date (MM/YYYY):</label>
                        <input type="text" id="expiryDate" name="expiryDate" placeholder="MM/YYYY">
                    </div>
                    
                    <div class="form-group">
                        <label for="cvc">CVC/CVV:</label>
                        <input type="text" id="cvc" name="cvc">
                    </div>
                </div>
                
                <c:if test="${not empty previousPayments}">
                    </div>
                </c:if>
                
                <div class="form-group">
                    <input type="submit" value="Process Payment" class="btn">
                    <a href="${pageContext.request.contextPath}/orders/user" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html> 