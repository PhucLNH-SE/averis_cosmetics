<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng | Averis Cosmetics</title>
    
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/assets/css/manageproductstyle.css">
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
                <a href="<%=request.getContextPath()%>/products" class="btn-checkout" style="width: 200px; margin: 0 auto; display: inline-block;">
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
                                    <c:set var="cartImgFolder" value="${entry.value.variant.imageUrl.contains('-') ? 'products/' : ''}" />
                                    <img src="<%=request.getContextPath()%>/assets/img/${cartImgFolder}${entry.value.variant.imageUrl}" 
                                         alt="Product" 
                                         style="width:100%; height:100%; object-fit:contain; padding: 5px;"
                                         onerror="this.src='<%=request.getContextPath()%>/assets/img/default-product.jpg';">
                                </c:when>
                                <c:otherwise>
                                    <img src="<%=request.getContextPath()%>/assets/img/default-product.jpg" alt="No Image" 
                                         style="width:100%; height:100%; object-fit:contain; padding: 5px;">
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
                                    <fmt:formatNumber value="${entry.value.variant.price}" type="currency" currencySymbol="$"/>
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
                           <fmt:formatNumber value="${entry.value.subtotal}" type="currency" currencySymbol="$"/>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="cart-sidebar">
                <div class="summary-row">
                    <span>Tạm tính:</span>
                    <span style="font-weight:600"><fmt:formatNumber value="${total}" type="currency" currencySymbol="$"/></span>
                </div>
                <div class="summary-row">
                    <span>Giảm giá:</span>
                    <span>$0.00</span>
                </div>
                
                <div class="summary-total">
                    <span>Tổng cộng:</span>
                    <span><fmt:formatNumber value="${total}" type="currency" currencySymbol="$"/></span>
                </div>
                
                <div style="text-align:right; font-size:12px; color:var(--muted); margin-top:5px;">
                    (Đã bao gồm VAT nếu có)
                </div>

                <a href="checkout" class="btn-checkout">Tiến hành đặt hàng</a>
                
                <a href="<%=request.getContextPath()%>/products" class="continue-link">
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