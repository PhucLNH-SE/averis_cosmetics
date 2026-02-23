<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
        }
        
        .hero-section {
            background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
            padding: 80px 0;
            text-align: center;
        }
        
        .hero-section h1 {
            font-size: 2.5rem;
            color: #b45309;
            margin-bottom: 20px;
        }
        
        .hero-section p {
            font-size: 1.2rem;
            max-width: 800px;
            margin: 0 auto;
            color: #555;
        }
        
        .section-padding {
            padding: 60px 0;
        }
        
        .section-title {
            text-align: center;
            margin-bottom: 40px;
            color: #b45309;
            font-size: 2rem;
        }
        
        .why-choose-us {
            background-color: #fafafa;
        }
        
        .feature-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            text-align: center;
            margin-bottom: 20px;
            transition: transform 0.3s ease;
        }
        
        .feature-card:hover {
            transform: translateY(-5px);
        }
        
        .feature-icon {
            font-size: 2.5rem;
            color: #b45309;
            margin-bottom: 15px;
        }
        
        .team-section {
            text-align: center;
        }
        
        .team-image {
            max-width: 100%;
            height: auto;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
        }
        
        .contact-footer {
            background-color: #1f2937;
            color: white;
            padding: 40px 0;
        }
        
        .contact-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            margin-bottom: 30px;
        }
        
        .contact-column h3 {
            color: #fbbf24;
            margin-bottom: 15px;
            font-size: 1.3rem;
        }
        
        .contact-column ul {
            list-style: none;
            padding: 0;
        }
        
        .contact-column ul li {
            margin-bottom: 8px;
        }
        
        .contact-column a {
            color: #e5e7eb;
            text-decoration: none;
        }
        
        .contact-column a:hover {
            color: #fbbf24;
        }
        
        .copyright {
            text-align: center;
            padding-top: 20px;
            border-top: 1px solid #374151;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <%@include file="/assets/header.jsp" %>
    
    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <h1>About Averis Cosmetics</h1>
            <p>Welcome to Averis Cosmetics, your trusted destination for premium beauty products that enhance your natural beauty and boost your confidence.</p>
        </div>
    </section>
    
    <!-- Introduction Section -->
    <section class="section-padding">
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <h2 class="section-title">Introduction to our cosmetics sales page</h2>
                    <p>Welcome to Averis Cosmetics - the online store specializing in high-quality cosmetic products that help enhance natural beauty and bring confidence to Vietnamese women.</p>
                    
                    <p>With the mission "Beauty begins with safety and effectiveness," we are committed to offering skincare and makeup products from reputable brands both domestic and international, all strictly quality-checked. Every product at Averis is carefully selected based on the criteria: safe ingredients, noticeable effectiveness, and suitability for Vietnamese skin.</p>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Why Choose Us Section -->
    <section class="why-choose-us section-padding">
        <div class="container">
            <h2 class="section-title">Why should you choose our product?</h2>
            <div class="row">
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">✨</div>
                        <h3>High Quality</h3>
                        <p>Our products are carefully selected from reputable brands, renowned for their quality formulas and proven effectiveness.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">🌿</div>
                        <h3>Natural Ingredients</h3>
                        <p>We prioritize products made from natural ingredients, cruelty-free, gentle on the skin, and environmentally friendly.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">💰</div>
                        <h3>Best Value</h3>
                        <p>Experience high-quality products at competitive prices with regular promotions and special offers for loyal customers.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">🚚</div>
                        <h3>Fast Delivery</h3>
                        <p>Fast and reliable delivery to get your favorite products to you as quickly as possible.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">🛡️</div>
                        <h3>Safety & Security</h3>
                        <p>All transactions are secured with advanced encryption technology to ensure your peace of mind.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">💬</div>
                        <h3>Professional Support</h3>
                        <p>Our beauty experts are always ready to help you find the perfect product that suits your needs.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Team Section -->
    <section class="team-section section-padding">
        <div class="container">
            <h2 class="section-title">Meet Our Team</h2>
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <img src="${pageContext.request.contextPath}/assets/img/123.png" alt="Our Team" class="team-image" onerror="this.style.display='none'; document.querySelector('.team-placeholder').style.display='block';">
                    <div class="team-placeholder" style="display:none; text-align:center; padding:50px; background:#f8f9fa; border-radius:10px;">
                        
                    </div>
                </div>
            </div>
        </div>
    </section>
    

    
    <%@include file="/assets/footer.jsp" %>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>