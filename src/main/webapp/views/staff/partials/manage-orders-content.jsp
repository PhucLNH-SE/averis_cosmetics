<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<section class="admin-content__section staff-orders-page">
    <div class="page-header">
        <div>
            <h4>Manage Orders</h4>
            <p class="text-muted mb-0">List of customer orders</p>
        </div>
    </div>

    <c:if test="${param.success == 'update'}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            Orders updated successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.error == 'updateFailed'}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            Failed to update orders!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card table-card">
        <div class="card-body p-0">
            <form action="${pageContext.request.contextPath}/staff/manage-orders" method="post">
                <input type="hidden" name="action" value="update">
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
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orderList}">
                                <tr>
                                    <td class="px-4">#${o.orderId}</td>
                                    <td><strong>${o.receiverName}</strong></td>
                                    <td>${o.voucherCode != null ? o.voucherCode : '-'}</td>
                                    <td><fmt:formatNumber value="${o.discountAmount != null ? o.discountAmount : 0}" pattern="#,##0"/> VND</td>
                                    <td>${o.paymentMethod}</td>
                                    <td>
                                        <input type="hidden" name="orderId" value="${o.orderId}">
                                        <select name="paymentStatus" class="action-select">
                                            <option value="PENDING" ${o.paymentStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                            <option value="SUCCESS" ${o.paymentStatus == 'SUCCESS' ? 'selected' : ''}>SUCCESS</option>
                                            <option value="FAILED" ${o.paymentStatus == 'FAILED' ? 'selected' : ''}>FAILED</option>
                                        </select>
                                    </td>
                                    <td>
                                        <select name="orderStatus" class="action-select">
                                            <option value="CREATED" ${o.orderStatus == 'CREATED' ? 'selected' : ''}>CREATED</option>
                                            <option value="PROCESSING" ${o.orderStatus == 'PROCESSING' ? 'selected' : ''}>PROCESSING</option>
                                            <option value="SHIPPING" ${o.orderStatus == 'SHIPPING' ? 'selected' : ''}>SHIPPING</option>
                                            <option value="COMPLETED" ${o.orderStatus == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                            <option value="CANCELLED" ${o.orderStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                        </select>
                                    </td>
                                    <td><strong><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> VND</strong></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty orderList}">
                                <tr>
                                    <td colspan="8" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No orders found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                <div class="p-3 d-flex justify-content-end">
                    <button type="submit" class="btn btn-add text-white">
                        <i class="bi bi-check2-circle"></i> Update Orders
                    </button>
                </div>
            </form>
        </div>
    </div>
</section>
