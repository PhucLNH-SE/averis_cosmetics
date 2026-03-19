<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - Averis Cosmetics</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@300;400;600;700;800&family=Playfair+Display:wght@600;700&display=swap" rel="stylesheet">
    <style>
        .about-layout {
            --bg: #fffdf9;
            --surface: #fff6ec;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --accent: #b45309;
            --accent-dark: #92400e;
            --line: #eadfce;
            font-family: "Manrope", Arial, sans-serif;
            color: var(--text);
            background:
                radial-gradient(circle at 12% 12%, #fff2dd 0%, transparent 36%),
                radial-gradient(circle at 86% 8%, #fff0d9 0%, transparent 32%),
                var(--bg);
            min-height: 100vh;
        }

        .about-container {
            width: min(1180px, 92%);
            margin: 0 auto;
        }

        .about-hero {
            position: relative;
            padding: 70px 0 40px;
            overflow: hidden;
        }

        .hero-orb {
            position: absolute;
            right: 10%;
            top: 20px;
            width: 220px;
            height: 220px;
            border-radius: 50%;
            background: linear-gradient(135deg, #fdba74, #fb923c);
            opacity: 0.25;
            animation: spin 18s linear infinite;
        }

        .hero-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(38px, 4.6vw, 60px);
            margin: 0 0 16px;
        }

        .hero-title span {
            color: var(--accent);
        }

        .hero-subtitle {
            font-size: 18px;
            color: var(--muted);
            max-width: 720px;
            line-height: 1.8;
        }

        .story-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 40px;
            align-items: center;
            margin-top: 40px;
        }

        .story-image {
            border-radius: 28px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(31, 41, 55, 0.18);
            height: 380px;
        }

        .story-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }

        .story-title {
            font-size: 28px;
            margin-bottom: 16px;
        }

        .story-text {
            color: var(--muted);
            line-height: 1.8;
            margin-bottom: 12px;
        }

        .story-stats {
            display: flex;
            gap: 26px;
            margin-top: 24px;
        }

        .story-stat {
            font-weight: 700;
        }

        .story-stat span {
            display: block;
            font-size: 26px;
            color: var(--accent);
            margin-bottom: 6px;
        }

        .section {
            padding: 70px 0;
        }

        .section.white {
            background: #fff;
        }

        .section-header {
            text-align: center;
            margin-bottom: 36px;
        }

        .section-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(28px, 3.4vw, 46px);
            margin: 12px 0 10px;
        }

        .section-title span {
            color: var(--accent);
        }

        .section-desc {
            color: var(--muted);
            max-width: 640px;
            margin: 0 auto;
            line-height: 1.7;
        }

        .values-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 24px;
        }

        .value-card {
            background: linear-gradient(145deg, #fff7ef, #fff1df);
            border-radius: 26px;
            padding: 24px;
            box-shadow: 0 18px 36px rgba(31, 41, 55, 0.12);
            transition: transform .2s ease, box-shadow .2s ease;
            min-height: 240px;
        }

        .value-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 22px 44px rgba(31, 41, 55, 0.18);
        }

        .value-icon {
            width: 56px;
            height: 56px;
            border-radius: 16px;
            background: linear-gradient(135deg, #f97316, #ea580c);
            color: #fff;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            margin-bottom: 16px;
        }

        .value-title {
            font-size: 18px;
            margin: 0 0 10px;
        }

        .value-text {
            color: var(--muted);
            line-height: 1.7;
            margin: 0;
        }

        .timeline {
            position: relative;
            margin-top: 40px;
        }

        .timeline::before {
            content: "";
            position: absolute;
            left: 50%;
            top: 0;
            bottom: 0;
            width: 3px;
            background: #f7c089;
            transform: translateX(-50%);
        }

        .timeline-item {
            display: grid;
            grid-template-columns: 1fr 36px 1fr;
            align-items: center;
            margin: 24px 0;
        }

        .timeline-card {
            background: #fff;
            border-radius: 18px;
            padding: 18px 22px;
            box-shadow: 0 16px 30px rgba(31, 41, 55, 0.12);
            display: inline-block;
            max-width: 420px;
        }

        .timeline-year {
            color: var(--accent);
            font-size: 22px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .timeline-dot {
            width: 14px;
            height: 14px;
            background: var(--accent);
            border-radius: 50%;
            margin: 0 auto;
            z-index: 1;
        }

        .timeline-item.right .timeline-card {
            margin-left: auto;
        }

        .mission-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 40px;
            align-items: center;
        }

        .mission-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: #fff1df;
            color: var(--accent);
            padding: 8px 16px;
            border-radius: 999px;
            font-weight: 700;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: .5px;
        }

        .mission-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(28px, 3.4vw, 40px);
            margin: 16px 0;
        }

        .mission-title span {
            color: var(--accent);
        }

        .mission-text {
            color: var(--muted);
            line-height: 1.8;
            margin-bottom: 12px;
        }

        .mission-image {
            border-radius: 28px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(31, 41, 55, 0.18);
            height: 360px;
        }

        .mission-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }

        .cta {
            background: linear-gradient(120deg, #ea580c, #f97316);
            color: #fff;
            padding: 70px 0;
            text-align: center;
        }

        .cta-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(30px, 4vw, 46px);
            margin: 0 0 18px;
        }

        .cta-text {
            color: rgba(255, 255, 255, 0.88);
            max-width: 640px;
            margin: 0 auto 26px;
            line-height: 1.7;
        }

        .cta-actions {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 14px;
        }

        .cta-btn {
            padding: 12px 26px;
            border-radius: 999px;
            text-decoration: none;
            font-weight: 700;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            transition: transform .2s ease, background .2s ease;
        }

        .cta-btn.primary {
            background: #fff;
            color: var(--accent);
        }

        .cta-btn.secondary {
            border: 2px solid #fff;
            color: #fff;
        }

        .cta-btn:hover {
            transform: translateY(-2px);
        }

        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }

        @media (max-width: 1100px) {
            .story-grid,
            .mission-grid {
                grid-template-columns: 1fr;
            }

            .values-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .timeline::before {
                left: 12px;
            }

            .timeline-item {
                grid-template-columns: 12px 1fr;
                gap: 18px;
            }

            .timeline-item .timeline-card,
            .timeline-item.right .timeline-card {
                margin-left: 0;
            }
        }

        @media (max-width: 720px) {
            .values-grid {
                grid-template-columns: 1fr;
            }

            .story-stats {
                flex-direction: column;
                gap: 12px;
            }
        }
    </style>
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

        <section class="section">
            <div class="about-container">
                <div class="section-header">
                    <h2 class="section-title">Our <span>journey</span></h2>
                </div>

                <div class="timeline">
                    <div class="timeline-item left">
                        <div class="timeline-card">
                            <div class="timeline-year">2019</div>
                            <p>Averis Cosmetics was founded with its first store in Ho Chi Minh City.</p>
                        </div>
                        <div class="timeline-dot"></div>
                        <div></div>
                    </div>
                    <div class="timeline-item right">
                        <div></div>
                        <div class="timeline-dot"></div>
                        <div class="timeline-card">
                            <div class="timeline-year">2020</div>
                            <p>Expanded service to 10 major provinces and cities.</p>
                        </div>
                    </div>
                    <div class="timeline-item left">
                        <div class="timeline-card">
                            <div class="timeline-year">2022</div>
                            <p>Reached 50,000+ trusted customers and five-star reviews.</p>
                        </div>
                        <div class="timeline-dot"></div>
                        <div></div>
                    </div>
                    <div class="timeline-item right">
                        <div></div>
                        <div class="timeline-dot"></div>
                        <div class="timeline-card">
                            <div class="timeline-year">2024</div>
                            <p>Launched our modern online platform with nationwide delivery.</p>
                        </div>
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
