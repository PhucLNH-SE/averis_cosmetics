<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Preview - Averis Cosmetics</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body>
    <jsp:include page="/assets/header.jsp" />

    <main class="catalog-shell">
        <section class="catalog-hero">
            <div>
                <span class="catalog-pill">Preview</span>
                <h1 class="catalog-title">Product preview page</h1>
                <p class="catalog-subtitle">This page now uses the same shared header and shared stylesheet as the rest of the customer-facing pages.</p>
            </div>
        </section>

        <section class="catalog-grid">
            <c:forEach var="p" items="${products}">
                <article class="catalog-card">
                    <div class="catalog-card-image">
                        <img src="${p.mainImage}" alt="${p.name}" width="120"/>
                    </div>
                    <div class="catalog-card-body">
                        <h3 class="catalog-card-title">${p.name}</h3>
                        <p class="catalog-card-meta">${p.brand.name} - ${p.category.name}</p>
                    </div>
                </article>
            </c:forEach>
        </section>
    </main>

    <jsp:include page="/assets/footer.jsp" />
</body>
</html>
