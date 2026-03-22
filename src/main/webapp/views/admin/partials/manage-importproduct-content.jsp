<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<section class="admin-content__section">
    <div class="page-header">
        <div>
            <h4>Import History</h4>
            <p class="text-muted mb-0">List of product import orders</p>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin/import-product?action=importproduct" class="btn btn-add text-white">
                <i class="bi bi-plus-circle me-1"></i> Import Product
            </a>
            <a href="${pageContext.request.contextPath}/admin/panel?view=dashboard" class="btn btn-back">
                <i class="bi bi-arrow-left"></i> Back
            </a>
        </div>
    </div>

    <c:if test="${param.success == 'import'}">
        <c:set var="popupMessage" scope="request" value="Product imported successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'importFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to import product." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card table-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th class="px-4 purchase-history-toggle-col"></th>
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
                                    <button type="button" class="btn-toggle" onclick="toggleImportDetail(event, ${h.purchaseOrderId})">
                                        <i class="bi bi-chevron-down"></i>
                                    </button>
                                </td>
                                <td><strong>#${h.purchaseOrderId}</strong></td>
                                <td>${h.brandName}</td>
                                <td>
                                    <div>${h.managerName}</div>
                                    <small class="info-text">${h.managerRole}</small>
                                </td>
                                <td class="amount">${h.totalAmount} VND</td>
                                <td>${h.createdAt}</td>
                            </tr>

                            <tr id="detail-${h.purchaseOrderId}" class="detail-row detail-row--hidden">
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
</section>

<script>
    let currentExpandedImport = null;

    function toggleImportDetail(event, orderId) {
        const row = document.getElementById('detail-' + orderId);
        const button = event.currentTarget;
        const icon = button.querySelector('i');

        if (currentExpandedImport && currentExpandedImport !== orderId) {
            const prevRow = document.getElementById('detail-' + currentExpandedImport);
            const prevButton = document.querySelector('[onclick="toggleImportDetail(event, ' + currentExpandedImport + ')"]');

            if (prevRow) {
                prevRow.style.display = 'none';
            }
            if (prevButton) {
                prevButton.classList.add('collapsed');
                prevButton.querySelector('i').classList.remove('bi-chevron-up');
                prevButton.querySelector('i').classList.add('bi-chevron-down');
            }
        }

        if (row.style.display === 'none') {
            row.style.display = 'table-row';
            icon.classList.remove('bi-chevron-down');
            icon.classList.add('bi-chevron-up');
            button.classList.remove('collapsed');
            currentExpandedImport = orderId;

            fetch('${pageContext.request.contextPath}/admin/import-product?action=viewdetail&orderId=' + orderId)
                .then(function (res) { return res.text(); })
                .then(function (data) {
                    document.getElementById('detail-content-' + orderId).innerHTML = data;
                })
                .catch(function () {
                    document.getElementById('detail-content-' + orderId).innerHTML =
                        '<div class="text-danger">Error loading details</div>';
                });
        } else {
            row.style.display = 'none';
            icon.classList.remove('bi-chevron-up');
            icon.classList.add('bi-chevron-down');
            button.classList.add('collapsed');
            currentExpandedImport = null;
        }
    }
</script>
