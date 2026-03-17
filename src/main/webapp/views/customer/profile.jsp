<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

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
                            <a class="${tab == 'voucher' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/profile?action=view&tab=voucher">
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
                                    <button type="button"
                                            class="btn-add-address"
                                            onclick="openAddressPopup('add')">
                                        <i class="fas fa-plus-circle"></i> Add New Address
                                    </button>
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
                                                <button type="button"
                                                        class="btn-add-first"
                                                        onclick="openAddressPopup('add')">
                                                    <i class="fas fa-plus"></i> Add Your First Address
                                                </button>
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
                                                        <button type="button"
                                                                class="action-btn edit"
                                                                title="Edit address"
                                                                data-address-id="${addr.addressId}"
                                                                data-receiver-name="<c:out value='${addr.receiverName}'/>"
                                                                data-phone="<c:out value='${addr.phone}'/>"
                                                                data-province="<c:out value='${addr.province}'/>"
                                                                data-district="<c:out value='${addr.district}'/>"
                                                                data-ward="<c:out value='${addr.ward}'/>"
                                                                data-street-address="<c:out value='${addr.streetAddress}'/>"
                                                                data-is-default="${addr.isDefault}"
                                                                onclick="openAddressPopup('edit', this)">
                                                            <i class="fas fa-pen"></i> Edit
                                                        </button>
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

                            <div class="address-popup-overlay" id="addressPopupOverlay" onclick="closeAddressPopup(event)">
                                <div class="address-popup-card address-popup-card--iframe">
                                    <div class="address-popup-header">
                                        <div>
                                            <h3 id="addressPopupTitle">Add New Address</h3>
                                            <p id="addressPopupSubtitle">Enter your delivery address details</p>
                                        </div>
                                        <button type="button" class="address-popup-close" onclick="closeAddressPopup()">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>

                                    <iframe id="addressPopupFrame"
                                            class="address-popup-frame"
                                            src="about:blank"
                                            title="Address Form"></iframe>
                                </div>
                            </div>

                        </c:when>



                        <c:when test="${tab == 'orders'}">

                            <div class="orders-container">
                                <div class="orders-header">
                                    <div>
                                        <h2>My Orders</h2>
                                        <p class="orders-subtitle">Track and manage your orders</p>
                                    </div>
                                </div>

                                <c:if test="${not empty profileMessage}">
                                    <div class="alert-message ${profileMessage.contains('success') ? 'alert-success' : 'alert-warning'}">
                                        <i class="fas ${profileMessage.contains('success') ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                                        ${profileMessage}
                                    </div>
                                </c:if>

                                <c:choose>
                                    <c:when test="${empty orders}">
                                        <div class="empty-orders">
                                            <div class="empty-icon">
                                                <i class="fas fa-shopping-bag"></i>
                                            </div>
                                            <h3>No orders yet</h3>
                                            <p>You haven't placed any orders yet. Start shopping to see your orders here!</p>
                                            <a href="${pageContext.request.contextPath}/products" class="btn-shop-now">
                                                <i class="fas fa-shopping-cart"></i> Shop Now
                                            </a>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="orders-list">
                                            <c:forEach var="o" items="${orders}">
                                                <div class="order-card">
                                                    <div class="order-header">
                                                        <div class="order-id">
                                                            <span class="order-label">Order ID:</span>
                                                            <span class="order-value">#${o.orderId}</span>
                                                        </div>
                                                        <div class="order-status status-${fn:toLowerCase(o.orderStatus)}">
                                                            <i class="fas ${o.orderStatus == 'COMPLETED' ? 'fa-check-circle' : o.orderStatus == 'CANCELLED' ? 'fa-times-circle' : o.orderStatus == 'SHIPPING' ? 'fa-truck' : 'fa-clock'}"></i>
                                                            ${o.orderStatus}
                                                        </div>
                                                    </div>

                                                    <div class="order-info">
                                                        <div class="order-info-item">
                                                            <i class="fas fa-user"></i>
                                                            <div>
                                                                <span class="info-label">Receiver</span>
                                                                <span class="info-value">${o.receiverName}</span>
                                                            </div>
                                                        </div>
                                                        <div class="order-info-item">
                                                            <i class="fas fa-calendar-alt"></i>
                                                            <div>
                                                                <span class="info-label">Date</span>
                                                                <span class="info-value">${o.createdAt}</span>
                                                            </div>
                                                        </div>
                                                        <div class="order-info-item">
                                                            <i class="fas fa-tag"></i>
                                                            <div>
                                                                <span class="info-label">Voucher</span>
                                                                <span class="info-value">${o.voucherCode != null ? o.voucherCode : 'None'}</span>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="order-footer">
                                                        <div class="order-total">
                                                            <span class="total-label">Total:</span>
                                                            <span class="total-value"><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> ₫</span>
                                                            <c:if test="${o.discountAmount > 0}">
                                                                <span class="discount-badge">-<fmt:formatNumber value="${o.discountAmount}" pattern="#,##0"/> ₫</span>
                                                            </c:if>
                                                        </div>
                                                        <div class="order-actions">
                                                            <a href="${pageContext.request.contextPath}/profile?action=orderDetail&orderId=${o.orderId}" class="action-btn view">
                                                                <i class="fas fa-eye"></i> View Detail
                                                            </a>
                                                            <c:if test="${o.orderStatus == 'CREATED' || o.orderStatus == 'PROCESSING'}">
                                                                <a href="#" onclick="confirmCancel(${o.orderId})" class="action-btn cancel">
                                                                    <i class="fas fa-times"></i> Cancel
                                                                </a>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                        </c:when>
                        <c:when test="${tab == 'orderDetail'}">

                            <div class="order-detail-container">
                                <div class="order-detail-header">
                                    <a href="${pageContext.request.contextPath}/profile?action=view&tab=orders" class="back-btn">
                                        <i class="fas fa-arrow-left"></i> Back to My Orders
                                    </a>
                                    <h2>Order Details</h2>
                                    <c:if test="${not empty order}">
                                        <div class="order-detail-status status-${fn:toLowerCase(order.orderStatus)}">
                                            <i class="fas ${order.orderStatus == 'COMPLETED' ? 'fa-check-circle' : order.orderStatus == 'CANCELLED' ? 'fa-times-circle' : order.orderStatus == 'SHIPPING' ? 'fa-truck' : 'fa-clock'}"></i>
                                            ${order.orderStatus}
                                        </div>
                                    </c:if>
                                </div>

                                <c:if test="${not empty order}">
                                    <div class="order-detail-info">
                                        <div class="info-card">
                                            <h3><i class="fas fa-box"></i> Order Information</h3>
                                            <div class="info-row">
                                                <span class="label">Order ID</span>
                                                <span class="value">#${order.orderId}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="label">Order Date</span>
                                                <span class="value">${order.createdAt}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="label">Payment Method</span>
                                                <span class="value">${order.paymentMethod}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="label">Payment Status</span>
                                                <span class="value">${order.paymentStatus}</span>
                                            </div>
                                        </div>

                                        <div class="info-card">
                                            <h3><i class="fas fa-map-marker-alt"></i> Shipping Address</h3>
                                            <div class="info-row">
                                                <span class="label">Receiver</span>
                                                <span class="value">${order.receiverName}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="label">Phone</span>
                                                <span class="value">${order.receiverPhone}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="label">Address</span>
                                                <span class="value">${order.streetAddress}, ${order.ward}, ${order.district}, ${order.province}</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="order-items-section">
                                        <h3><i class="fas fa-shopping-bag"></i> Order Items</h3>
                                        <div class="order-items-list">
                                            <c:forEach var="d" items="${details}">
                                                <div class="order-item">
                                                    <div class="item-image">
                                                        <img src="${pageContext.request.contextPath}/assets/img/${d.imageUrl}" 
                                                             alt="${d.productName}"
                                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/default-product.jpg'">
                                                    </div>
                                                    <div class="item-details">
                                                        <div class="item-name">${d.productName}</div>
                                                        <div class="item-meta">
                                                            <span>${d.brandName}</span>
                                                            <span class="separator">|</span>
                                                            <span>${d.categoryName}</span>
                                                        </div>
                                                    </div>
                                                    <div class="item-quantity">
                                                        <span class="qty-label">Qty:</span>
                                                        <span class="qty-value">${d.quantity}</span>
                                                    </div>
                                                    <div class="item-price">
                                                        <span class="price-value">
                                                            <fmt:formatNumber value="${d.priceAtOrder * d.quantity}" pattern="#,##0"/> ₫
                                                        </span>
                                                        <c:if test="${d.quantity > 1}">
                                                            <div style="font-size: 0.85em; color: #9ca3af; text-decoration: none; margin-top: 4px;">
                                                                (Original Price: <fmt:formatNumber value="${d.priceAtOrder}" pattern="#,##0"/> ₫)
                                                            </div>
                                                        </c:if>
                                                    </div>

                                                    <div class="item-feedback" style="margin-left: auto; padding-left: 20px;">
                                                        <c:if test="${order.orderStatus == 'COMPLETED'}">
                                                            <c:choose>
                                                                <c:when test="${empty d.rating || d.rating == 0}">
                                                                    <button type="button" class="btn-feedback" 
                                                                            style="padding: 6px 12px; background: #000; color: #fff; border: none; border-radius: 4px; cursor: pointer;"
                                                                            data-id="${d.orderDetailId}"
                                                                            data-name="${fn:escapeXml(d.productName)}"
                                                                            data-rating="5"
                                                                            data-comment=""
                                                                            onclick="openFeedbackPopup(this)">
                                                                        <i class="fas fa-star"></i> Feedback
                                                                    </button>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="rated-stars" style="color: #f59e0b; font-size: 14px; text-align: right;">
                                                                        <c:forEach begin="1" end="5" var="i">
                                                                            <i class="fas fa-star ${i <= d.rating ? '' : 'text-muted'}" style="${i > d.rating ? 'color: #e5e7eb;' : ''}"></i>
                                                                        </c:forEach>

                                                                        <div style="font-size: 12px; color: #059669; margin-top: 4px; font-weight: 600;">
                                                                            <i class="fas fa-check-circle"></i> Reviewed
                                                                        </div>

                                                                        <c:set var="isEdited" value="${fn:contains(d.reviewComment, '[EDITED]')}" />
                                                                        <c:set var="displayComment" value="${fn:replace(d.reviewComment, '[EDITED]', '')}" />

                                                                        <c:if test="${not empty displayComment}">
                                                                            <div class="user-comment-display" style="font-size: 13px; color: #4b5563; margin-top: 8px; font-style: italic; max-width: 250px; line-height: 1.4; word-wrap: break-word;">
                                                                                "${displayComment}"
                                                                            </div>
                                                                        </c:if>

                                                                        <c:if test="${!isEdited}">
                                                                            <button type="button" class="btn-edit-feedback"
                                                                                    style="margin-top: 8px; padding: 4px 8px; font-size: 12px; background: #f3f4f6; color: #374151; border: 1px solid #d1d5db; border-radius: 4px; cursor: pointer; transition: 0.2s;"
                                                                                    data-id="${d.orderDetailId}"
                                                                                    data-name="${fn:escapeXml(d.productName)}"
                                                                                    data-rating="${d.rating}"
                                                                                    data-comment="${fn:escapeXml(displayComment)}"
                                                                                    onclick="openFeedbackPopup(this)">
                                                                                <i class="fas fa-pen"></i> Edit Feedback
                                                                            </button>
                                                                        </c:if>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <div class="order-summary">
                                            <div class="summary-row">
                                                <span>Subtotal</span>
                                                <span><fmt:formatNumber value="${order.totalAmount + order.discountAmount}" pattern="#,##0"/> ₫</span>
                                            </div>
                                            <c:if test="${order.discountAmount > 0}">
                                                <div class="summary-row discount">
                                                    <span>Discount</span>
                                                    <span>-<fmt:formatNumber value="${order.discountAmount}" pattern="#,##0"/> ₫</span>
                                                </div>
                                            </c:if>
                                            <div class="summary-row total">
                                                <span>Total</span>
                                                <span><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> ₫</span>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                            <div class="feedback-popup-overlay" id="feedbackPopupOverlay" style="display: none; position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center;">
                                <div class="feedback-popup-card" style="background: #fff; width: 100%; max-width: 500px; border-radius: 8px; padding: 24px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                    <div class="feedback-popup-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                                        <h3 style="margin: 0; font-size: 1.25rem;">Đánh giá sản phẩm</h3>
                                        <button type="button" onclick="closeFeedbackPopup()" style="background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #6b7280;">&times;</button>
                                    </div>

                                    <form action="${pageContext.request.contextPath}/review" method="POST" id="feedbackForm">
                                        <input type="hidden" name="orderDetailId" id="feedbackOrderDetailId">
                                        <input type="hidden" name="orderId" value="${order.orderId}">

                                        <p id="feedbackProductName" style="font-weight: 500; margin-bottom: 16px; color: #374151;"></p>

                                        <div style="text-align: center; margin-bottom: 20px;">
                                            <input type="hidden" name="rating" id="feedbackRating" value="5">
                                            <div class="star-rating-select" style="font-size: 32px; color: #e5e7eb; cursor: pointer; display: inline-flex; gap: 8px;">
                                                <i class="fas fa-star" data-val="1" style="color: #f59e0b;"></i>
                                                <i class="fas fa-star" data-val="2" style="color: #f59e0b;"></i>
                                                <i class="fas fa-star" data-val="3" style="color: #f59e0b;"></i>
                                                <i class="fas fa-star" data-val="4" style="color: #f59e0b;"></i>
                                                <i class="fas fa-star" data-val="5" style="color: #f59e0b;"></i>
                                            </div>
                                            <div id="ratingText" style="font-size: 14px; color: #6b7280; margin-top: 8px;">Tuyệt vời</div>
                                        </div>

                                        <textarea name="comment" rows="4" style="width: 100%; padding: 12px; border-radius: 6px; border: 1px solid #d1d5db; resize: vertical;" placeholder="Chia sẻ cảm nhận của bạn về sản phẩm này... (tối đa 500 ký tự)" maxlength="500"></textarea>

                                        <div style="text-align: right; margin-top: 20px; display: flex; gap: 12px; justify-content: flex-end;">
                                            <button type="button" onclick="closeFeedbackPopup()" style="padding: 10px 20px; border: 1px solid #d1d5db; background: #fff; border-radius: 4px; cursor: pointer; font-weight: 500;">Hủy</button>
                                            <button type="submit" style="padding: 10px 20px; border: none; background: #111827; color: #fff; border-radius: 4px; cursor: pointer; font-weight: 500;">Gửi đánh giá</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </c:when>

                        <c:when test="${tab == 'voucher'}">

                            <div class="voucher-container">
                                <div class="voucher-header">
                                    <div>
                                        <h2>My Voucher</h2>
                                        <p class="voucher-subtitle">Quản lý các voucher bạn đang sở hữu</p>
                                    </div>
                                </div>

                                <div class="voucher-claim-card">
                                    <form method="post" action="${pageContext.request.contextPath}/my-voucher" class="voucher-claim-form">
                                        <input type="text" name="voucherCode" placeholder="Nhập voucher code" required>
                                        <button type="submit">Add Voucher</button>
                                    </form>
                                </div>

                                <c:if test="${param.success == 'claimed'}">
                                    <div class="alert-message alert-success">
                                        <i class="fas fa-check-circle"></i> Claim voucher thành công.
                                    </div>
                                </c:if>
                                <c:if test="${not empty param.error}">
                                    <div class="alert-message alert-warning">
                                        <i class="fas fa-exclamation-circle"></i>
                                        <c:choose>
                                            <c:when test="${param.error == 'emptyCode'}">Vui lòng nhập mã voucher.</c:when>
                                            <c:when test="${param.error == 'codeNotFound'}">Mã voucher không tồn tại hoặc đã bị tắt.</c:when>
                                            <c:when test="${param.error == 'outOfStock'}">Voucher đã hết số lượng.</c:when>
                                            <c:when test="${param.error == 'alreadyClaimed'}">Bạn đã nhận voucher này rồi.</c:when>
                                            <c:when test="${param.error == 'voucherExpired'}">Voucher đã hết hạn nhận.</c:when>
                                            <c:otherwise>Lỗi khi nhận voucher.</c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>

                                <div class="voucher-table-card">
                                    <div class="voucher-table-wrap">
                                        <table class="voucher-table">
                                            <thead>
                                                <tr>
                                                    <th>Code</th>
                                                    <th>Discount</th>
                                                    <th>Ngày bắt đầu</th>
                                                    <th>Hạn sử dụng</th>
                                                    <th>Status</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:choose>
                                                    <c:when test="${empty myVouchers}">
                                                        <tr>
                                                            <td colspan="5" class="voucher-empty">Bạn chưa có voucher nào.</td>
                                                        </tr>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach var="cv" items="${myVouchers}">
                                                            <c:set var="effectiveFromStr" value="${fn:replace(cv.effectiveFrom, 'T', ' ')}"/>
                                                            <c:set var="effectiveToStr" value="${fn:replace(cv.effectiveTo, 'T', ' ')}"/>
                                                            <c:set var="discountTypeNormalized" value="${fn:toUpperCase(cv.voucher.discountType)}"/>
                                                            <tr>
                                                                <td><strong>${cv.voucher.code}</strong></td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${discountTypeNormalized eq 'PERCENT' or discountTypeNormalized eq 'PERCENTAGE'}">
                                                                            <fmt:formatNumber value="${cv.voucher.discountValue}" pattern="#,##0" />%
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <fmt:formatNumber value="${cv.voucher.discountValue}" pattern="#,##0" /> ₫
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>${fn:length(effectiveFromStr) > 16 ? fn:substring(effectiveFromStr, 0, 16) : effectiveFromStr}</td>
                                                                <td>${fn:length(effectiveToStr) > 16 ? fn:substring(effectiveToStr, 0, 16) : effectiveToStr}</td>
                                                                <td>
                                                                    <span class="voucher-status ${cv.status == 'ACTIVE' ? 'active' : (cv.status == 'USED' ? 'used' : 'expired')}">
                                                                        ${cv.status}
                                                                    </span>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>

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
                                    <c:if test="${not empty errors.errorPassword}">
                                        <div class="alert alert-danger">
                                            ${errors.errorPassword}
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

    </div>

</div>

<%-- (Các phần trên giữ nguyên) --%>

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
                                            });
                                        }

                                        function openAddressPopup(mode, trigger) {
                                            const overlay = document.getElementById('addressPopupOverlay');
                                            const title = document.getElementById('addressPopupTitle');
                                            const subtitle = document.getElementById('addressPopupSubtitle');
                                            const frame = document.getElementById('addressPopupFrame');

                                            if (!overlay || !frame) {
                                                return;
                                            }

                                            if (mode === 'edit' && trigger) {
                                                title.textContent = 'Edit Address';
                                                subtitle.textContent = 'Update your delivery address details';
                                                frame.src = '${pageContext.request.contextPath}/address?action=edit&id='
                                                        + encodeURIComponent(trigger.getAttribute('data-address-id') || '');
                                            } else {
                                                title.textContent = 'Add New Address';
                                                subtitle.textContent = 'Enter your delivery address details';
                                                frame.src = '${pageContext.request.contextPath}/address?action=add';
                                            }

                                            overlay.classList.add('show');
                                            document.body.classList.add('address-popup-open');
                                        }

                                        function closeAddressPopup(event) {
                                            const overlay = document.getElementById('addressPopupOverlay');
                                            const frame = document.getElementById('addressPopupFrame');
                                            if (!overlay) {
                                                return;
                                            }

                                            if (!event || event.target === overlay) {
                                                overlay.classList.remove('show');
                                                document.body.classList.remove('address-popup-open');
                                                if (frame) {
                                                    frame.src = 'about:blank';
                                                }
                                            }
                                        }

                                        document.addEventListener('DOMContentLoaded', function () {
                                            const frame = document.getElementById('addressPopupFrame');
                                            if (!frame) {
                                                return;
                                            }

                                            frame.addEventListener('load', function () {
                                                try {
                                                    const frameWindow = frame.contentWindow;
                                                    const doc = frame.contentDocument || frameWindow.document;
                                                    const frameUrl = new URL(frameWindow.location.href);
                                                    const isProfileAddressPage = frameUrl.pathname.endsWith('/profile')
                                                            && frameUrl.searchParams.get('action') === 'view'
                                                            && frameUrl.searchParams.get('tab') === 'address';

                                                    if (isProfileAddressPage) {
                                                        closeAddressPopup();
                                                        window.location.href = frameUrl.pathname + frameUrl.search;
                                                        return;
                                                    }

                                                    const topbar = doc.querySelector('.topbar-shell');
                                                    const footer = doc.querySelector('footer');
                                                    const container = doc.querySelector('.container');

                                                    if (topbar) {
                                                        topbar.style.display = 'none';
                                                    }
                                                    if (footer) {
                                                        footer.style.display = 'none';
                                                    }
                                                    if (container) {
                                                        container.style.margin = '0 auto';
                                                        container.style.padding = '24px';
                                                        container.style.maxWidth = '100%';
                                                    }
                                                } catch (error) {
                                                    console.error('Cannot optimize address popup frame:', error);
                                                }
                                            });
                                        });

                                        // ===== PHẦN CODE MỚI DÀNH CHO FEEDBACK ===== //
                                        // Biến lưu trữ mảng text tương ứng với số sao
                                        const ratingTexts = ["Tệ", "Không hài lòng", "Bình thường", "Hài lòng", "Tuyệt vời"];

                                        function openFeedbackPopup(orderDetailId, productName) {
                                            const overlay = document.getElementById('feedbackPopupOverlay');
                                            document.getElementById('feedbackOrderDetailId').value = orderDetailId;
                                            document.getElementById('feedbackProductName').textContent = productName;

                                            // Reset form về mặc định 5 sao
                                            document.getElementById('feedbackForm').reset();
                                            updateStars(5);
                                            document.getElementById('feedbackRating').value = 5; // Cập nhật luôn giá trị input ẩn

                                            overlay.style.display = 'flex';
                                        }

                                        function closeFeedbackPopup() {
                                            document.getElementById('feedbackPopupOverlay').style.display = 'none';
                                        }

                                        // Xử lý hiệu ứng click chọn sao
                                        document.addEventListener('DOMContentLoaded', function () {
                                            const stars = document.querySelectorAll('.star-rating-select .fa-star');
                                            const ratingInput = document.getElementById('feedbackRating');

                                            stars.forEach(star => {
                                                star.addEventListener('click', function () {
                                                    const val = this.getAttribute('data-val');
                                                    ratingInput.value = val;
                                                    updateStars(val);
                                                });

                                                // ===== PHẦN CODE MỚI DÀNH CHO FEEDBACK ===== //
                                                // Biến lưu trữ mảng text tương ứng với số sao
                                                const ratingTexts = ["Tệ", "Không hài lòng", "Bình thường", "Hài lòng", "Tuyệt vời"];

                                                function openFeedbackPopup(buttonElement) {
    const overlay = document.getElementById('feedbackPopupOverlay');
    const form = document.getElementById('feedbackForm');
    
    // Lấy dữ liệu từ data-attributes của nút được bấm
    const orderDetailId = buttonElement.getAttribute('data-id');
    const productName = buttonElement.getAttribute('data-name');
    const existingRating = parseInt(buttonElement.getAttribute('data-rating')) || 5;
    const existingComment = buttonElement.getAttribute('data-comment') || '';

    // Gán dữ liệu vào Form
    document.getElementById('feedbackOrderDetailId').value = orderDetailId;
    document.getElementById('feedbackProductName').textContent = productName;
    
    // Reset và set lại giá trị cũ
    form.reset();
    document.getElementById('feedbackRating').value = existingRating;
    updateStars(existingRating); // Hàm của bro đã viết ở dưới, cứ gọi lại thôi
    form.querySelector('textarea[name="comment"]').value = existingComment;

    // Đổi Title của Popup tùy thuộc vào việc Thêm mới hay Sửa
    const headerTitle = overlay.querySelector('.feedback-popup-header h3');
    if (existingComment !== '' || existingRating !== 5 || buttonElement.classList.contains('btn-edit-feedback')) {
        headerTitle.textContent = "Chỉnh sửa đánh giá";
    } else {
        headerTitle.textContent = "Đánh giá sản phẩm";
    }

    // Hiển thị Popup
    overlay.style.display = 'flex';
}



                                                function updateStars(value) {
                                                    const stars = document.querySelectorAll('.star-rating-select .fa-star');
                                                    const ratingText = document.getElementById('ratingText');

                                                    stars.forEach(s => {
                                                        if (parseInt(s.getAttribute('data-val')) <= value) {
                                                            s.style.color = '#f59e0b'; // Màu vàng
                                                        } else {
                                                            s.style.color = '#e5e7eb'; // Màu xám nhạt
                                                        }
                                                    });
                                                    ratingText.textContent = ratingTexts[value - 1];
                                                }




</script> 
</body>
</html>
