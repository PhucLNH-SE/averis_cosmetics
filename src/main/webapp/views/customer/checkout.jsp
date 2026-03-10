<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán | Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/checkout.css">
</head>
<body>

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
                <a href="${pageContext.request.contextPath}/home" class="popup-btn" id="popupBtn">
                    Quay về trang chủ
                </a>
            </div>
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

                <div class="checkout-main">
                    <div class="checkout-section">
                        <h2 class="section-title">
                            <span class="number">1</span>
                            Thông tin giao hàng
                        </h2>

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
                                   id="openCheckoutAddressLink"
                                   style="color: var(--accent); font-size: 14px; text-decoration: underline;">
                                    + Thêm địa chỉ mới
                                </a>
                            </div>
                        </c:if>

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
                                            <img src="${pageContext.request.contextPath}/assets/img/${entry.value.variant.imageUrl}" alt="Product">
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
                                    <fmt:formatNumber value="${entry.value.subtotal}" pattern="#,##0"/> ₫
                                </div>
                            </div>
                        </c:forEach>
                    </div>

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
                                                          <!-- MOMO -->
                                <label class="address-item">
                                    <input type="radio" name="paymentMethod" value="MOMO">
                                    <div class="address-details">
                                        <div class="address-name">Thanh toán qua ví MoMo</div>
                                        <div class="address-text">
                                            Thanh toán nhanh chóng và an toàn qua ví MoMo.
                                        </div>
                                    </div>
                                </label>
                        </div>

                        <c:if test="${not empty error}">
                            <div style="margin-top: 16px; padding: 12px; background: #fee2e2; border: 1px solid #fecaca; border-radius: 6px; color: #dc2626; font-size: 14px;">
                                ${error}
                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="checkout-sidebar">
                    <h3 class="summary-title">Đơn hàng của bạn</h3>

                    <div class="summary-row">
                        <span>Tạm tính (${cart.size()} sản phẩm):</span>
                        <span style="font-weight:600"><fmt:formatNumber value="${total}" pattern="#,##0"/> ₫</span>
                    </div>
                    <div class="summary-row">
                        <span>Phí vận chuyển:</span>
                        <span>Miễn phí</span>
                    </div>

                    <div class="voucher-section">
                        <div class="voucher-label">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M9 14.25l6-6m4.5-3.493V21.75l-3.75-1.5-3.75 1.5-3.75-1.5-3.75 1.5V4.757c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0c1.1.128 1.907 1.077 1.907 2.185zM9.75 9h.008v.008H9.75V9zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm4.125 4.5h.008v.008h-.008V13.5zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
                            </svg>
                            Mã giảm giá
                        </div>
                        <div class="voucher-input-group">
                            <input type="text" name="voucherCode" id="voucherCode" placeholder="Nhấn để chọn voucher"
                                   value="${not empty appliedVoucherCode ? appliedVoucherCode : param.voucherCode}" readonly>
                            <button type="button" class="voucher-open-btn" id="openVoucherPopupBtn">Chọn voucher</button>
                        </div>

                    </div>

                    <div class="summary-row">
                        <span>Giảm giá:</span>
                        <span class="discount" id="discountAmount"><fmt:formatNumber value="${discountAmount}" pattern="#,##0"/> ₫</span>
                    </div>

                    <div class="summary-divider"></div>

                    <div class="summary-total">
                        <span>Tổng cộng:</span>
                        <span id="totalAmount"><fmt:formatNumber value="${finalTotal}" pattern="#,##0"/> ₫</span>
                    </div>

                    <div style="text-align:right; font-size:12px; color:var(--muted); margin-top:5px;">
                        (Đã bao gồm VAT nếu có)
                    </div>

                    <button type="submit" class="btn-place-order" name="action" value="placeOrder">Đặt hàng</button>

                    <a href="${pageContext.request.contextPath}/cart" class="btn-back">← Quay về giỏ hàng</a>
                </div>

                <div id="voucherSelectPopup" class="checkout-voucher-popup" onclick="closeVoucherSelectPopup(event)">
                    <div class="checkout-voucher-popup-card">
                        <div class="checkout-voucher-popup-head">
                            <h4>Chọn voucher của bạn</h4>
                            <button type="button" class="checkout-voucher-popup-close" onclick="closeVoucherSelectPopup()">&times;</button>
                        </div>

                        <div class="checkout-voucher-popup-body">
                            <div class="voucher-manual-row">
                                <input type="text" id="manualVoucherCode" placeholder="Nhập mã voucher">
                                <button type="button" onclick="applyManualVoucherCode()">Dùng mã</button>
                            </div>

                            <div class="checkout-voucher-list">
                                <c:choose>
                                    <c:when test="${empty checkoutVouchers}">
                                        <div class="checkout-voucher-empty">Bạn chưa có voucher khả dụng.</div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${checkoutVouchers}" var="cv">
                                            <fmt:formatNumber value="${cv.voucher.discountValue}" pattern="#,##0" var="popupDiscountRaw"/>
                                            <div class="checkout-voucher-item" data-voucher-code="${cv.voucher.code}" onclick="selectVoucherCode('${cv.voucher.code}')">
                                                <div class="voucher-item-top">
                                                    <strong>${cv.voucher.code}</strong>
                                                    <span class="voucher-item-type">${cv.voucher.discountType}</span>
                                                </div>
                                                <div class="voucher-item-mid">
                                                    <c:choose>
                                                        <c:when test="${cv.voucher.discountType eq 'PERCENT'}">
                                                            Giảm ${popupDiscountRaw}%
                                                        </c:when>
                                                        <c:otherwise>
                                                            Giảm ${popupDiscountRaw} ₫
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="voucher-item-bottom">Hiệu lực đến: ${cv.effectiveTo}</div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="checkoutAddressPopup" class="checkout-address-popup" onclick="closeCheckoutAddressPopup(event)">
                    <div class="checkout-address-popup-card">
                        <div class="checkout-address-popup-head">
                            <div>
                                <h4>Thêm địa chỉ giao hàng</h4>
                            </div>
                            <button type="button" class="checkout-address-popup-close" onclick="closeCheckoutAddressPopup()">&times;</button>
                        </div>
                        <iframe id="checkoutAddressFrame"
                                class="checkout-address-frame"
                                src="about:blank"
                                title="Add Address"></iframe>
                    </div>
                </div>

            </form>
        </c:if>
    </div>

    <jsp:include page="/assets/footer.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const urlParams = new URLSearchParams(window.location.search);

            if (urlParams.get('success') === 'true') {
                const orderId = urlParams.get('orderId');
                showPopup(true, 'Đặt hàng thành công!<br>Mã đơn hàng: #' + orderId);
                window.history.replaceState({}, document.title, window.location.pathname);
            }

            if (urlParams.get('error')) {
                const errorMsg = decodeURIComponent(urlParams.get('error'));
                showPopup(false, errorMsg);
                window.history.replaceState({}, document.title, window.location.pathname);
            }

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

            initVoucherSelector();
            initCheckoutAddressPopup();
        });

        document.querySelectorAll('.address-item').forEach(item => {
            item.addEventListener('click', function () {
                const radio = this.querySelector('input[type="radio"]');
                if (radio) {
                    radio.checked = true;
                }
                document.querySelectorAll('.address-item').forEach(i => i.classList.remove('selected'));
                this.classList.add('selected');
            });
        });

        function initVoucherSelector() {
            const voucherInput = document.getElementById('voucherCode');
            const openBtn = document.getElementById('openVoucherPopupBtn');

            if (voucherInput) {
                voucherInput.addEventListener('click', function () {
                    openVoucherSelectPopup();
                });
            }
            if (openBtn) {
                openBtn.addEventListener('click', function () {
                    openVoucherSelectPopup();
                });
            }
        }

        function initCheckoutAddressPopup() {
            const openLink = document.getElementById('openCheckoutAddressLink');
            const frame = document.getElementById('checkoutAddressFrame');

            if (openLink) {
                openLink.addEventListener('click', function (event) {
                    event.preventDefault();
                    openCheckoutAddressPopup(this.href);
                });
            }

            if (!frame) {
                return;
            }

            frame.addEventListener('load', function () {
                try {
                    const frameWindow = frame.contentWindow;
                    const doc = frame.contentDocument || frameWindow.document;
                    const frameUrl = new URL(frameWindow.location.href);
                    const isProfileAddressPage = frameUrl.pathname.endsWith('/profile')
                            && frameUrl.searchParams.get('action') === 'view'
                            && frameUrl.searchParams.get('tab') === 'address';

                    if (isProfileAddressPage) {
                        closeCheckoutAddressPopup();
                        window.location.href = '${pageContext.request.contextPath}/checkout';
                        return;
                    }

                    const topbar = doc.querySelector('.topbar-shell');
                    const footer = doc.querySelector('footer');
                    const container = doc.querySelector('.container');

                    if (topbar) {
                        topbar.style.display = 'none';
                    }
                    if (footer) {
                        footer.style.display = 'none';
                    }
                    if (container) {
                        container.style.margin = '0 auto';
                        container.style.padding = '24px';
                        container.style.maxWidth = '100%';
                    }
                } catch (error) {
                    console.error('Cannot optimize checkout address frame:', error);
                }
            });
        }

        function openCheckoutAddressPopup(url) {
            const popup = document.getElementById('checkoutAddressPopup');
            const frame = document.getElementById('checkoutAddressFrame');
            if (!popup || !frame) {
                return;
            }

            frame.src = url || '${pageContext.request.contextPath}/address?action=add';
            popup.classList.add('show');
            document.body.classList.add('checkout-popup-open');
        }

        function closeCheckoutAddressPopup(event) {
            const popup = document.getElementById('checkoutAddressPopup');
            const frame = document.getElementById('checkoutAddressFrame');
            if (!popup) {
                return;
            }

            if (!event || event.target.id === 'checkoutAddressPopup') {
                popup.classList.remove('show');
                document.body.classList.remove('checkout-popup-open');
                if (frame) {
                    frame.src = 'about:blank';
                }
            }
        }


        function openVoucherSelectPopup() {
            const popup = document.getElementById('voucherSelectPopup');
            if (popup) {
                popup.classList.add('show');
            }
        }

        function closeVoucherSelectPopup(event) {
            const popup = document.getElementById('voucherSelectPopup');
            if (!popup) {
                return;
            }
            if (!event || event.target.id === 'voucherSelectPopup') {
                popup.classList.remove('show');
            }
        }

        function selectVoucherCode(code) {
            const voucherInput = document.getElementById('voucherCode');
            const manualInput = document.getElementById('manualVoucherCode');

            if (voucherInput) {
                voucherInput.value = code || '';
            }
            if (manualInput) {
                manualInput.value = code || '';
            }
            closeVoucherSelectPopup();
            submitApplyVoucher();
        }

        function applyManualVoucherCode() {
            const manualInput = document.getElementById('manualVoucherCode');
            const voucherInput = document.getElementById('voucherCode');

            if (!manualInput || !voucherInput) {
                return;
            }
            voucherInput.value = manualInput.value.trim();
            closeVoucherSelectPopup();
            submitApplyVoucher();
        }

        function submitApplyVoucher() {
            const form = document.getElementById('checkoutForm');
            const voucherInput = document.getElementById('voucherCode');
            if (!form || !voucherInput || !voucherInput.value.trim()) {
                return;
            }
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'applyVoucher';
            form.appendChild(actionInput);
            form.submit();
        }

        function showPopup(success, message) {
            const popup = document.getElementById('resultPopup');
            const icon = document.getElementById('popupIcon');
            const title = document.getElementById('popupTitle');
            const msg = document.getElementById('popupMessage');
            const btn = document.getElementById('popupBtn');

            if (!popup) {
                return;
            }

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
                btn.onclick = function () {
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
