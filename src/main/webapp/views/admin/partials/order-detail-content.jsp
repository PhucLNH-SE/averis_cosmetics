<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<section class="admin-content__section admin-page admin-page--order-detail">
    <div class="page-header">
        <div>
            <h4>Order Detail</h4>
            <p class="text-muted mb-0">Review full information for order #${order.orderId}.</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/manage-orders" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Back to Orders
        </a>
    </div>

    <div class="order-detail-card order-detail-card--summary">
        <div class="order-detail-summary-grid">
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Order ID</span>
                <strong class="order-detail-value">#${order.orderId}</strong>
            </div>
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Receiver</span>
                <strong class="order-detail-value">${order.receiverName}</strong>
            </div>
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Phone</span>
                <strong class="order-detail-value">${order.receiverPhone}</strong>
            </div>
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Payment Method</span>
                <strong class="order-detail-value">${order.paymentMethod}</strong>
            </div>
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Payment Status</span>
                <strong class="order-detail-value">${order.paymentStatus}</strong>
            </div>
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Order Status</span>
                <strong class="order-detail-value">${order.orderStatus}</strong>
            </div>
            <div class="order-detail-summary-item">
                <span class="order-detail-label">Handled By</span>
                <strong class="order-detail-value">
                    <c:choose>
                        <c:when test="${not empty handledStaff}">${handledStaff.fullName}</c:when>
                        <c:otherwise>Unassigned</c:otherwise>
                    </c:choose>
                </strong>
            </div>
            <div class="order-detail-summary-item order-detail-summary-item--wide">
                <span class="order-detail-label">Address</span>
                <strong class="order-detail-value">
                    ${order.streetAddress}, ${order.ward}, ${order.district}, ${order.province}
                </strong>
            </div>
        </div>
    </div>

    <div class="order-detail-card order-detail-table-card">
        <div class="order-detail-card__header">
            <h5>Ordered Items</h5>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Image</th>
                        <th>Product</th>
                        <th>Brand</th>
                        <th>Category</th>
                        <th>Quantity</th>
                        <th>Price</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${details}" var="d">
                        <tr>
                            <td>
                                <img class="order-detail-image" src="${pageContext.request.contextPath}/assets/img/${d.imageUrl}" alt="${d.productName}">
                            </td>
                            <td>${d.productName}</td>
                            <td>${d.brandName}</td>
                            <td>${d.categoryName}</td>
                            <td>${d.quantity}</td>
                            <td><fmt:formatNumber value="${d.priceAtOrder}" pattern="#,##0"/> VND</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty details}">
                        <tr>
                            <td colspan="6" class="text-center text-muted">No order items found.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="order-detail-total">
        <span>Total Amount</span>
        <strong><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> VND</strong>
    </div>

    <div class="order-detail-card order-detail-table-card order-detail-feedback">
        <div class="order-detail-card__header">
            <h5>Feedback</h5>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Rating</th>
                        <th>Review</th>
                        <th>Response</th>
                        <th>Responded At</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${details}" var="d">
                        <tr>
                            <td>${d.productName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${d.rating != null}">${d.rating}</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty d.reviewComment}">${fn:replace(d.reviewComment, '[EDITED]', '')}</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty d.responseContent}">${d.responseContent}</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty d.respondedAt}">${d.respondedAt}</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty details}">
                        <tr>
                            <td colspan="5" class="text-center text-muted">No feedback found.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</section>
