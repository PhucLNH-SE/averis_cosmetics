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
                    <a href="${pageContext.request.contextPath}/my-voucher">
                        My Voucher
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
                            <div>
                                <h2>My Addresses</h2>
                                <p class="address-subtitle">Manage your delivery addresses</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/address?action=add"
                               class="btn-add-address">
                                <i class="fas fa-plus-circle"></i> Add New Address
                            </a>
                        </div>

                        <c:if test="${not empty profileMessage}">
                            <div class="alert-message ${profileMessage.contains('success') ? 'alert-success' : 'alert-warning'}">
                                <i class="fas ${profileMessage.contains('success') ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                                ${profileMessage}
                            </div>
                        </c:if>

                        <div class="address-grid">
                            <c:choose>
                                <c:when test="${empty addresses}">
                                    <div class="empty-address">
                                        <div class="empty-icon">
                                            <i class="fas fa-map-marker-alt"></i>
                                        </div>
                                        <h3>No addresses yet</h3>
                                        <p>Add your first delivery address to get started</p>
                                        <a href="${pageContext.request.contextPath}/address?action=add"
                                           class="btn-add-first">
                                            <i class="fas fa-plus"></i> Add Your First Address
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${addresses}" var="addr">
                                        <div class="address-card ${addr.isDefault ? 'default-address' : ''}">
                                            <c:if test="${addr.isDefault}">
                                                <div class="default-badge">
                                                    <i class="fas fa-star"></i> Default
                                                </div>
                                            </c:if>
                                            
                                            <div class="address-card-content">
                                                <div class="address-receiver">
                                                    <i class="fas fa-user"></i>
                                                    <span>${addr.receiverName}</span>
                                                </div>
                                                <div class="address-phone">
                                                    <i class="fas fa-phone"></i>
                                                    <span>${addr.phone}</span>
                                                </div>
                                                <div class="address-location">
                                                    <i class="fas fa-map-pin"></i>
                                                    <span>${addr.streetAddress}, ${addr.ward}, ${addr.district}, ${addr.province}</span>
                                                </div>
                                            </div>
                                            
                                            <div class="address-card-actions">
                                                <c:if test="${!addr.isDefault}">
                                                    <a href="${pageContext.request.contextPath}/address?action=setdefault&id=${addr.addressId}"
                                                       class="action-btn set-default"
                                                       title="Set as default">
                                                        <i class="fas fa-check"></i> Set Default
                                                    </a>
                                                </c:if>
                                                <a href="${pageContext.request.contextPath}/address?action=edit&id=${addr.addressId}"
                                                   class="action-btn edit"
                                                   title="Edit address">
                                                    <i class="fas fa-pen"></i> Edit
                                                </a>
                                                <a href="${pageContext.request.contextPath}/address?action=delete&id=${addr.addressId}"
                                                   class="action-btn delete"
                                                   title="Delete address"
                                                   onclick="return confirm('Are you sure you want to delete this address?')">
                                                    <i class="fas fa-trash"></i> Delete
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </c:when>


               
<c:when test="${tab == 'orders'}">

<h2>My Orders</h2>

<table border="1">

<tr>
    <th>Receiver</th>
    <th>Voucher</th>
    <th>Discount</th>
    <th>Status</th>
    <th>Total</th>
    <th>Date</th>
    <th>Action</th>
</tr>

<c:forEach var="o" items="${orders}">

<tr>
    <td>${o.receiverName}</td>
    <td>${o.voucherCode}</td>
    <td>${o.discountAmount}</td>
    <td>${o.orderStatus}</td>
    <td>${o.totalAmount}</td>
    <td>${o.createdAt}</td>

    <td>
        <a href="${pageContext.request.contextPath}/profile?action=orderDetail&orderId=${o.orderId}">
            View Detail
        </a>
              <c:if test="${o.orderStatus == 'CREATED' || o.orderStatus == 'PROCESSING'}">
      <c:if test="${o.orderStatus == 'CREATED' || o.orderStatus == 'PROCESSING'}">
        | <a href="#" onclick="confirmCancel(${o.orderId})">Cancel</a>
    </c:if>
    </c:if>
    </td>
</tr>

</c:forEach>

</table>

</c:when>
<c:when test="${tab == 'orderDetail'}">

<h2>Order Details</h2>

<table border="1">

<tr>
<th>Image</th>
<th>Product</th>
<th>Brand</th>
<th>Category</th>
<th>Quantity</th>
<th>Price</th>
</tr>

<c:forEach var="d" items="${details}">
<tr>

<td>
<img src="${pageContext.request.contextPath}/assets/img/${d.imageUrl}" width="80">
</td>

<td>${d.productName}</td>
<td>${d.brandName}</td>
<td>${d.categoryName}</td>
<td>${d.quantity}</td>
<td>${d.priceAtOrder}</td>

</tr>
</c:forEach>

</table>

<br>

<a href="${pageContext.request.contextPath}/profile?action=orders">
Back to My Orders
</a>

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
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
function confirmCancel(orderId) {
    Swal.fire({
        title: 'Cancel Order',
        text: 'Bạn có chắc muốn hủy đơn này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, Cancel',
        cancelButtonText: 'No',
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href =
                'profile?action=cancelOrder&orderId=' + orderId;
        }
    });
}
</script>
</body>
</html>
