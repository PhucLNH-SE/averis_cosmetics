<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Profile - Averis Cosmetics</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    <style>
        .profile-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 30px;
            border: 1px solid #e9e2d8;
            border-radius: 12px;
            background: white;
            box-shadow: 0 8px 22px rgba(31, 41, 55, .06);
        }
        
        .profile-header {
            text-align: center;
            margin-bottom: 30px;
            border-bottom: 1px solid #e9e2d8;
            padding-bottom: 20px;
        }
        
        .profile-header h2 {
            color: #1f2937;
            margin: 0;
            font-size: 28px;
        }
        
        .profile-info {
            margin-bottom: 30px;
        }
        
        .info-row {
            display: flex;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #f3f4f6;
        }
        
        .info-label {
            font-weight: 600;
            color: #1f2937;
            width: 150px;
            flex-shrink: 0;
        }
        
        .info-value {
            flex-grow: 1;
            color: #6b7280;
        }
        
        .btn-logout {
            display: inline-block;
            padding: 10px 20px;
            background: #b45309;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: background 0.3s ease;
        }
        
        .btn-logout:hover {
            background: #92400e;
            text-decoration: none;
            color: white;
        }
    </style>
</head>
<body>
    <%@include file="/assets/header.jsp" %>
    
    <div class="container">
        <div class="profile-container">
            <div class="profile-header">
                <h2>Your Profile</h2>
                <p>Manage your account information</p>
            </div>
            
            <c:choose>
                <c:when test="${sessionScope.customer != null}">
                    <div class="profile-info">
                        <div class="info-row">
                            <div class="info-label">Customer ID:</div>
                            <div class="info-value">${customer.customerId}</div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Username:</div>
                            <div class="info-value">${customer.username}</div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Full Name:</div>
                            <div class="info-value">${customer.fullName}</div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Email:</div>
                            <div class="info-value">${customer.email}</div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Gender:</div>
                            <div class="info-value">${customer.gender}</div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Date of Birth:</div>
                            <div class="info-value">${customer.dateOfBirth}</div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Status:</div>
                            <div class="info-value">${customer.status ? 'Active' : 'Inactive'}</div>
                        </div>
                    </div>
                    
                    <div>
                        <a href="<%=request.getContextPath()%>/logout" class="btn-logout">Logout</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <p>You are not logged in. <a href="<%=request.getContextPath()%>/auth">Please login</a>.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <%@include file="/assets/footer.jsp" %>
</body>
</html>