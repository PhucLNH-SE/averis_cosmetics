<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Us - Averis Cosmetics</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@300;400;600;700;800&family=Playfair+Display:wght@600;700&display=swap" rel="stylesheet">
    <style>
        .contact-layout {
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

        .contact-container {
            width: min(1180px, 92%);
            margin: 0 auto;
        }

        .contact-hero {
            position: relative;
            padding: 70px 0 40px;
            overflow: hidden;
        }

        .hero-orb {
            position: absolute;
            left: 6%;
            bottom: 10px;
            width: 220px;
            height: 220px;
            border-radius: 50%;
            background: linear-gradient(135deg, #fdba74, #fb923c);
            opacity: 0.25;
            animation: spin 20s linear infinite;
        }

        .hero-pill {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: var(--accent);
            color: #fff;
            padding: 8px 16px;
            border-radius: 999px;
            font-size: 13px;
            letter-spacing: .5px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .hero-title {
            font-family: "Playfair Display", serif;
            font-size: clamp(38px, 4.6vw, 60px);
            margin: 16px 0 14px;
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

        .info-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 24px;
        }

        .info-card {
            background: linear-gradient(145deg, #fff7ef, #fff1df);
            border-radius: 26px;
            padding: 24px;
            text-align: center;
            box-shadow: 0 18px 36px rgba(31, 41, 55, 0.12);
            transition: transform .2s ease, box-shadow .2s ease;
        }

        .info-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 22px 44px rgba(31, 41, 55, 0.18);
        }

        .info-icon {
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

        .info-title {
            font-size: 18px;
            margin: 0 0 8px;
        }

        .info-text {
            color: var(--muted);
            line-height: 1.7;
            margin: 0;
        }

        .support-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 40px;
            align-items: start;
        }

        .support-card {
            border-radius: 28px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(31, 41, 55, 0.18);
            height: 360px;
            position: relative;
        }

        .support-card img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }

        .support-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(180deg, rgba(0,0,0,0.1), rgba(0,0,0,0.55));
            display: flex;
            align-items: flex-end;
            padding: 24px;
            color: #fff;
        }

        .support-box {
            background: linear-gradient(145deg, #fff7ef, #fff1df);
            border-radius: 24px;
            padding: 24px;
            box-shadow: 0 18px 36px rgba(31, 41, 55, 0.12);
        }

        .support-box h3 {
            margin: 0 0 12px;
            font-size: 20px;
        }

        .support-list {
            margin: 0;
            padding: 0;
            list-style: none;
            display: grid;
            gap: 12px;
        }

        .support-item {
            display: flex;
            gap: 10px;
            align-items: flex-start;
            color: var(--muted);
        }

        .support-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: var(--accent);
            margin-top: 6px;
        }

        .faq-wrap {
            max-width: 920px;
            margin: 0 auto;
            display: grid;
            gap: 16px;
        }

        .faq-card {
            background: #fff;
            border-radius: 24px;
            padding: 22px;
            box-shadow: 0 16px 30px rgba(31, 41, 55, 0.12);
        }

        .faq-card h3 {
            margin: 0 0 10px;
        }

        .map-card {
            border-radius: 28px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(31, 41, 55, 0.18);
            height: 360px;
            position: relative;
        }

        .map-card img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }

        .map-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(180deg, rgba(0,0,0,0.05), rgba(0,0,0,0.6));
            display: flex;
            align-items: flex-end;
            padding: 26px;
            color: #fff;
        }

        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }

        @media (max-width: 1100px) {
            .info-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .support-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 720px) {
            .info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<div class="contact-layout">
    <jsp:include page="/assets/header.jsp" />

    <main>
        <section class="contact-hero">
            <div class="hero-orb"></div>
            <div class="contact-container">
                <span class="hero-pill"><i class="fa-solid fa-message"></i> We are here to help</span>
                <h1 class="hero-title">Contact <span>us</span></h1>
                <p class="hero-subtitle">
                    Share your questions and we will get back to you quickly with the right guidance and support.
                </p>
            </div>
        </section>

        <section class="section white">
            <div class="contact-container">
                <div class="info-grid">
                    <div class="info-card">
                        <div class="info-icon"><i class="fa-solid fa-location-dot"></i></div>
                        <h3 class="info-title">Address</h3>
                        <p class="info-text">123 Nguyen Hue Street, District 1, Ho Chi Minh City</p>
                    </div>
                    <div class="info-card">
                        <div class="info-icon"><i class="fa-solid fa-phone"></i></div>
                        <h3 class="info-title">Hotline</h3>
                        <p class="info-text">0866 434 787</p>
                    </div>
                    <div class="info-card">
                        <div class="info-icon"><i class="fa-solid fa-envelope"></i></div>
                        <h3 class="info-title">Email</h3>
                        <p class="info-text">support@averis.com</p>
                    </div>
                    <div class="info-card">
                        <div class="info-icon"><i class="fa-solid fa-clock"></i></div>
                        <h3 class="info-title">Working hours</h3>
                        <p class="info-text">Mon - Sun: 08:00 - 22:00</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="section">
            <div class="contact-container support-grid">
                <div>
                    <div class="support-card">
                        <img src="https://images.unsplash.com/photo-1553775282-20af80779df7?auto=format&fit=crop&w=1100&q=80" alt="Support">
                        <div class="support-overlay">
                            <div>
                                <h3>24/7 Customer Care</h3>
                                <p>Our support team is always ready to assist you.</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="support-box">
                    <h3>How we can help</h3>
                    <ul class="support-list">
                        <li class="support-item"><span class="support-dot"></span>Free consultation to match products to your skin type.</li>
                        <li class="support-item"><span class="support-dot"></span>Order support and delivery tracking.</li>
                        <li class="support-item"><span class="support-dot"></span>Guidance on usage, routines, and ingredients.</li>
                        <li class="support-item"><span class="support-dot"></span>Fast handling for returns and warranties.</li>
                    </ul>
                </div>
            </div>
        </section>

        <section class="section white">
            <div class="contact-container">
                <div class="section-header">
                    <h2 class="section-title">Frequently <span>asked questions</span></h2>
                    <p class="section-desc">Helpful answers to common questions.</p>
                </div>
                <div class="faq-wrap">
                    <div class="faq-card">
                        <h3>How do I place an order?</h3>
                        <p>Browse products, add items to your cart, and complete checkout. We support multiple payment methods.</p>
                    </div>
                    <div class="faq-card">
                        <h3>How long does delivery take?</h3>
                        <p>Local orders arrive in 1-2 days, and nationwide deliveries take 2-4 business days.</p>
                    </div>
                    <div class="faq-card">
                        <h3>What is the return policy?</h3>
                        <p>Returns are accepted within 7 days if the product is unused and sealed.</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="section">
            <div class="contact-container">
                <div class="section-header">
                    <h2 class="section-title">Find <span>us</span></h2>
                </div>
                <div class="map-card">
                    <img src="https://images.unsplash.com/photo-1644337540803-2b2fb3cebf12?auto=format&fit=crop&w=1100&q=80" alt="Location">
                    <div class="map-overlay">
                        <div>
                            <h3>Main office</h3>
                            <p>123 Nguyen Hue Street, District 1, Ho Chi Minh City</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <jsp:include page="/assets/footer.jsp" />
</div>
</body>
</html>
