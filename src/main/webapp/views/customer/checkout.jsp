<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán | Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/checkout.css">
</head>
<body>

    <!-- Popup Thông báo -->
    <div class="popup-overlay" id="resultPopup">
        <div class="popup-content">
            <div class="popup-icon" id="popupIcon">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                </svg>
            </div>
            <h2 class="popup-title" id="popupTitle">Thành công!</h2>
            <p class="popup-message" id="popupMessage">
                Đặt hàng thành công!
            </p>
            <a href="${pageContext.request.contextPath}/" class="popup-btn" id="popupBtn">
                Quay về trang chủ
            </a>
        </div>
    </div>

    <div class="header-wrapper">
        <jsp:include page="/assets/header.jsp" />
    </div>

    <div class="checkout-container">
        <h1 class="page-title">Thanh toán</h1>
        
        <c:if test="${orderSuccess}">
            <div class="checkout-section" style="text-align: center; padding: 40px;">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" style="width: 60px; height: 60px; color: var(--success); margin-bottom: 16px;">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <h2 style="color: var(--success); margin-bottom: 8px;">Đơn hàng đã được đặt thành công!</h2>
                <p style="color: var(--muted);">Cảm ơn bạn đã mua sắm tại Averis Cosmetics.</p>
                <a href="${pageContext.request.contextPath}/home" class="popup-btn" style="display: inline-block; margin-top: 20px;">Về trang chủ</a>
            </div>
        </c:if>
        
        <c:if test="${!orderSuccess}">
        <form method="POST" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">
        
        <!-- CỘT TRÁI -->
        <div class="checkout-main">
            
            <!-- Section 1: Thông tin giao hàng -->
            <div class="checkout-section">
                <h2 class="section-title">
                    <span class="number">1</span>
                    Thông tin giao hàng
                </h2>
                
                <!-- Hiển thị danh sách địa chỉ đã lưu -->
                <c:if test="${not empty addresses}">
                    <div class="address-list">
                        <c:forEach items="${addresses}" var="addr">
                            <label class="address-item ${addr.isDefault ? 'selected' : ''}">
                                <input type="radio" name="addressId" value="${addr.addressId}" ${addr.isDefault ? 'checked' : ''}>
                                <div class="address-details">
                                    <div class="address-name">${addr.receiverName} - ${addr.phone}</div>
                                    <div class="address-text">
                                        ${addr.streetAddress}, ${addr.ward}, ${addr.district}, ${addr.province}
                                    </div>
                                    <c:if test="${addr.isDefault}">
                                        <span class="address-default">Mặc định</span>
                                    </c:if>
                                </div>
                            </label>
                        </c:forEach>
                    </div>
                    
                    <div style="margin-top: 16px;">
                        <a href="${pageContext.request.contextPath}/address?action=add" 
                           style="color: var(--accent); font-size: 14px; text-decoration: underline;">
                            + Thêm địa chỉ mới
                        </a>
                    </div>
                </c:if>
                
                <!-- Nếu chưa có địa chỉ -->
                <c:if test="${empty addresses}">
                    <p style="color: var(--muted); margin-bottom: 16px;">
                        Bạn chưa có địa chỉ giao hàng. Vui lòng thêm địa chỉ mới.
                    </p>
                    <a href="${pageContext.request.contextPath}/address?action=add" 
                       class="btn-place-order" style="width: fit-content; padding: 12px 24px; display: inline-block;">
                        + Thêm địa chỉ
                    </a>
                </c:if>
            </div>

            <!-- Section 2: Sản phẩm -->
            <div class="checkout-section">
                <h2 class="section-title">
                    <span class="number">2</span>
                    Sản phẩm (${cart.size()})
                </h2>
                
                <c:forEach items="${cart}" var="entry">
                    <div class="product-item">
                        <div class="product-image">
                            <c:choose>
                                <c:when test="${not empty entry.value.variant.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/assets/img/${entry.value.variant.imageUrl}" 
                                         alt="Product">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="No Image">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="product-info">
                            <div class="product-name">${entry.value.variant.productName}</div>
                            <div class="product-variant">${entry.value.variant.variantName}</div>
                            <div class="product-qty">Số lượng: ${entry.value.quantity}</div>
                        </div>
                        <div class="product-price">
                            <fmt:formatNumber value="${entry.value.subtotal}" type="currency" currencySymbol="₫"/>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Section 3: Phương thức thanh toán -->
            <div class="checkout-section">
                <h2 class="section-title">
                    <span class="number">3</span>
                    Phương thức thanh toán
                </h2>
                
                <div class="address-list">
                    <label class="address-item selected">
                        <input type="radio" name="paymentMethod" value="COD" checked>
                        <div class="address-details">
                            <div class="address-name">Thanh toán khi nhận hàng (COD)</div>
                            <div class="address-text">
                                Bạn sẽ thanh toán tiền mặt cho nhân viên giao hàng khi nhận được sản phẩm.
                            </div>
                        </div>
                    </label>
                </div>
                
                <!-- Error message -->
                <c:if test="${not empty error}">
                    <div style="margin-top: 16px; padding: 12px; background: #fee2e2; border: 1px solid #fecaca; border-radius: 6px; color: #dc2626; font-size: 14px;">
                        ${error}
                    </div>
                </c:if>
            </div>

        </div>

        <!-- CỘT PHẢI: TỔNG KẾT -->
        <div class="checkout-sidebar">
            <h3 class="summary-title">Đơn hàng của bạn</h3>
            
            <div class="summary-row">
                <span>Tạm tính (${cart.size()} sản phẩm):</span>
                <span style="font-weight:600"><fmt:formatNumber value="${total}" type="currency" currencySymbol="₫"/></span>
            </div>
            <div class="summary-row">
                <span>Phí vận chuyển:</span>
                <span>Miễn phí</span>
            </div>
            
            <!-- Voucher Section -->
            <div class="voucher-section">
                <div class="voucher-label">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 14.25l6-6m4.5-3.493V21.75l-3.75-1.5-3.75 1.5-3.75-1.5-3.75 1.5V4.757c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0c1.1.128 1.907 1.077 1.907 2.185zM9.75 9h.008v.008H9.75V9zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm4.125 4.5h.008v.008h-.008V13.5zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
                    </svg>
                    Mã giảm giá
                </div>
                <div class="voucher-input-group">
                    <input type="text" name="voucherCode" id="voucherCode" placeholder="Nhập mã voucher">
                    <button type="button" id="applyVoucherBtn">Áp dụng</button>
                </div>
            </div>
            
            <div class="summary-row">
                <span>Giảm giá:</span>
                <span class="discount" id="discountAmount">0₫</span>
            </div>
            
            <div class="summary-divider"></div>
            
            <div class="summary-total">
                <span>Tổng cộng:</span>
                <span id="totalAmount"><fmt:formatNumber value="${total}" type="currency" currencySymbol="₫"/></span>
            </div>
            
            <div style="text-align:right; font-size:12px; color:var(--muted); margin-top:5px;">
                (Đã bao gồm VAT nếu có)
            </div>

            <button type="submit" class="btn-place-order">
                Đặt hàng
            </button>
            
            <a href="${pageContext.request.contextPath}/cart" class="btn-back">
                ← Quay về giỏ hàng
            </a>
        </div>
        </form>
        </c:if>

    </div>

    <jsp:include page="/assets/footer.jsp" />

    <script>
        // Auto-select default address on page load
        document.addEventListener('DOMContentLoaded', function() {
            // Check for order success - show popup
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('success') === 'true') {
                const orderId = urlParams.get('orderId');
                showPopup(true, 'Đặt hàng thành công!<br>Mã đơn hàng: #' + orderId);
                // Remove query params to prevent showing again on refresh
                window.history.replaceState({}, document.title, window.location.pathname);
            }
            
            // Check for error - show popup
            if (urlParams.get('error')) {
                const errorMsg = decodeURIComponent(urlParams.get('error'));
                showPopup(false, errorMsg);
                // Remove query params
                window.history.replaceState({}, document.title, window.location.pathname);
            }

            // Auto-select default address
            const defaultAddress = document.querySelector('input[name="addressId"][checked]');
            if (defaultAddress) {
                const parentItem = defaultAddress.closest('.address-item');
                if (parentItem) {
                    parentItem.classList.add('selected');
                }
            } else {
                const firstAddress = document.querySelector('input[name="addressId"]');
                if (firstAddress) {
                    firstAddress.checked = true;
                    const parentItem = firstAddress.closest('.address-item');
                    if (parentItem) {
                        parentItem.classList.add('selected');
                    }
                }
            }
            
            // Voucher apply button handler (UI only)
            document.getElementById('applyVoucherBtn').addEventListener('click', function() {
                const voucherCode = document.getElementById('voucherCode').value.trim();
                if (!voucherCode) {
                    showPopup(false, 'Vui lòng nhập mã voucher!');
                    return;
                }
                showPopup(false, 'Chức năng voucher sẽ sớm được cập nhật!');
            });
        });
        
        // Auto-select address when clicking on address item
        document.querySelectorAll('.address-item').forEach(item => {
            item.addEventListener('click', function(e) {
                const radio = this.querySelector('input[type="radio"]');
                if (radio) {
                    radio.checked = true;
                }
                document.querySelectorAll('.address-item').forEach(i => i.classList.remove('selected'));
                this.classList.add('selected');
            });
        });
        
        // Function to show popup
        function showPopup(success, message) {
            const popup = document.getElementById('resultPopup');
            const icon = document.getElementById('popupIcon');
            const title = document.getElementById('popupTitle');
            const msg = document.getElementById('popupMessage');
            const btn = document.getElementById('popupBtn');
            
            if (!popup) return;
            
            if (success) {
                icon.classList.remove('error');
                icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" /></svg>';
                title.textContent = 'Đặt hàng thành công!';
                btn.style.display = 'inline-block';
                btn.textContent = 'Quay về trang chủ';
                btn.onclick = null;
            } else {
                icon.classList.add('error');
                icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>';
                title.textContent = 'Đặt hàng thất bại';
                btn.style.display = 'inline-block';
                btn.textContent = 'Đóng';
                btn.onclick = function() {
                    popup.classList.remove('show');
                    return false;
                };
            }
            
            msg.innerHTML = message;
            popup.classList.add('show');
        }
    </script>

</body>
</html>
