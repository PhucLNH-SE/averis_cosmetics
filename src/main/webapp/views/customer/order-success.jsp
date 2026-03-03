<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt hàng thành công | Averis Cosmetics</title>
    
    <style>
        :root {
            --bg: #fff;
            --surface: #faf8f5;
            --border: #e9e2d8;
            --text: #1f2937;
            --muted: #6b7280;
            --accent: #b45309;
            --accent-hover: #92400e;
            --success: #16a34a;
        }

        body {
            font-family: Arial, sans-serif;
            background: var(--bg);
            color: var(--text);
            margin: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        .success-container {
            width: 92%;
            max-width: 600px;
            margin: 60px auto;
            flex: 1;
            text-align: center;
            padding: 40px;
        }

        .success-icon {
            width: 80px;
            height: 80px;
            background: #dcfce7;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 24px;
        }

        .success-icon svg {
            width: 40px;
            height: 40px;
            color: var(--success);
        }

        .success-title {
            font-size: 28px;
            font-weight: 700;
            color: var(--text);
            margin: 0 0 12px 0;
        }

        .success-message {
            font-size: 16px;
            color: var(--muted);
            margin: 0 0 32px 0;
            line-height: 1.6;
        }

        .order-info {
            background: var(--surface);
            border-radius: 8px;
            padding: 24px;
            margin-bottom: 32px;
            text-align: left;
        }

        .order-info-row {
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid var(--border);
            font-size: 15px;
        }

        .order-info-row:last-child {
            border-bottom: none;
        }

        .order-info-label {
            color: var(--muted);
        }

        .order-info-value {
            font-weight: 600;
            color: var(--text);
        }

        .order-info-value.highlight {
            color: var(--accent);
            font-size: 18px;
        }

        .delivery-address {
            background: var(--surface);
            border-radius: 8px;
            padding: 24px;
            margin-bottom: 32px;
            text-align: left;
        }

        .delivery-address-title {
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 12px;
            color: var(--text);
        }

        .delivery-address-text {
            font-size: 14px;
            color: var(--muted);
            line-height: 1.6;
        }

        .btn-home {
            display: inline-block;
            background: var(--accent);
            color: white;
            text-decoration: none;
            padding: 14px 32px;
            border-radius: 6px;
            font-weight: 600;
            transition: background 0.2s ease;
        }

        .btn-home:hover {
            background: var(--accent-hover);
        }

        .cod-notice {
            display: inline-block;
            background: #fef3c7;
            border: 1px solid #fcd34d;
            border-radius: 6px;
            padding: 16px;
            margin-bottom: 24px;
            font-size: 14px;
            color: #92400e;
            line-height: 1.5;
            text-align: left;
        }

    </style>
</head>
<body>

    <div class="header-wrapper">
        <jsp:include page="/assets/header.jsp" />
    </div>

    <div class="success-container">
        
        <!-- Success Icon -->
        <div class="success-icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
            </svg>
        </div>

        <!-- Success Title -->
        <h1 class="success-title">Đặt hàng thành công!</h1>
        
        <!-- Success Message -->
        <p class="success-message">
            Cảm ơn bạn đã đặt hàng tại Averis Cosmetics.<br>
            Chúng tôi sẽ liên hệ với bạn sớm nhất để xác nhận đơn hàng.
        </p>

        <!-- COD Notice -->
        <div class="cod-notice">
            <strong>Phương thức thanh toán:</strong> Thanh toán khi nhận hàng (COD)<br>
            Vui lòng chuẩn bị số tiền <strong><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/></strong> 
            để thanh toán cho nhân viên giao hàng khi nhận được sản phẩm.
        </div>

        <!-- Order Info -->
        <div class="order-info">
            <div class="order-info-row">
                <span class="order-info-label">Mã đơn hàng:</span>
                <span class="order-info-value">#${order.orderId}</span>
            </div>
            <div class="order-info-row">
                <span class="order-info-label">Ngày đặt:</span>
                <span class="order-info-value"><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
            </div>
            <div class="order-info-row">
                <span class="order-info-label">Trạng thái:</span>
                <span class="order-info-value">${order.orderStatus}</span>
            </div>
            <div class="order-info-row">
                <span class="order-info-label">Thanh toán:</span>
                <span class="order-info-value">${order.paymentStatus}</span>
            </div>
            <div class="order-info-row">
                <span class="order-info-label">Tổng tiền:</span>
                <span class="order-info-value highlight"><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/></span>
            </div>
        </div>

        <!-- Delivery Address -->
        <c:if test="${not empty address}">
            <div class="delivery-address">
                <div class="delivery-address-title">Thông tin giao hàng</div>
                <div class="delivery-address-text">
                    <strong>${address.receiverName}</strong> - ${address.phone}<br>
                    ${address.streetAddress}, ${address.ward}, ${address.district}, ${address.province}
                </div>
            </div>
        </c:if>

        <!-- Back to Home -->
        <a href="${pageContext.request.contextPath}/" class="btn-home">
            Tiếp tục mua sắm
        </a>

    </div>

    <jsp:include page="/assets/footer.jsp" />

</body>
</html>
