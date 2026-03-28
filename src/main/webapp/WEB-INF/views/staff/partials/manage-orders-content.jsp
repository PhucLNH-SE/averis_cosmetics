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
            <input type="text" name="keyword" class="form-control staff-order-filter-select"
                   value="<c:out value='${searchKeyword}'/>"
                   placeholder="Search by order ID, receiver, voucher, payment, status...">
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-search"></i> Search
            </button>
            <a href="${pageContext.request.contextPath}/staff/manage-orders" class="btn btn-outline-secondary">Reset</a>
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
    <div class="card table-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th class="px-4">Order ID</th>
                            <th>Username</th>
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
                            <c:url var="staffOrderDetailUrl" value="/staff/manage-orders">
                                <c:param name="action" value="detail" />
                                <c:param name="orderId" value="${o.orderId}" />
                                <c:if test="${not empty searchKeyword}">
                                    <c:param name="keyword" value="${searchKeyword}" />
                                </c:if>
                            </c:url>
                            <tr>
                                <td class="px-4">#${o.orderId}</td>
                                <td><strong>${o.receiverName}</strong></td>
                                <td>${o.paymentMethod}</td>
                                <td>
                                    <span class="badge rounded-pill text-bg-light border">${o.paymentStatus}</span>
                                </td>
                                <td>
                                    <span class="badge rounded-pill text-bg-light border">${o.orderStatus}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty o.handledByName}">${o.handledByName}</c:when>
                                        <c:otherwise>Chưa gán</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${staffOrderDetailUrl}" class="btn btn-sm btn-primary">
                                        <i class="bi bi-eye"></i> View Detail
                                    </a>
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
        </div>
    </div>
</section>

