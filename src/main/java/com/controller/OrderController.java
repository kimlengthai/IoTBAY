package com.controller;

import com.dao.OrderDAO;
import com.dao.DeviceDAO;
import com.model.Order;
import com.model.OrderItem;
import com.model.Device;
import com.model.User;
import com.model.CartBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.lang.StringBuilder;

/**
 * Controller servlet for handling Order-related HTTP requests
 * This is part of the Controller in MVC architecture
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders/*"})
public class OrderController extends HttpServlet {
    
    private OrderDAO orderDAO;
    private DeviceDAO deviceDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAO();
        deviceDAO = new DeviceDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract the path info to determine the action
        String pathInfo = request.getPathInfo();
        
        // Special handling for POST-REDIRECT from add_to_cart
        if (request.getParameter("action") != null && request.getParameter("action").equals("add_to_cart")) {
            response.sendRedirect(request.getContextPath() + "/devices");
            return;
        }
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // List all orders (/orders/)
            listOrders(request, response);
        } else if (pathInfo.equals("/user")) {
            // List orders for the current user (/orders/user)
            listUserOrders(request, response);
        } else if (pathInfo.equals("/search")) {
            // Search orders (/orders/search)
            searchUserOrders(request, response);
        } else if (pathInfo.equals("/new")) {
            // Show form to create a new order (/orders/new)
            showNewOrderForm(request, response);
        } else if (pathInfo.equals("/cart")) {
            // Show shopping cart (/orders/cart)
            viewCart(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // View a specific order (/orders/view/{id})
            int orderId = Integer.parseInt(pathInfo.substring(6));
            viewOrder(request, response, orderId);
        } else if (pathInfo.startsWith("/edit/")) {
            // Show form to edit an order (/orders/edit/{id})
            int orderId = Integer.parseInt(pathInfo.substring(6));
            showEditOrderForm(request, response, orderId);
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/orders/");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/orders/");
            return;
        }
        
        switch (action) {
            case "add_to_cart":
                addToCart(request, response);
                break;
            case "update_cart":
                updateCart(request, response);
                break;
            case "remove_from_cart":
                removeFromCart(request, response);
                break;
            case "clear_cart":
                clearCart(request, response);
                break;
            case "save_cart":
                saveCart(request, response);
                break;
            case "create_order":
                createOrder(request, response);
                break;
            case "update_order":
                updateOrder(request, response);
                break;
            case "update_status":
                updateOrderStatus(request, response);
                break;
            case "delete_order":
                deleteOrder(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/orders/");
                break;
        }
    }
    
    // Handler methods for GET requests
    
    private void listOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Check if user is staff/admin
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !user.isStaff()) {
                // Redirect non-staff users to their own orders
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            
            List<Order> orders = orderDAO.getAllOrders();
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving orders: " + e.getMessage());
        }
    }
    
    private void listUserOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<Order> orders = orderDAO.getOrdersByUserId(user.getUserId());
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/orders/user_orders.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving user orders: " + e.getMessage());
        }
    }
    
    private void showNewOrderForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<Device> devices = deviceDAO.getAllDevices();
            request.setAttribute("devices", devices);
            request.getRequestDispatcher("/WEB-INF/views/orders/new.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving devices: " + e.getMessage());
        }
    }
    
    private void viewCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<OrderItem> cart = getCartFromSession(session);
        
        // Initialize or update the CartBean
        CartBean cartBean = (CartBean) session.getAttribute("cartBean");
        if (cartBean == null) {
            cartBean = new CartBean();
            session.setAttribute("cartBean", cartBean);
        }
        
        // Calculate the total amount
        cartBean.calculateTotalAmount(cart);
        
        // Check if user is logged in
        User user = (User) session.getAttribute("user");
        request.setAttribute("isAnonymous", user == null);
        
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/views/orders/cart.jsp").forward(request, response);
    }
    
    private void viewOrder(HttpServletRequest request, HttpServletResponse response, int orderId) throws ServletException, IOException {
        try {
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/orders/");
                return;
            }
            
            // Check if user has permission to view this order
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Special handling for guest orders (userId = 0)
            if (order.getUserId() == 0) {
                // Guest orders can only be viewed by staff or immediately after creation
                if (user != null && user.isStaff()) {
                    request.setAttribute("order", order);
                    request.setAttribute("isGuestOrder", true);
                    request.getRequestDispatcher("/WEB-INF/views/orders/view.jsp").forward(request, response);
                    return;
                } else {
                    // Non-staff users can't view guest orders unless they just created it
                    response.sendRedirect(request.getContextPath() + "/devices");
                    return;
                }
            }
            
            // Regular user order access check
            if (user == null || (!user.isStaff() && user.getUserId() != order.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/orders/view.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving order: " + e.getMessage());
        }
    }
    
    private void showEditOrderForm(HttpServletRequest request, HttpServletResponse response, int orderId) throws ServletException, IOException {
        try {
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/orders/");
                return;
            }
            
            // Check if user has permission to edit this order
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || (!user.isStaff() && user.getUserId() != order.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            
            // Check if order is editable
            if (!order.isEditable()) {
                request.setAttribute("errorMessage", "This order cannot be edited because it is already " + order.getOrderStatus());
                viewOrder(request, response, orderId);
                return;
            }
            
            List<Device> devices = deviceDAO.getAllDevices();
            request.setAttribute("devices", devices);
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/orders/edit.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving order: " + e.getMessage());
        }
    }
    
    // Handler methods for POST requests
    
    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<OrderItem> cart = getCartFromSession(session);
        CartBean cartBean = (CartBean) session.getAttribute("cartBean");
        if (cartBean == null) {
            cartBean = new CartBean();
            session.setAttribute("cartBean", cartBean);
        }
        
        try {
            int deviceId = Integer.parseInt(request.getParameter("deviceId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            // Server-side validation
            if (quantity <= 0) {
                session.setAttribute("errorMessage", "Quantity must be greater than zero");
                response.sendRedirect(request.getContextPath() + "/devices");
                return;
            }
            
            Device device = deviceDAO.getDeviceById(deviceId);
            
            if (device == null) {
                session.setAttribute("errorMessage", "Device not found");
                response.sendRedirect(request.getContextPath() + "/devices");
                return;
            }
            
            if (!device.isQuantityAvailable(quantity)) {
                session.setAttribute("errorMessage", "Only " + device.getStockQuantity() + " units available");
                response.sendRedirect(request.getContextPath() + "/devices");
                return;
            }
            
            // Check if the device is already in the cart
            boolean found = false;
            for (OrderItem item : cart) {
                if (item.getDeviceId() == deviceId) {
                    int newQuantity = item.getQuantity() + quantity;
                    if (device.isQuantityAvailable(newQuantity)) {
                        // Update stock quantity in DB
                        int updatedStock = device.getStockQuantity() - quantity;
                        deviceDAO.updateStockQuantity(deviceId, updatedStock);
                        
                        // Update the device in memory with the new stock quantity
                        device.setStockQuantity(updatedStock);
                        
                        // Update item quantity
                        item.setQuantity(newQuantity);
                        found = true;
                    } else {
                        session.setAttribute("errorMessage", "Cannot add more. Only " + device.getStockQuantity() + " units available");
                        response.sendRedirect(request.getContextPath() + "/devices");
                        return;
                    }
                    break;
                }
            }
            
            if (!found) {
                // Update stock quantity in DB
                int updatedStock = device.getStockQuantity() - quantity;
                deviceDAO.updateStockQuantity(deviceId, updatedStock);
                
                // Update the device in memory with the new stock quantity
                device.setStockQuantity(updatedStock);
                
                // Add to cart
                OrderItem item = new OrderItem();
                item.setDeviceId(deviceId);
                item.setQuantity(quantity);
                item.setPrice(device.getPrice());
                item.setDevice(device);
                cart.add(item);
            }
            
            // Update cart total amount
            cartBean.calculateTotalAmount(cart);
            
            session.setAttribute("cart", cart);
            session.setAttribute("successMessage", device.getDeviceName() + " added to cart successfully!");
            
            // Redirect back to the devices page
            response.sendRedirect(request.getContextPath() + "/devices");
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid device ID or quantity");
            response.sendRedirect(request.getContextPath() + "/devices");
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error adding to cart: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/devices");
        }
    }
    
    private void updateCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<OrderItem> cart = getCartFromSession(session);
        CartBean cartBean = (CartBean) session.getAttribute("cartBean");
        if (cartBean == null) {
            cartBean = new CartBean();
            session.setAttribute("cartBean", cartBean);
        }
        
        try {
            String[] deviceIds = request.getParameterValues("deviceId");
            String[] quantities = request.getParameterValues("quantity");
            
            if (deviceIds == null || quantities == null || deviceIds.length != quantities.length) {
                session.setAttribute("errorMessage", "Invalid update request");
                response.sendRedirect(request.getContextPath() + "/orders/cart");
                return;
            }
            
            // First, create a copy of the current cart to track changes
            List<OrderItem> oldCart = new ArrayList<>();
            for (OrderItem item : cart) {
                OrderItem oldItem = new OrderItem();
                oldItem.setDeviceId(item.getDeviceId());
                oldItem.setQuantity(item.getQuantity());
                oldItem.setPrice(item.getPrice());
                oldItem.setDevice(item.getDevice());
                oldCart.add(oldItem);
            }
            
            for (int i = 0; i < deviceIds.length; i++) {
                String deviceIdStr = deviceIds[i];
                String quantityStr = quantities[i];
                
                // Validate device ID
                if (!deviceIdStr.matches("\\d+")) {
                    session.setAttribute("errorMessage", "Invalid device ID format");
                    response.sendRedirect(request.getContextPath() + "/orders/cart");
                    return;
                }
                
                // Validate quantity
                if (!quantityStr.matches("\\d+")) {
                    session.setAttribute("errorMessage", "Quantity must be a positive number");
                    response.sendRedirect(request.getContextPath() + "/orders/cart");
                    return;
                }
                
                int deviceId = Integer.parseInt(deviceIdStr);
                int quantity = Integer.parseInt(quantityStr);
                
                // Find the old quantity for this device
                int oldQuantity = 0;
                for (OrderItem item : oldCart) {
                    if (item.getDeviceId() == deviceId) {
                        oldQuantity = item.getQuantity();
                        break;
                    }
                }
                
                if (quantity <= 0) {
                    // Remove the item if quantity is 0 or negative
                    // First, restore the stock quantity
                    Device device = deviceDAO.getDeviceById(deviceId);
                    if (device != null) {
                        int newStockQuantity = device.getStockQuantity() + oldQuantity;
                        deviceDAO.updateStockQuantity(deviceId, newStockQuantity);
                        
                        // Update the device in memory with the new stock quantity
                        device.setStockQuantity(newStockQuantity);
                    }
                    
                    cart.removeIf(item -> item.getDeviceId() == deviceId);
                    continue;
                }
                
                Device device = deviceDAO.getDeviceById(deviceId);
                if (device == null) {
                    session.setAttribute("errorMessage", "Device not found");
                    response.sendRedirect(request.getContextPath() + "/orders/cart");
                    return;
                }
                
                // Calculate the quantity difference
                int quantityDifference = quantity - oldQuantity;
                
                // Check if increase in quantity is available
                if (quantityDifference > 0 && !device.isQuantityAvailable(quantityDifference)) {
                    session.setAttribute("errorMessage", "Device " + device.getDeviceName() + " only has " + device.getStockQuantity() + " units available to add");
                    response.sendRedirect(request.getContextPath() + "/orders/cart");
                    return;
                }
                
                // Update the stock quantity in the database
                int newStockQuantity = device.getStockQuantity() - quantityDifference;
                deviceDAO.updateStockQuantity(deviceId, newStockQuantity);
                
                // Update the device in memory with the new stock quantity
                device.setStockQuantity(newStockQuantity);
                
                // Update the item quantity in the cart
                for (OrderItem item : cart) {
                    if (item.getDeviceId() == deviceId) {
                        item.setQuantity(quantity);
                        break;
                    }
                }
            }
            
            // Update cart total amount after any changes
            cartBean.calculateTotalAmount(cart);
            
            session.setAttribute("cart", cart);
            session.setAttribute("successMessage", "Cart updated successfully");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid input format");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error updating cart: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        }
    }
    
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<OrderItem> cart = getCartFromSession(session);
        
        try {
            int deviceId = Integer.parseInt(request.getParameter("deviceId"));
            
            // Find the item to remove and restore its quantity
            for (OrderItem item : cart) {
                if (item.getDeviceId() == deviceId) {
                    // Get current quantity of the item in the cart
                    int quantityToRestore = item.getQuantity();
                    
                    // Get the device and restore its stock quantity
                    Device device = deviceDAO.getDeviceById(deviceId);
                    if (device != null) {
                        int newStockQuantity = device.getStockQuantity() + quantityToRestore;
                        deviceDAO.updateStockQuantity(deviceId, newStockQuantity);
                        
                        // Update the device in memory with the new stock quantity
                        device.setStockQuantity(newStockQuantity);
                    }
                    
                    break;
                }
            }
            
            // Remove the item from the cart
            cart.removeIf(item -> item.getDeviceId() == deviceId);
            
            session.setAttribute("cart", cart);
            session.setAttribute("successMessage", "Item removed from cart successfully");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid device ID");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error removing item from cart: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        }
    }
    
    private void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // If user is logged in, also clear saved cart in database
        if (user != null) {
            try {
                orderDAO.clearSavedCart(user.getUserId());
            } catch (SQLException e) {
                System.err.println("Error clearing saved cart from database: " + e.getMessage());
            }
        }
        
        // Simply clear the cart in the session
        session.removeAttribute("cart");
        session.removeAttribute("cartBean");
        
        session.setAttribute("successMessage", "Cart cleared successfully");
        response.sendRedirect(request.getContextPath() + "/orders/cart");
    }
    
    private void saveCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<OrderItem> cart = getCartFromSession(session);
        
        if (user == null) {
            session.setAttribute("errorMessage", "You must be logged in to save your cart");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
            return;
        }
        
        if (cart == null || cart.isEmpty()) {
            session.setAttribute("errorMessage", "Cannot save an empty cart");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
            return;
        }
        
        try {
            // First, clear any existing saved cart for this user
            orderDAO.clearSavedCart(user.getUserId());
            
            // Then save the current cart
            for (OrderItem item : cart) {
                orderDAO.saveCartItem(user.getUserId(), item);
            }
            
            session.setAttribute("successMessage", "Cart saved successfully");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error saving cart: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        }
    }
    
    private void createOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<OrderItem> cart = getCartFromSession(session);
        
        if (cart == null || cart.isEmpty()) {
            session.setAttribute("errorMessage", "Your cart is empty");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
            return;
        }
        
        // Validate shipping address
        String shippingAddress = request.getParameter("shippingAddress");
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Shipping address is required");
            response.sendRedirect(request.getContextPath() + "/orders/cart");
            return;
        }
        
        // For anonymous user, collect customer info
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        
        // Validate guest checkout fields if user is anonymous
        if (user == null) {
            if (name == null || name.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Name is required for guest checkout");
                response.sendRedirect(request.getContextPath() + "/orders/cart");
                return;
            }
            
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                session.setAttribute("errorMessage", "Valid email is required for guest checkout");
                response.sendRedirect(request.getContextPath() + "/orders/cart");
                return;
            }
        }
        
        try {
            // Double-check product availability (in case it changed since added to cart)
            // This is just a validation check, we don't need to modify stock again as it was
            // already reduced when items were added to the cart
            for (OrderItem item : cart) {
                Device device = deviceDAO.getDeviceById(item.getDeviceId());
                if (device == null) {
                    session.setAttribute("errorMessage", "One of the products in your cart is no longer available");
                    response.sendRedirect(request.getContextPath() + "/orders/cart");
                    return;
                }
            }
            
            // Create new order
            Order order = new Order();
            
            // If logged in, associate with user account
            if (user != null) {
                order.setUserId(user.getUserId());
            } else {
                // For anonymous users, use a special guest user ID (0)
                // and store customer info in shipping address
                order.setUserId(0);
                order.setShippingAddress("GUEST ORDER - Name: " + name + ", Email: " + email + "\n" + shippingAddress);
            }
            
            order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
            if (user != null) {
                order.setShippingAddress(shippingAddress);
            }
            order.setOrderStatus(Order.STATUS_SUBMITTED);
            
            // Add items to the order
            for (OrderItem item : cart) {
                // Only add items with quantity > 0
                if (item.getQuantity() > 0) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setDeviceId(item.getDeviceId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setDevice(item.getDevice());
                    order.addOrderItem(orderItem); // This will also trigger order.calculateTotalAmount()
                }
            }
            
            // Save order to database
            orderDAO.createOrder(order);
            
            // Clear the cart without restoring quantities
            // since we already reduced inventory when items were added to cart
            session.removeAttribute("cart");
            session.removeAttribute("cartBean");
            
            // Save confirmation message
            session.setAttribute("successMessage", "Order placed successfully! Order ID: " + order.getOrderId());
            
            // Redirect based on user type
            if (user != null) {
                response.sendRedirect(request.getContextPath() + "/orders/view/" + order.getOrderId());
            } else {
                // For anonymous users, redirect to a confirmation page or back to devices
                response.sendRedirect(request.getContextPath() + "/devices");
            }
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error creating order: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders/cart");
        }
    }
    
    private void updateOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            Order existingOrder = orderDAO.getOrderById(orderId);
            
            if (existingOrder == null) {
                response.sendRedirect(request.getContextPath() + "/orders/");
                return;
            }
            
            // Check if user has permission to edit this order
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || (!user.isStaff() && user.getUserId() != existingOrder.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            
            // Check if order is editable
            if (!existingOrder.isEditable()) {
                request.setAttribute("errorMessage", "This order cannot be edited because it is already " + existingOrder.getOrderStatus());
                viewOrder(request, response, orderId);
                return;
            }
            
            String shippingAddress = request.getParameter("shippingAddress");
            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Shipping address is required");
                showEditOrderForm(request, response, orderId);
                return;
            }
            
            existingOrder.setShippingAddress(shippingAddress);
            
            // Update order items if form contains item information
            String[] deviceIds = request.getParameterValues("deviceId");
            String[] quantities = request.getParameterValues("quantity");
            
            if (deviceIds != null && quantities != null && deviceIds.length == quantities.length) {
                List<OrderItem> items = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;
                
                for (int i = 0; i < deviceIds.length; i++) {
                    int deviceId = Integer.parseInt(deviceIds[i]);
                    int quantity = Integer.parseInt(quantities[i]);
                    
                    if (quantity <= 0) {
                        continue;
                    }
                    
                    Device device = deviceDAO.getDeviceById(deviceId);
                    if (!device.isQuantityAvailable(quantity)) {
                        request.setAttribute("errorMessage", "Device " + device.getDeviceName() + " only has " + device.getStockQuantity() + " units available");
                        showEditOrderForm(request, response, orderId);
                        return;
                    }
                    
                    OrderItem item = new OrderItem();
                    item.setOrderId(orderId);
                    item.setDeviceId(deviceId);
                    item.setQuantity(quantity);
                    item.setPrice(device.getPrice());
                    item.setDevice(device);
                    items.add(item);
                    
                    BigDecimal itemTotal = device.getPrice().multiply(new BigDecimal(quantity));
                    total = total.add(itemTotal);
                }
                
                if (items.isEmpty()) {
                    request.setAttribute("errorMessage", "Order must contain at least one item");
                    showEditOrderForm(request, response, orderId);
                    return;
                }
                
                existingOrder.setOrderItems(items);
                existingOrder.setTotalAmount(total);
            }
            
            // Update the order
            orderDAO.updateOrder(existingOrder);
            
            // Redirect to order view
            response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders/");
        } catch (SQLException e) {
            handleError(request, response, "Error updating order: " + e.getMessage());
        }
    }
    
    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String status = request.getParameter("status");
            
            if (!isValidStatus(status)) {
                response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
                return;
            }
            
            // Check if user is staff
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !user.isStaff()) {
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            
            orderDAO.updateOrderStatus(orderId, status);
            response.sendRedirect(request.getContextPath() + "/orders/view/" + orderId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders/");
        } catch (SQLException e) {
            handleError(request, response, "Error updating order status: " + e.getMessage());
        }
    }
    
    private void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/orders/");
                return;
            }
            
            // Check if user has permission to delete this order
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || (!user.isStaff() && user.getUserId() != order.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/orders/user");
                return;
            }
            
            // Regular users can only cancel pending orders
            if (!user.isStaff() && !order.getOrderStatus().equals(Order.STATUS_SUBMITTED)) {
                request.setAttribute("errorMessage", "You can only cancel orders that are pending");
                viewOrder(request, response, orderId);
                return;
            }
            
            // Staff can delete any order, users can only cancel their orders
            if (user.isStaff()) {
                orderDAO.deleteOrder(orderId);
            } else {
                // For users, we just update the status to cancelled
                order.setOrderStatus(Order.STATUS_CANCELLED);
                orderDAO.updateOrder(order);
            }
            
            // Redirect based on user role
            if (user.isStaff()) {
                response.sendRedirect(request.getContextPath() + "/orders/");
            } else {
                response.sendRedirect(request.getContextPath() + "/orders/user");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders/");
        } catch (SQLException e) {
            handleError(request, response, "Error deleting order: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    private List<OrderItem> getCartFromSession(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        
        return cart;
    }
    
    private boolean isValidStatus(String status) {
        return status != null && (
            status.equals(Order.STATUS_DRAFT) ||
            status.equals(Order.STATUS_SAVED) ||
            status.equals(Order.STATUS_SUBMITTED) ||
            status.equals(Order.STATUS_PROCESSING) ||
            status.equals(Order.STATUS_SHIPPED) ||
            status.equals(Order.STATUS_DELIVERED) ||
            status.equals(Order.STATUS_CANCELLED)
        );
    }
    
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }

    /**
     * Search orders for a user based on order ID and date range
     */
    private void searchUserOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Get search parameters
            String orderIdStr = request.getParameter("orderId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            // Validation flags and messages
            boolean hasValidationError = false;
            String errorMessage = "";
            
            // Convert parameters
            int orderId = 0;
            java.sql.Date startDate = null;
            java.sql.Date endDate = null;
            
            // Validate Order ID
            if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
                try {
                    orderId = Integer.parseInt(orderIdStr);
                    if (orderId <= 0) {
                        hasValidationError = true;
                        errorMessage += "Order ID must be a positive number. ";
                    }
                } catch (NumberFormatException e) {
                    hasValidationError = true;
                    errorMessage += "Invalid Order ID format. ";
                }
            }
            
            // Validate Start Date
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                try {
                    java.util.Date utilDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
                    startDate = new java.sql.Date(utilDate.getTime());
                } catch (Exception e) {
                    hasValidationError = true;
                    errorMessage += "Invalid start date format. ";
                }
            }
            
            // Validate End Date
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                try {
                    java.util.Date utilDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
                    endDate = new java.sql.Date(utilDate.getTime());
                } catch (Exception e) {
                    hasValidationError = true;
                    errorMessage += "Invalid end date format. ";
                }
            }
            
            // Validate Date Range
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                hasValidationError = true;
                errorMessage += "Start date must be before end date. ";
            }
            
            List<Order> orders;
            
            // If validation errors exist, show error and return default list
            if (hasValidationError) {
                request.setAttribute("errorMessage", errorMessage);
                orders = orderDAO.getOrdersByUserId(user.getUserId());
            } else {
                // Perform search
                orders = orderDAO.searchOrders(user.getUserId(), orderId, startDate, endDate);
                
                // Check if any search parameters were provided
                boolean hasSearchParams = (orderIdStr != null && !orderIdStr.trim().isEmpty()) || 
                                         (startDateStr != null && !startDateStr.trim().isEmpty()) || 
                                         (endDateStr != null && !endDateStr.trim().isEmpty());
                
                // If search parameters were provided, create search results message
                if (hasSearchParams) {
                    StringBuilder searchResults = new StringBuilder("<strong>Search Results:</strong> ");
                    
                    if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
                        searchResults.append("Order ID: ").append(orderIdStr).append(" ");
                    }
                    
                    if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                        if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
                            searchResults.append("| ");
                        }
                        searchResults.append("From: ").append(startDateStr).append(" ");
                    }
                    
                    if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                        if ((orderIdStr != null && !orderIdStr.trim().isEmpty()) || 
                            (startDateStr != null && !startDateStr.trim().isEmpty())) {
                            searchResults.append("| ");
                        }
                        searchResults.append("To: ").append(endDateStr);
                    }
                    
                    searchResults.append(" (Found ").append(orders.size()).append(" orders)");
                    request.setAttribute("searchResults", searchResults.toString());
                }
            }
            
            // Set orders attribute for the view
            request.setAttribute("orders", orders);
            
            // Forward to the user orders page
            request.getRequestDispatcher("/WEB-INF/views/orders/user_orders.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error searching orders: " + e.getMessage());
        }
    }
} 