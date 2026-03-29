<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="vi_VN"/>

<c:set var="isAdmin" value="${currentManagerRole eq 'ADMIN'}" />

<form action="${importBasePath}" method="post">
    <input type="hidden" name="action" value="receive">
    <input type="hidden" name="orderId" value="${orderId}">

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Import Code</div>
                <div class="fw-bold">${empty importOrder.importCode ? '-' : importOrder.importCode}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Supplier</div>
                <div class="fw-bold">${empty importOrder.supplierName ? '-' : importOrder.supplierName}</div>
                <div class="small text-muted">${empty importOrder.supplierPhone ? '' : importOrder.supplierPhone}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Invoice No</div>
                <div class="fw-bold">${empty importOrder.invoiceNo ? '-' : importOrder.invoiceNo}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Created By</div>
                <div class="fw-bold">${empty importOrder.managerName ? '-' : importOrder.managerName}</div>
                <div class="small text-muted">${empty importOrder.managerRole ? '' : importOrder.managerRole}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Status</div>
                <span class="badge ${orderStatus == 'RECEIVED' ? 'bg-success' : 'bg-warning text-dark'}">${orderStatus}</span>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Received By</div>
                <div class="fw-bold">${empty importOrder.receivedByName ? '-' : importOrder.receivedByName}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="border rounded-3 p-3 bg-light h-100">
                <div class="text-muted small mb-1">Received At</div>
                <div class="fw-bold">
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

    <div class="table-responsive">
        <table class="table table-sm mb-0 align-middle">
            <thead>
                <tr>
                    <th>Brand</th>
                    <th>Product</th>
                    <th>Variant</th>
                    <th class="text-end">Ordered</th>
                    <c:if test="${isAdmin}">
                        <th class="text-end">Import Price</th>
                    </c:if>
                    <th class="text-end">Received</th>
                    <c:if test="${isAdmin}">
                        <th class="text-end">Subtotal</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${details}" var="d">
                    <tr>
                        <td>${d.brandName}</td>
                        <td>
                            <strong>${d.productName}</strong>
                            <input type="hidden" name="variantId" value="${d.variantId}">
                        </td>
                        <td>${d.variantName}</td>
                        <td class="text-end">${d.quantity}</td>
                        <c:if test="${isAdmin}">
                            <td class="text-end">
                                <fmt:formatNumber value="${d.importPrice}" pattern="#,##0"/> VND
                            </td>
                        </c:if>
                        <td class="text-end">
                            <c:choose>
                                <c:when test="${orderStatus == 'PENDING'}">
                                    <input type="number"
                                           class="form-control form-control-sm text-end d-inline-block"
                                           style="max-width: 120px;"
                                           name="receivedQuantity"
                                           min="0"
                                           max="${d.quantity}"
                                           value="${d.quantity}">
                                </c:when>
                                <c:otherwise>
                                    ${d.receivedQuantity != null ? d.receivedQuantity : d.quantity}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${isAdmin}">
                            <td class="text-end amount">
                                <c:choose>
                                    <c:when test="${orderStatus == 'PENDING'}">
                                        <fmt:formatNumber value="${d.quantity * d.importPrice}" pattern="#,##0"/> VND
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${(d.receivedQuantity != null ? d.receivedQuantity : d.quantity) * d.importPrice}" pattern="#,##0"/> VND
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="p-3 border-top d-flex justify-content-between align-items-center">
        <c:if test="${isAdmin}">
            <div class="text-muted small">
                Total Amount:
                <strong>
                    <fmt:formatNumber value="${importOrder.totalAmount}" pattern="#,##0"/> VND
                </strong>
            </div>
        </c:if>
        <div class="ms-auto">
            <c:if test="${orderStatus == 'PENDING'}">
                <button type="submit" class="btn btn-add text-white">
                    <i class="bi bi-check-circle me-1"></i> Confirm Receipt
                </button>
            </c:if>
            <c:if test="${orderStatus != 'PENDING'}">
                <span class="badge bg-success">Order already received</span>
            </c:if>
        </div>
    </div>
</form>

