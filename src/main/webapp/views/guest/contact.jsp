<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Us - Averis Cosmetics</title>
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
        
        .section-padding {
            padding: 60px 0;
        }
        
        .contact-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .contact-info {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .contact-form {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-control {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
        }
        
        textarea.form-control {
            height: 150px;
            resize: vertical;
        }
        
        .btn {
            background-color: #b45309;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        
        .btn:hover {
            background-color: #92400e;
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .info-item {
            text-align: center;
            padding: 20px;
        }
        
        .info-icon {
            font-size: 2rem;
            color: #b45309;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <%@include file="/assets/header.jsp" %>
    
    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <h1>Contact Us</h1>
            <p>Have questions or feedback? We'd love to hear from you!</p>
        </div>
    </section>
    
    <section class="section-padding">
        <div class="container contact-container">
            <div class="row">
                <div class="col-md-6">
                    <div class="contact-info">
                        <h3>Contact Information</h3>
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-icon">🏢</div>
                                <h5>Address</h5>
                                <p>123 Beauty Street<br>Cosmo City, BC 12345</p>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">📞</div>
                                <h5>Phone</h5>
                                <p>+1 (555) 123-4567</p>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">✉️</div>
                                <h5>Email</h5>
                                <p>info@averiscosmetics.com</p>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">🕒</div>
                                <h5>Hours</h5>
                                <p>Mon-Fri: 9AM-6PM<br>Sat-Sun: 10AM-4PM</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="col-md-6">
                    <div class="contact-form">
                        <h3>Send us a Message</h3>
                        <form action="#" method="post">
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Your Name" required>
                            </div>
                            <div class="form-group">
                                <input type="email" class="form-control" placeholder="Your Email" required>
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Subject">
                            </div>
                            <div class="form-group">
                                <textarea class="form-control" placeholder="Your Message" required></textarea>
                            </div>
                            <button type="submit" class="btn">Send Message</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <%@include file="/assets/footer.jsp" %>
    
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>