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
        
        .btn-verify {
            padding: 8px 16px;
            background: #b45309;
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
        }
        .btn-verify:hover {
            background: #92400e;
        }
        .info-value-email {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
        }
        .email-status {
            font-size: 13px;
            padding: 2px 8px;
            border-radius: 6px;
        }
        .status-verified { background: #d1fae5; color: #059669; }
        .status-unverified { background: #fef3c7; color: #b45309; }
        .verify-form-inline { display: inline; margin: 0; }
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
                        <div class="info-row info-row-email">
                            <div class="info-label">Email:</div>
                            <div class="info-value info-value-email">
                                <span>${customer.email != null ? customer.email : '—'}</span>
                                <c:if test="${customer.email != null && !customer.email.isEmpty()}">
                                    <span class="email-status ${customer.emailVerified ? 'status-verified' : 'status-unverified'}">${customer.emailVerified ? 'Đã xác thực' : 'Chưa xác thực'}</span>
                                    <c:if test="${!customer.emailVerified}">
                                        <form action="${pageContext.request.contextPath}/send-verify-email" method="post" class="verify-form-inline">
                                            <button type="submit" class="btn-verify">Gửi email xác thực</button>
                                        </form>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>
                        <c:if test="${not empty profileMessage}">
                            <div class="info-row">
                                <div class="info-label"></div>
                                <div class="info-value" style="color: #b45309;">${profileMessage}</div>
                            </div>
                        </c:if>
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