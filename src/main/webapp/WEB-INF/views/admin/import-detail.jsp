<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="vi_VN"/>

<c:set var="isAdmin" value="${currentManagerRole eq 'ADMIN'}" />

<form action="${importBasePath}" method="post" data-import-detail-form="true" data-max-total="${maxImportTotalAmount}" data-max-quantity="2147483647">
    <input type="hidden" name="action" value="receive">
    <input type="hidden" name="orderId" value="${orderId}">

    <c:if test="${not empty importDetailError}">
        <div class="alert alert-danger py-2 mb-3">${importDetailError}</div>
    </c:if>

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Import Code</div>
                <div class="fw-bold import-detail-card__value import-detail-card__value--code">${empty importOrder.importCode ? '-' : importOrder.importCode}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Supplier</div>
                <div class="fw-bold import-detail-card__value">${empty importOrder.supplierName ? '-' : importOrder.supplierName}</div>
                <div class="small text-muted import-detail-card__meta">${empty importOrder.supplierPhone ? '' : importOrder.supplierPhone}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Invoice No</div>
                <div class="fw-bold import-detail-card__value">${empty importOrder.invoiceNo ? '-' : importOrder.invoiceNo}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Created By</div>
                <div class="fw-bold import-detail-card__value">${empty importOrder.managerName ? '-' : importOrder.managerName}</div>
                <div class="small text-muted import-detail-card__meta">${empty importOrder.managerRole ? '' : importOrder.managerRole}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Status</div>
                <span class="badge ${orderStatus == 'RECEIVED' ? 'bg-success' : 'bg-warning text-dark'}">${orderStatus}</span>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Received By</div>
                <div class="fw-bold import-detail-card__value">${empty importOrder.receivedByName ? '-' : importOrder.receivedByName}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100 import-detail-card">
                <div class="text-muted small mb-1 import-detail-card__label">Received At</div>
                <div class="fw-bold import-detail-card__value">
                    <c:choose>
                        <c:when test="${not empty importOrder.receivedAt}">
                            <fmt:parseDate value="${importOrder.receivedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDetailReceivedAt" type="both" />
                            <fmt:formatDate value="${parsedDetailReceivedAt}" pattern="dd/MM/yyyy HH:mm" />
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <c:if test="${not empty importOrder.note}">
            <div class="col-12">
                <div class="border rounded-3 p-3 bg-light">
                    <div class="text-muted small mb-1">Note</div>
                    <div>${importOrder.note}</div>
                </div>
            </div>
        </c:if>
    </div>

    <div class="table-responsive import-detail-table-wrap">
        <table class="table table-sm mb-0 align-middle import-detail-table">
            <thead>
                <tr>
                    <th class="import-detail-table__brand">Brand</th>
                    <th class="import-detail-table__product">Product</th>
                    <th class="import-detail-table__variant">Variant</th>
                    <th class="text-end import-detail-table__ordered">Ordered</th>
                    <c:if test="${isAdmin}">
                        <th class="text-end import-detail-table__price">Import Price</th>
                    </c:if>
                    <th class="text-end import-detail-table__received">Received</th>
                    <c:if test="${isAdmin}">
                        <th class="text-end import-detail-table__subtotal">Subtotal</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${details}" var="d">
                    <tr data-import-price="${d.importPrice}" data-default-quantity="${d.quantity}">
                        <td class="import-detail-table__brand"><c:out value="${d.brandName}" /></td>
                        <td class="import-detail-table__product">
                            <strong>${d.productName}</strong>
                            <input type="hidden" name="variantId" value="${d.variantId}">
                        </td>
                        <td class="import-detail-table__variant"><c:out value="${d.variantName}" /></td>
                        <td class="text-end import-detail-table__ordered">${d.quantity}</td>
                        <c:if test="${isAdmin}">
                            <td class="text-end import-detail-table__price">
                                <fmt:formatNumber value="${d.importPrice}" pattern="#,##0"/> VND
                            </td>
                        </c:if>
                        <td class="text-end import-detail-table__received">
                            <c:choose>
                                <c:when test="${orderStatus == 'PENDING'}">
                                    <input type="text"
                                           class="form-control form-control-sm text-end import-detail-table__received-input"
                                           name="receivedQuantity"
                                           inputmode="numeric"
                                           autocomplete="off"
                                           value="${d.receivedQuantity != null ? d.receivedQuantity : d.quantity}">
                                </c:when>
                                <c:otherwise>
                                    ${d.receivedQuantity != null ? d.receivedQuantity : d.quantity}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${isAdmin}">
                            <td class="text-end amount import-detail-row-subtotal import-detail-table__subtotal">
                                <fmt:formatNumber value="${(d.receivedQuantity != null ? d.receivedQuantity : d.quantity) * d.importPrice}" pattern="#,##0"/> VND
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="p-3 border-top">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
            <c:if test="${isAdmin}">
                <div class="text-muted small import-detail-summary">
                    <span class="import-detail-summary__label">Total Amount:</span>
                    <strong id="importDetailGrandTotal" class="import-detail-summary__value">
                        <fmt:formatNumber value="${importOrder.totalAmount}" pattern="#,##0"/> VND
                    </strong>
                </div>
            </c:if>
            <div class="ms-auto">
                <c:if test="${orderStatus == 'PENDING'}">
                    <button type="submit" class="btn btn-add text-white" id="confirmImportReceiptBtn">
                        <i class="bi bi-check-circle me-1"></i> Confirm Receipt
                    </button>
                </c:if>
                <c:if test="${orderStatus != 'PENDING'}">
                    <span class="badge bg-success">Order already received</span>
                </c:if>
            </div>
        </div>
        <div class="text-danger small mt-2 d-none" id="importDetailTotalLimitAlert"></div>
    </div>
</form>

