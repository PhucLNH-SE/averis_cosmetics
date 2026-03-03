<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Profile - Averis Cosmetics</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
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
                       href="${pageContext.request.contextPath}/profile?action=view&tab=profile">
                        Profile
                    </a>
                </li>

                <li>
                    <a class="${tab == 'address' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/profile?action=view&tab=address">
                        Address
                    </a>
                </li>

                <li>
                    <a class="${tab == 'orders' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/profile?action=view&tab=orders">
                        My Orders
                    </a>
                </li>

                <li>
                    <a class="${tab == 'password' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/profile?action=view&tab=password">
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
                                            <span class="email-status ${customer.emailVerified ? 'status-verified' : 'status-unverified'}">
                                                ${customer.emailVerified ? 'Verified' : 'Not verified'}
                                            </span>

                                            <c:if test="${!customer.emailVerified}">
                                                <form action="${pageContext.request.contextPath}/send-verify-email"
                                                      method="post"
                                                      class="verify-form-inline">
                                                    <button type="submit" class="btn-verify">
                                                        Send verification email
                                                    </button>
                                                </form>
                                            </c:if>
                                        </c:if>
                                    </div>
                                </div>

                                <c:if test="${not empty profileMessage}">
                                    <div class="info-row">
                                        <div class="info-label"></div>
                                        <div class="info-value" style="color: #b45309;">
                                            ${profileMessage}
                                        </div>
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
                                <a href="<%=request.getContextPath()%>/profile?action=edit"
                                   class="btn-edit-profile">
                                    Edit Profile
                                </a>
                            </div>

                        </c:when>
                        <c:otherwise>
                            <p>
                                You are not logged in.
                                <a href="<%=request.getContextPath()%>/auth">Please login</a>.
                            </p>
                        </c:otherwise>
                    </c:choose>

                </c:when>


          
                <c:when test="${tab == 'address'}">

                    <div class="address-container">
                        <div class="address-header">
                            <h2>My Addresses</h2>
                            <a href="${pageContext.request.contextPath}/address?action=add"
                               class="btn btn-primary">
                                <i class="fas fa-plus"></i> Add New Address
                            </a>
                        </div>

                        <c:if test="${not empty profileMessage}">
                            <div class="alert alert-info">${profileMessage}</div>
                        </c:if>

                        <div class="address-list">
                            <c:choose>
                                <c:when test="${empty addresses}">
                                    <div class="no-address">
                                        <p>You don't have any addresses yet.</p>
                                        <a href="${pageContext.request.contextPath}/address?action=add"
                                           class="btn btn-primary">
                                            Add Your First Address
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="row">
                                        <c:forEach items="${addresses}" var="addr">
                                            <div class="col-md-6 mb-4">
                                                <div class="address-card ${addr.isDefault ? 'default' : ''}">
                                                    <div class="address-card-header">
                                                        <h5>
                                                            ${addr.receiverName}
                                                            <c:if test="${addr.isDefault}">
                                                                <span class="badge bg-primary">Default</span>
                                                            </c:if>
                                                        </h5>
                                                        <div class="address-actions">
                                                            <c:if test="${!addr.isDefault}">
                                                                <a href="${pageContext.request.contextPath}/address?action=setdefault&id=${addr.addressId}"
                                                                   class="btn btn-sm btn-outline-primary">
                                                                    Set Default
                                                                </a>
                                                            </c:if>
                                                            <a href="${pageContext.request.contextPath}/address?action=edit&id=${addr.addressId}"
                                                               class="btn btn-sm btn-outline-secondary">
                                                                Edit
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/address?action=delete&id=${addr.addressId}"
                                                               class="btn btn-sm btn-outline-danger"
                                                               onclick="return confirm('Are you sure you want to delete this address?')">
                                                                Delete
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="address-card-body">
                                                        <p><i class="fas fa-phone"></i> ${addr.phone}</p>
                                                        <p><i class="fas fa-map-marker-alt"></i> ${addr.streetAddress}</p>
                                                        <p>${addr.ward}, ${addr.district}, ${addr.province}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </c:when>


               
                <c:when test="${tab == 'orders'}">
                    <h2>My Orders</h2>
                    <p>This is the My Orders page. You can implement the content here.</p>
                </c:when>


        
                <c:when test="${tab == 'password'}">

                    <form action="${pageContext.request.contextPath}/profile?action=changePassword"
                          method="post">

                        <div class="profile-card__header">
                            <h2>Change Password</h2>
                        </div>

                        

                        <div class="profile-card__body">
                            <div class="form-grid">

                                <label>Old password</label>
                                <input type="password" name="oldPassword">

                                <label>New password</label>
                                <input type="password" name="newPassword">

                                <label>Confirm password</label>
                                <input type="password" name="confirmPassword">

                            </div>
<c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                ${error}
                            </div>
                        </c:if>
                            <c:if test="${not empty profileMessage}">
                                <div class="alert alert-success">
                                    ${profileMessage}
                                </div>
                            </c:if>

                            <div class="btns">
                                <button type="submit">Save</button>
                                <a class="linkbtn"
                                   href="${pageContext.request.contextPath}/profile?action=view">
                                    Cancel
                                </a>
                            </div>

                        </div>

                    </form>

                </c:when>

            </c:choose>

        </div>

    </div>

</div>

<%@include file="/assets/footer.jsp" %>
</body>
</html>