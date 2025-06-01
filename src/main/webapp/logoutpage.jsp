<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Invalidate the current session
    session.invalidate();
    
    // Redirect to the landing page
    response.sendRedirect("Landingpage.jsp");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Logging Out...</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                margin: 0;
                padding: 20px;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
            }
            .logout-container {
                background-color: white;
                padding: 30px;
                border-radius: 5px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                text-align: center;
            }
            .spinner-border {
                width: 3rem;
                height: 3rem;
                margin: 20px 0;
            }
        </style>
    </head>
    <body>
        <div class="logout-container">
            <h2>Logging out...</h2>
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p>You will be redirected to the landing page shortly.</p>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
