<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Voucher</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="customer-voucher-body">
<jsp:include page="/assets/header.jsp" />

<main class="customer-voucher-wrapper">
    <section class="customer-voucher-head">
        <div>
            <h1>My Voucher</h1>
            <p>Nhập mã để thêm voucher và quản lý các voucher bạn đang sở hữu.</p>
        </div>
    </section>

    <section class="customer-voucher-claim-card">
        <c:if test="${param.success == 'claimed'}">
            <div class="customer-voucher-alert success">Claim voucher thành công.</div>
        </c:if>
        <c:if test="${param.error == 'emptyCode'}">
            <div class="customer-voucher-alert error">Vui lòng nhập mã voucher.</div>
        </c:if>
        <c:if test="${param.error == 'codeNotFound'}">
            <div class="customer-voucher-alert error">Mã voucher không tồn tại hoặc đã bị tắt.</div>
        </c:if>
        <c:if test="${param.error == 'outOfStock'}">
            <div class="customer-voucher-alert error">Voucher đã hết số lượng.</div>
        </c:if>
        <c:if test="${param.error == 'alreadyClaimed'}">
            <div class="customer-voucher-alert error">Bạn đã nhận voucher này rồi.</div>
        </c:if>
        <c:if test="${param.error == 'voucherExpired'}">
            <div class="customer-voucher-alert error">Voucher đã hết hạn nhận.</div>
        </c:if>
        <c:if test="${param.error == 'dbError'}">
            <div class="customer-voucher-alert error">Lỗi hệ thống khi thêm voucher. Vui lòng thử lại.</div>
        </c:if>
        <c:if test="${param.error == 'claimFailed'}">
            <div class="customer-voucher-alert error">Claim voucher thất bại.</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/my-voucher" class="customer-voucher-claim-form">
            <input type="text" name="voucherCode" placeholder="Nhập voucher code" required>
            <button type="submit">Add Voucher</button>
        </form>
    </section>

    <section class="customer-voucher-table-card">
        <div class="customer-voucher-table-wrap">
            <table class="customer-voucher-table">
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Discount</th>
                    <th>Ngày bắt đầu</th>
                    <th>Hạn sử dụng</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty myVouchers}">
                        <tr>
                            <td colspan="5" class="customer-voucher-empty">Bạn chưa có voucher nào.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${myVouchers}" var="cv">
                            <fmt:formatNumber value="${cv.voucher.discountValue}" pattern="#,##0.##" var="discountRaw"/>
                            <c:set var="discountDot" value="${fn:replace(discountRaw, ',', '.')}"/>
                            <c:set var="effectiveFromRaw" value="${fn:replace(cv.effectiveFrom, 'T', ' ')}"/>
                            <c:set var="effectiveRaw" value="${fn:replace(cv.effectiveTo, 'T', ' ')}"/>
                            <tr>
                                <td><strong>${cv.voucher.code}</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cv.voucher.discountType eq 'PERCENT'}">
                                            ${discountDot}%
                                        </c:when>
                                        <c:otherwise>
                                            ${discountDot}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${fn:length(effectiveFromRaw) >= 16 ? fn:substring(effectiveFromRaw, 0, 16) : effectiveFromRaw}</td>
                                <td>${fn:length(effectiveRaw) >= 16 ? fn:substring(effectiveRaw, 0, 16) : effectiveRaw}</td>
                                <td>
                                    <span class="customer-voucher-status ${cv.status == 'ACTIVE' ? 'active' : (cv.status == 'USED' ? 'used' : 'expired')}">
                                        ${cv.status}
                                    </span>
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

<jsp:include page="/assets/footer.jsp" />
</body>
</html>
