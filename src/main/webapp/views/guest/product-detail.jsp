<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Product Detail - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    </head>
    <body>
        <%@include file="/assets/header.jsp" %>
        
        <div class="container">
            <a href="javascript:history.back()" class="back-link" title="Back to previous page">← Back to product list</a>
            
            <c:if test="${not empty product}">
                <div class="product-detail">
                    <!-- Hình ảnh sản phẩm -->
                    <div class="product-images">
                        <c:choose>
                            <c:when test="${not empty product.images}">
                                <!-- Nếu có hình ảnh trong database -->
                                <img id="mainImage" class="main-image" 
                                     src="<%=request.getContextPath()%>/assets/img/${product.mainImage}" 
                                     alt="${product.name}">
                                
                                <div class="thumbnail-images">
                                    <c:forEach items="${product.images}" var="img" varStatus="loop">
                                        <img class="thumbnail ${loop.index == 0 ? 'active' : ''}" 
                                             src="<%=request.getContextPath()%>/assets/img/${img.image}" 
                                             alt="${product.name}" 
                                             onclick="changeImage('${img.image}', this)">
                                    </c:forEach>
                                </div>
                                <c:if test="${empty product.images}">
                                    <div class="thumbnail-images">
                                        <div class="default-image" style="width: 80px; height: 80px;"></div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <!-- Nếu không có hình ảnh, hiển thị hình ảnh mặc định -->
                                <div class="main-image default-image"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- Thông tin sản phẩm -->
                    <div class="product-info">
                        <h1>${product.name}</h1>
                        <div class="brand-name">${product.brand.name}</div>
                        <div class="category">Category: ${product.category.name}</div>
                        
                        <div class="description">
                            <c:choose>
                                <c:when test="${not empty product.description}">
                                    ${product.description}
                                </c:when>
                                <c:otherwise>
                                    High quality product from brand ${product.brand.name}
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <!-- Price Section with Variants -->
                        <div class="price-section">
                            <div class="price-label">Price:</div>
                            <c:choose>
                                <c:when test="${not empty product.variants}">
                                    <div class="current-price">
                                        <fmt:formatNumber value="${product.variants[0].price}" type="currency" currencySymbol="$"/>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="price">Contact</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <!-- Product Variants Selection -->
                        <c:if test="${not empty product.variants}">
                            <div class="variants-section">
                                <div class="variants-label">Version:</div>
                                <div class="variants-container">
                                    <c:forEach items="${product.variants}" var="variant" varStatus="loop">
                                        <div class="variant-item ${loop.index == 0 ? 'active' : ''}" 
                                             data-variant-id="${variant.variantId}" 
                                             data-price="${variant.price}">
                                            <c:choose>
                                                <c:when test="${not empty variant.variantName}">
                                                    <span class="variant-name">${variant.variantName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="variant-name">Variant ${loop.index + 1}</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="variant-price"><fmt:formatNumber value="${variant.price}" type="currency" currencySymbol="$"/></span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                        
                        <div class="actions">
                            <button class="btn btn-primary" onclick="addToCart(${product.productId})">
                                Add to cart
                            </button>
                            <a href="<%=request.getContextPath()%>/contact" class="btn btn-secondary">
                                Contact for consultation
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <c:if test="${empty product}">
                <div style="text-align: center; padding: 50px;">
                    <h2>Product does not exist</h2>
                    <p>Sorry, the product you are looking for was not found.</p>
                    <a href="<%=request.getContextPath()%>/products" class="btn btn-primary">
                        Back to store
                    </a>
                </div>
            </c:if>
        </div>
        
        <%@include file="/assets/footer.jsp" %>
        
        <script>
            function changeImage(imagePath, element) {
                document.getElementById('mainImage').src = '<%=request.getContextPath()%>/assets/img/' + imagePath;
                
                // Update active state for thumbnail
                const thumbnails = document.querySelectorAll('.thumbnail');
                thumbnails.forEach(thumb => thumb.classList.remove('active'));
                element.classList.add('active');
            }
            
            // Handle variant selection
            document.addEventListener('DOMContentLoaded', function() {
                const variantItems = document.querySelectorAll('.variant-item');
                
                variantItems.forEach(item => {
                    item.addEventListener('click', function() {
                        // Remove active class from all variants
                        variantItems.forEach(v => v.classList.remove('active'));
                        
                        // Add active class to clicked variant
                        this.classList.add('active');
                        
                        // Update price display
                        const price = this.getAttribute('data-price');
                        const formattedPrice = formatCurrency(price);
                        document.querySelector('.price-section .current-price').textContent = formattedPrice;
                    });
                });
            });
            
            function formatCurrency(amount) {
                return new Intl.NumberFormat('en-US', {
                    style: 'currency',
                    currency: 'USD'
                }).format(amount);
            }
            
            function addToCart(productId) {
                // Get selected variant ID
                const selectedVariant = document.querySelector('.variant-item.active');
                let variantId = null;
                if (selectedVariant) {
                    variantId = selectedVariant.getAttribute('data-variant-id');
                }
                
                alert('Add to cart functionality will be implemented in the next version!');
                console.log('Product ID:', productId, 'Variant ID:', variantId);
                // TODO: Implement add to cart with selected variant
            }
        </script>
    </body>
</html>