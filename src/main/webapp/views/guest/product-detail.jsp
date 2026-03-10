<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${not empty product ? product.name : 'Product Detail'} - Averis Cosmetics</title>
        
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
        
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/feedback.css">
        
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <%@include file="/assets/header.jsp" %>
        
        <div class="container">
            <a href="javascript:history.back()" class="back-link" title="Back to previous page">← Back to product list</a>
            
            <c:if test="${not empty product}">
                <div class="product-detail">
                    <div class="product-images">
                        <c:choose>
                            <c:when test="${not empty product.images}">
                                <img id="mainImage" class="main-image" 
                                     src="<%=request.getContextPath()%>/assets/img/${product.mainImage}" 
                                     alt="${product.name}"
                                     onerror="this.src='<%=request.getContextPath()%>/assets/img/Logo.png';">
                                
                                <div class="thumbnail-images">
                                    <c:forEach items="${product.images}" var="img" varStatus="loop">
                                        <img class="thumbnail ${loop.index == 0 ? 'active' : ''}" 
                                             src="<%=request.getContextPath()%>/assets/img/${img.image}" 
                                             alt="${product.name}" 
                                             onclick="changeImage('${img.image}', this)"
                                             onerror="this.style.display='none';">
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <img class="main-image" src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="No image available">
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div class="product-info">
                        <h1>${product.name}</h1>
                        <div class="brand-name">${product.brand.name}</div>
                        <div class="category">Category: ${product.category.name}</div>
                        
                        <div class="description">
                            <c:choose>
                                <c:when test="${not empty product.description}">
                                    ${product.description}
                                </c:when>
                                <c:otherwise>
                                    High quality product from brand ${product.brand.name}
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="price-section">
                            <div class="price-label">Price:</div>
                            <c:choose>
                                <c:when test="${not empty product.variants}">
                                    <div class="current-price">
                                        <fmt:formatNumber value="${product.variants[0].price}" pattern="#,##0"/> ₫
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="price">Contact</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <c:if test="${not empty product.variants}">
                            <div class="variants-section">
                                <div class="variants-label">Version:</div>
                                <div class="variants-container">
                                    <c:forEach items="${product.variants}" var="variant" varStatus="loop">
                                        <div class="variant-item ${loop.index == 0 ? 'active' : ''}" 
                                             data-variant-id="${variant.variantId}" 
                                             data-price="${variant.price}">
                                            <span class="variant-name">
                                                ${not empty variant.variantName ? variant.variantName : 'Variant '.concat(loop.index + 1)}
                                            </span>
                                            <span class="variant-price"><fmt:formatNumber value="${variant.price}" pattern="#,##0"/> ₫</span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                        
                        <div class="actions">
                            <button class="btn btn-primary" onclick="addToCart(${product.productId})">
                                Add to cart
                            </button>
                            <a href="<%=request.getContextPath()%>/contact" class="btn btn-secondary">
                                Contact for consultation
                            </a>
                        </div>
                    </div> </div> <div class="reviews-container">
                    <h2>Customer reviews</h2>
                    
                    <c:choose>
                        <c:when test="${empty reviews}">
                            <div style="text-align: center; padding: 40px; color: #94a3b8; background: #fdfdfd; border-radius: 12px; border: 1px dashed #e2e8f0;">
                                <i class="far fa-comments" style="font-size: 3rem; margin-bottom: 10px; display: block; opacity: 0.5;"></i>
                                This product has no reviews yet. Be the first buyer to leave your feedback!
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="reviews-list">
                                <c:forEach var="r" items="${reviews}">
                                    <div class="review-item">
                                        <div class="review-meta">
                                            <span class="user-name">${r.customerName}</span>
                                            <span class="review-date">
                                                <i class="far fa-calendar-alt"></i> 
                                                <fmt:parseDate value="${r.reviewedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" type="both" />
                                                <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy" />
                                            </span>
                                        </div>
                                        
                                        <div class="stars-orange">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="fas fa-star" style="${i <= r.rating ? '' : 'color: #e2e8f0;'}"></i>
                                            </c:forEach>
                                        </div>
                                        
                                        <div class="comment-text">
                                            ${not empty r.reviewComment ? r.reviewComment : '<span style="color:#cbd5e1; font-style:italic;">(Người dùng không để lại bình luận)</span>'}
                                        </div>

                                        <c:if test="${not empty r.responseContent}">
                                            <div class="staff-reply-box">
                                                <div class="staff-header">
                                                    <i class="fas fa-reply-all"></i> Feedback from Averis Cosmetics (${r.managerName})
                                                </div>
                                                <div class="reply-content">
                                                    ${r.responseContent}
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div> </c:if>
            
            <c:if test="${empty product}">
                <div style="text-align: center; padding: 100px 50px;">
                    <h2>Product does not exist</h2>
                    <p>Sorry, the product you are looking for was not found.</p>
                    <a href="<%=request.getContextPath()%>/products" class="btn btn-primary" style="margin-top: 20px;">
                        Back to store
                    </a>
                </div>
            </c:if>
        </div> <%@include file="/assets/footer.jsp" %>
        
        <script>
            // Đổi ảnh chính khi click thumbnail
            function changeImage(imagePath, element) {
                document.getElementById('mainImage').src = '<%=request.getContextPath()%>/assets/img/' + imagePath;
                const thumbnails = document.querySelectorAll('.thumbnail');
                thumbnails.forEach(thumb => thumb.classList.remove('active'));
                element.classList.add('active');
            }
            
            // Xử lý chọn variant
            document.addEventListener('DOMContentLoaded', function() {
                const variantItems = document.querySelectorAll('.variant-item');
                variantItems.forEach(item => {
                    item.addEventListener('click', function() {
                        variantItems.forEach(v => v.classList.remove('active'));
                        this.classList.add('active');
                        
                        const price = this.getAttribute('data-price');
                        document.querySelector('.price-section .current-price').textContent = 
                            Number(price).toLocaleString('vi-VN') + ' ₫';
                    });
                });
            });
            
            // Thêm vào giỏ hàng
            function addToCart(productId) {
                const selectedVariant = document.querySelector('.variant-item.active');
                if (!selectedVariant) {
                    alert('Please select a product version before adding to cart!');
                    return;
                }
                const variantId = selectedVariant.getAttribute('data-variant-id');
                const quantity = 1; 

                const url = '${pageContext.request.contextPath}/cart';
                const params = new URLSearchParams();
                params.append('variantId', variantId);
                params.append('quantity', quantity);
                params.append('ajax', 'true');
                params.append('action', 'add');

                fetch(url, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                    body: params
                })
                .then(response => {
                    if (response.status === 401) {
                        return response.text().then(loginUrl => { window.location.href = loginUrl; });
                    }
                    return response.text().then(data => {
                        const cartCountEl = document.getElementById('cartCount');
                        if (cartCountEl) cartCountEl.innerText = data;
                        alert('Added to cart successfully!');
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                });
            }
        </script>
    </body>
</html>