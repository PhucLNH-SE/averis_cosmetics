<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setLocale value="en_US"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Averis Cosmetics - Home</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@300;400;600;700;800&family=Playfair+Display:wght@600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg: #fffdf9;
            --surface: #fff6ec;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --accent: #b45309;
            --accent-dark: #92400e;
            --line: #eadfce;
            --shadow: 0 18px 40px rgba(31, 41, 55, 0.12);
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
        }

        .home-layout {
            min-height: 100vh;
            background:
                radial-gradient(circle at 12% 12%, #fff2dd 0%, transparent 36%),
                radial-gradient(circle at 85% 8%, #fff0d9 0%, transparent 32%),
                var(--bg);
            font-family: "Manrope", Arial, sans-serif;
            color: var(--text);
        }

        .home-container {
            width: min(1180px, 92%);
            margin: 0 auto;
        }

        .home-hero {
            position: relative;
            overflow: hidden;
            padding: 72px 0 40px;
        }

        .hero-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 40px;
            align-items: center;
        }

        .hero-badge {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            background: rgba(180, 83, 9, 0.12);
            color: var(--accent);
            padding: 8px 16px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .hero-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(36px, 4vw, 58px);
            line-height: 1.05;
            margin: 18px 0 16px;
        }

        .hero-title span {
            color: var(--accent);
        }

        .hero-copy {
            font-size: 16px;
            color: var(--muted);
            line-height: 1.8;
            max-width: 520px;
        }

        .hero-actions {
            margin-top: 28px;
            display: flex;
            flex-wrap: wrap;
            gap: 14px;
        }

        .hero-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 14px 28px;
            border-radius: 999px;
            font-weight: 700;
            text-decoration: none;
            transition: transform .2s ease, box-shadow .2s ease, background .2s ease;
        }

        .hero-btn.primary {
            background: var(--accent);
            color: #fff;
            box-shadow: 0 12px 26px rgba(180, 83, 9, 0.3);
        }

        .hero-btn.primary:hover {
            background: var(--accent-dark);
            transform: translateY(-2px);
        }

        .hero-btn.ghost {
            border: 1px solid var(--line);
            color: var(--text);
            background: #fff;
        }

        .hero-btn.ghost:hover {
            border-color: var(--accent);
            color: var(--accent);
        }

        .hero-media {
            position: relative;
            min-height: 480px;
        }

        .hero-media img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }

        .hero-main-image {
            position: absolute;
            inset: 50% auto auto 50%;
            transform: translate(-50%, -50%);
            width: 320px;
            height: 320px;
            border-radius: 50%;
            overflow: hidden;
            box-shadow: var(--shadow);
            animation: float-up 6s ease-in-out infinite;
        }

        .hero-float {
            position: absolute;
            width: 140px;
            height: 140px;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 14px 30px rgba(0, 0, 0, 0.18);
        }

        .hero-float.one {
            top: 20px;
            left: 10px;
            animation: float-down 7s ease-in-out infinite;
        }

        .hero-float.two {
            bottom: 24px;
            right: 20px;
            animation: float-up 8s ease-in-out infinite;
        }

        .hero-orb {
            position: absolute;
            width: 90px;
            height: 90px;
            border-radius: 50%;
            background: linear-gradient(135deg, #f59e0b, #ea580c);
            opacity: 0.2;
            top: 22%;
            right: 22%;
            animation: spin 12s linear infinite;
        }

        .hero-decor {
            position: absolute;
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: #fde6c9;
            opacity: 0.7;
            bottom: -20px;
            left: 40px;
            animation: float-down 10s ease-in-out infinite;
        }

        .section-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .section-pill {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: #fff1df;
            color: var(--accent);
            padding: 8px 16px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: .5px;
            text-transform: uppercase;
        }

        .section-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(28px, 3.2vw, 46px);
            margin: 18px 0 12px;
        }

        .section-desc {
            color: var(--muted);
            max-width: 640px;
            margin: 0 auto;
            line-height: 1.7;
        }

        .home-featured {
            padding: 40px 0 70px;
            background: #fff;
        }

        .featured-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 24px;
        }

        .featured-card {
            background: linear-gradient(145deg, #fff7ef, #fff1df);
            border-radius: 28px;
            overflow: hidden;
            box-shadow: 0 18px 35px rgba(31, 41, 55, 0.12);
            position: relative;
            display: flex;
            flex-direction: column;
            min-height: 420px;
            transition: transform .2s ease, box-shadow .2s ease;
        }

        .featured-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 22px 44px rgba(31, 41, 55, 0.2);
        }

        .featured-badge {
            position: absolute;
            top: 18px;
            left: 18px;
            background: var(--accent);
            color: #fff;
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .featured-image {
            height: 240px;
            overflow: hidden;
            display: block;
        }

        .featured-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform .4s ease;
        }

        .featured-card:hover .featured-image img {
            transform: scale(1.06);
        }

        .featured-body {
            padding: 20px 22px 24px;
            display: flex;
            flex-direction: column;
            gap: 12px;
            flex: 1;
        }

        .featured-name {
            font-size: 18px;
            font-weight: 700;
            margin: 0;
        }

        .featured-meta {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            margin-top: auto;
        }

        .featured-price {
            color: var(--accent);
            font-weight: 700;
            font-size: 18px;
        }

        .featured-btn {
            background: var(--accent);
            color: #fff;
            padding: 8px 16px;
            border-radius: 999px;
            font-size: 13px;
            text-decoration: none;
            font-weight: 700;
        }

        .featured-btn:hover {
            background: var(--accent-dark);
        }

        .home-categories {
            padding: 60px 0 20px;
        }

        .category-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 20px;
        }

        .category-card {
            position: relative;
            border-radius: 22px;
            overflow: hidden;
            min-height: 220px;
            color: #fff;
            text-decoration: none;
            box-shadow: var(--shadow);
        }

        .category-image {
            position: absolute;
            inset: 0;
            background-size: cover;
            background-position: center;
            filter: brightness(0.8);
            transition: transform .4s ease;
        }

        .category-card:hover .category-image {
            transform: scale(1.08);
        }

        .category-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(180deg, rgba(0,0,0,0.05), rgba(0,0,0,0.55));
        }

        .category-content {
            position: relative;
            z-index: 1;
            padding: 18px;
            display: flex;
            flex-direction: column;
            height: 100%;
            justify-content: flex-end;
            gap: 6px;
        }

        .category-title {
            font-size: 18px;
            font-weight: 700;
        }

        .category-link {
            font-size: 13px;
            letter-spacing: .4px;
            text-transform: uppercase;
            font-weight: 700;
        }

        .home-benefits {
            padding: 50px 0 90px;
        }

        .benefit-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 22px;
        }

        .benefit-card {
            background: #fff;
            border: 1px solid var(--line);
            border-radius: 20px;
            padding: 24px;
            box-shadow: 0 16px 32px rgba(31, 41, 55, 0.1);
        }

        .benefit-title {
            font-size: 18px;
            font-weight: 700;
            margin: 0 0 10px;
        }

        .benefit-text {
            color: var(--muted);
            line-height: 1.7;
            margin: 0;
        }

        @keyframes float-up {
            0% { transform: translate(-50%, -50%) translateY(0); }
            50% { transform: translate(-50%, -50%) translateY(-12px); }
            100% { transform: translate(-50%, -50%) translateY(0); }
        }

        @keyframes float-down {
            0% { transform: translateY(0); }
            50% { transform: translateY(12px); }
            100% { transform: translateY(0); }
        }

        @keyframes spin {
            from { transform: rotate(0deg) scale(1); }
            to { transform: rotate(360deg) scale(1); }
        }

        @media (max-width: 1100px) {
            .hero-grid {
                grid-template-columns: 1fr;
                text-align: left;
            }

            .hero-media {
                min-height: 380px;
            }

            .featured-grid,
            .benefit-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .category-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 720px) {
            .hero-actions {
                flex-direction: column;
                align-items: stretch;
            }

            .featured-grid,
            .benefit-grid,
            .category-grid {
                grid-template-columns: 1fr;
            }

            .hero-media {
                min-height: 320px;
            }

            .hero-main-image {
                width: 240px;
                height: 240px;
            }

            .hero-float {
                width: 110px;
                height: 110px;
            }
        }
    </style>
</head>
<body>
<div class="home-layout">
    <jsp:include page="/assets/header.jsp" />

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
                        <a class="hero-btn primary" href="<%=request.getContextPath()%>/products">Shop now</a>
                        <a class="hero-btn ghost" href="<%=request.getContextPath()%>/introduce">View collection</a>
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
                                    <a class="featured-image" href="<%=request.getContextPath()%>/products?id=${p.productId}">
                                        <c:choose>
                                            <c:when test="${not empty p.imageUrl}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(p.imageUrl, 'http')}">
                                                        <img src="${p.imageUrl}" alt="${p.productName}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="<%=request.getContextPath()%>/assets/img/${p.imageUrl}" alt="${p.productName}" onerror="this.src='<%=request.getContextPath()%>/assets/img/Logo.png';">
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="Product image">
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
                                            <a class="featured-btn" href="<%=request.getContextPath()%>/products?id=${p.productId}">View</a>
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
                                    <img src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="Product">
                                </div>
                                <div class="featured-body">
                                    <h3 class="featured-name">Top Products Coming Soon</h3>
                                    <div class="featured-meta">
                                        <div class="featured-price">Contact</div>
                                        <a class="featured-btn" href="<%=request.getContextPath()%>/products">Browse</a>
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
                                <c:choose>
                                    <c:when test="${loop.index == 0}"><c:set var="categoryImage" value="https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=900&q=80"/></c:when>
                                    <c:when test="${loop.index == 1}"><c:set var="categoryImage" value="https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=900&q=80"/></c:when>
                                    <c:when test="${loop.index == 2}"><c:set var="categoryImage" value="https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=900&q=80"/></c:when>
                                    <c:otherwise><c:set var="categoryImage" value="https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=900&q=80"/></c:otherwise>
                                </c:choose>
                                <a class="category-card" href="<%=request.getContextPath()%>/products?category=${cat.name}">
                                    <div class="category-image" style="background-image: url('${categoryImage}')"></div>
                                    <div class="category-overlay"></div>
                                    <div class="category-content">
                                        <div class="category-title">${cat.name}</div>
                                        <div class="category-link">Shop now</div>
                                    </div>
                                </a>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <a class="category-card" href="<%=request.getContextPath()%>/products">
                                <div class="category-image" style="background-image: url('https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=900&q=80')"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Skincare</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                            <a class="category-card" href="<%=request.getContextPath()%>/products">
                                <div class="category-image" style="background-image: url('https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=900&q=80')"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Makeup</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                            <a class="category-card" href="<%=request.getContextPath()%>/products">
                                <div class="category-image" style="background-image: url('https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=900&q=80')"></div>
                                <div class="category-overlay"></div>
                                <div class="category-content">
                                    <div class="category-title">Body Care</div>
                                    <div class="category-link">Shop now</div>
                                </div>
                            </a>
                            <a class="category-card" href="<%=request.getContextPath()%>/products">
                                <div class="category-image" style="background-image: url('https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=900&q=80')"></div>
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
</body>
</html>
