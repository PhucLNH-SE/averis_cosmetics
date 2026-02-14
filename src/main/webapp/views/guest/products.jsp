<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Products - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    </head>
    <body>
        <%@include file="/assets/header.jsp" %>
        
        <div class="container">
            <h1 class="page-title">Product List</h1>
            
            <c:if test="${searchKeyword != null}">
                <div class="search-result-info">
                    Found ${products.size()} product(s) for "${searchKeyword}"
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${not empty products and products.size() > 0}">
                    <div class="products-grid">
                        <c:forEach items="${products}" var="product">
                            <a href="<%=request.getContextPath()%>/products?id=${product.productId}" 
                               class="product-card">
                                <c:choose>
                                    <c:when test="${not empty product.mainImage}">
                                        <img class="product-image" 
                                             src="<%=request.getContextPath()%>/assets/img/${product.mainImage}" 
                                             alt="${product.name}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-image default-image"></div>
                                    </c:otherwise>
                                </c:choose>
                                
                                <div class="product-info">
                                    <div class="product-details-container">
                                        <div class="product-name">${product.name}</div>
                                        <div class="product-brand">${product.brand.name}</div>
                                        <div class="product-category">${product.category.name}</div>
                                        <div class="product-price">
                                            <c:choose>
                                                <c:when test="${not empty product.variants}">
                                                    <fmt:formatNumber value="${product.variants[0].price}" type="currency" currencySymbol="$"/>
                                                </c:when>
                                                <c:otherwise>
                                                    Contact
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="view-detail-btn">View Details</div>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-products">
                        <h3>No products available</h3>
                        <p>Sorry, there are currently no products in the store.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
      <%@include file="/assets/footer.jsp" %> 
    </body>
</html>