<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Averis Cosmetics - Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    
</head>
<body>
<div class="home-layout">
    <jsp:include page="/assets/header.jsp" />

    <c:if test="${not empty homeVouchers}">
        <div class="home-voucher-modal" id="homeVoucherModal" aria-hidden="true">
            <div class="home-voucher-modal__backdrop" onclick="closeHomeVoucherModal(event)"></div>
            <div class="home-voucher-modal__panel">
                <button type="button" class="home-voucher-modal__close" aria-label="Close voucher popup" onclick="closeHomeVoucherModal()">
                    &times;
                </button>

                <div class="home-voucher-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="homeVoucherTitle">
                    <div class="home-voucher-modal__hero">
                        <div class="home-voucher-modal__visual">
                            <div class="home-voucher-modal__sticker">Hot deal</div>
                            <div class="home-voucher-modal__spark home-voucher-modal__spark--one"></div>
                            <div class="home-voucher-modal__spark home-voucher-modal__spark--two"></div>
                            <div class="home-voucher-modal__discount-badge">UP TO 50%</div>
                            <div class="home-voucher-modal__ticket">
                                <span>Voucher Drop</span>
                            </div>
                        </div>
                        <div class="home-voucher-modal__hero-copy">
                            <div class="home-voucher-modal__eyebrow">Averis exclusive offers</div>
                            <h2 id="homeVoucherTitle" class="home-voucher-modal__title">Pick your voucher</h2>
                        </div>
                    </div>

                    <c:if test="${param.success == 'claimed'}">
                        <div class="home-voucher-modal__alert home-voucher-modal__alert--success">
                            Voucher claimed successfully. It has been added to My Voucher.
                        </div>
                    </c:if>

                    <c:if test="${not empty param.error}">
                        <div class="home-voucher-modal__alert home-voucher-modal__alert--error">
                            <c:choose>
                                <c:when test="${param.error == 'emptyCode'}">Voucher code is missing.</c:when>
                                <c:when test="${param.error == 'codeNotFound'}">This voucher is not available right now.</c:when>
                                <c:when test="${param.error == 'outOfStock'}">This voucher has run out.</c:when>
                                <c:when test="${param.error == 'alreadyClaimed'}">You already saved this voucher.</c:when>
                                <c:when test="${param.error == 'voucherExpired'}">This voucher is no longer available.</c:when>
                                <c:otherwise>Unable to claim voucher right now.</c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>

                    <div class="home-voucher-modal__list">
                        <c:forEach items="${homeVouchers}" var="voucher" varStatus="loop">
                            <fmt:formatNumber value="${voucher.discountValue}" pattern="#,##0" var="discountDisplay"/>
                            <div class="home-voucher-card ${loop.index == 0 ? 'home-voucher-card--featured' : ''}">
                                <div class="home-voucher-card__main">
                                    <div class="home-voucher-card__tag">Voucher</div>
                                    <div class="home-voucher-card__discount">
                                        <c:choose>
                                            <c:when test="${voucher.discountType eq 'PERCENT'}">${discountDisplay}% OFF</c:when>
                                            <c:otherwise>${discountDisplay} VND OFF</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="home-voucher-card__code">${voucher.code}</div>
                                    <div class="home-voucher-card__desc">
                                        <c:choose>
                                            <c:when test="${voucher.discountType eq 'PERCENT'}">Save instantly on your next checkout.</c:when>
                                            <c:otherwise>Direct discount for qualifying orders.</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="home-voucher-card__aside">
                                    <div class="home-voucher-card__meta-line">
                                        <span class="home-voucher-card__meta-label">Left</span>
                                        <strong>${voucher.quantity - voucher.claimedQuantity}</strong>
                                    </div>
                                    <div class="home-voucher-card__meta-line">
                                        <span class="home-voucher-card__meta-label">Valid</span>
                                        <strong>
                                            <c:choose>
                                                <c:when test="${voucher.voucherType eq 'FIXED_END_DATE' && not empty voucher.fixedEndAt}">
                                                    until ${fn:substring(fn:replace(voucher.fixedEndAt, 'T', ' '), 0, 16)}
                                                </c:when>
                                                <c:when test="${voucher.voucherType eq 'RELATIVE_DAYS' && not empty voucher.relativeDays}">
                                                    ${voucher.relativeDays} days after claim
                                                </c:when>
                                                <c:otherwise>Limited time</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>

                                    <div class="home-voucher-card__actions">
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.customer}">
                                                <form action="${pageContext.request.contextPath}/voucher-free" method="post" class="home-voucher-card__form">
                                                    <input type="hidden" name="voucherCode" value="${voucher.code}">
                                                    <input type="hidden" name="source" value="home">
                                                    <button type="submit" class="home-voucher-card__btn">
                                                        Claim now
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/login"
                                                   class="home-voucher-card__btn">
                                                    Login to claim
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <main>
        <section class="home-hero">
            <div class="home-container hero-grid">
                <div>
                    <span class="hero-badge">New Season Glow</span>
                    <h1 class="hero-title">
                        Beautiful skin with a <span>simple</span> and <span>effective</span> daily routine.
                    </h1>
                    <p class="hero-copy">
                        Averis Cosmetics brings curated skincare and makeup collections crafted for
                        modern lifestyles, trusted quality, and everyday confidence.
                    </p>
                    <div class="hero-actions">
                        <a class="hero-btn primary" href="${pageContext.request.contextPath}/products">Shop now</a>
                        <button type="button" class="hero-btn voucher" onclick="openHomeVoucherModal()">GET FREE VOUCHER!!</button>
                    </div>
                </div>

                <div class="hero-media">
                    <div class="hero-main-image">
                        <img src="https://images.unsplash.com/photo-1739979054787-719a848cd684?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixlib=rb-4.1.0&q=80&w=1080" alt="Skincare hero">
                    </div>
                    <div class="hero-float one">
                        <img src="https://images.unsplash.com/photo-1763503836825-97f5450d155a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixlib=rb-4.1.0&q=80&w=600" alt="Hydrating cream">
                    </div>
                    <div class="hero-float two">
                        <img src="https://images.unsplash.com/photo-1643379850623-7eb6442cd262?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixlib=rb-4.1.0&q=80&w=600" alt="Beauty serum">
                    </div>
                    <div class="hero-orb"></div>
                    <div class="hero-decor"></div>
                </div>
            </div>
        </section>

        <section class="home-featured">
            <div class="home-container">
                <div class="section-header">
                    <span class="section-pill">Top Selling Products</span>
                    <h2 class="section-title">Bestsellers this month</h2>
                    <p class="section-desc">Handpicked from real orders so you can discover what customers love most.</p>
                </div>

                <c:choose>
                    <c:when test="${not empty topSellingProducts}">
                        <div class="featured-grid">
                            <c:forEach items="${topSellingProducts}" var="p" varStatus="loop">
                                <article class="featured-card">
                                    <div class="featured-badge">
                                        <c:choose>
                                            <c:when test="${loop.index == 0}">Best Seller</c:when>
                                            <c:when test="${loop.index == 1}">Hot Deal</c:when>
                                            <c:otherwise>New</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <a class="featured-image" href="${pageContext.request.contextPath}/products?id=${p.productId}">
                                        <c:choose>
                                            <c:when test="${not empty p.imageUrl}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(p.imageUrl, 'http')}">
                                                        <img src="${p.imageUrl}" alt="${p.productName}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/assets/img/${p.imageUrl}" alt="${p.productName}" onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="Product image">
                                            </c:otherwise>
                                        </c:choose>
                                    </a>
                                    <div class="featured-body">
                                        <h3 class="featured-name">
                                            <c:choose>
                                                <c:when test="${not empty p.productName}">${p.productName}</c:when>
                                                <c:otherwise>Product</c:otherwise>
                                            </c:choose>
                                        </h3>
                                        <div class="featured-meta">
                                            <div class="featured-price">
                                                <c:choose>
                                                    <c:when test="${not empty p.minPrice && p.minPrice gt 0}">
                                                        <c:choose>
                                                            <c:when test="${not empty p.maxPrice && p.maxPrice gt p.minPrice}">
                                                                <fmt:formatNumber value="${p.minPrice}" pattern="#,##0"/> -
                                                                <fmt:formatNumber value="${p.maxPrice}" pattern="#,##0"/> VND
                                                            </c:when>
                                                            <c:otherwise>
                                                                <fmt:formatNumber value="${p.minPrice}" pattern="#,##0"/> VND
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>Contact</c:otherwise>
                                                </c:choose>
                                            </div>
                                            <a class="featured-btn" href="${pageContext.request.contextPath}/products?id=${p.productId}">View</a>
                                        </div>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="featured-grid">
                            <article class="featured-card">
                                <div class="featured-badge">Best Seller</div>
                                <div class="featured-image">
                                    <img src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="Product">
                                </div>
                                <div class="featured-body">
                                    <h3 class="featured-name">Top Products Coming Soon</h3>
                                    <div class="featured-meta">
                                        <div class="featured-price">Contact</div>
                                        <a class="featured-btn" href="${pageContext.request.contextPath}/products">Browse</a>
                                    </div>
                                </div>
                            </article>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <section class="home-categories">
            <div class="home-container">
                <div class="section-header">
                    <span class="section-pill">Explore Categories</span>
                    <h2 class="section-title">Find your next favorite routine</h2>
                    <p class="section-desc">Pick a category tailored to your skin goals and daily rituals.</p>
                </div>

                <div class="category-grid">
                    <c:choose>
                        <c:when test="${not empty featuredCategories}">
                            <c:forEach items="${featuredCategories}" var="cat" varStatus="loop">
                                <a class="category-card" href="${pageContext.request.contextPath}/products?category=${cat.name}">
                                    <div class="category-image category-image--featured-${loop.index + 1}"></div>
                                    <div class="category-overlay"></div>
                                    <div class="category-content">
                                        <div class="category-title">${cat.name}</div>
                                        <div class="category-link">Shop now</div>
                                    </div>
                                </a>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <a class="category-card" href="${pageContext.request.contextPath}/products">
                                <div class="category-image category-image--featured-1"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Skincare</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                            <a class="category-card" href="${pageContext.request.contextPath}/products">
                                <div class="category-image category-image--featured-2"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Makeup</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                            <a class="category-card" href="${pageContext.request.contextPath}/products">
                                <div class="category-image category-image--featured-3"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Body Care</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                            <a class="category-card" href="${pageContext.request.contextPath}/products">
                                <div class="category-image category-image--featured-4"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Fragrance</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>

        <section class="home-benefits">
            <div class="home-container">
                <div class="section-header">
                    <span class="section-pill">Why Averis</span>
                    <h2 class="section-title">Everything you need for a confident routine</h2>
                    <p class="section-desc">Fast delivery, transparent prices, and expert guidance built into every order.</p>
                </div>
                <div class="benefit-grid">
                    <div class="benefit-card">
                        <h3 class="benefit-title">Authentic Products</h3>
                        <p class="benefit-text">Only verified brands and products with clear origin and quality checks.</p>
                    </div>
                    <div class="benefit-card">
                        <h3 class="benefit-title">Personalized Support</h3>
                        <p class="benefit-text">Get recommendations for sensitive, dry, oily, or combination skin types.</p>
                    </div>
                    <div class="benefit-card">
                        <h3 class="benefit-title">Member Rewards</h3>
                        <p class="benefit-text">Sign up to unlock exclusive vouchers and early access promotions.</p>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <jsp:include page="/assets/footer.jsp" />
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const shouldCleanUrl = new URLSearchParams(window.location.search).get('voucherPopup') === '1';
        if (shouldCleanUrl) {
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });

    function openHomeVoucherModal() {
        const modal = document.getElementById('homeVoucherModal');
        if (!modal) {
            return;
        }
        modal.classList.add('show');
        modal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('home-voucher-open');
    }

    function closeHomeVoucherModal(event) {
        const modal = document.getElementById('homeVoucherModal');
        if (!modal) {
            return;
        }
        if (!event || event.target.classList.contains('home-voucher-modal__backdrop')) {
            modal.classList.remove('show');
            modal.setAttribute('aria-hidden', 'true');
            document.body.classList.remove('home-voucher-open');
        }
    }
</script>
</body>
</html>



