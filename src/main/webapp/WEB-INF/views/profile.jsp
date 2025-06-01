<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Profile | IoTBay</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 800px;
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
        .profile-section {
            margin-bottom: 30px;
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
        input[type="email"],
        input[type="password"],
        input[type="date"] {
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
        .success-message {
            color: green;
            background-color: #dff0d8;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        .error-message {
            color: red;
            background-color: #f2dede;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h1 style="margin: 0;">User Profile</h1>
            <a href="${pageContext.request.contextPath}/user/" class="btn">Home</a>
        </div>
        
        <c:if test="${not empty successMessage}">
            <div class="success-message">${successMessage}</div>
        </c:if>
        
        <div class="profile-section">
            <h2>Personal Information</h2>
            <form action="${pageContext.request.contextPath}/user/profile/update" method="post">
                <div class="form-group">
                    <label for="name">Full Name:</label>
                    <input type="text" id="name" name="name" value="${user.name}">
                </div>
                
                <div class="form-group">
                    <label for="email">Email Address:</label>
                    <input type="text" id="email" name="email" value="${user.email}">
                </div>
                
                <div class="form-group">
                    <label for="dateOfBirth">Date of Birth:</label>
                    <input type="text" id="dateOfBirth" name="dateOfBirth" value="${user.dateOfBirth}">
                </div>
                
                <div class="form-group">
                    <label for="address">Address:</label>
                    <input type="text" id="address" name="address" value="${user.address}">
                </div>
                
                <div class="form-group">
                    <label for="phone">Phone Number:</label>
                    <input type="text" id="phone" name="phone" value="${user.phone}">
                </div>
                
                <div class="form-group">
                    <label for="userType">Account Type:</label>
                    <input type="text" id="userType" value="${user.userType}" readonly>
                </div>
                
                <button type="submit" class="btn">Update Profile</button>
            </form>
        </div>
        
        <div class="profile-section">
            <h2>Change Password</h2>
            <form action="${pageContext.request.contextPath}/user/password/change" method="post">
                <div class="form-group">
                    <label for="currentPassword">Current Password:</label>
                    <input type="password" id="currentPassword" name="currentPassword" required>
                </div>
                
                <div class="form-group">
                    <label for="newPassword">New Password:</label>
                    <input type="password" id="newPassword" name="newPassword" required>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirm New Password:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <button type="submit" class="btn">Change Password</button>
            </form>
        </div>

        <div class="profile-section">
            <h2>Danger Zone</h2>
            <p style="color: #721c24; margin-bottom: 15px;">
                Warning: Deleting your account is permanent. All your data will be permanently removed.
            </p>
            <button type="button" class="btn" style="background-color: #dc3545;" 
                    onclick="document.getElementById('deleteAccountModal').style.display='block'">
                Delete Account
            </button>
        </div>
    </div>
    
    <!-- Delete Account Confirmation Modal -->
    <div id="deleteAccountModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
         background-color: rgba(0,0,0,0.4); z-index: 1000;">
        <div style="background-color: white; margin: 15% auto; padding: 20px; width: 50%; border-radius: 5px; box-shadow: 0 4px 8px rgba(0,0,0,0.2);">
            <h2 style="color: #721c24;">Confirm Account Deletion</h2>
            <p>Are you sure you want to delete your account? This action cannot be undone.</p>
            <p>All your data, including your profile information and access logs, will be permanently removed.</p>
            
            <form action="${pageContext.request.contextPath}/user/delete" method="post" style="margin-top: 20px;">
                <div class="form-group">
                    <label for="confirmPassword">Enter your password to confirm:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <div style="margin-top: 20px; text-align: right;">
                    <button type="button" class="btn btn-secondary" 
                            onclick="document.getElementById('deleteAccountModal').style.display='none'">
                        Cancel
                    </button>
                    <button type="submit" class="btn" style="background-color: #dc3545;">
                        Yes, Delete My Account
                    </button>
                </div>
            </form>
        </div>
    </div>

    <style>
        .error-message {
            color: #dc3545;
            font-size: 0.875em;
            margin-top: 0.25rem;
            display: block;
        }
    </style>
</body>
</html> 