<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<section class="admin-content__section admin-page staff-page staff-page--orders staff-orders-page">
    <div class="page-header">
        <div>
            <h4>Manage Orders</h4>
            <p class="text-muted mb-0">List of customer orders</p>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/staff/manage-orders" class="mb-3 d-flex align-items-center gap-2">

    <label><strong>Filter by Status:</strong></label>

    <select name="status" class="form-select staff-order-filter-select">
        <option value="">All</option>
        <option value="CREATED" ${selectedStatus == 'CREATED' ? 'selected' : ''}>CREATED</option>
        <option value="PROCESSING" ${selectedStatus == 'PROCESSING' ? 'selected' : ''}>PROCESSING</option>
        <option value="SHIPPING" ${selectedStatus == 'SHIPPING' ? 'selected' : ''}>SHIPPING</option>
        <option value="COMPLETED" ${selectedStatus == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
        <option value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
    </select>

    <button type="submit" class="btn btn-primary">
        <i class="bi bi-funnel"></i> Filter
    </button>

</form>
    </div>

    <c:if test="${param.success == 'update'}">
        <c:set var="popupMessage" scope="request" value="Orders updated successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'updateFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to update orders." />
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
    <form action="${pageContext.request.contextPath}/staff/manage-orders" method="post">
        <input type="hidden" name="action" value="update">
        <div class="d-flex justify-content-end mb-3">
            <button type="submit" class="btn btn-add text-white">
                <i class="bi bi-check2-circle"></i> Update Orders
            </button>
        </div>
        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4">Order ID</th>
                                <th>Username</th>
                                <th>Voucher</th>
                                <th>Discount</th>
                                <th>Payment Method</th>
                                <th>Payment Status</th>
                                <th>Order Status</th>
                                <th>Handled By</th>
                                <th>Action</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orderList}">
                                <c:set var="canEdit"
                                       value="${not empty currentManagerId && (o.handledBy == null || o.handledBy == currentManagerId)}" />
                                <tr>
                                    <td class="px-4">#${o.orderId}</td>
                                    <td><strong>${o.receiverName}</strong></td>
                                    <td>${o.voucherCode != null ? o.voucherCode : '-'}</td>
                                    <td><fmt:formatNumber value="${o.discountAmount != null ? o.discountAmount : 0}" pattern="#,##0"/> VND</td>
                                    <td>${o.paymentMethod}</td>
                                    <td>
                                        <input type="hidden" name="orderId" value="${o.orderId}">
                                        <c:choose>
                                            <c:when test="${canEdit}">
                                                <select name="paymentStatus" class="action-select">
                                                    <option value="PENDING" ${o.paymentStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                                    <option value="SUCCESS" ${o.paymentStatus == 'SUCCESS' ? 'selected' : ''}>SUCCESS</option>
                                                    <option value="FAILED" ${o.paymentStatus == 'FAILED' ? 'selected' : ''}>FAILED</option>
                                                </select>
                                            </c:when>
                                            <c:otherwise>
                                                <select name="paymentStatus" class="action-select" disabled>
                                                    <option value="PENDING" ${o.paymentStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                                    <option value="SUCCESS" ${o.paymentStatus == 'SUCCESS' ? 'selected' : ''}>SUCCESS</option>
                                                    <option value="FAILED" ${o.paymentStatus == 'FAILED' ? 'selected' : ''}>FAILED</option>
                                                </select>
                                                <input type="hidden" name="paymentStatus" value="${o.paymentStatus}">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${canEdit}">
                                                <select name="orderStatus" class="action-select">
                                                    <option value="${o.orderStatus}" selected>${o.orderStatus}</option>
                                                    <c:choose>
                                                        <c:when test="${o.orderStatus == 'CREATED'}">
                                                            <option value="PROCESSING">PROCESSING</option>
                                                            <option value="CANCELLED">CANCELLED</option>
                                                        </c:when>
                                                        <c:when test="${o.orderStatus == 'PROCESSING'}">
                                                            <option value="SHIPPING">SHIPPING</option>
                                                            <option value="CANCELLED">CANCELLED</option>
                                                        </c:when>
                                                        <c:when test="${o.orderStatus == 'SHIPPING'}">
                                                            <option value="COMPLETED">COMPLETED</option>
                                                            <option value="CANCELLED">CANCELLED</option>
                                                        </c:when>
                                                    </c:choose>
                                                </select>
                                            </c:when>
                                            <c:otherwise>
                                                <select name="orderStatus" class="action-select" disabled>
                                                    <option value="${o.orderStatus}" selected>${o.orderStatus}</option>
                                                    <c:choose>
                                                        <c:when test="${o.orderStatus == 'CREATED'}">
                                                            <option value="PROCESSING">PROCESSING</option>
                                                            <option value="CANCELLED">CANCELLED</option>
                                                        </c:when>
                                                        <c:when test="${o.orderStatus == 'PROCESSING'}">
                                                            <option value="SHIPPING">SHIPPING</option>
                                                            <option value="CANCELLED">CANCELLED</option>
                                                        </c:when>
                                                        <c:when test="${o.orderStatus == 'SHIPPING'}">
                                                            <option value="COMPLETED">COMPLETED</option>
                                                            <option value="CANCELLED">CANCELLED</option>
                                                        </c:when>
                                                    </c:choose>
                                                </select>
                                                <input type="hidden" name="orderStatus" value="${o.orderStatus}">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty o.handledByName}">${o.handledByName}</c:when>
                                            <c:otherwise>Chưa gán</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                    <a href="${pageContext.request.contextPath}/staff/manage-orders?action=detail&orderId=${o.orderId}"
   class="btn btn-sm btn-primary">
   View Detail
</a>
                                    </td>
                                    <td><strong><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> VND</strong></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty orderList}">
                                <tr>
                                    <td colspan="10" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No orders found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>
</section>

