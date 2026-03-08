<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý voucher</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="voucher-manage-body">
<fmt:setLocale value="en_US"/>
<main class="voucher-manage-wrapper">
    <section class="voucher-manage-header">
        <div>
            <h1>Quản lý voucher</h1>
            <p>Danh sách voucher và thao tác thêm/cập nhật bằng popup</p>
        </div>
        <div class="voucher-manage-header-actions">
            <button type="button" class="voucher-btn-add" onclick="openVoucherPopup('create')">
                <i class="bi bi-plus-circle"></i> Thêm voucher
            </button>
            <a class="voucher-btn-back" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="bi bi-arrow-left"></i> Quay lại
            </a>
        </div>
    </section>

    <section class="voucher-manage-panel">
        <div class="voucher-table-wrap">
            <table class="voucher-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Kiểu voucher</th>
                    <th>Giảm giá</th>
                    <th>Số lượng</th>
                    <th>Đã nhận</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty vouchers}">
                        <tr>
                            <td colspan="8" class="voucher-empty">Không có voucher nào.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${vouchers}" var="v">
                            <tr>
                                <td>${v.voucherId}</td>
                                <td><strong>${v.code}</strong></td>
                                <td>
                                    <span class="voucher-type-chip">${v.voucherType}</span>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${v.discountValue}" pattern="#,##0.##" var="discountRaw"/>
                                    <c:set var="discountDot" value="${fn:replace(discountRaw, ',', '.')}"/>
                                    <c:choose>
                                        <c:when test="${v.discountType eq 'PERCENT'}">
                                            <span class="voucher-discount-percent">${discountDot}%</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="voucher-discount-fixed">${discountDot}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${v.quantity}</td>
                                <td>${v.claimedQuantity}</td>
                                <td>
                                    <span class="${v.status ? 'voucher-status-active' : 'voucher-status-inactive'}">
                                        <i class="bi ${v.status ? 'bi-check-circle-fill' : 'bi-slash-circle-fill'}"></i>
                                        ${v.status ? 'Hoạt động' : 'Tắt'}
                                    </span>
                                </td>
                                <td>
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
                                            <i class="bi bi-pencil-square"></i> Sửa
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/manage-voucher" method="post">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="voucherId" value="${v.voucherId}">
                                            <button type="submit" class="voucher-btn-delete">
                                                <i class="bi bi-trash"></i> Xóa
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
    </section>
</main>

<div id="voucherPopup" class="voucher-popup-overlay" onclick="closeVoucherPopup(event)">
    <div class="voucher-popup-card">
        <div class="voucher-popup-head">
            <h3 id="voucherPopupTitle">Thêm voucher</h3>
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
                    <label>Loại giảm giá</label>
                    <select id="popupDiscountType" name="discountType" required>
                        <option value="PERCENT">PERCENT</option>
                        <option value="FIXED">FIXED</option>
                    </select>
                </div>
                <div>
                    <label>Giá trị giảm</label>
                    <input type="number" id="popupDiscountValue" name="discountValue" min="0" step="0.01" required>
                </div>
                <div>
                    <label>Số lượng</label>
                    <input type="number" id="popupQuantity" name="quantity" min="0" required>
                </div>
                <div>
                    <label>Loại voucher</label>
                    <select id="popupVoucherType" name="voucherType" onchange="toggleVoucherTypeFields()" required>
                        <option value="FIXED_END_DATE">FIXED_END_DATE</option>
                        <option value="RELATIVE_DAYS">RELATIVE_DAYS</option>
                    </select>
                </div>
                <div>
                    <label>Trạng thái</label>
                    <select id="popupStatus" name="status" required>
                        <option value="1">Hoạt động</option>
                        <option value="0">Tắt</option>
                    </select>
                </div>
                <div id="fixedStartBox">
                    <label>Ngày bắt đầu</label>
                    <input type="datetime-local" id="popupFixedStartAt" name="fixedStartAt">
                </div>
                <div id="fixedEndBox">
                    <label>Ngày kết thúc</label>
                    <input type="datetime-local" id="popupFixedEndAt" name="fixedEndAt">
                </div>
                <div id="relativeDaysBox">
                    <label>Số ngày hiệu lực</label>
                    <input type="number" id="popupRelativeDays" name="relativeDays" min="1">
                </div>
            </div>

            <div class="voucher-popup-actions">
                <button type="submit" id="voucherPopupSubmit" class="voucher-btn-save">
                    <i class="bi bi-check2-circle"></i> Lưu
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function openVoucherPopup(mode, id, code, discountType, discountValue, quantity, voucherType, fixedStartAt, fixedEndAt, relativeDays, status) {
        document.getElementById('voucherPopup').classList.add('show');
        document.getElementById('popupAction').value = mode === 'update' ? 'update' : 'create';
        document.getElementById('voucherPopupTitle').textContent = mode === 'update' ? 'Cập nhật voucher' : 'Thêm voucher';
        document.getElementById('voucherPopupSubmit').innerHTML = mode === 'update'
                ? '<i class="bi bi-floppy"></i> Cập nhật'
                : '<i class="bi bi-check2-circle"></i> Thêm mới';

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
