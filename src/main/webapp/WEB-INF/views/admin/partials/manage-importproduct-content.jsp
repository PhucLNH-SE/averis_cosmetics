<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section">
    <div class="page-header">
        <div>
            <h4>Manage Import</h4>
            <p class="text-muted mb-0">Review and confirm received quantities</p>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin/import-product?action=importproduct" class="btn btn-add text-white">
                <i class="bi bi-plus-circle me-1"></i> Create Import Order
            </a>
            <a href="${pageContext.request.contextPath}/admin/manage-statistic" class="btn btn-back">
                <i class="bi bi-arrow-left"></i> Back
            </a>
        </div>
    </div>

    <c:if test="${param.success == 'import'}">
        <c:set var="popupMessage" scope="request" value="Import order created successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'received'}">
        <c:set var="popupMessage" scope="request" value="Import receipt confirmed successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'importFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to import product." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'totalAmountExceeded'}">
        <c:set var="popupMessage" scope="request" value="Total amount cannot exceed 9,999,999,999 VND." />
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
                            <th>Manager</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Received Info</th>
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
                                <td class="amount">
                                    <fmt:formatNumber value="${h.totalAmount}" pattern="#,##0"/> VND
                                </td>
                                <td>
                                    <span class="badge ${h.status == 'RECEIVED' ? 'bg-success' : 'bg-warning text-dark'}">
                                        ${h.status}
                                    </span>
                                </td>
                                <td>
                                    <fmt:parseDate value="${h.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedCreatedAt" type="both" />
                                    <fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty h.receivedAt}">
                                            <div>${empty h.receivedByName ? 'Updated' : h.receivedByName}</div>
                                            <fmt:parseDate value="${h.receivedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedReceivedAt" type="both" />
                                            <small class="info-text"><fmt:formatDate value="${parsedReceivedAt}" pattern="dd/MM/yyyy HH:mm" /></small>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Pending</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                            onclick="openImportDetail(${h.purchaseOrderId})">
                                        View
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty history}">
                            <tr>
                                <td colspan="10" class="text-center empty-state">
                                    <i class="bi bi-inbox d-block"></i>
                                    No Manage Import found
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
    <div class="modal-dialog modal-xl modal-dialog-scrollable import-detail-modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Import Order Detail</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="import-detail-body">
                <c:choose>
                    <c:when test="${autoOpenImportDetail}">
                        <jsp:include page="/WEB-INF/views/admin/import-detail.jsp" />
                    </c:when>
                    <c:otherwise>
                        <div class="text-center">
                            <div class="spinner-border" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <span class="ms-2">Loading...</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
    function parseImportDetailDigits(raw) {
        const text = String(raw == null ? '' : raw).replace(/,/g, '').trim();
        const dotParts = text.split('.');
        const digits = dotParts.length === 2 && dotParts[1].length <= 2
                ? (dotParts[0] || '').replace(/\D/g, '')
                : text.replace(/\D/g, '');
        return digits ? BigInt(digits) : 0n;
    }

    function formatImportDetailMoney(value) {
        const digits = String(value == null ? 0 : value).replace(/^0+(?=\d)/, '') || '0';
        return digits.replace(/\B(?=(\d{3})+(?!\d))/g, '.') + ' VND';
    }

    function initializeImportDetailForm(root) {
        const form = root.querySelector('form[data-import-detail-form]');
        if (!form || form.dataset.initialized === 'true') {
            return;
        }

        form.dataset.initialized = 'true';
        const maxTotal = parseImportDetailDigits(form.getAttribute('data-max-total'));
        const maxQuantity = parseImportDetailDigits(form.getAttribute('data-max-quantity'));

        function normalizeImportDetailQuantityInput(input) {
            input.value = String(input.value == null ? '' : input.value).replace(/\D/g, '');
        }

        function updateImportDetailTotals() {
            let total = 0n;
            let quantityOverflow = false;
            form.querySelectorAll('tbody tr[data-import-price]').forEach(function (row) {
                const qtyInput = row.querySelector('input[name="receivedQuantity"]');
                const quantity = qtyInput
                        ? parseImportDetailDigits(qtyInput.value)
                        : parseImportDetailDigits(row.getAttribute('data-default-quantity'));
                if (quantity > maxQuantity) {
                    quantityOverflow = true;
                }
                const price = parseImportDetailDigits(row.getAttribute('data-import-price'));
                const subtotal = quantity * price;
                const subtotalEl = row.querySelector('.import-detail-row-subtotal');
                if (subtotalEl) {
                    subtotalEl.textContent = formatImportDetailMoney(subtotal);
                }
                total += subtotal;
            });

            const totalEl = form.querySelector('#importDetailGrandTotal');
            if (totalEl) {
                totalEl.textContent = formatImportDetailMoney(total);
            }

            const alertEl = form.querySelector('#importDetailTotalLimitAlert');
            const submitBtn = form.querySelector('#confirmImportReceiptBtn');
            const limitExceeded = total > maxTotal;
            const blocked = limitExceeded || quantityOverflow;

            if (alertEl) {
                if (quantityOverflow) {
                    alertEl.textContent = 'Received quantity cannot exceed 2.147.483.647.';
                    alertEl.classList.remove('d-none');
                } else if (limitExceeded) {
                    alertEl.textContent = 'Total amount cannot exceed ' + formatImportDetailMoney(maxTotal) + '.';
                    alertEl.classList.remove('d-none');
                } else {
                    alertEl.textContent = '';
                    alertEl.classList.add('d-none');
                }
            }

            if (submitBtn) {
                submitBtn.disabled = blocked;
            }
            form.dataset.limitExceeded = blocked ? 'true' : 'false';
        }

        form.querySelectorAll('input[name="receivedQuantity"]').forEach(function (input) {
            input.addEventListener('input', function () {
                normalizeImportDetailQuantityInput(input);
                updateImportDetailTotals();
            });
        });

        form.addEventListener('submit', function (event) {
            updateImportDetailTotals();
            if (form.dataset.limitExceeded === 'true') {
                event.preventDefault();
            }
        });

        updateImportDetailTotals();
    }

    function openImportDetail(orderId) {
        const modalEl = document.getElementById('importDetailModal');
        const modal = new bootstrap.Modal(modalEl);
        const body = document.getElementById('import-detail-body');
        body.innerHTML =
            '<div class="text-center">' +
            '<div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div>' +
            '<span class="ms-2">Loading...</span>' +
            '</div>';

        fetch('${pageContext.request.contextPath}/admin/import-product?action=viewdetail&orderId=' + orderId)
            .then(function (res) { return res.text(); })
            .then(function (data) {
                body.innerHTML = data;
                initializeImportDetailForm(modalEl);
            })
            .catch(function () {
                body.innerHTML = '<div class="text-danger">Error loading details</div>';
            });

        modal.show();
    }

    document.addEventListener('DOMContentLoaded', function () {
        const modalEl = document.getElementById('importDetailModal');
        initializeImportDetailForm(modalEl);
        <c:if test="${autoOpenImportDetail}">
            new bootstrap.Modal(modalEl).show();
        </c:if>
    });
</script>




