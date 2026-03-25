<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<section class="admin-content__section admin-page staff-page voucher-section">
    <div class="container py-4">
        <div class="page-header d-flex justify-content-between align-items-center mb-3">
            <div>
                <h4>View Voucher List</h4>
                <p class="text-muted mb-0">Staff can review voucher information here.</p>
            </div>
        </div>

        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Code</th>
                                <th>Type</th>
                                <th>Discount</th>
                                <th>Quantity</th>
                                <th>Claimed</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty vouchers}">
                                    <tr>
                                        <td colspan="7" class="text-center empty-state">
                                            <i class="bi bi-inbox d-block"></i>
                                            No vouchers found
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${vouchers}" var="v">
                                        <fmt:formatNumber value="${v.discountValue}" pattern="#,##0.##" var="discountRaw"/>
                                        <tr>
                                            <td class="px-4">${v.voucherId}</td>
                                            <td><strong>${v.code}</strong></td>
                                            <td>
                                                <span class="voucher-type-chip">${v.voucherType}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${v.discountType eq 'PERCENT'}">
                                                        <span class="voucher-discount-percent">${discountRaw}%</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="voucher-discount-fixed">${discountRaw} VND</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${v.quantity}</td>
                                            <td>${v.claimedQuantity}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${v.status}">
                                                        <span class="voucher-status-active">
                                                            <i class="bi bi-check-circle-fill"></i> Active
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="voucher-status-inactive">
                                                            <i class="bi bi-slash-circle-fill"></i> Inactive
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
