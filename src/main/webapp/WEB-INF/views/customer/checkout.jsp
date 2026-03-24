<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout | Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="checkout-page">

    <div class="header-wrapper">
        <jsp:include page="/assets/header.jsp" />
    </div>

    <div class="checkout-container">
        <div class="checkout-top-actions">
            <a href="${pageContext.request.contextPath}/home" class="product-back-link" title="Back to home">
                <i class="fa-solid fa-arrow-left-long"></i>
                Back to home
            </a>
        </div>
        <h1 class="page-title">Checkout</h1>

        <c:if test="${not empty error}">
            <c:set var="popupMessage" scope="request" value="${error}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${not empty successMessage}">
            <c:set var="popupMessage" scope="request" value="${successMessage}" />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>

        <c:if test="${orderSuccess}">
            <div class="checkout-section checkout-success">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="checkout-success-icon">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <h2 class="checkout-success-title">Your order has been placed successfully!</h2>
                <p class="checkout-success-copy">Thank you for shopping with Averis Cosmetics.</p>
                <a href="${pageContext.request.contextPath}/home" class="popup-btn checkout-success-link">Back to home</a>
            </div>
        </c:if>

        <c:if test="${!orderSuccess}">
            <form method="POST" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">

                <div class="checkout-main">
                    <div class="checkout-section">
                        <h2 class="section-title">
                            <span class="number">1</span>
                            Shipping Information
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
                                                <span class="address-default">Default</span>
                                            </c:if>
                                        </div>
                                    </label>
                                </c:forEach>
                            </div>

                            <div class="checkout-address-add-row">
                                <a href="${pageContext.request.contextPath}/address?action=add"
                                   id="openCheckoutAddressLink"
                                   class="checkout-address-popup-trigger checkout-address-add-link">
                                    + Add new address
                                </a>
                            </div>
                        </c:if>

                        <c:if test="${empty addresses}">
                            <p class="checkout-empty-address-note">
                                You do not have a delivery address yet. Please add a new address.
                            </p>
                            <a href="${pageContext.request.contextPath}/address?action=add"
                               class="checkout-address-popup-trigger checkout-address-add-link">
                                + Add address
                            </a>
                        </c:if>
                    </div>

                    <div class="checkout-section">
                        <h2 class="section-title">
                            <span class="number">2</span>
                            Items (${cart.size()})
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
                                    <div class="product-qty">Quantity: ${entry.value.quantity}</div>
                                </div>
                                <div class="product-price">
                                    <fmt:formatNumber value="${entry.value.subtotal}" pattern="#,##0"/> VND
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="checkout-section">
                        <h2 class="section-title">
                            <span class="number">3</span>
                            Payment Method
                        </h2>

                        <div class="address-list">
                            <label class="address-item ${selectedPaymentMethod ne 'MOMO' ? 'selected' : ''}">
                                <input type="radio" name="paymentMethod" value="COD" ${selectedPaymentMethod ne 'MOMO' ? 'checked' : ''}>
                                <div class="address-details">
                                    <div class="address-name">Cash on Delivery (COD)</div>
                                    <div class="address-text">
                                        Pay cash to the courier when you receive your items.
                                    </div>
                                </div>
                            </label>
                                                          <!-- MOMO -->
                                <label class="address-item ${selectedPaymentMethod eq 'MOMO' ? 'selected' : ''}">
                                    <input type="radio" name="paymentMethod" value="MOMO" ${selectedPaymentMethod eq 'MOMO' ? 'checked' : ''}>
                                    <div class="address-details">
                                        <div class="address-name">Pay with MoMo</div>
                                        <div class="address-text">
                                            Fast and secure payment via MoMo.
                                        </div>
                                    </div>
                                </label>
                        </div>
                    </div>
                </div>

                <div class="checkout-sidebar">
                    <h3 class="summary-title">Your Order</h3>

                    <div class="summary-row">
                        <span>Subtotal (${cart.size()} items):</span>
                        <span class="summary-value-strong"><fmt:formatNumber value="${total}" pattern="#,##0"/> VND</span>
                    </div>
                    <div class="summary-row">
                        <span>Shipping:</span>
                        <span>Free</span>
                    </div>

                    <div class="voucher-section">
                        <div class="voucher-label">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M9 14.25l6-6m4.5-3.493V21.75l-3.75-1.5-3.75 1.5-3.75-1.5-3.75 1.5V4.757c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0c1.1.128 1.907 1.077 1.907 2.185zM9.75 9h.008v.008H9.75V9zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm4.125 4.5h.008v.008h-.008V13.5zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
                            </svg>
                            Voucher
                        </div>
                        <div class="voucher-input-group">
                            <input type="text" name="voucherCode" id="voucherCode" placeholder="Click to choose a voucher"
                                   value="${not empty appliedVoucherCode ? appliedVoucherCode : param.voucherCode}" readonly>
                            <button type="button" class="voucher-open-btn" id="openVoucherPopupBtn">Choose voucher</button>
                        </div>

                    </div>

                    <div class="summary-row">
                        <span>Discount:</span>
                        <span class="discount" id="discountAmount"><fmt:formatNumber value="${discountAmount}" pattern="#,##0"/> VND</span>
                    </div>

                    <div class="summary-divider"></div>

                    <div class="summary-total">
                        <span>Total:</span>
                        <span id="totalAmount"><fmt:formatNumber value="${finalTotal}" pattern="#,##0"/> VND</span>
                    </div>

                    <div class="checkout-vat-note">
                        (VAT included if applicable)
                    </div>

                    <button type="submit" class="btn-place-order" name="action" value="placeOrder">Place order</button>

                </div>

                <div id="voucherSelectPopup" class="checkout-voucher-popup" onclick="closeVoucherSelectPopup(event)">
                    <div class="checkout-voucher-popup-card">
                        <div class="checkout-voucher-popup-head">
                            <h4>Choose your voucher</h4>
                            <button type="button" class="checkout-voucher-popup-close" onclick="closeVoucherSelectPopup()">&times;</button>
                        </div>

                        <div class="checkout-voucher-popup-body">
                            <div class="voucher-manual-row">
                                <input type="text" id="manualVoucherCode" placeholder="Enter voucher code">
                                <button type="button" onclick="applyManualVoucherCode()">Apply code</button>
                            </div>

                            <div class="checkout-voucher-list">
                                <c:choose>
                                    <c:when test="${empty checkoutVouchers}">
                                        <div class="checkout-voucher-empty">You have no available vouchers.</div>
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
                                                            Discount ${popupDiscountRaw}%
                                                        </c:when>
                                                        <c:otherwise>
                                                            Discount ${popupDiscountRaw} VND
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="voucher-item-bottom">Valid until: ${cv.effectiveTo}</div>
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
                                <h4>Add delivery address</h4>
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
                showPopup(true, 'Order placed successfully!<br>Order ID: #' + orderId, 'Success', 'Back to home',
                    '${pageContext.request.contextPath}/home');
                window.history.replaceState({}, document.title, window.location.pathname);
            }

            if (urlParams.get('error')) {
                const errorMsg = decodeURIComponent(urlParams.get('error'));
                showPopup(false, errorMsg, 'Error', 'Back to home',
                    '${pageContext.request.contextPath}/home');
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
                    document.querySelectorAll('input[name="' + radio.name + '"]').forEach(function (input) {
                        const parent = input.closest('.address-item');
                        if (parent) {
                            parent.classList.remove('selected');
                        }
                    });
                    this.classList.add('selected');
                }
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
            const openLinks = document.querySelectorAll('a.checkout-address-popup-trigger');
            const frame = document.getElementById('checkoutAddressFrame');

            openLinks.forEach(function (openLink) {
                openLink.addEventListener('click', function (event) {
                    event.preventDefault();
                    openCheckoutAddressPopup(this.href);
                });
            });

            if (!frame) {
                return;
            }

            frame.addEventListener('load', function () {
                try {
                    const frameWindow = frame.contentWindow;
                    const doc = frame.contentDocument || frameWindow.document;
                    const frameUrl = new URL(frameWindow.location.href);
                    const action = frameUrl.searchParams.get('action');
                    const isAddressListPage = frameUrl.pathname.endsWith('/address')
                            && (!action || action === 'view' || action === 'list');
                    const isLegacyProfileAddressPage = frameUrl.pathname.endsWith('/profile')
                            && frameUrl.searchParams.get('action') === 'view'
                            && frameUrl.searchParams.get('tab') === 'address';

                    if (isAddressListPage || isLegacyProfileAddressPage) {
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

                    doc.querySelectorAll('.address-form-actions .btn-secondary').forEach(function (button) {
                        button.style.display = 'none';
                    });
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
    </script>
</body>
</html>

