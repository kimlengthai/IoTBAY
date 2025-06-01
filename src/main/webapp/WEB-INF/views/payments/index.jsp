<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payments | IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 1000px;
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
        h1, h2 {
            margin: 0;
        }
        .card {
            background: white;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        /* Tab styles */
        .tabs {
            display: flex;
            margin-bottom: 20px;
            border-bottom: 1px solid #ddd;
        }
        .tab {
            padding: 10px 20px;
            cursor: pointer;
            background-color: #f8f8f8;
            border: 1px solid #ddd;
            border-bottom: none;
            border-radius: 5px 5px 0 0;
            margin-right: 5px;
        }
        .tab.active {
            background-color: #fff;
            border-bottom: 1px solid #fff;
            position: relative;
            bottom: -1px;
        }
        .tab-content {
            display: none;
        }
        .tab-content.active {
            display: block;
        }
        
        /* Form styles */
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
        
        /* Message styles */
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        /* Table styles */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
        }
        .no-records {
            padding: 20px;
            text-align: center;
            color: #666;
        }
    </style>
</head>
<body>
    <header>
        <h1>Payment Management</h1>
    </header>
    
    <div class="container">
        <!-- Flash message -->
        <c:if test="${not empty sessionScope.message}">
            <div class="message">
                ${sessionScope.message}
                <c:remove var="message" scope="session" />
            </div>
        </c:if>
        
        <!-- Tabs navigation -->
        <div class="tabs">
            <div id="tab-pay-now"
                 class="tab ${activeTab == 'pay-now' ? 'active' : ''}"
                 onclick="openTab('pay-now')">Pay Now</div>
                 
            <div id="tab-add-payment"
                 class="tab ${activeTab == 'add-payment' ? 'active' : ''}"
                 onclick="openTab('add-payment')">Add Payment</div>
                 
            <div id="tab-payment-history"
                 class="tab ${activeTab == 'payment-history' ? 'active' : ''}"
                 onclick="openTab('payment-history')">Payment History</div>
                 
            <div id="tab-saved-payments"
                 class="tab ${activeTab == 'saved-payments' ? 'active' : ''}"
                 onclick="openTab('saved-payments')">Saved Payments</div>
        </div>
        
        <!-- Pay Now Pane -->
        <div id="pay-now-content" class="tab-content ${activeTab == 'pay-now' ? 'active' : ''}">
            <div class="card">
                <h2>Pay for Your Order</h2>
                <form action="${pageContext.request.contextPath}/payments/" method="post">
                    <input type="hidden" name="action" value="pay_now">
                    
                    <div class="form-group">
                        <label for="orderToPay">Select Order to Pay:</label>
                        <select id="orderToPay" name="orderToPay" required>
                            <option value="">-- Select an Order --</option>
                            <c:forEach items="${unpaidOrders}" var="order">
                                <option value="${order.orderId}">
                                    Order #${order.orderId} -
                                    $<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" />
                                    (<fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd" />)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="savedPaymentMethod">Use Saved Payment Method:</label>
                        <select id="savedPaymentMethod"
                                name="savedPaymentMethod"
                                onchange="fillPaymentDetails()">
                            <option value="">-- Select a Saved Payment Method --</option>
                            <c:forEach items="${savedPaymentMethods}" var="method">
                                <option value="${method.paymentID}"
                                        data-cardholder="${method.cardHolderName}"
                                        data-cardnumber="${method.creditCardNumber}"
                                        data-expiry="${method.expiryDate}"
                                        data-cvc="${method.cvc}">
                                    **** **** ****
                                    ${fn:substring(
                                        method.creditCardNumber,
                                        method.creditCardNumber.length() - 4,
                                        method.creditCardNumber.length()
                                    )}
                                    – ${method.cardHolderName} (Exp: ${method.expiryDate})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="cardHolderName">Card Holder Name:</label>
                        <input type="text" id="cardHolderName" name="cardHolderName" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="creditCardNumber">Credit Card Number:</label>
                        <input type="text" id="creditCardNumber" name="creditCardNumber" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="expiryDate">Expiry Date (MM/YYYY):</label>
                        <input type="text" id="expiryDate" name="expiryDate" placeholder="MM/YYYY" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="cvc">CVC/CVV:</label>
                        <input type="text" id="cvc" name="cvc" required>
                    </div>
                    
                    <button type="submit" class="btn">Pay Now</button>
                </form>
            </div>
        </div>
        
        <!-- Add Payment Pane -->
        <div id="add-payment-content" class="tab-content ${activeTab == 'add-payment' ? 'active' : ''}">
            <div class="card">
                <h2>Add Payment Method</h2>
                <form action="${pageContext.request.contextPath}/payments/" method="post">
                    <input type="hidden" name="action" value="add_payment_method">
                    
                    <div class="form-group">
                        <label for="paymentMethod">Payment Method:</label>
                        <select id="paymentMethod" name="paymentMethod" required>
                            <option value="Credit Card">Credit Card</option>
                            <option value="Debit Card">Debit Card</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="newCardHolderName">Card Holder Name:</label>
                        <input type="text" id="newCardHolderName" name="cardHolderName" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="newCreditCardNumber">Card Number:</label>
                        <input type="text" id="newCreditCardNumber" name="creditCardNumber" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="newExpiryDate">Expiry Date (MM/YYYY):</label>
                        <input type="text" id="newExpiryDate" name="expiryDate" placeholder="MM/YYYY" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="newCvc">CVC/CVV:</label>
                        <input type="text" id="newCvc" name="cvc" required>
                    </div>
                    
                    <button type="submit" class="btn">Save Payment Method</button>
                </form>
            </div>
        </div>
        
        <!-- Payment History Pane -->
        <div id="payment-history-content" class="tab-content ${activeTab == 'payment-history' ? 'active' : ''}">
            <div class="card">
                <h2>Search Payments</h2>
                <form action="${pageContext.request.contextPath}/payments/" method="post" class="search-form">
                    <input type="hidden" name="action" value="search_payment">
                    
                    <div class="form-group">
                        <label for="paymentId">Payment ID:</label>
                        <input type="number" id="paymentId" name="paymentId"
                               value="${searchPaymentId}" min="1">
                    </div>
                    
                    <div class="form-group">
                        <label for="startDate">Start Date:</label>
                        <input type="date" id="startDate" name="startDate"
                               value="${searchStartDate}">
                    </div>
                    
                    <div class="form-group">
                        <label for="endDate">End Date:</label>
                        <input type="date" id="endDate" name="endDate"
                               value="${searchEndDate}">
                    </div>
                    
                    <button type="submit" class="btn">Search</button>
                </form>
            </div>
            
            <div class="card">
                <h2>Payment Records</h2>
                <c:choose>
                    <c:when test="${empty payments}">
                        <div class="no-records">
                            <p>No payment records found.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Payment ID</th>
                                    <th>Order ID</th>
                                    <th>Payment Date</th>
                                    <th>Amount</th>
                                    <th>Payment Method</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${payments}" var="payment">
                                    <tr>
                                        <td>${payment.paymentID}</td>
                                        <td>${payment.orderID}</td>
                                        <td><fmt:formatDate value="${payment.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                        <td>$<fmt:formatNumber value="${payment.amount}" pattern="#,##0.00"/></td>
                                        <td>${payment.paymentMethod}</td>
                                        <td>${payment.paymentStatus}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/payments/view/${payment.paymentID}" class="btn">View Details</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <!-- Saved Payments Pane -->
<!-- Saved Payments Pane -->
<div id="saved-payments-content"
     class="tab-content ${activeTab == 'saved-payments' ? 'active' : ''}">
  <div class="card">
    <h2>Saved Payment Methods</h2>

    <c:if test="${empty savedPaymentMethods}">
      <p>You have no saved payment methods.</p>
    </c:if>

    <c:if test="${not empty savedPaymentMethods}">
      <!-- 1) Dropdown -->
      <div class="form-group">
        <label for="savedPaymentView">Select a Saved Payment Method:</label>
        <select id="savedPaymentView"
                name="savedPaymentView"
                onchange="displayAndManagePayment()">
          <option value="">-- Select a Payment Method --</option>
          <c:forEach items="${savedPaymentMethods}" var="method">
            <option value="${method.paymentID}"
                    data-cardholder="${method.cardHolderName}"
                    data-cardnumber="${method.creditCardNumber}"
                    data-expiry="${method.expiryDate}"
                    data-cvc="${method.cvc}">
              **** **** ****
              ${fn:substring(
                 method.creditCardNumber,
                 method.creditCardNumber.length() - 4,
                 method.creditCardNumber.length()
              )} – ${method.cardHolderName}
            </option>
          </c:forEach>
        </select>
      </div>

      <!-- 2) Edit/Delete form -->
      <div id="managePaymentForm" style="display:none; margin-top:15px;">
        <h3>Manage Payment Method</h3>

        <!-- UPDATE form -->
        <form action="${pageContext.request.contextPath}/payments/" method="post" style="margin-bottom:10px;">
          <input type="hidden" name="action" value="update_payment_method" />
          <input type="hidden" id="editPaymentID" name="paymentID" />
          
          <div class="form-group">
            <label for="editCardHolderName">Card Holder Name:</label>
            <input type="text" id="editCardHolderName"
                   name="cardHolderName" required />
          </div>

          <div class="form-group">
            <label for="editCreditCardNumber">Card Number:</label>
            <input type="text" id="editCreditCardNumber"
                   name="creditCardNumber" required />
          </div>

          <div class="form-group">
            <label for="editExpiryDate">Expiry Date (MM/YYYY):</label>
            <input type="text" id="editExpiryDate"
                   name="expiryDate" placeholder="MM/YYYY" required />
          </div>

          <div class="form-group">
            <label for="editCvc">CVC/CVV:</label>
            <input type="text" id="editCvc"
                   name="cvc" required />
          </div>

          <button type="submit" class="btn">Update</button>
        </form>

        <!-- DELETE form -->
        <form action="${pageContext.request.contextPath}/payments/" method="post"
              onsubmit="return confirm('Are you sure you want to remove this payment method?');">
          <input type="hidden" name="action" value="delete_payment_method" />
          <input type="hidden" id="deletePaymentID" name="paymentID" />
          <button type="submit" class="btn btn-secondary">Remove</button>
        </form>
      </div>
    </c:if>
  </div>
</div>



        
    </div> <!-- /.container -->
    
    <script>
        function openTab(tabName) {
            // Hide all panes
            document.querySelectorAll('.tab-content').forEach(p => p.classList.remove('active'));
            // Deactivate all tabs
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            // Activate chosen pane + tab
            document.getElementById(tabName + '-content').classList.add('active');
            document.getElementById('tab-' + tabName).classList.add('active');
            // Persist via AJAX
            fetch('${pageContext.request.contextPath}/payments/?action=set_active_tab&tab=' + tabName);
        }

        function fillPaymentDetails() {
            var sel = document.getElementById('savedPaymentMethod'),
                opt = sel.options[sel.selectedIndex];
            if (opt.value) {
                document.getElementById('cardHolderName').value = opt.getAttribute('data-cardholder');
                document.getElementById('creditCardNumber').value = opt.getAttribute('data-cardnumber');
                document.getElementById('expiryDate').value = opt.getAttribute('data-expiry');
                document.getElementById('cvc').value = opt.getAttribute('data-cvc');
            } else {
                ['cardHolderName','creditCardNumber','expiryDate','cvc']
                  .forEach(id => document.getElementById(id).value = '');
            }
        }

        window.onload = function() {
            if (!document.querySelector('.tab.active')) {
                openTab('pay-now');
            }
        }
        
          function displayAndEditPayment() {
    var sel  = document.getElementById('savedPaymentView'),
        opt  = sel.options[sel.selectedIndex],
        form = document.getElementById('editPaymentForm');

    if (opt.value) {
      // Populate hidden ID
      document.getElementById('editPaymentID').value = opt.value;
      // Populate fields
      document.getElementById('editCardHolderName').value    = opt.getAttribute('data-cardholder');
      document.getElementById('editCreditCardNumber').value = opt.getAttribute('data-cardnumber');
      document.getElementById('editExpiryDate').value       = opt.getAttribute('data-expiry');
      document.getElementById('editCvc').value              = opt.getAttribute('data-cvc');
      // Show the form
      form.style.display = 'block';
    } else {
      form.style.display = 'none';
    }
  }
 
function displayAndManagePayment() {
    var sel  = document.getElementById('savedPaymentView'),
        opt  = sel.options[sel.selectedIndex],
        form = document.getElementById('managePaymentForm');

    if (opt.value) {
      // Set IDs on both forms
      document.getElementById('editPaymentID').value   = opt.value;
      document.getElementById('deletePaymentID').value = opt.value;
      
      // Fill edit fields
      document.getElementById('editCardHolderName').value    = opt.getAttribute('data-cardholder');
      document.getElementById('editCreditCardNumber').value = opt.getAttribute('data-cardnumber');
      document.getElementById('editExpiryDate').value       = opt.getAttribute('data-expiry');
      document.getElementById('editCvc').value              = opt.getAttribute('data-cvc');

      form.style.display = 'block';
    } else {
      form.style.display = 'none';
    }
  }
        
        
    </script>
</body>
</htm
