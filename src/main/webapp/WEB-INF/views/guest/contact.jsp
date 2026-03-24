<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Contact Us - Averis Cosmetics</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

        
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



