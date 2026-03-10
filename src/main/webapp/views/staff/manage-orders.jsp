<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-brand.css" rel="stylesheet">
    <style>
        .status-badge {
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }
        .status-created { background-color: #e3f2fd; color: #0d47a1; }
        .status-processing { background-color: #fff3e0; color: #e65100; }
        .status-shipping { background-color: #e8f5e9; color: #1b5e20; }
        .status-completed { background-color: #c8e6c9; color: #1b5e20; }
        .status-cancelled { background-color: #ffcdd2; color: #b71c1c; }
        .status-pending { background-color: #fff8e1; color: #f57f17; }
        .status-success { background-color: #c8e6c9; color: #1b5e20; }
        .status-failed { background-color: #ffcdd2; color: #b71c1c; }
        .action-select {
            padding: 6px 10px;
            border: 1px solid #ced4da;
            border-radius: 8px;
            background-color: white;
            min-width: 120px;
        }
        .action-select:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
            outline: none;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <!-- Header -->
        <div class="page-header">
            <div>
                <h4>Manage Orders</h4>
                <p class="text-muted mb-0">List of customer orders</p>
            </div>
            <a href="${pageContext.request.contextPath}/staff/dashboard" class="btn btn-outline-secondary">
                Back
            </a>
        </div>

        <!-- Alerts -->
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

        <!-- Table Card -->
        <div class="card table-card">
            <div class="card-body p-0">
                <div class="p-3 d-flex justify-content-end">
                    <form action="${pageContext.request.contextPath}/ManageOrderController" method="post">
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
                                            <td><strong>${o.username}</strong></td>
                                            <td>${o.voucherCode != null ? o.voucherCode : '-'}</td>
                                            <td><fmt:formatNumber value="${o.discountAmount != null ? o.discountAmount : 0}" pattern="#,##0"/> ₫</td>
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
                                            <td><strong><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> ₫</strong></td>
                                            <td class="text-end px-4">
                                            </td>
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
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
