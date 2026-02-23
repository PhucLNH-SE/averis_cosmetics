<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profile - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
        <style>
            .account-sidebar{
                width:260px;
            }

            .account-card{
                background:#fff;
                border:1px solid #f1e8dd;
                border-radius:14px;
                padding:20px 18px 18px;
                box-shadow:0 8px 22px rgba(31,41,55,.06);
            }

            .account-avatar{
                display:flex;
                justify-content:center;
                margin-bottom:16px;
            }

            .avatar-circle{
                width:72px;
                height:72px;
                border-radius:50%;
                background:#e5e7eb;
            }

            .account-menu{
                list-style:none;
                padding:0;
                margin:0 0 18px 0;
            }

            .account-menu li{
                margin-bottom:10px;
            }

            .account-menu a{
                display:block;
                padding:8px 10px;
                border-radius:8px;
                color:#374151;
                text-decoration:none;
                font-weight:500;
            }

            .account-menu a:hover{
                background:#f3f4f6;
                text-decoration:none;
            }

            .account-menu a.active{
                color:#7c3aed;
                background:#f5f3ff;
                font-weight:600;
            }

            .account-logout{
                display:block;
                margin-top:6px;
                padding:10px 0;
                text-align:center;
                background:#dc2626;
                color:#fff;
                border-radius:8px;
                text-decoration:none;
                font-weight:600;
            }

            .account-logout:hover{
                background:#b91c1c;
                color:#fff;
                text-decoration:none;
            }


            .profile-container {
                width: 100%;
                max-width: 900px;
                margin: 0;   /* QUAN TRỌNG */
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
            .btn-edit-profile{
                display: inline-block;
                padding: 10px 26px;
                background: #dc2626;
                color: #fff;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                transition: background .2s;
            }

            .btn-edit-profile:hover{
                background: #b91c1c;
                color: #fff;
                text-decoration: none;
            }
            .status-verified {
                background: #d1fae5;
                color: #059669;
            }
            .status-unverified {
                background: #fef3c7;
                color: #b45309;
            }
            .verify-form-inline {
                display: inline;
                margin: 0;
            }
        </style>
    </head>
    <body>
        <%@include file="/assets/header.jsp" %>
        <div class="container" style="display:flex; gap:24px; align-items:flex-start; margin-top:40px">
            <div class="account-sidebar">

                <div class="account-card">

                    <div class="account-avatar">
                        <div class="avatar-circle"></div>
                    </div>

                    <ul class="account-menu">
                        <li>
                            <div class="profile-header">

                                <div class="info-value">${customer.username}</div>
                            </div>
                            <a class="${tab == null || tab == 'profile' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/CustomerController?action=view&tab=profile">
                                Profile
                            </a>
                        </li>

                        <li>
                            <a class="${tab == 'address' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/CustomerController?action=view&tab=address">
                                Address
                            </a>
                        </li>

                        <li>
                            <a class="${tab == 'orders' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/CustomerController?action=view&tab=orders">
                                My Orders
                            </a>
                        </li>

                        <li>
                            <a class="${tab == 'password' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/CustomerController?action=view&tab=password">
                                Change password
                            </a>
                        </li>
                    </ul>

                    <a class="account-logout"
                       href="${pageContext.request.contextPath}/logout">
                        Logout
                    </a>

                </div>
            </div>



            <div style="flex:1">

                <div class="profile-container">

                    <c:choose>


                        <c:when test="${tab == null || tab == 'profile'}">



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

                                    <div style="text-align:right; margin-top:20px;">
                                        <a href="<%=request.getContextPath()%>/CustomerController?action=edit"
                                           class="btn-edit-profile">
                                            Edit Profile
                                        </a>


                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p>You are not logged in. <a href="<%=request.getContextPath()%>/auth">Please login</a>.</p>
                                </c:otherwise>
                            </c:choose>

                        </c:when>



                        <c:when test="${tab == 'address'}">
                            <h2>Address</h2>
                            <p>Trang address – bạn làm nội dung ở đây.</p>
                        </c:when>



                        <c:when test="${tab == 'orders'}">
                            <h2>My orders</h2>
                            <p>Trang my orders – bạn làm nội dung ở đây.</p>
                        </c:when>



                        <c:when test="${tab == 'password'}">
                            <h2>Change password</h2>
                            <p>Trang change password – bạn làm form ở đây.</p>
                        </c:when>

                    </c:choose>

                </div>

            </div>

        </div>

        <%@include file="/assets/footer.jsp" %>
    </body>
</html>