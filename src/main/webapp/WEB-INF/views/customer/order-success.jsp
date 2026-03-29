<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Success | Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="order-success-page">

    <div class="header-wrapper">
        <jsp:include page="/assets/header.jsp" />
    </div>

    <main class="order-success-shell">
        <section class="order-success-card">
            <div class="order-success-badge">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
            </div>

            <p class="order-success-eyebrow">
                <c:choose>
                    <c:when test="${order.paymentMethod eq 'MOMO'}">Payment completed</c:when>
                    <c:otherwise>Order confirmed</c:otherwise>
                </c:choose>
            </p>
            <h1 class="order-success-title">
                <c:choose>
                    <c:when test="${order.paymentMethod eq 'MOMO'}">Your payment was successful.</c:when>
                    <c:otherwise>Your order has been placed successfully.</c:otherwise>
                </c:choose>
            </h1>
            <p class="order-success-copy">
                <c:choose>
                    <c:when test="${order.paymentMethod eq 'MOMO'}">
                        Averis Cosmetics has received your payment and we will prepare your order as soon as possible.
                    </c:when>
                    <c:otherwise>
                        Averis Cosmetics has received your order. Please prepare cash when the courier delivers your package.
                    </c:otherwise>
                </c:choose>
            </p>

            <div class="order-success-summary">
                <div class="order-success-summary-item">
                    <span>Order ID</span>
                    <strong>#${order.orderId}</strong>
                </div>
                <div class="order-success-summary-item">
                    <span>Payment method</span>
                    <strong>${order.paymentMethod}</strong>
                </div>
                <div class="order-success-summary-item">
                    <span>Payment status</span>
                    <strong>${order.paymentStatus}</strong>
                </div>
                <div class="order-success-summary-item">
                    <span>Total amount</span>
                    <strong><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> VND</strong>
                </div>
            </div>

            <div class="order-success-layout">
                <div class="order-success-panel">
                    <h2>Order status</h2>
                    <p class="order-success-panel-copy">
                        <c:choose>
                            <c:when test="${order.paymentMethod eq 'MOMO'}">
                                Your online payment was confirmed successfully and the order is now being processed.
                            </c:when>
                            <c:otherwise>
                                Your order has been recorded successfully. Please prepare cash when the courier delivers your package.
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <div class="order-success-status-chip">${order.orderStatus}</div>
                </div>

                <div class="order-success-panel">
                    <h2>Delivery information</h2>
                    <p class="order-success-address-name">
                        ${order.receiverName}
                        <c:if test="${not empty order.receiverPhone}">
                            - ${order.receiverPhone}
                        </c:if>
                    </p>
                    <p class="order-success-address-copy">
                        <c:choose>
                            <c:when test="${not empty order.streetAddress}">
                                ${order.streetAddress}, ${order.ward}, ${order.district}, ${order.province}
                            </c:when>
                            <c:when test="${not empty address}">
                                ${address.streetAddress}, ${address.ward}, ${address.district}, ${address.province}
                            </c:when>
                            <c:otherwise>
                                Your delivery address has been confirmed in the order.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>

            <div class="order-success-actions">
                <a href="${pageContext.request.contextPath}/home" class="order-success-btn order-success-btn--primary">
                    Back to home
                </a>
                <a href="${pageContext.request.contextPath}/profile?action=view&tab=orders" class="order-success-btn order-success-btn--secondary">
                    View my orders
                </a>
            </div>
        </section>
    </main>

    <jsp:include page="/assets/footer.jsp" />
</body>
</html>
