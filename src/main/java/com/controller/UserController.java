package com.controller;

import com.dao.AccessLogDAO;
import com.dao.UserDAO;
import com.model.User;
import com.model.AccessLog;
import com.dao.OrderDAO;
import com.model.OrderItem;
import com.model.CartBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

/**
 * Controller servlet for handling user-related operations
 * This is part of the Controller in MVC architecture
 */
@WebServlet("/user/*")
public class UserController extends HttpServlet {
    
    private final UserDAO userDAO;
    private final AccessLogDAO accessLogDAO;
    
    /**
     * Constructor initializing the DAOs
     */
    public UserController() {
        this.userDAO = new UserDAO();
        this.accessLogDAO = new AccessLogDAO();
    }
    
    /**
     * Handles GET requests for user-related operations
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Default - show user dashboard or redirect to login
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                // User is logged in, show dashboard
                request.getRequestDispatcher("/WEB-INF/views/user-dashboard.jsp").forward(request, response);
            } else {
                // User not logged in, redirect to login page
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        } else if (pathInfo.equals("/login")) {
            // Show login page
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if (pathInfo.equals("/register")) {
            // Show registration page
            request.getRequestDispatcher("/registerpage.jsp").forward(request, response);
        } else if (pathInfo.equals("/logout")) {
            // Handle logout
            handleLogout(request, response);
        } else if (pathInfo.equals("/profile")) {
            // Show user profile
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        } else if (pathInfo.equals("/accesslogs")) {
            // Show access logs (for the user or all logs for admin)
            handleAccessLogs(request, response);
        } else {
            // Invalid path, show 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Handles POST requests for user-related operations
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Default action - do nothing
            response.sendRedirect(request.getContextPath() + "/user/");
        } else if (pathInfo.equals("/login")) {
            // Handle login form submission
            handleLogin(request, response);
        } else if (pathInfo.equals("/register")) {
            // Handle registration form submission
            handleRegistration(request, response);
        } else if (pathInfo.equals("/profile/update")) {
            // Handle profile update
            handleProfileUpdate(request, response);
        } else if (pathInfo.equals("/password/change")) {
            // Handle password change
            handlePasswordChange(request, response);
        } else if (pathInfo.equals("/delete")) {
            // Handle account deletion
            handleAccountDeletion(request, response);
        } else {
            // Invalid path, show 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Handles user login
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate inputs
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user
        User user = userDAO.authenticateUser(email, password);
        
        if (user != null) {
            // Authentication successful
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            // Record login in access logs
            int logId = accessLogDAO.recordLogin(user.getUserId());
            session.setAttribute("accessLogId", logId);
            
            // Load saved cart items if they exist
            try {
                OrderDAO orderDAO = new OrderDAO();
                List<OrderItem> savedCartItems = orderDAO.getSavedCartItems(user.getUserId());
                
                if (savedCartItems != null && !savedCartItems.isEmpty()) {
                    session.setAttribute("cart", savedCartItems);
                    
                    // Initialize or update the CartBean
                    CartBean cartBean = new CartBean();
                    cartBean.calculateTotalAmount(savedCartItems);
                    session.setAttribute("cartBean", cartBean);
                    
                    session.setAttribute("successMessage", "Your saved cart items have been loaded");
                }
            } catch (SQLException e) {
                // Log the error but don't prevent login
                System.err.println("Error loading saved cart: " + e.getMessage());
            }
            
            response.sendRedirect(request.getContextPath() + "/user/");
            
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid email or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles user registration
     */
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String userType = "Customer"; // Default to Customer
        
        // Validate inputs
        StringBuilder errorMessage = new StringBuilder();
        
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Name is required. ");
        }
        
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            errorMessage.append("Valid email is required. ");
        } else {
            // Check if email already exists
            User existingUser = userDAO.getUserByEmail(email);
            if (existingUser != null) {
                errorMessage.append("Email already in use. ");
            }
        }
        
        if (password == null || password.trim().isEmpty()) {
            errorMessage.append("Password is required. ");
        } else if (!password.equals(confirmPassword)) {
            errorMessage.append("Passwords do not match. ");
        }
        
        if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
            try {
                Date dateOfBirth = Date.valueOf(dateOfBirthStr);
                // Check if date of birth is in the future
                Date currentDate = new Date(System.currentTimeMillis());
                if (dateOfBirth.after(currentDate)) {
                    errorMessage.append("Date of birth cannot be in the future. ");
                }
            } catch (IllegalArgumentException e) {
                errorMessage.append("Invalid date format. Please use YYYY-MM-DD format. ");
            }
        }
        
        if (phone != null && !phone.trim().isEmpty()) {
            // Validate phone number contains only digits and spaces
            if (!phone.replaceAll("\\s+", "").matches("\\d+")) {
                errorMessage.append("Phone number must contain only digits and spaces. ");
            }
        }
        
        if (address != null && !address.trim().isEmpty()) {
            // Validate address format: must start with numbers followed by letters
            if (!address.matches("^\\d+\\s+[a-zA-Z\\s]+$")) {
                errorMessage.append("Address must start with numbers followed by letters (e.g., 123 William Street). ");
            }
        }
        
        // If there are validation errors, return to registration page
        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.setAttribute("address", address);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/registerpage.jsp").forward(request, response);
            return;
        }
        
        // Create User object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserType(userType);
        
        // Set optional fields if provided
        if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
            try {
                Date dateOfBirth = Date.valueOf(dateOfBirthStr);
                user.setDateOfBirth(dateOfBirth);
            } catch (IllegalArgumentException e) {
                // Invalid date format, just ignore it
            }
        }
        
        if (address != null && !address.trim().isEmpty()) {
            user.setAddress(address);
        }
        
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone);
        }
        
        // Save the user to database
        int userId = userDAO.createUser(user);
        
        if (userId > 0) {
            // Registration successful
            user.setUserId(userId);
            
            // Auto-login the user
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            // Record login in access logs
            int logId = accessLogDAO.recordLogin(userId);
            session.setAttribute("accessLogId", logId);
            
            // Redirect to user dashboard
            response.sendRedirect(request.getContextPath() + "/user/");
        } else {
            // Registration failed
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.getRequestDispatcher("/registerpage.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles user logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Record logout in access logs
            Integer logId = (Integer) session.getAttribute("accessLogId");
            if (logId != null) {
                accessLogDAO.recordLogout(logId);
            }
            
            // Invalidate session
            session.invalidate();
        }
        
        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
    
    /**
     * Handles displaying access logs
     */
    private void handleAccessLogs(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        List<com.model.AccessLog> accessLogs;
        
        // Get date filter parameters
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        try {
            if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                // Convert string dates to SQL Date objects
                Date startDate = Date.valueOf(startDateStr);
                Date endDate = Date.valueOf(endDateStr);
                
                // Get logs for the current user (both staff and regular users)
                List<AccessLog> userLogs = accessLogDAO.getAccessLogsByUserId(user.getUserId());
                accessLogs = userLogs.stream()
                    .filter(log -> {
                        Date logDate = new Date(log.getLoginTime().getTime());
                        return !logDate.before(startDate) && !logDate.after(endDate);
                    })
                    .collect(Collectors.toList());
            } else {
                // No date filter, get all logs for the current user
                accessLogs = accessLogDAO.getAccessLogsByUserId(user.getUserId());
            }
        } catch (IllegalArgumentException e) {
            // Invalid date format, show error and get all logs
            request.setAttribute("errorMessage", "Invalid date format. Please use YYYY-MM-DD format.");
            accessLogs = accessLogDAO.getAccessLogsByUserId(user.getUserId());
        }
        
        // Set access logs in request attribute
        request.setAttribute("accessLogs", accessLogs);
        request.setAttribute("user", user); // Needed for the JSP to determine if user is staff
        
        // Forward to access logs JSP
        request.getRequestDispatcher("/WEB-INF/views/access-logs.jsp").forward(request, response);
    }
    
    /**
     * Handles profile update
     */
    private void handleProfileUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        
        // Validate inputs
        StringBuilder errorMessage = new StringBuilder();
        
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Name is required. ");
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            errorMessage.append("Valid email is required. ");
        } else if (!email.equals(user.getEmail())) {
            // Check if new email already exists (only if email is being changed)
            User existingUser = userDAO.getUserByEmail(email);
            if (existingUser != null) {
                errorMessage.append("Email already in use. ");
            }
        }
        
        // Date of birth validation
        if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
            try {
                Date dateOfBirth = Date.valueOf(dateOfBirthStr);
                Date currentDate = new Date(System.currentTimeMillis());
                if (dateOfBirth.after(currentDate)) {
                    errorMessage.append("Date of birth cannot be in the future. ");
                } else {
                    user.setDateOfBirth(dateOfBirth);
                }
            } catch (IllegalArgumentException e) {
                errorMessage.append("Invalid date format. Please use YYYY-MM-DD format. ");
            }
        }
        
        // Phone number validation
        if (phone != null && !phone.trim().isEmpty()) {
            // Remove all spaces and check if it's a valid number
            String cleanPhone = phone.replaceAll("\\s+", "");
            if (!cleanPhone.matches("\\d+")) {
                errorMessage.append("Phone number must contain only digits and spaces. ");
            } else {
                user.setPhone(phone);
            }
        }
        
        // If there are validation errors, return to profile page
        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }
        
        // Update user object with validated data
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        
        if (address != null) {
            user.setAddress(address);
        }
        
        // Save the updated user to database
        boolean success = userDAO.updateUser(user);
        
        if (success) {
            // Update successful
            session.setAttribute("user", user);
            request.setAttribute("successMessage", "Profile updated successfully");
        } else {
            // Update failed
            request.setAttribute("errorMessage", "Profile update failed");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
    
    /**
     * Handles password change
     */
    private void handlePasswordChange(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Get form parameters
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate inputs
        StringBuilder errorMessage = new StringBuilder();
        
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            errorMessage.append("Current password is required. ");
        } else if (!currentPassword.equals(user.getPassword())) {
            errorMessage.append("Current password is incorrect. ");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            errorMessage.append("New password is required. ");
        } else if (!newPassword.equals(confirmPassword)) {
            errorMessage.append("New passwords do not match. ");
        }
        
        // If there are validation errors, return to profile page
        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }
        
        // Update password
        user.setPassword(newPassword);
        boolean success = userDAO.updateUser(user);
        
        if (success) {
            // Password change successful
            session.setAttribute("user", user);
            request.setAttribute("successMessage", "Password changed successfully");
        } else {
            // Password change failed
            request.setAttribute("errorMessage", "Password change failed");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
    
    /**
     * Handles user account deletion
     */
    private void handleAccountDeletion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate password
        if (confirmPassword == null || confirmPassword.trim().isEmpty() || !confirmPassword.equals(user.getPassword())) {
            request.setAttribute("errorMessage", "Incorrect password. Account deletion canceled.");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }
        
        // Delete user
        boolean success = userDAO.deleteUser(user.getUserId());
        
        if (success) {
            // Record logout in access logs
            Integer logId = (Integer) session.getAttribute("accessLogId");
            if (logId != null) {
                accessLogDAO.recordLogout(logId);
            }
            
            // Invalidate session
            session.invalidate();
            
            // Redirect to a confirmation page
            request.setAttribute("successMessage", "Your account has been successfully deleted.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            // Deletion failed
            request.setAttribute("errorMessage", "Account deletion failed. Please try again or contact support.");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        }
    }
} 