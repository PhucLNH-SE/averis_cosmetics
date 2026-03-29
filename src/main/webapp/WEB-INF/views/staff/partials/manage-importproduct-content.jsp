<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section">
    <div class="page-header">
        <div>
            <h4>Pending Import Orders</h4>
            <p class="text-muted mb-0">Review pending import orders and confirm received quantities</p>
        </div>
    </div>

    <c:if test="${param.success == 'received'}">
        <c:set var="popupMessage" scope="request" value="Import receipt confirmed successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'importFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to confirm import receipt." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card table-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Import Code</th>
                            <th>Order ID</th>
                            <th>Supplier</th>
                            <th>Created By</th>
                            <th>Created At</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${history}">
                            <tr>
                                <td><strong>${empty h.importCode ? '-' : h.importCode}</strong></td>
                                <td><strong>${h.purchaseOrderId}</strong></td>
                                <td>
                                    <div>${empty h.supplierName ? '-' : h.supplierName}</div>
                                    <small class="info-text">${empty h.invoiceNo ? 'No invoice' : h.invoiceNo}</small>
                                </td>
                                <td>
                                    <div>${h.managerName}</div>
                                    <small class="info-text">${h.managerRole}</small>
                                </td>
                                <td>
                                    <fmt:parseDate value="${h.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedCreatedAt" type="both" />
                                    <fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <span class="badge bg-warning text-dark">${h.status}</span>
                                </td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                            onclick="openImportDetail(${h.purchaseOrderId})">
                                        Review
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty history}">
                            <tr>
                                <td colspan="8" class="text-center empty-state">
                                    <i class="bi bi-inbox d-block"></i>
                                    No pending import orders
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<div class="modal fade" id="importDetailModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Import Order Detail</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="import-detail-body">
                <div class="text-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <span class="ms-2">Loading...</span>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function openImportDetail(orderId) {
        const modalEl = document.getElementById('importDetailModal');
        const modal = new bootstrap.Modal(modalEl);
        const body = document.getElementById('import-detail-body');
        body.innerHTML =
            '<div class="text-center">' +
            '<div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div>' +
            '<span class="ms-2">Loading...</span>' +
            '</div>';

        fetch('${pageContext.request.contextPath}/staff/import-product?action=viewdetail&orderId=' + orderId)
            .then(function (res) { return res.text(); })
            .then(function (data) {
                body.innerHTML = data;
            })
            .catch(function () {
                body.innerHTML = '<div class="text-danger">Error loading details</div>';
            });

        modal.show();
    }
</script>

