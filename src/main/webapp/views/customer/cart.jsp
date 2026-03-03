<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng | Averis Cosmetics</title>
    
    <style>
        /* Tận dụng lại biến màu từ Header để đồng bộ */
        :root {
            --bg: #fff;
            --surface: #faf8f5;
            --border: #e9e2d8;
            --text: #1f2937;
            --muted: #6b7280;
            --accent: #b45309;         /* Màu cam đất chủ đạo */
            --accent-hover: #92400e;
            --danger: #dc2626;         /* Màu đỏ cho nút xóa */
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

        /* Container chính giới hạn chiều rộng */
        .cart-container {
            width: 92%;
            max-width: 1200px;
            margin: 40px auto;
            flex: 1; /* Đẩy footer xuống đáy nếu nội dung ngắn */
            display: flex;
            gap: 30px;
            align-items: flex-start; /* Quan trọng để cột phải sticky hoạt động */
            position: relative;
        }

        /* --- CỘT TRÁI: DANH SÁCH SẢN PHẨM (Chiếm 65-70%) --- */
        .cart-main {
            flex: 1;
        }

        .cart-title-block {
            border-bottom: 2px solid var(--surface);
            padding-bottom: 15px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .cart-heading {
            font-size: 20px;
            font-weight: 700;
            text-transform: uppercase;
            color: var(--text);
            letter-spacing: 0.5px;
            margin: 0;
        }

        .cart-count {
            font-size: 14px;
            color: var(--muted);
        }

        /* Item Row */
        .cart-item {
            display: flex;
            padding: 24px 0;
            border-bottom: 1px solid var(--border);
            gap: 20px;
        }

        .cart-item:last-child {
            border-bottom: none;
        }

        /* Ảnh sản phẩm placeholder */
        .item-image {
            width: 100px;
            height: 100px;
            background-color: var(--surface);
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 10px;
            color: var(--muted);
            border: 1px solid var(--border);
            overflow: hidden; /* Để ảnh bo góc theo khung */
        }

        .item-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .item-name {
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            color: var(--text);
            line-height: 1.4;
            margin-bottom: 4px;
            display: block;
        }
        
        .item-name:hover {
            color: var(--accent);
        }

        .item-price {
            font-weight: 700;
            color: var(--text);
            font-size: 15px;
        }

        .item-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 10px;
        }

        /* Bộ nút tăng giảm số lượng */
        .qty-control {
            display: flex;
            border: 1px solid var(--border);
            border-radius: 4px;
            height: 32px;
            width: fit-content;
        }

        .qty-btn {
            width: 32px;
            height: 32px;
            background: #fff;
            border: none;
            cursor: pointer;
            font-size: 16px;
            color: var(--muted);
            transition: 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .qty-btn:hover {
            background: var(--surface);
            color: var(--accent);
        }

        .qty-input {
            width: 40px;
            border: none;
            border-left: 1px solid var(--border);
            border-right: 1px solid var(--border);
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: var(--text);
            outline: none;
            /* Xóa mũi tên input number */
            -moz-appearance: textfield;
        }
        .qty-input::-webkit-outer-spin-button,
        .qty-input::-webkit-inner-spin-button {
            -webkit-appearance: none;
            margin: 0;
        }

        /* Nút xóa */
        .btn-delete {
            font-size: 13px;
            color: var(--muted);
            background: none;
            border: none;
            cursor: pointer;
            text-decoration: underline;
            padding: 0;
        }
        .btn-delete:hover {
            color: var(--danger);
        }

        /* --- CỘT PHẢI: TỔNG KẾT (STICKY) --- */
        .cart-sidebar {
            width: 320px;
            position: sticky;
            top: 20px; /* Dính lại khi cuộn trang */
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.05);
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 16px;
            font-size: 14px;
            color: var(--text);
        }

        .summary-total {
            border-top: 1px solid var(--border);
            padding-top: 16px;
            margin-top: 16px;
            font-weight: 700;
            font-size: 18px;
            color: var(--accent);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .btn-checkout {
            display: block;
            width: 100%;
            background: var(--accent);
            color: white;
            text-align: center;
            padding: 14px 0;
            border-radius: 4px;
            text-decoration: none;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-top: 24px;
            transition: background 0.2s ease;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }

        .btn-checkout:hover {
            background: var(--accent-hover);
        }

        .continue-link {
            display: block;
            text-align: center;
            margin-top: 16px;
            font-size: 13px;
            color: var(--muted);
            text-decoration: none;
        }
        .continue-link:hover {
            color: var(--accent);
            text-decoration: underline;
        }

        /* Trạng thái giỏ hàng trống */
        .empty-cart {
            text-align: center;
            padding: 60px 0;
        }
        .empty-icon {
            font-size: 48px;
            color: var(--border);
            margin-bottom: 20px;
        }

        /* Responsive Mobile */
        @media (max-width: 900px) {
            .cart-container {
                flex-direction: column;
            }
            .cart-sidebar {
                width: 100%; /* Sidebar tràn màn hình trên mobile */
                box-sizing: border-box;
                position: static; /* Hết sticky */
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

    <div class="cart-container">
        
        <c:if test="${empty cart}">
            <div class="cart-main empty-cart">
                <div class="empty-icon">🛒</div> 
                <h3>Giỏ hàng của bạn đang trống</h3>
                <p style="color: var(--muted); margin-bottom: 24px;">Hãy chọn thêm sản phẩm để mua sắm nhé.</p>
                <a href="${pageContext.request.contextPath}/products" class="btn-checkout" style="width: 200px; margin: 0 auto; display: inline-block;">
                    Tiếp tục mua hàng
                </a>
            </div>
        </c:if>

        <c:if test="${not empty cart}">
            
            <div class="cart-main">
                <div class="cart-title-block">
                    <h1 class="cart-heading">Giỏ hàng</h1>
                    <span class="cart-count">(${cart.size()} sản phẩm)</span>
                </div>

                <c:forEach items="${cart}" var="entry">
                    <div class="cart-item">
                        <div class="item-image">
                            <c:choose>
                                <c:when test="${not empty entry.value.variant.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/assets/img/${entry.value.variant.imageUrl}" 
                                         alt="Product" 
                                         style="width:100%; height:100%; object-fit:cover;">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="No Image" 
                                         style="width:60%; height:60%; object-fit:contain; opacity:0.5;">
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="item-details">
                            <div>
                                <a href="#" class="item-name">
                                    ${entry.value.variant.productName} 
                                    <span style="color:var(--muted); font-weight:400;">(${entry.value.variant.variantName})</span>
                                </a>
                                <div class="item-price">
                                    <fmt:formatNumber value="${entry.value.variant.price}" type="currency" currencySymbol="₫"/>
                                </div>
                            </div>

                            <div class="item-actions">
                                <form action="cart" method="post" id="form-${entry.key}">
                                    <input type="hidden" name="action" value="update"> <input type="hidden" name="variantId" value="${entry.key}">
                                    
                                    <div class="qty-control">
                                        <button type="button" class="qty-btn" onclick="updateQty('${entry.key}', -1)">-</button>
                                        
                                        <input type="number" name="quantity" id="qty-${entry.key}" 
                                               value="${entry.value.quantity}" class="qty-input" readonly>
                                        
                                        <button type="button" class="qty-btn" onclick="updateQty('${entry.key}', 1)">+</button>
                                    </div>
                                </form>

                                <form action="cart" method="post" style="margin:0;">
                                    <input type="hidden" name="action" value="update"> <input type="hidden" name="variantId" value="${entry.key}">
                                    <input type="hidden" name="quantity" value="0">
                                    <button class="btn-delete" onclick="return confirm('Bạn chắc chắn muốn xóa sản phẩm này?')">
                                        Xóa
                                    </button>
                                </form>
                            </div>
                        </div>
                        
                        <div style="font-weight:700; font-size:14px; align-self:flex-end; display:none;">
                           <fmt:formatNumber value="${entry.value.subtotal}" type="currency" currencySymbol="₫"/>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="cart-sidebar">
                <div class="summary-row">
                    <span>Tạm tính:</span>
                    <span style="font-weight:600"><fmt:formatNumber value="${total}" type="currency" currencySymbol="₫"/></span>
                </div>
                <div class="summary-row">
                    <span>Giảm giá:</span>
                    <span>0₫</span>
                </div>
                
                <div class="summary-total">
                    <span>Tổng cộng:</span>
                    <span><fmt:formatNumber value="${total}" type="currency" currencySymbol="₫"/></span>
                </div>
                
                <div style="text-align:right; font-size:12px; color:var(--muted); margin-top:5px;">
                    (Đã bao gồm VAT nếu có)
                </div>

                <a href="checkout" class="btn-checkout">Tiến hành đặt hàng</a>
                
                <a href="${pageContext.request.contextPath}/products" class="continue-link">
                    &larr; Tiếp tục mua sắm
                </a>
            </div>
            
        </c:if>
    </div>

    <jsp:include page="/assets/footer.jsp" />

    <script>
        function updateQty(variantId, delta) {
            const input = document.getElementById('qty-' + variantId);
            let currentVal = parseInt(input.value);
            let newVal = currentVal + delta;

            if (newVal < 1) return;

            input.value = newVal;
            
            document.getElementById('form-' + variantId).submit();
        }
    </script>

</body>
</html>