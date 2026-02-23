<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Averis Cosmetics - Home</title>
    <style>
        :root {
            --bg: #fffefb;
            --surface: #f9f3eb;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --accent: #b45309;
            --accent-hover: #92400e;
            --line: #eadfce;
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            color: var(--text);
            background:
                radial-gradient(circle at 10% 10%, #fff6ea 0%, transparent 35%),
                radial-gradient(circle at 85% 20%, #fff0dc 0%, transparent 28%),
                var(--bg);
        }

        .home-page {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .hero {
            width: min(1120px, 92%);
            margin: 36px auto 22px;
            border: 1px solid var(--line);
            border-radius: 22px;
            background: linear-gradient(135deg, #fff, #fff7ee 55%, #fcefdc);
            padding: 54px 48px;
            display: grid;
            grid-template-columns: 1.1fr 0.9fr;
            gap: 28px;
            align-items: center;
            box-shadow: 0 18px 38px rgba(31, 41, 55, 0.09);
        }

        .hero h1 {
            margin: 0 0 12px;
            font-size: clamp(34px, 4vw, 54px);
            line-height: 1.05;
        }

        .hero p {
            margin: 0 0 22px;
            color: var(--muted);
            font-size: 16px;
            line-height: 1.7;
            max-width: 560px;
        }

        .hero-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
        }

        .btn {
            display: inline-block;
            padding: 12px 22px;
            border-radius: 999px;
            text-decoration: none;
            font-weight: 700;
            border: 1px solid transparent;
            transition: .2s ease;
        }

        .btn-primary {
            background: var(--accent);
            color: #fff;
            box-shadow: 0 8px 18px rgba(180, 83, 9, 0.26);
        }

        .btn-primary:hover {
            background: var(--accent-hover);
            transform: translateY(-1px);
        }

        .btn-outline {
            border-color: var(--line);
            color: var(--text);
            background: #fff;
        }

        .btn-outline:hover {
            border-color: var(--accent);
            color: var(--accent);
        }

        .hero-highlight {
            display: grid;
            gap: 14px;
        }

        .badge {
            display: inline-block;
            background: rgba(180, 83, 9, 0.12);
            color: var(--accent);
            padding: 7px 14px;
            border-radius: 999px;
            font-weight: 700;
            font-size: 13px;
            width: fit-content;
            text-transform: uppercase;
            letter-spacing: .4px;
        }

        .feature-box {
            background: #fff;
            border: 1px solid var(--line);
            border-radius: 16px;
            padding: 16px 18px;
        }

        .feature-box strong {
            display: block;
            margin-bottom: 6px;
            font-size: 16px;
        }

        .feature-box span {
            color: var(--muted);
            font-size: 14px;
            line-height: 1.5;
        }

        .section {
            width: min(1120px, 92%);
            margin: 10px auto 34px;
        }

        .section-title {
            margin: 0 0 14px;
            font-size: 28px;
        }

        .section-subtitle {
            margin: 0 0 20px;
            color: var(--muted);
        }

        .cards {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 18px;
        }

        .card {
            background: var(--card);
            border: 1px solid var(--line);
            border-radius: 16px;
            padding: 20px;
            box-shadow: 0 10px 20px rgba(31, 41, 55, 0.05);
        }

        .card h3 {
            margin: 0 0 8px;
            font-size: 18px;
        }

        .card p {
            margin: 0;
            color: var(--muted);
            line-height: 1.65;
            font-size: 14px;
        }

        @media (max-width: 900px) {
            .hero {
                grid-template-columns: 1fr;
                padding: 34px 24px;
            }

            .cards {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<div class="home-page">
    <jsp:include page="/assets/header.jsp" />

    <main>
        <section class="hero">
            <div>
                <span class="badge">New Season Glow</span>
                <h1>Beautiful skin with a simple and effective daily routine.</h1>
                <p>
                    Averis Cosmetics offers curated skincare and makeup collections,
                    optimized for Asian skin and modern lifestyles.
                </p>
                <div class="hero-actions">
                    <a class="btn btn-primary" href="<%=request.getContextPath()%>/products">Shop Now</a>
                </div>
            </div>

            <div class="hero-highlight">
                <div class="feature-box">
                    <strong>100% Authentic Products</strong>
                    <span>We guarantee clear origins and strict quality checks before every order is delivered.</span>
                </div>
                <div class="feature-box">
                    <strong>Personalized Routine Support</strong>
                    <span>Suggestions for acne-prone, sensitive, dry, and combination skin types.</span>
                </div>
                <div class="feature-box">
                    <strong>New Member Benefits</strong>
                    <span>Create an account to receive promotions and updates on the latest collections.</span>
                </div>
            </div>
        </section>

        <section class="section">
            <h2 class="section-title">Why Choose Averis?</h2>
            <p class="section-subtitle">Built for a fast, reliable, and user-friendly shopping experience.</p>
            <div class="cards">
                <article class="card">
                    <h3>Diverse Product Catalog</h3>
                    <p>Hundreds of skincare and makeup products, clearly categorized for quick browsing.</p>
                </article>
                <article class="card">
                    <h3>Transparent Information</h3>
                    <p>Detailed descriptions, real product images, and updated pricing for better decisions.</p>
                </article>
                <article class="card">
                    <h3>Dedicated Support</h3>
                    <p>Our team is always ready to help with orders, shipping, and product usage guidance.</p>
                </article>
            </div>
        </section>
    </main>

    <jsp:include page="/assets/footer.jsp" />
</div>
</body>
</html>
