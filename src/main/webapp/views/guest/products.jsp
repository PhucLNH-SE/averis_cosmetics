<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Products - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    </head>
    <body class="products-page">
        <jsp:include page="/assets/header.jsp" />

        <main class="catalog-shell">
            <section class="catalog-hero">
                <div>
                    <c:choose>
                        <c:when test="${isTopSalesLanding}">
                            <span class="catalog-pill">Top Sales</span>
                            <h1 class="catalog-title">Best-selling products customers love most</h1>
                            <p class="catalog-subtitle">
                                Explore our strongest sellers first, then discover a few extra curated picks
                                to round out the collection.
                            </p>
                        </c:when>
                        <c:otherwise>
                            <span class="catalog-pill">Curated Beauty Picks</span>
                            <h1 class="catalog-title">Find products that fit your routine</h1>
                            <p class="catalog-subtitle">
                                Explore skincare and makeup from trusted brands, compare categories quickly,
                                and move from discovery to checkout with a cleaner shopping flow.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <section class="catalog-layout">
                <aside class="catalog-filter-card">
                    <div class="catalog-filter-head">
                        <h2 class="catalog-filter-title">Refine results</h2>
                        <p class="catalog-filter-subtitle">Filter by brand, category, or your preferred ordering.</p>
                    </div>

                    <form class="catalog-filter-form" method="get" action="<%=request.getContextPath()%>/products">
                        <c:if test="${not empty searchKeyword}">
                            <input type="hidden" name="keyword" value="${searchKeyword}">
                        </c:if>

                        <div class="catalog-filter-group">
                            <label for="brandFilter">Brand</label>
                            <select id="brandFilter" name="brand">
                                <option value="">All brands</option>
                                <c:forEach items="${availableBrands}" var="brandName">
                                    <option value="${brandName}" <c:if test="${filterBrand == brandName}">selected</c:if>>${brandName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="catalog-filter-group">
                            <label for="categoryFilter">Category</label>
                            <select id="categoryFilter" name="category">
                                <option value="">All categories</option>
                                <c:forEach items="${availableCategories}" var="categoryName">
                                    <option value="${categoryName}" <c:if test="${filterCategory == categoryName}">selected</c:if>>${categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="catalog-filter-group">
                            <label for="sortFilter">Sort</label>
                            <select id="sortFilter" name="sort">
                                <option value="" <c:if test="${empty sortBy}">selected</c:if>>Most relevant</option>
                                <option value="price_asc" <c:if test="${sortBy == 'price_asc'}">selected</c:if>>Price: Low to High</option>
                                <option value="price_desc" <c:if test="${sortBy == 'price_desc'}">selected</c:if>>Price: High to Low</option>
                                <option value="name_asc" <c:if test="${sortBy == 'name_asc'}">selected</c:if>>Name: A to Z</option>
                                <option value="name_desc" <c:if test="${sortBy == 'name_desc'}">selected</c:if>>Name: Z to A</option>
                            </select>
                        </div>

                        <div class="catalog-filter-actions">
                            <button type="submit" class="catalog-filter-btn">Apply filters</button>
                            <c:choose>
                                <c:when test="${not empty searchKeyword}">
                                    <a class="catalog-reset-btn" href="<%=request.getContextPath()%>/products?keyword=${searchKeyword}">Reset</a>
                                </c:when>
                                <c:otherwise>
                                    <a class="catalog-reset-btn" href="<%=request.getContextPath()%>/products">Reset</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </form>
                </aside>

                <section class="catalog-results">
                    <c:choose>
                        <c:when test="${not empty products and products.size() > 0}">
                            <div class="catalog-grid">
                                <c:forEach items="${products}" var="product">
                                    <a href="<%=request.getContextPath()%>/products?id=${product.productId}" class="catalog-card">
                                        <div class="catalog-image-wrap">
                                            <c:choose>
                                                <c:when test="${not empty product.mainImage}">
                                                    <img class="catalog-image"
                                                         src="<%=request.getContextPath()%>/assets/img/${product.mainImage}"
                                                         alt="${product.name}"
                                                         onerror="this.src='<%=request.getContextPath()%>/assets/img/Logo.png';">
                                                </c:when>
                                                <c:otherwise>
                                                    <img class="catalog-image" src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="No image">
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="catalog-card-body">
                                            <div class="catalog-brand-row">
                                                <span class="catalog-brand">${product.brand.name}</span>
                                                <span class="catalog-category">${product.category.name}</span>
                                            </div>

                                            <h3 class="catalog-name">${product.name}</h3>

                                            <div class="catalog-price-row">
                                                <div class="catalog-price">
                                                    <c:choose>
                                                        <c:when test="${product.price > 0}">
                                                            <c:choose>
                                                                <c:when test="${product.maxPrice > product.price}">
                                                                    <fmt:formatNumber value="${product.price}" pattern="#,##0"/>
                                                                    -
                                                                    <fmt:formatNumber value="${product.maxPrice}" pattern="#,##0"/> VND
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <fmt:formatNumber value="${product.price}" pattern="#,##0"/> VND
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Contact
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <span class="catalog-link">View details</span>
                                            </div>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="catalog-empty-state">
                                <h3>No products found</h3>
                                <p>Try a different keyword or adjust your filter options.</p>
                                <a href="<%=request.getContextPath()%>/products" class="catalog-filter-btn">Browse all products</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </section>
        </main>

        <jsp:include page="/assets/footer.jsp" />
    </body>
</html>

