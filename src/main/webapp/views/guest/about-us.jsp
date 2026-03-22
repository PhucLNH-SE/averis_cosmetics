<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - Averis Cosmetics</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">

    
</head>
<body>
<div class="about-layout">
    <jsp:include page="/assets/header.jsp" />

    <main>
        <section class="about-hero">
            <div class="hero-orb"></div>
            <div class="about-container">
                <h1 class="hero-title">About <span>Averis Cosmetics</span></h1>
                <p class="hero-subtitle">
                    A journey to bring natural beauty and confidence through trusted, transparent, and
                    carefully curated skincare and makeup for Asian skin.
                </p>

                <div class="story-grid">
                    <div class="story-image">
                        <img src="https://images.unsplash.com/photo-1758873268364-15bef4162221?auto=format&fit=crop&w=1100&q=80" alt="Averis team">
                    </div>
                    <div>
                        <h2 class="story-title">Our story</h2>
                        <p class="story-text">
                            Averis Cosmetics was founded with a mission to deliver high-quality beauty products
                            that are safe, effective, and made for Vietnamese skin. We believe everyone deserves
                            a routine that feels personal and reliable.
                        </p>
                        <p class="story-text">
                            With a team of skincare specialists, we carefully select every product from reputable
                            brands around the world, ensuring clear origins and proven results.
                        </p>
                        <div class="story-stats">
                            <div class="story-stat">
                                <span>500+</span>
                                Products
                            </div>
                            <div class="story-stat">
                                <span>50K+</span>
                                Customers
                            </div>
                            <div class="story-stat">
                                <span>98%</span>
                                Satisfaction
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="section white">
            <div class="about-container">
                <div class="section-header">
                    <h2 class="section-title">Our <span>core values</span></h2>
                    <p class="section-desc">The principles that guide everything we do at Averis Cosmetics.</p>
                </div>

                <div class="values-grid">
                    <div class="value-card">
                        <div class="value-icon"><i class="fa-solid fa-heart"></i></div>
                        <h3 class="value-title">Quality first</h3>
                        <p class="value-text">Committed to authentic, safe, and effective products for Vietnamese skin.</p>
                    </div>
                    <div class="value-card">
                        <div class="value-icon"><i class="fa-solid fa-users"></i></div>
                        <h3 class="value-title">Customer-centric</h3>
                        <p class="value-text">We listen closely to deliver the best possible shopping experience.</p>
                    </div>
                    <div class="value-card">
                        <div class="value-icon"><i class="fa-solid fa-bullseye"></i></div>
                        <h3 class="value-title">Professional expertise</h3>
                        <p class="value-text">A dedicated team with deep product knowledge and skincare insight.</p>
                    </div>
                    <div class="value-card">
                        <div class="value-icon"><i class="fa-solid fa-arrow-trend-up"></i></div>
                        <h3 class="value-title">Continuous innovation</h3>
                        <p class="value-text">We stay ahead with the newest beauty trends and technologies.</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="section white">
            <div class="about-container mission-grid">
                <div>
                    <span class="mission-badge"><i class="fa-solid fa-globe"></i> Mission &amp; vision</span>
                    <h2 class="mission-title">Sharing beauty with <span>everyone</span></h2>
                    <p class="mission-text">
                        Our mission is to help every customer find the best products for their unique skin,
                        building confidence and helping them shine every day.
                    </p>
                    <p class="mission-text">
                        Our vision is to become Vietnam's most trusted beauty shopping destination,
                        where anyone can discover, compare, and choose with ease.
                    </p>
                </div>
                <div class="mission-image">
                    <img src="https://images.unsplash.com/photo-1658387576612-10096aa099de?auto=format&fit=crop&w=1100&q=80" alt="Mission">
                </div>
            </div>
        </section>

        <section class="cta">
            <div class="about-container">
                <h2 class="cta-title">Start your beauty journey with us</h2>
                <p class="cta-text">Explore curated premium cosmetics made for your everyday routine.</p>
                <div class="cta-actions">
                    <a class="cta-btn primary" href="<%=request.getContextPath()%>/products">Explore products</a>
                    <a class="cta-btn secondary" href="<%=request.getContextPath()%>/contact">Contact us</a>
                </div>
            </div>
        </section>
    </main>

    <jsp:include page="/assets/footer.jsp" />
</div>
</body>
</html>


