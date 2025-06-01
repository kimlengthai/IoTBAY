package com.controller;

import com.dao.PaymentDAO;
import com.dao.OrderDAO;
import com.model.PaymentBean;
import com.model.User;
import com.model.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@WebServlet(name = "PaymentController", urlPatterns = {"/payments/*"})
public class PaymentController extends HttpServlet {
    
    private PaymentDAO paymentDAO;
    private OrderDAO orderDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentDAO = new PaymentDAO();
        orderDAO   = new OrderDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo    = request.getPathInfo();
        String queryString = request.getQueryString();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Not logged in → redirect to login
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // AJAX set active tab
        if (queryString != null && queryString.startsWith("action=set_active_tab")) {
            String tab = request.getParameter("tab");
            if (tab != null) {
                session.setAttribute("activePaymentTab", tab);
            }
            return;
        }
        
        // Route based on path
        if (pathInfo == null || "/".equals(pathInfo)) {
            showPaymentTabs(request, response, user.getUserId());
        } else if (pathInfo.equals("/add")) {
            showAddPaymentForm(request, response);
        } else if (pathInfo.equals("/search")) {
            searchPayments(request, response, user.getUserId());
        } else if (pathInfo.startsWith("/order/")) {
            int orderId = Integer.parseInt(pathInfo.substring(7));
            showAddPaymentFormForOrder(request, response, orderId);
        } else if (pathInfo.startsWith("/view/")) {
            int paymentId = Integer.parseInt(pathInfo.substring(6));
            viewPayment(request, response, paymentId, user.getUserId());
        } else {
            response.sendRedirect(request.getContextPath() + "/payments/");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/payments/");
            return;
        }
        
        switch (action) {
            case "add_payment":
                addPayment(request, response);
                break;
            case "add_payment_method":
                addPaymentMethod(request, response, user.getUserId());
                break;
            case "pay_now":
                processPayNow(request, response, user.getUserId());
                break;
            case "search_payment":
                searchPayments(request, response, user.getUserId());
                break;
            case "update_payment_method":
                updatePaymentMethod(request, response, user.getUserId());
                break;
            case "delete_payment_method":
                deletePaymentMethod(request, response, user.getUserId());
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/payments/");
        }
    }
    
    private void showPaymentTabs(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            String activeTab = (String) session.getAttribute("activePaymentTab");
            if (activeTab == null) activeTab = "pay-now";
            request.setAttribute("activeTab", activeTab);
            
            // Pay Now data
            List<Order> unpaidOrders = orderDAO.getUnpaidOrdersByUserId(userId);
            request.setAttribute("unpaidOrders", unpaidOrders);
            
            // Saved payment methods
            List<PaymentBean> savedPaymentMethods = getSavedPaymentMethods(userId);
            request.setAttribute("savedPaymentMethods", savedPaymentMethods);
            
            // Payment history
            List<PaymentBean> payments = paymentDAO.getPaymentsByUserId(userId);
            request.setAttribute("payments", payments);
            
            request.getRequestDispatcher("/WEB-INF/views/payments/index.jsp")
                   .forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving payment data: " + e.getMessage());
        }
    }
    
    private List<PaymentBean> getSavedPaymentMethods(int userId) throws SQLException {
        List<PaymentBean> all = paymentDAO.getPaymentsByUserId(userId);
        List<PaymentBean> saved = new ArrayList<>();
        for (PaymentBean p : all) {
            if ("SavedMethod".equals(p.getPaymentStatus()) ||
                (p.getCreditCardNumber() != null && !p.getCreditCardNumber().isEmpty())) {
                saved.add(p);
            }
        }
        return saved;
    }
    
    private void processPayNow(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int orderId = Integer.parseInt(request.getParameter("orderToPay"));
            String cardHolderName = request.getParameter("cardHolderName");
            String ccNumber  = request.getParameter("creditCardNumber");
            String expiry    = request.getParameter("expiryDate");
            String cvc       = request.getParameter("cvc");
            
            Order order = orderDAO.getOrderById(orderId);
            if (order == null || order.getUserId() != userId) {
                session.setAttribute("message", "Error: Order not found or not authorized.");
                response.sendRedirect(request.getContextPath() + "/payments/");
                return;
            }
            
            PaymentBean payment = new PaymentBean();
            payment.setOrderID(orderId);
            payment.setPaymentMethod("Credit Card");
            payment.setPaymentStatus("Completed");
            payment.setPaymentDate(new Date());
            payment.setCreditCardNumber(ccNumber);
            payment.setCardHolderName(cardHolderName);
            payment.setExpiryDate(expiry);
            payment.setCvc(cvc);
            payment.setAmount(order.getTotalAmount());
            
            paymentDAO.createPayment(payment);
            
            order.setOrderStatus("Paid");
            orderDAO.updateOrder(order);
            
            session.setAttribute("message", "Payment processed successfully!");
            session.setAttribute("activePaymentTab", "payment-history");
            response.sendRedirect(request.getContextPath() + "/payments/");
        } catch (SQLException | NumberFormatException e) {
            handleError(request, response, "Error processing payment: " + e.getMessage());
        }
    }
    
    private void addPaymentMethod(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            String methodType = request.getParameter("paymentMethod");
            String cardHolder = request.getParameter("cardHolderName");
            String ccNumber   = request.getParameter("creditCardNumber");
            String expiry     = request.getParameter("expiryDate");
            String cvc        = request.getParameter("cvc");
            
            // Associate with most recent order if exists
            List<Order> userOrders = orderDAO.getOrdersByUserId(userId);
            if (userOrders.isEmpty()) {
                session.setAttribute("message", "Error: Place an order first before saving a payment method.");
                response.sendRedirect(request.getContextPath() + "/payments/");
                return;
            }
            int orderId = userOrders.get(0).getOrderId();
            
            PaymentBean payment = new PaymentBean();
            payment.setOrderID(orderId);
            payment.setPaymentMethod(methodType);
            payment.setPaymentStatus("SavedMethod");
            payment.setPaymentDate(new Date());
            payment.setCreditCardNumber(ccNumber);
            payment.setCardHolderName(cardHolder);
            payment.setExpiryDate(expiry);
            payment.setCvc(cvc);
            payment.setAmount(new BigDecimal("0.00"));
            
            paymentDAO.createPayment(payment);
            
            session.setAttribute("message", "Payment method saved successfully!");
            session.setAttribute("activePaymentTab", "add-payment");
            response.sendRedirect(request.getContextPath() + "/payments/");
        } catch (SQLException e) {
            handleError(request, response, "Error saving payment method: " + e.getMessage());
        }
    }
    
    private void updatePaymentMethod(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("paymentID"));
            PaymentBean payment = paymentDAO.getPaymentById(id);
            Order order = orderDAO.getOrderById(payment.getOrderID());
            
            if (payment == null || order.getUserId() != userId) {
                session.setAttribute("message", "Not authorized to edit that method.");
            } else {
                payment.setCardHolderName(request.getParameter("cardHolderName"));
                payment.setCreditCardNumber(request.getParameter("creditCardNumber"));
                payment.setExpiryDate(request.getParameter("expiryDate"));
                payment.setCvc(request.getParameter("cvc"));
                paymentDAO.updatePaymentMethod(payment);
                session.setAttribute("message", "Payment method updated.");
            }
            
            session.setAttribute("activePaymentTab", "saved-payments");
            response.sendRedirect(request.getContextPath() + "/payments/");
        } catch (SQLException | NumberFormatException e) {
            handleError(request, response, "Error updating payment method: " + e.getMessage());
        }
    }
    
    private void deletePaymentMethod(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("paymentID"));
            PaymentBean payment = paymentDAO.getPaymentById(id);
            Order order = orderDAO.getOrderById(payment.getOrderID());
            
            if (payment == null || order.getUserId() != userId) {
                session.setAttribute("message", "Not authorized to delete that method.");
            } else {
                paymentDAO.deletePaymentMethodById(id);
                session.setAttribute("message", "Payment method removed.");
            }
            
            session.setAttribute("activePaymentTab", "saved-payments");
            response.sendRedirect(request.getContextPath() + "/payments/");
        } catch (SQLException | NumberFormatException e) {
            handleError(request, response, "Error deleting payment method: " + e.getMessage());
        }
    }
    
    private void viewPayment(HttpServletRequest request, HttpServletResponse response,
                             int paymentId, int userId) throws ServletException, IOException {
        try {
            PaymentBean payment = paymentDAO.getPaymentById(paymentId);
            if (payment == null) {
                response.sendRedirect(request.getContextPath() + "/payments/");
                return;
            }
            Order order = orderDAO.getOrderById(payment.getOrderID());
            if (order == null || order.getUserId() != userId) {
                response.sendRedirect(request.getContextPath() + "/payments/");
                return;
            }
            request.setAttribute("payment", payment);
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/payments/view.jsp")
                   .forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving payment: " + e.getMessage());
        }
    }
    
    private void searchPayments(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            String paymentIdStr = request.getParameter("paymentId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr   = request.getParameter("endDate");
            Integer paymentId = null;
            Date startDate = null, endDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            if (paymentIdStr != null && !paymentIdStr.isBlank()) {
                paymentId = Integer.parseInt(paymentIdStr);
            }
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = sdf.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = sdf.parse(endDateStr);
            }
            
            List<PaymentBean> payments = paymentDAO.searchPayments(userId, paymentId, startDate, endDate);
            request.setAttribute("searchPaymentId", paymentIdStr);
            request.setAttribute("searchStartDate", startDateStr);
            request.setAttribute("searchEndDate", endDateStr);
            request.setAttribute("payments", payments);
            
            session.setAttribute("activePaymentTab", "payment-history");
            response.sendRedirect(request.getContextPath() + "/payments/");
        } catch (SQLException | ParseException | NumberFormatException e) {
            handleError(request, response, "Error searching payments: " + e.getMessage());
        }
    }
    
    private void addPayment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If you have a separate addPayment action, implement here or remove if unused
        response.sendRedirect(request.getContextPath() + "/payments/");
    }
    
    private void showAddPaymentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        try {
            List<Order> orders = orderDAO.getOrdersByUserId(user.getUserId());
            List<PaymentBean> previousPayments = paymentDAO.getPaymentsByUserId(user.getUserId());
            request.setAttribute("orders", orders);
            request.setAttribute("previousPayments", previousPayments);
            request.getRequestDispatcher("/WEB-INF/views/payments/add.jsp")
                   .forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving data: " + e.getMessage());
        }
    }
    
    private void showAddPaymentFormForOrder(HttpServletRequest request, HttpServletResponse response,
                                            int orderId) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        try {
            Order order = orderDAO.getOrderById(orderId);
            if (order == null || order.getUserId() != user.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            List<PaymentBean> previousPayments = paymentDAO.getPaymentsByUserId(user.getUserId());
            request.setAttribute("order", order);
            request.setAttribute("previousPayments", previousPayments);
            request.getRequestDispatcher("/WEB-INF/views/payments/add.jsp")
                   .forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving order: " + e.getMessage());
        }
    }
    
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/views/error.jsp")
               .forward(request, response);
    }
}
