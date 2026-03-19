<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en_US"/>
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
            <a href="javascript:history.back()" class="back-link" title="Back to previous page">&larr; Back to product list</a>

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
                                        <fmt:formatNumber value="${product.variants[0].price}" pattern="#,##0"/> VND
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="price">Contact</div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <c:if test="${not empty product.variants}">
                            <div class="variants-section">
                                <div class="variants-label">Variant:</div>
                                <div class="variants-container">
                                    <c:forEach items="${product.variants}" var="variant" varStatus="loop">
                                        <div class="variant-item ${loop.index == 0 ? 'active' : ''}"
                                             data-variant-id="${variant.variantId}"
                                             data-price="${variant.price}">
                                            <span class="variant-name">
                                                ${not empty variant.variantName ? variant.variantName : 'Variant '.concat(loop.index + 1)}
                                            </span>
                                            <span class="variant-price"><fmt:formatNumber value="${variant.price}" pattern="#,##0"/> VND</span>
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

                    <c:set var="totalStars" value="0" />
                    <c:set var="reviewCount" value="0" />

                    <c:if test="${not empty reviews}">
                        <c:forEach var="r" items="${reviews}">
                            <c:set var="totalStars" value="${totalStars + r.rating}" />
                            <c:set var="reviewCount" value="${reviewCount + 1}" />
                        </c:forEach>
                    </c:if>

                    <div class="reviews-header-wrap">
                        <h2>Customer reviews</h2>

                        <c:if test="${reviewCount > 0}">
                            <c:set var="avgStar" value="${totalStars / reviewCount}" />
                            <div class="avg-star-badge">
                                <i class="fas fa-star avg-star-icon"></i>
                                <span class="avg-star-value">
                                    <fmt:formatNumber value="${avgStar}" maxFractionDigits="1" minFractionDigits="1"/>
                                </span>
                                <span class="avg-star-max">/ 5</span>
                                <span class="avg-star-count">(${reviewCount} reviews)</span>
                            </div>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${empty reviews}">
                            <div class="empty-reviews">
                                <i class="far fa-comments empty-reviews-icon"></i>
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
                                                <fmt:formatDate value="${parsedDate}" pattern="MM/dd/yyyy" />
                                            </span>
                                        </div>

                                        <div class="stars-orange">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="fas fa-star" style="${i <= r.rating ? '' : 'color: #e2e8f0;'}"></i>
                                            </c:forEach>
                                        </div>

                                        <div class="comment-text">
                                            ${not empty r.reviewComment ? r.reviewComment : '<span style="color:#cbd5e1; font-style:italic;">(No comment left by the customer)</span>'}
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
                </div>
            </c:if>

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
            // Switch main image when clicking a thumbnail
            function changeImage(imagePath, element) {
                document.getElementById('mainImage').src = '<%=request.getContextPath()%>/assets/img/' + imagePath;
                const thumbnails = document.querySelectorAll('.thumbnail');
                thumbnails.forEach(thumb => thumb.classList.remove('active'));
                element.classList.add('active');
            }

            // Handle variant selection
            document.addEventListener('DOMContentLoaded', function() {
                const variantItems = document.querySelectorAll('.variant-item');
                variantItems.forEach(item => {
                    item.addEventListener('click', function() {
                        variantItems.forEach(v => v.classList.remove('active'));
                        this.classList.add('active');

                        const price = this.getAttribute('data-price');
                        document.querySelector('.price-section .current-price').textContent =
                            Number(price).toLocaleString('en-US') + ' VND';
                    });
                });
            });

            // Add to cart without a blocking alert
            function addToCart(productId) {
                const selectedVariant = document.querySelector('.variant-item.active');
                if (!selectedVariant) {
                    showPopup(false, 'Please select a product variant before adding to cart.', 'Missing selection');
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

                // Capture the button that was clicked
                const btn = event.currentTarget || document.querySelector('.actions .btn-primary');

                fetch(url, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                    body: params
                })
                .then(response => {
                    if (response.status === 401) {
                        return response.text().then(loginUrl => { window.location.href = loginUrl; });
                    }
                    if (!response.ok) {
                        return response.text().then(message => {
                            showPopup(false, message || 'Unable to add item to cart.', 'Error');
                        });
                    }
                    return response.text().then(data => {
                        // Update cart count in the header
                        const cartCountEl = document.getElementById('cartCount');
                        if (cartCountEl) cartCountEl.innerText = data;

                        // Smooth feedback: temporarily change button text
                        if (btn) {
                            const originalText = btn.innerText;
                            const originalBg = btn.style.backgroundColor;

                            btn.innerText = 'Added';
                            btn.style.backgroundColor = '#059669';

                            setTimeout(() => {
                                btn.innerText = originalText;
                                btn.style.backgroundColor = originalBg;
                            }, 1000);
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                });
            }
        </script>
    </body>
</html>
