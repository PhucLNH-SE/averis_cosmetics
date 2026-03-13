<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Import History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-importproduct.css" rel="stylesheet">
</head>
<body>
    <div class="container py-4">
        <!-- Header -->
        <div class="page-header">
            <div>
                <h4>Import History</h4>
                <p class="text-muted mb-0">List of product import orders</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/ImportProductController?action=importproduct" class="btn btn-add text-white">
                    <i class="bi bi-plus-circle me-1"></i> Import Product
                </a>
                <a href="${pageContext.request.contextPath}/views/admin/dashboard.jsp" class="btn btn-back">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
            </div>
        </div>

        <!-- Alerts -->
        <c:if test="${param.success == 'import'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Product imported successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'importFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to import product!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Table Card -->
        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4" style="width: 50px;"></th>
                                <th>Order ID</th>
                                <th>Brand</th>
                                <th>Manager</th>
                                <th>Total Amount</th>
                                <th>Created At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="h" items="${history}">
                                <tr>
                                    <td class="px-4">
                                        <button type="button" class="btn-toggle" onclick="toggleDetail(${h.purchaseOrderId})">
                                            <i class="bi bi-chevron-down"></i>
                                        </button>
                                    </td>
                                    <td><strong>#${h.purchaseOrderId}</strong></td>
                                    <td>${h.brandName}</td>
                                    <td>
                                        <div>${h.managerName}</div>
                                        <small class="info-text">${h.managerRole}</small>
                                    </td>
                                    <td class="amount">${h.totalAmount} VNĐ</td>
                                    <td>${h.createdAt}</td>
                                </tr>
                                
                                <!-- Row hiển thị detail -->
                                <tr id="detail-${h.purchaseOrderId}" class="detail-row" style="display: none;">
                                    <td colspan="6">
                                        <div id="detail-content-${h.purchaseOrderId}" class="detail-content">
                                            <div class="text-center">
                                                <div class="spinner-border spinner-border-sm" role="status">
                                                    <span class="visually-hidden">Loading...</span>
                                                </div>
                                                <span class="ms-2">Loading...</span>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty history}">
                                <tr>
                                    <td colspan="6" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No import history found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let currentExpanded = null;

        function toggleDetail(orderId) {
            let row = document.getElementById("detail-" + orderId);
            let button = event.currentTarget;
            let icon = button.querySelector('i');
            
            // Close previously expanded row
            if (currentExpanded && currentExpanded !== orderId) {
                let prevRow = document.getElementById("detail-" + currentExpanded);
                let prevButton = document.querySelector('[onclick="toggleDetail(' + currentExpanded + ')"]');
                if (prevRow) prevRow.style.display = "none";
                if (prevButton) {
                    prevButton.classList.add('collapsed');
                    prevButton.querySelector('i').classList.remove('bi-chevron-up');
                    prevButton.querySelector('i').classList.add('bi-chevron-down');
                }
            }
            
            if (row.style.display === "none") {
                row.style.display = "table-row";
                icon.classList.remove('bi-chevron-down');
                icon.classList.add('bi-chevron-up');
                button.classList.remove('collapsed');
                currentExpanded = orderId;
                
                // Fetch detail
                fetch("ImportProductController?action=viewdetail&orderId=" + orderId)
                    .then(res => res.text())
                    .then(data => {
                        document.getElementById("detail-content-" + orderId).innerHTML = data;
                    })
                    .catch(err => {
                        document.getElementById("detail-content-" + orderId).innerHTML = 
                            '<div class="text-danger">Error loading details</div>';
                    });
            } else {
                row.style.display = "none";
                icon.classList.remove('bi-chevron-up');
                icon.classList.add('bi-chevron-down');
                button.classList.add('collapsed');
                currentExpanded = null;
            }
        }
    </script>
</body>
</html>
