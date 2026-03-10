<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Vouchers</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-brand.css" rel="stylesheet">
    <style>
        .voucher-type-chip {
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            background-color: #e3f2fd;
            color: #0d47a1;
        }
        .voucher-discount-percent {
            color: #dc3545;
            font-weight: 600;
        }
        .voucher-discount-fixed {
            color: #28a745;
            font-weight: 600;
        }
        .voucher-status-active {
            color: #28a745;
            font-weight: 600;
        }
        .voucher-status-inactive {
            color: #dc3545;
            font-weight: 600;
        }
        .voucher-actions {
            display: flex;
            gap: 8px;
            justify-content: flex-end;
        }
        .voucher-btn-edit {
            background-color: #0d6efd;
            border: none;
            border-radius: 8px;
            padding: 6px 14px;
            color: white;
            font-weight: 500;
            cursor: pointer;
        }
        .voucher-btn-edit:hover {
            background-color: #0b5ed7;
        }
        .voucher-btn-delete {
            background-color: #dc3545;
            border: none;
            border-radius: 8px;
            padding: 6px 14px;
            color: white;
            font-weight: 500;
            cursor: pointer;
        }
        .voucher-btn-delete:hover {
            background-color: #bb2d3b;
        }
        /* Popup Styles */
        .voucher-popup-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1050;
            justify-content: center;
            align-items: center;
        }
        .voucher-popup-overlay.show {
            display: flex;
        }
        .voucher-popup-card {
            background: white;
            border-radius: 16px;
            width: 90%;
            max-width: 600px;
            max-height: 90vh;
            overflow-y: auto;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        }
        .voucher-popup-head {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 24px;
            border-bottom: 1px solid #e9ecef;
        }
        .voucher-popup-head h3 {
            margin: 0;
            font-weight: 600;
            color: #2c3e50;
        }
        .voucher-popup-close {
            background: none;
            border: none;
            font-size: 24px;
            color: #6c757d;
            cursor: pointer;
        }
        .voucher-popup-form {
            padding: 24px;
        }
        .voucher-form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 16px;
        }
        .voucher-form-grid label {
            display: block;
            font-weight: 500;
            color: #495057;
            margin-bottom: 8px;
        }
        .voucher-form-grid input,
        .voucher-form-grid select {
            width: 100%;
            padding: 10px 14px;
            border: 1px solid #ced4da;
            border-radius: 8px;
            font-size: 14px;
        }
        .voucher-form-grid input:focus,
        .voucher-form-grid select:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
            outline: none;
        }
        .voucher-popup-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            padding-top: 16px;
            border-top: 1px solid #e9ecef;
            margin-top: 16px;
        }
        .voucher-btn-save {
            background-color: #28a745;
            border: none;
            border-radius: 8px;
            padding: 10px 24px;
            color: white;
            font-weight: 500;
            cursor: pointer;
        }
        .voucher-btn-save:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <!-- Header -->
        <div class="page-header">
            <div>
                <h4>Manage Vouchers</h4>
                <p class="text-muted mb-0">List of vouchers and management</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                Back
            </a>
        </div>

        <!-- Alerts -->
        <c:if test="${param.success == 'add'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Voucher added successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.success == 'update'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Voucher updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.success == 'delete'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Voucher deleted successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'addFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to add voucher!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'updateFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to update voucher!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'deleteFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to delete voucher!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Table Card -->
        <div class="card table-card">
            <div class="card-body p-0">
                <div class="p-3 d-flex justify-content-end">
                    <button type="button" class="btn btn-add text-white" onclick="openVoucherPopup('create')">
                        <i class="bi bi-plus-circle"></i> Add Voucher
                    </button>
                </div>
                
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
                                        <tr>
                                            <td class="px-4">${v.voucherId}</td>
                                            <td><strong>${v.code}</strong></td>
                                            <td>
                                                <span class="voucher-type-chip">${v.voucherType}</span>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${v.discountValue}" pattern="#,##0.##" var="discountRaw"/>
                                                <c:choose>
                                                    <c:when test="${v.discountType eq 'PERCENT'}">
                                                        <span class="voucher-discount-percent">${discountRaw}%</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="voucher-discount-fixed">${discountRaw} ₫</span>
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
    </div>

    <!-- Add/Edit Popup -->
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
                        <label>Code</label>
                        <input type="text" id="popupCode" name="code" required>
                    </div>
                    <div>
                        <label>Discount Type</label>
                        <select id="popupDiscountType" name="discountType" required>
                            <option value="PERCENT">PERCENT</option>
                            <option value="FIXED">FIXED</option>
                        </select>
                    </div>
                    <div>
                        <label>Discount Value</label>
                        <input type="number" id="popupDiscountValue" name="discountValue" min="0" step="0.01" required>
                    </div>
                    <div>
                        <label>Quantity</label>
                        <input type="number" id="popupQuantity" name="quantity" min="0" required>
                    </div>
                    <div>
                        <label>Voucher Type</label>
                        <select id="popupVoucherType" name="voucherType" onchange="toggleVoucherTypeFields()" required>
                            <option value="FIXED_END_DATE">FIXED_END_DATE</option>
                            <option value="RELATIVE_DAYS">RELATIVE_DAYS</option>
                        </select>
                    </div>
                    <div>
                        <label>Status</label>
                        <select id="popupStatus" name="status" required>
                            <option value="1">Active</option>
                            <option value="0">Inactive</option>
                        </select>
                    </div>
                    <div id="fixedStartBox">
                        <label>Start Date</label>
                        <input type="datetime-local" id="popupFixedStartAt" name="fixedStartAt">
                    </div>
                    <div id="fixedEndBox">
                        <label>End Date</label>
                        <input type="datetime-local" id="popupFixedEndAt" name="fixedEndAt">
                    </div>
                    <div id="relativeDaysBox" style="display: none;">
                        <label>Valid Days</label>
                        <input type="number" id="popupRelativeDays" name="relativeDays" min="1">
                    </div>
                </div>

                <div class="voucher-popup-actions">
                    <button type="submit" id="voucherPopupSubmit" class="voucher-btn-save">
                        <i class="bi bi-check2-circle"></i> Save
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function openVoucherPopup(mode, id, code, discountType, discountValue, quantity, voucherType, fixedStartAt, fixedEndAt, relativeDays, status) {
            document.getElementById('voucherPopup').classList.add('show');
            document.getElementById('popupAction').value = mode === 'update' ? 'update' : 'create';
            document.getElementById('voucherPopupTitle').textContent = mode === 'update' ? 'Update Voucher' : 'Add Voucher';
            document.getElementById('voucherPopupSubmit').innerHTML = mode === 'update'
                    ? '<i class="bi bi-floppy"></i> Update'
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
            if (!raw || raw === 'null') return '';
            return raw.replace(' ', 'T').substring(0, 16);
        }

        document.addEventListener('DOMContentLoaded', function () {
            toggleVoucherTypeFields();
        });
    </script>
</body>
</html>
