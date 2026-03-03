<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán | Averis Cosmetics</title>
    
    <style>
        :root {
            --bg: #fff;
            --surface: #faf8f5;
            --border: #e9e2d8;
            --text: #1f2937;
            --muted: #6b7280;
            --accent: #b45309;
            --accent-hover: #92400e;
            --danger: #dc2626;
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

        .checkout-container {
            width: 92%;
            max-width: 1200px;
            margin: 40px auto;
            flex: 1;
            display: flex;
            gap: 30px;
            align-items: flex-start;
        }

        /* --- CỘT TRÁI: THÔNG TIN --- */
        .checkout-main {
            flex: 1;
        }

        .checkout-section {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 24px;
            margin-bottom: 24px;
        }

        .section-title {
            font-size: 18px;
            font-weight: 700;
            color: var(--text);
            margin: 0 0 20px 0;
            padding-bottom: 12px;
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section-title .number {
            background: var(--accent);
            color: white;
            width: 28px;
            height: 28px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
        }

        /* Form elements */
        .form-row {
            display: flex;
            gap: 16px;
            margin-bottom: 16px;
        }

        .form-group {
            flex: 1;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            font-weight: 600;
            color: var(--text);
            margin-bottom: 6px;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid var(--border);
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: var(--accent);
        }

        /* Saved addresses */
        .address-list {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .address-item {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 16px;
            border: 1px solid var(--border);
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.2s;
        }

        .address-item:hover {
            border-color: var(--accent);
            background: var(--surface);
        }

        .address-item.selected {
            border-color: var(--accent);
            background: #fff7ed;
        }

        .address-item input[type="radio"] {
            margin-top: 4px;
        }

        .address-details {
            flex: 1;
        }

        .address-name {
            font-weight: 600;
            color: var(--text);
            margin-bottom: 4px;
        }

        .address-text {
            font-size: 14px;
            color: var(--muted);
            line-height: 1.5;
        }

        .address-default {
            display: inline-block;
            font-size: 12px;
            background: var(--accent);
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            margin-top: 8px;
        }

        /* Product list in checkout */
        .product-item {
            display: flex;
            gap: 16px;
            padding: 16px 0;
            border-bottom: 1px solid var(--border);
        }

        .product-item:last-child {
            border-bottom: none;
        }

        .product-image {
            width: 80px;
            height: 80px;
            background: var(--surface);
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            flex-shrink: 0;
        }

        .product-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .product-info {
            flex: 1;
        }

        .product-name {
            font-weight: 600;
            color: var(--text);
            margin-bottom: 4px;
        }

        .product-variant {
            font-size: 13px;
            color: var(--muted);
            margin-bottom: 8px;
        }

        .product-qty {
            font-size: 13px;
            color: var(--muted);
        }

        .product-price {
            font-weight: 700;
            color: var(--text);
            text-align: right;
        }

        /* --- CỘT PHẢI: TỔNG KẾT --- */
        .checkout-sidebar {
            width: 380px;
            position: sticky;
            top: 20px;
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 24px;
        }

        .summary-title {
            font-size: 18px;
            font-weight: 700;
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 1px solid var(--border);
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            font-size: 14px;
            color: var(--text);
        }

        .summary-row.discount {
            color: #16a34a;
        }

        .summary-total {
            border-top: 1px solid var(--border);
            padding-top: 16px;
            margin-top: 16px;
            font-weight: 700;
            font-size: 20px;
            color: var(--accent);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .btn-place-order {
            display: block;
            width: 100%;
            background: var(--accent);
            color: white;
            text-align: center;
            padding: 16px 0;
            border-radius: 4px;
            text-decoration: none;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-top: 24px;
            transition: background 0.2s ease;
            border: none;
            cursor: pointer;
            font-size: 15px;
        }

        .btn-place-order:hover {
            background: var(--accent-hover);
        }

        .btn-back {
            display: block;
            text-align: center;
            margin-top: 16px;
            font-size: 14px;
            color: var(--muted);
            text-decoration: none;
        }
        
        .btn-back:hover {
            color: var(--accent);
            text-decoration: underline;
        }

        .payment-method-placeholder {
            padding: 20px;
            background: var(--surface);
            border-radius: 6px;
            text-align: center;
            color: var(--muted);
            font-style: italic;
        }

        /* Responsive */
        @media (max-width: 900px) {
            .checkout-container {
                flex-direction: column;
            }
            .checkout-sidebar {
                width: 100%;
                box-sizing: border-box;
                position: static;
            }
            .form-row {
                flex-direction: column;
                gap: 12px;
            }
        }
        
        .header-wrapper {
            width: 100%;
            flex-shrink: 0;
        }
    </style>
</head>
<body>

    <div class="header-wrapper">
        <jsp:include page="/assets/header.jsp" />
    </div>

    <div class="checkout-container">
        <form method="POST" action="${pageContext.request.contextPath}/checkout">
        
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
            <div class="summary-row">
                <span>Giảm giá:</span>
                <span class="discount">0₫</span>
            </div>
            
            <div class="summary-total">
                <span>Tổng cộng:</span>
                <span><fmt:formatNumber value="${total}" type="currency" currencySymbol="₫"/></span>
            </div>
            
            <div style="text-align:right; font-size:12px; color:var(--muted); margin-top:5px;">
                (Đã bao gồm VAT nếu có)
            </div>

            <button type="submit" class="btn-place-order">
                Đặt hàng
            </button>
            
            <a href="${pageContext.request.contextPath}/cart" class="btn-back">
                &larr; Quay về giỏ hàng
            </a>
        </div>
        </form>

    </div>

    <jsp:include page="/assets/footer.jsp" />

    <script>
        // Auto-select address when clicking on address item
        document.querySelectorAll('.address-item').forEach(item => {
            item.addEventListener('click', function(e) {
                if (e.target.type !== 'radio') {
                    const radio = this.querySelector('input[type="radio"]');
                    radio.checked = true;
                }
                // Update visual selection
                document.querySelectorAll('.address-item').forEach(i => i.classList.remove('selected'));
                this.classList.add('selected');
            });
        });

        // Form submission handler
        document.querySelector('form').addEventListener('submit', function(e) {
            // Check if address is selected
            const addressSelected = document.querySelector('input[name="addressId"]:checked');
            if (!addressSelected) {
                e.preventDefault();
                alert('Vui lòng chọn địa chỉ giao hàng!');
                return false;
            }
            
            // Check if payment method is selected
            const paymentSelected = document.querySelector('input[name="paymentMethod"]:checked');
            if (!paymentSelected) {
                e.preventDefault();
                alert('Vui lòng chọn phương thức thanh toán!');
                return false;
            }
            
            console.log('Form submitting...');
            console.log('addressId:', addressSelected.value);
            console.log('paymentMethod:', paymentSelected.value);
            return true;
        });
    </script>

</body>
</html>
