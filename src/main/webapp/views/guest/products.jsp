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

        <div class="container products-list-container">
            <h1 class="page-title">Product List</h1>

            <c:if test="${searchKeyword != null}">
                <div class="search-tools">
                    <div class="search-result-info">
                        Found ${products.size()} product(s) for "${searchKeyword}"
                    </div>

                    <form class="filter-form" method="get" action="<%=request.getContextPath()%>/products">
                        <input type="hidden" name="keyword" value="${searchKeyword}">

                        <div class="filter-group">
                            <label for="brandFilter">Brand</label>
                            <select id="brandFilter" name="brand">
                                <option value="">All brands</option>
                                <c:forEach items="${availableBrands}" var="brandName">
                                    <option value="${brandName}" <c:if test="${filterBrand == brandName}">selected</c:if>>${brandName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="categoryFilter">Category</label>
                            <select id="categoryFilter" name="category">
                                <option value="">All categories</option>
                                <c:forEach items="${availableCategories}" var="categoryName">
                                    <option value="${categoryName}" <c:if test="${filterCategory == categoryName}">selected</c:if>>${categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="sortFilter">Sort</label>
                            <select id="sortFilter" name="sort">
                                <option value="" <c:if test="${empty sortBy}">selected</c:if>>Most relevant</option>
                                <option value="price_asc" <c:if test="${sortBy == 'price_asc'}">selected</c:if>>Price: Low to High</option>
                                <option value="price_desc" <c:if test="${sortBy == 'price_desc'}">selected</c:if>>Price: High to Low</option>
                                <option value="name_asc" <c:if test="${sortBy == 'name_asc'}">selected</c:if>>Name: A to Z</option>
                                <option value="name_desc" <c:if test="${sortBy == 'name_desc'}">selected</c:if>>Name: Z to A</option>
                            </select>
                        </div>

                        <div class="filter-actions">
                            <button type="submit" class="filter-btn">Apply Filters</button>
                            <a class="reset-btn" href="<%=request.getContextPath()%>/products?keyword=${searchKeyword}">Reset</a>
                        </div>
                    </form>
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
                                        <c:set var="imageFolder" value="${product.mainImage.contains('-') ? 'products/' : ''}" />
                                        <img class="product-image"
                                             src="<%=request.getContextPath()%>/assets/img/${imageFolder}${product.mainImage}"
                                             alt="${product.name}"
                                             onerror="this.src='<%=request.getContextPath()%>/assets/img/default-product.jpg';">
                                    </c:when>
                                    <c:otherwise>
                                        <img class="product-image" src="<%=request.getContextPath()%>/assets/img/default-product.jpg" alt="No image">
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
                        <h3>No products found</h3>
                        <p>Try a different keyword or adjust your filter options.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%@include file="/assets/footer.jsp" %>
    </body>
</html>
