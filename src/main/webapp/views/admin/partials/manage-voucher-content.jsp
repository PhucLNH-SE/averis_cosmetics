<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<section class="admin-content__section admin-page admin-page--voucher voucher-section">
    <div class="page-header">
        <div>
            <h4>Manage Vouchers</h4>
            <p class="text-muted mb-0">List of vouchers and management</p>
        </div>
        <button type="button" class="btn btn-add text-white" onclick="openVoucherPopup('create')">
            <i class="bi bi-plus-circle"></i> Add Voucher
        </button>
    </div>

    <c:if test="${param.success == 'created'}">
        <c:set var="popupMessage" scope="request" value="Voucher created successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'updated'}">
        <c:set var="popupMessage" scope="request" value="Voucher updated successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <c:set var="popupMessage" scope="request" value="Voucher deleted successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'duplicateCode'}">
        <c:set var="popupMessage" scope="request" value="Voucher code already exists." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'invalidData'}">
        <c:set var="popupMessage" scope="request" value="Voucher data is invalid." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'invalidId' || param.error == 'notFound' || param.success == 'failed'}">
        <c:set var="popupMessage" scope="request" value="Voucher action failed." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

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
                            <th class="text-end px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty vouchers}">
                                <tr>
                                    <td colspan="8" class="text-center empty-state">
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
                                        <td class="text-end px-4">
                                            <div class="voucher-actions">
                                                <button type="button" class="voucher-btn-edit"
                                                        onclick="openVoucherPopup('update',
                                                                '${v.voucherId}',
                                                                '${v.code}',
                                                                '${v.discountType}',
                                                                '${v.discountValue}',
                                                                '${v.quantity}',
                                                                '${v.voucherType}',
                                                                '${v.fixedStartAt}',
                                                                '${v.fixedEndAt}',
                                                                '${v.relativeDays}',
                                                                '${v.status ? 1 : 0}')">
                                                    <i class="bi bi-pencil-square"></i> Edit
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/manage-voucher" method="post">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="voucherId" value="${v.voucherId}">
                                                    <button type="submit" class="voucher-btn-delete">
                                                        <i class="bi bi-trash"></i> Delete
                                                    </button>
                                                </form>
                                            </div>
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

    <div id="voucherPopup" class="voucher-popup-overlay" onclick="closeVoucherPopup(event)">
        <div class="voucher-popup-card">
            <div class="voucher-popup-head">
                <h3 id="voucherPopupTitle">Add Voucher</h3>
                <button type="button" class="voucher-popup-close" onclick="closeVoucherPopup()">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/manage-voucher" method="post" class="voucher-popup-form">
                <input type="hidden" id="popupAction" name="action" value="create">
                <input type="hidden" id="popupVoucherId" name="voucherId">

                <div class="voucher-form-grid">
                    <div>
                        <label for="popupCode">Code</label>
                        <input type="text" id="popupCode" name="code" required>
                    </div>
                    <div>
                        <label for="popupDiscountType">Discount Type</label>
                        <select id="popupDiscountType" name="discountType" required>
                            <option value="PERCENT">PERCENT</option>
                            <option value="FIXED">FIXED</option>
                        </select>
                    </div>
                    <div>
                        <label for="popupDiscountValue">Discount Value</label>
                        <input type="number" id="popupDiscountValue" name="discountValue" min="0" step="0.01" required>
                    </div>
                    <div>
                        <label for="popupQuantity">Quantity</label>
                        <input type="number" id="popupQuantity" name="quantity" min="0" required>
                    </div>
                    <div>
                        <label for="popupVoucherType">Voucher Type</label>
                        <select id="popupVoucherType" name="voucherType" onchange="toggleVoucherTypeFields()" required>
                            <option value="FIXED_END_DATE">FIXED_END_DATE</option>
                            <option value="RELATIVE_DAYS">RELATIVE_DAYS</option>
                        </select>
                    </div>
                    <div>
                        <label for="popupStatus">Status</label>
                        <select id="popupStatus" name="status" required>
                            <option value="1">Active</option>
                            <option value="0">Inactive</option>
                        </select>
                    </div>
                    <div id="fixedStartBox">
                        <label for="popupFixedStartAt">Start Date</label>
                        <input type="datetime-local" id="popupFixedStartAt" name="fixedStartAt">
                    </div>
                    <div id="fixedEndBox">
                        <label for="popupFixedEndAt">End Date</label>
                        <input type="datetime-local" id="popupFixedEndAt" name="fixedEndAt">
                    </div>
                    <div id="relativeDaysBox" class="voucher-relative-box">
                        <label for="popupRelativeDays">Valid Days</label>
                        <input type="number" id="popupRelativeDays" name="relativeDays" min="1">
                    </div>
                </div>

                <div class="voucher-popup-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeVoucherPopup()">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <button type="submit" id="voucherPopupSubmit" class="btn btn-success px-4">
                        <i class="bi bi-check2-circle"></i> Save
                    </button>
                </div>
            </form>
        </div>
    </div>
</section>

<script>
    function openVoucherPopup(mode, id, code, discountType, discountValue, quantity, voucherType, fixedStartAt, fixedEndAt, relativeDays, status) {
        document.getElementById('voucherPopup').classList.add('show');
        document.getElementById('popupAction').value = mode === 'update' ? 'update' : 'create';
        document.getElementById('voucherPopupTitle').textContent = mode === 'update' ? 'Update Voucher' : 'Add Voucher';
        document.getElementById('voucherPopupSubmit').className = mode === 'update'
                ? 'btn btn-primary px-4'
                : 'btn btn-success px-4';
        document.getElementById('voucherPopupSubmit').innerHTML = mode === 'update'
                ? '<i class="bi bi-check2-circle"></i> Update Changes'
                : '<i class="bi bi-check2-circle"></i> Save';

        document.getElementById('popupVoucherId').value = mode === 'update' ? (id || '') : '';
        document.getElementById('popupCode').value = mode === 'update' ? (code || '') : '';
        document.getElementById('popupDiscountType').value = mode === 'update' ? (discountType || 'PERCENT') : 'PERCENT';
        document.getElementById('popupDiscountValue').value = mode === 'update' ? (discountValue || '0') : '';
        document.getElementById('popupQuantity').value = mode === 'update' ? (quantity || '0') : '';
        document.getElementById('popupVoucherType').value = mode === 'update' ? (voucherType || 'FIXED_END_DATE') : 'FIXED_END_DATE';
        document.getElementById('popupStatus').value = mode === 'update' ? (status || '1') : '1';
        document.getElementById('popupFixedStartAt').value = mode === 'update' ? toDateTimeLocal(fixedStartAt) : '';
        document.getElementById('popupFixedEndAt').value = mode === 'update' ? toDateTimeLocal(fixedEndAt) : '';
        document.getElementById('popupRelativeDays').value = mode === 'update' && relativeDays !== 'null' ? (relativeDays || '') : '';

        toggleVoucherTypeFields();
    }

    function closeVoucherPopup(event) {
        if (!event || event.target.id === 'voucherPopup') {
            document.getElementById('voucherPopup').classList.remove('show');
        }
    }

    function toggleVoucherTypeFields() {
        const type = document.getElementById('popupVoucherType').value;
        const showFixed = type === 'FIXED_END_DATE';

        document.getElementById('fixedStartBox').style.display = showFixed ? 'block' : 'none';
        document.getElementById('fixedEndBox').style.display = showFixed ? 'block' : 'none';
        document.getElementById('relativeDaysBox').style.display = showFixed ? 'none' : 'block';
    }

    function toDateTimeLocal(raw) {
        if (!raw || raw === 'null') {
            return '';
        }
        return raw.replace(' ', 'T').substring(0, 16);
    }

    document.addEventListener('DOMContentLoaded', function () {
        toggleVoucherTypeFields();
    });
</script>
