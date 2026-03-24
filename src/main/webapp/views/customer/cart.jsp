<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cart | Averis Cosmetics</title>
    
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="cart-page">

    <div class="header-wrapper">
        <jsp:include page="/assets/header.jsp" />
    </div>

    <div class="cart-container">
        <a href="<%=request.getContextPath()%>/home" class="product-back-link" title="Back to home">
            <i class="fa-solid fa-arrow-left-long"></i>
            Back to home
        </a>
        
        <c:if test="${empty cart}">
            <div class="cart-main empty-cart">
                <div class="empty-icon"><i class="fa-solid fa-bag-shopping"></i></div>
                <h3>Your cart is empty</h3>
                <p class="empty-cart-copy">Browse products and add items to get started.</p>
                <a href="<%=request.getContextPath()%>/products" class="btn-checkout empty-cart-btn">
                    Continue Shopping
                </a>
            </div>
        </c:if>

        <c:if test="${not empty cart}">
            
            <div class="cart-main">
                <div class="cart-title-block">
                    <div>
                        <span class="cart-pill">Shopping Bag</span>
                        <h1 class="cart-heading">Your cart</h1>
                    </div>
                    <span class="cart-count">${cart.size()} items</span>
                </div>

                <c:forEach items="${cart}" var="entry">
                    <div class="cart-item">
                        <div class="item-image">
                            <c:choose>
                                <c:when test="${not empty entry.value.variant.imageUrl}">
                                    <img class="cart-product-image"
                                         src="<%=request.getContextPath()%>/assets/img/${entry.value.variant.imageUrl}"
                                         alt="Product"
                                         onerror="this.src='<%=request.getContextPath()%>/assets/img/Logo.png';">
                                </c:when>
                                <c:otherwise>
                                    <img class="cart-product-image"
                                         src="<%=request.getContextPath()%>/assets/img/Logo.png"
                                         alt="No Image">
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="item-details">
                            <div>
                                <a href="#" class="item-name">
                                    ${entry.value.variant.productName} 
                                </a>
                                
                                <form action="cart" method="post" id="form-variant-${entry.key}" class="cart-variant-form">
                                    <input type="hidden" name="action" value="changeVariant">
                                    <input type="hidden" name="variantId" value="${entry.key}">
                                    <select name="newVariantId" onchange="document.getElementById('form-variant-${entry.key}').submit()">
                                        <c:forEach items="${availableVariants[entry.value.variant.productId]}" var="v">
                                            <option value="${v.variantId}" ${v.variantId == entry.key ? 'selected' : ''}>
                                                ${v.variantName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </form>

                                <div class="item-price">
                                    <fmt:formatNumber value="${entry.value.variant.price}" pattern="#,##0"/> VND
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
                                    
                                    <c:if test="${entry.value.quantity >= entry.value.variant.stock}">
                                        <span class="cart-stock-note">(Max in stock)</span>
                                    </c:if>
                                </form>

                                <form action="cart" method="post" class="cart-remove-form">
                                    <input type="hidden" name="action" value="update"> <input type="hidden" name="variantId" value="${entry.key}">
                                    <input type="hidden" name="quantity" value="0">
                                    
                                    <button type="submit" class="btn-delete">
                                        Remove
                                    </button>
                                </form>
                            </div>
                        </div>
                        
                        <div class="cart-line-total">
                            <fmt:formatNumber value="${entry.value.subtotal}" pattern="#,##0"/> VND
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="cart-sidebar">
                <div class="summary-row">
                    <span>Subtotal:</span>
                    <span class="summary-value-strong"><fmt:formatNumber value="${total}" pattern="#,##0"/> VND</span>
                </div>
                <div class="summary-row">
                    <span>Discount:</span>
                    <span>0 VND</span>
                </div>
                
                <div class="summary-total">
                    <span>Total:</span>
                    <span><fmt:formatNumber value="${total}" pattern="#,##0"/> VND</span>
                </div>
                
                <div class="cart-vat-note">
                    (VAT included if applicable)
                </div>

                <a href="checkout" class="btn-checkout">Proceed to Checkout</a>
                
                <a href="<%=request.getContextPath()%>/products" class="continue-link">
                    &larr; Continue Shopping
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


