<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${not empty product ? product.name : 'Product Detail'} - Averis Cosmetics</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    </head>
    <body class="product-detail-page">
        <jsp:include page="/assets/header.jsp" />

        <main class="product-detail-shell">
            <c:if test="${not empty product}">
                <section class="product-detail-hero">
                    <a href="${pageContext.request.contextPath}/products" class="product-back-link" title="Back to products">
                        <i class="fa-solid fa-arrow-left-long"></i>
                        Back to products
                    </a>

                    <div class="product-detail-card">
                        <section class="product-gallery-panel">
                            <div class="product-gallery-frame">
                                <c:choose>
                                    <c:when test="${not empty product.images}">
                                        <img id="mainImage" class="product-main-image"
                                             src="${pageContext.request.contextPath}/assets/img/${product.mainImage}"
                                             alt="${product.name}"
                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                    </c:when>
                                    <c:otherwise>
                                        <img class="product-main-image" src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="No image available">
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <c:if test="${not empty product.images}">
                                <div class="product-thumbnail-track">
                                    <c:forEach items="${product.images}" var="img" varStatus="loop">
                                        <img class="product-thumbnail ${loop.index == 0 ? 'active' : ''}"
                                             src="${pageContext.request.contextPath}/assets/img/${img.image}"
                                             alt="${product.name}"
                                             onclick="changeImage('${img.image}', this)"
                                             onerror="this.style.display='none';">
                                    </c:forEach>
                                </div>
                            </c:if>
                        </section>

                        <section class="product-summary-panel">
                            <div class="product-summary-meta">
                                <span class="product-summary-pill">${product.category.name}</span>
                                <span class="product-summary-pill subtle">${product.brand.name}</span>
                            </div>

                            <h1 class="product-summary-title">${product.name}</h1>

                            <p class="product-summary-copy">
                                <c:choose>
                                    <c:when test="${not empty product.description}">
                                        ${product.description}
                                    </c:when>
                                    <c:otherwise>
                                        High quality product from ${product.brand.name}, selected to bring a smoother and more reliable beauty routine.
                                    </c:otherwise>
                                </c:choose>
                            </p>

                            <div class="product-price-card">
                                <div class="product-price-label">Current price</div>
                                <c:choose>
                                    <c:when test="${not empty product.variants}">
                                        <div class="product-current-price" id="productCurrentPrice">
                                            <fmt:formatNumber value="${product.variants[0].price}" pattern="#,##0"/> VND
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-current-price">Contact</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <c:if test="${not empty product.variants}">
                                <div class="product-variant-section">
                                    <div class="product-variant-head">
                                        <div>
                                            <div class="product-variant-label">Choose variant</div>
                                            <div class="product-variant-note">Select a size to update the displayed price before adding to cart.</div>
                                        </div>
                                    </div>
                                    <div class="product-variant-grid">
                                        <c:forEach items="${product.variants}" var="variant" varStatus="loop">
                                            <button type="button"
                                                    class="product-variant-card ${loop.index == 0 ? 'active' : ''}"
                                                    data-variant-id="${variant.variantId}"
                                                    data-price="${variant.price}">
                                                <span class="product-variant-name">
                                                    ${not empty variant.variantName ? variant.variantName : 'Variant '.concat(loop.index + 1)}
                                                </span>
                                                <span class="product-variant-price"><fmt:formatNumber value="${variant.price}" pattern="#,##0"/> VND</span>
                                            </button>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <div class="product-detail-actions">
                                <button class="product-action-btn primary" onclick="addToCart(${product.productId}, event)">
                                    Add to cart
                                </button>
                                <a href="${pageContext.request.contextPath}/contact" class="product-action-btn secondary">
                                    Contact for consultation
                                </a>
                            </div>

                            <div class="product-detail-notes">
                                <div class="product-detail-note">
                                    <strong>Authentic products</strong>
                                    Carefully selected items with clear origin and quality assurance.
                                </div>
                                <div class="product-detail-note">
                                    <strong>Suitable choices</strong>
                                    Helpful variants and curated selections for different routines.
                                </div>
                                <div class="product-detail-note">
                                    <strong>Fast support</strong>
                                    Need help before ordering? Our team is ready to assist.
                                </div>
                            </div>
                        </section>
                    </div>
                </section>

                <section class="product-review-shell">
                    <div class="reviews-container">
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
                                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy" />
                                                </span>
                                            </div>

                                            <div class="stars-orange">
                                                <c:forEach begin="1" end="5" var="i">
                                                    <i class="fas fa-star ${i <= r.rating ? '' : 'review-star--empty'}"></i>
                                                </c:forEach>
                                            </div>

                                            <div class="comment-text">
                                                ${not empty r.reviewComment ? r.reviewComment : '<span class="review-comment-empty">(No comment left by the customer)</span>'}
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
                </section>
            </c:if>

            <c:if test="${empty product}">
                <div class="product-empty-state">
                    <h2>Product does not exist</h2>
                    <p>Sorry, the product you are looking for was not found.</p>
                    <a href="${pageContext.request.contextPath}/products" class="product-action-btn primary">
                        Back to store
                    </a>
                </div>
            </c:if>
        </main>

        <jsp:include page="/assets/footer.jsp" />

        <script>
            function formatCurrencyVN(value) {
                return new Intl.NumberFormat('vi-VN').format(Number(value || 0)) + ' VND';
            }

            function changeImage(imagePath, element) {
                document.getElementById('mainImage').src = '${pageContext.request.contextPath}/assets/img/' + imagePath;
                const thumbnails = document.querySelectorAll('.product-thumbnail');
                thumbnails.forEach(thumb => thumb.classList.remove('active'));
                element.classList.add('active');
            }

            document.addEventListener('DOMContentLoaded', function() {
                const variantItems = document.querySelectorAll('.product-variant-card');
                const currentPrice = document.getElementById('productCurrentPrice');

                variantItems.forEach(item => {
                    item.addEventListener('click', function() {
                        variantItems.forEach(v => v.classList.remove('active'));
                        this.classList.add('active');

                        if (currentPrice) {
                            const price = this.getAttribute('data-price');
                            currentPrice.textContent = formatCurrencyVN(price);
                        }
                    });
                });
            });

            function addToCart(productId, event) {
                const selectedVariant = document.querySelector('.product-variant-card.active');
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

                const btn = event ? event.currentTarget : document.querySelector('.product-action-btn.primary');

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
                        const cartCountEl = document.getElementById('cartCount');
                        if (cartCountEl) {
                            cartCountEl.innerText = data;
                        }

                        if (btn) {
                            const originalText = btn.innerText;
                            btn.innerText = 'Added';
                            btn.classList.add('is-success');

                            setTimeout(() => {
                                btn.innerText = originalText;
                                btn.classList.remove('is-success');
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


