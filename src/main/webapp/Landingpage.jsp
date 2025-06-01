<%-- 
    Document   : Landingpage
    Created on : 28 Mar 2025, 11:30:14 am
    Author     : yadupillai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>IoTBay - Welcome</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .hero-section {
                background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://images.unsplash.com/photo-1451187580459-43490279c0fa?ixlib=rb-4.0.3');
                background-size: cover;
                background-position: center;
                height: 80vh;
                display: flex;
                align-items: center;
                color: white;
            }
            .nav-link {
                color: #333;
                font-weight: 500;
            }
            .nav-link:hover {
                color: #007bff;
            }
            .btn-primary {
                padding: 10px 25px;
                margin: 0 10px;
            }
            .btn-browse {
                background-color: #28a745;
                border-color: #28a745;
            }
            .btn-browse:hover {
                background-color: #218838;
                border-color: #1e7e34;
            }
        </style>
    </head>
    <body>
        <!-- Navigation -->
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <div class="container">
                <a class="navbar-brand" href="#">IoTBay</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/devices">Browse Devices</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/orders/cart">View Cart</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="login.jsp">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="registerpage.jsp">Register</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- Hero Section -->
        <section class="hero-section">
            <div class="container text-center">
                <h1 class="display-4 mb-4">Welcome to IoTBay</h1>
                <p class="lead mb-4">Your one-stop shop for all IoT devices and accessories</p>
                <div>
                    <a href="login.jsp" class="btn btn-primary">Login</a>
                    <a href="registerpage.jsp" class="btn btn-outline-light">Register</a>
                </div>
            </div>
        </section>

        <!-- Features Section -->
        <section class="py-5">
            <div class="container">
                <div class="row">
                    <div class="col-md-4 text-center mb-4">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="card-title">Wide Selection</h5>
                                <p class="card-text">Browse through our extensive collection of IoT devices and accessories.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 text-center mb-4">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="card-title">Secure Shopping</h5>
                                <p class="card-text">Shop with confidence with our secure payment system and data protection.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 text-center mb-4">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="card-title">Guest Checkout</h5>
                                <p class="card-text">Shop as a guest without registration or login in.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Footer -->
        <footer class="bg-light py-4">
            <div class="container text-center">
                <p class="mb-0">&copy; 2024 IoTBay. All rights reserved.</p>
            </div>
        </footer>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
