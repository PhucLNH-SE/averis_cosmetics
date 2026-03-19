<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
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
                        <div class="feature-icon">+</div>
                        <h3>High Quality</h3>
                        <p>Our products are carefully selected from reputable brands, renowned for their quality formulas and proven effectiveness.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">+</div>
                        <h3>Natural Ingredients</h3>
                        <p>We prioritize products made from natural ingredients, cruelty-free, gentle on the skin, and environmentally friendly.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">+</div>
                        <h3>Best Value</h3>
                        <p>Experience high-quality products at competitive prices with regular promotions and special offers for loyal customers.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">+</div>
                        <h3>Fast Delivery</h3>
                        <p>Fast and reliable delivery to get your favorite products to you as quickly as possible.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">+</div>
                        <h3>Safety & Security</h3>
                        <p>All transactions are secured with advanced encryption technology to ensure your peace of mind.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">+</div>
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
