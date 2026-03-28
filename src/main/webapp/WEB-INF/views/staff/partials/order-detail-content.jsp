<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<section class="admin-content__section admin-page admin-page--order-detail staff-page staff-page--order-detail">
    <div class="page-header">
        <div>
            <h4>Order Detail</h4>
            <p class="text-muted mb-0">Review the assigned order and related customer feedback.</p>
        </div>
        <c:url var="staffBackToOrdersUrl" value="/staff/manage-orders">
            <c:if test="${not empty searchKeyword}">
                <c:param name="keyword" value="${searchKeyword}" />
            </c:if>
        </c:url>
        <a href="${staffBackToOrdersUrl}" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Back to Orders
        </a>
    </div>

    <c:if test="${param.success == 'update'}">
        <c:set var="popupMessage" scope="request" value="Order updated successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'updateFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to update this order." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'notAllowed'}">
        <c:set var="popupMessage" scope="request" value="You can only update orders assigned to you." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'validationError'}">
        <c:set var="popupMessage" scope="request" value="${not empty param.message ? param.message : 'Order status transition is not valid.'}" />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="order-detail-card">
        <div class="order-detail-card__header">
            <h5>Order Information</h5>
        </div>
        <div class="order-detail-info-list">
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Order ID:</span>
                <strong class="order-detail-info-text">#${order.orderId}</strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Receiver:</span>
                <strong class="order-detail-info-text">${order.receiverName}</strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Phone:</span>
                <strong class="order-detail-info-text">${order.receiverPhone}</strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Payment Method:</span>
                <strong class="order-detail-info-text">${order.paymentMethod}</strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Handled By:</span>
                <strong class="order-detail-info-text">
                    <c:choose>
                        <c:when test="${not empty handledStaff}">${handledStaff.fullName}</c:when>
                        <c:otherwise>Unassigned</c:otherwise>
                    </c:choose>
                </strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Address:</span>
                <strong class="order-detail-info-text">
                    ${order.streetAddress}, ${order.ward}, ${order.district}, ${order.province}
                </strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Voucher:</span>
                <strong class="order-detail-info-text">${not empty order.voucherCode ? order.voucherCode : '-'}</strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Discount:</span>
                <strong class="order-detail-info-text"><fmt:formatNumber value="${order.discountAmount != null ? order.discountAmount : 0}" pattern="#,##0"/> VND</strong>
            </div>
            <div class="order-detail-info-line">
                <span class="order-detail-info-label">Total Amount:</span>
                <strong class="order-detail-info-text"><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> VND</strong>
            </div>
        </div>
    </div>

    <div class="order-detail-card order-detail-table-card">
        <div class="order-detail-card__header">
            <h5>Order Items</h5>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Image</th>
                        <th>Product</th>
                        <th>Variant</th>
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
                            <td>${not empty d.variantName ? d.variantName : '-'}</td>
                            <td>${d.brandName}</td>
                            <td>${d.categoryName}</td>
                            <td>${d.quantity}</td>
                            <td><fmt:formatNumber value="${d.priceAtOrder}" pattern="#,##0"/> VND</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty details}">
                        <tr>
                            <td colspan="7" class="text-center text-muted">No order items found.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="order-detail-card">
        <div class="order-detail-card__header">
            <h5>Update Status</h5>
        </div>
        <div class="p-4">
            <form action="${pageContext.request.contextPath}/staff/manage-orders" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="orderId" value="${order.orderId}">
                <input type="hidden" name="returnKeyword" value="${fn:escapeXml(searchKeyword)}">

                <div class="d-grid gap-3">
                    <div>
                        <label class="form-label" for="staffPaymentStatus">Payment Status</label>
                        <select id="staffPaymentStatus" name="paymentStatus" class="action-select w-100" ${not canEditOrder ? 'disabled' : ''}>
                            <option value="PENDING" ${order.paymentStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                            <option value="SUCCESS" ${order.paymentStatus == 'SUCCESS' ? 'selected' : ''}>SUCCESS</option>
                            <option value="FAILED" ${order.paymentStatus == 'FAILED' ? 'selected' : ''}>FAILED</option>
                        </select>
                    </div>
                    <div>
                        <label class="form-label" for="staffOrderStatus">Order Status</label>
                        <select id="staffOrderStatus" name="orderStatus" class="action-select w-100" ${not canEditOrder ? 'disabled' : ''}>
                            <option value="${order.orderStatus}" selected>${order.orderStatus}</option>
                            <c:choose>
                                <c:when test="${order.orderStatus == 'CREATED'}">
                                    <option value="PROCESSING">PROCESSING</option>
                                    <option value="CANCELLED">CANCELLED</option>
                                </c:when>
                                <c:when test="${order.orderStatus == 'PROCESSING'}">
                                    <option value="SHIPPING">SHIPPING</option>
                                    <option value="CANCELLED">CANCELLED</option>
                                </c:when>
                                <c:when test="${order.orderStatus == 'SHIPPING'}">
                                    <option value="COMPLETED">COMPLETED</option>
                                    <option value="CANCELLED">CANCELLED</option>
                                </c:when>
                            </c:choose>
                        </select>
                    </div>
                </div>

                <c:if test="${not canEditOrder}">
                    <p class="text-muted small mb-0 mt-3">This order can only be viewed here because it is assigned to another manager.</p>
                </c:if>

                <c:if test="${canEditOrder}">
                    <div class="d-flex justify-content-end mt-4">
                        <button type="submit" class="btn btn-add text-white">
                            <i class="bi bi-check2-circle me-1"></i>Update
                        </button>
                    </div>
                </c:if>
            </form>
        </div>
    </div>
</section>
