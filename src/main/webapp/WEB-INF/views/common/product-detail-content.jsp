<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
    <c:when test="${empty detailProduct}">
        <p class="text-muted mb-0">Product detail is unavailable.</p>
    </c:when>
    <c:otherwise>
        <div class="staff-product-detail">
            <div class="staff-product-detail__hero">
                <div class="staff-product-detail__image-box">
                    <c:choose>
                        <c:when test="${not empty detailProduct.mainImage}">
                            <img
                                src="${pageContext.request.contextPath}/assets/img/${detailProduct.mainImage}"
                                alt="${detailProduct.name}"
                                class="staff-product-detail__image">
                        </c:when>
                        <c:otherwise>
                            <img
                                src="${pageContext.request.contextPath}/assets/img/Logo.png"
                                alt="${detailProduct.name}"
                                class="staff-product-detail__image">
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="staff-product-detail__summary">
                    <div class="staff-product-detail__meta-grid">
                        <div class="staff-product-detail__meta-item">
                            <span class="staff-product-detail__meta-label">Product ID</span>
                            <strong>${detailProduct.productId}</strong>
                        </div>
                        <div class="staff-product-detail__meta-item">
                            <span class="staff-product-detail__meta-label">Brand</span>
                            <strong><c:out value="${detailProduct.brand.name}" /></strong>
                        </div>
                        <div class="staff-product-detail__meta-item">
                            <span class="staff-product-detail__meta-label">Category</span>
                            <strong><c:out value="${detailProduct.category.name}" /></strong>
                        </div>
                        <div class="staff-product-detail__meta-item">
                            <span class="staff-product-detail__meta-label">Status</span>
                            <strong>${detailProduct.status ? 'Active' : 'Inactive'}</strong>
                        </div>
                        <div class="staff-product-detail__meta-item">
                            <span class="staff-product-detail__meta-label">Price Range</span>
                            <strong>
                                <c:choose>
                                    <c:when test="${empty detailProduct.variants}">
                                        No variants
                                    </c:when>
                                    <c:when test="${detailProduct.price == detailProduct.maxPrice}">
                                        <fmt:formatNumber value="${detailProduct.price}" pattern="#,##0" /> VND
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${detailProduct.price}" pattern="#,##0" />
                                        -
                                        <fmt:formatNumber value="${detailProduct.maxPrice}" pattern="#,##0" /> VND
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                        <div class="staff-product-detail__meta-item">
                            <span class="staff-product-detail__meta-label">Variants</span>
                            <strong>${fn:length(detailProduct.variants)} variant(s)</strong>
                        </div>
                    </div>
                </div>
            </div>

            <div class="staff-product-detail__section">
                <h6 class="staff-product-detail__section-title">Description</h6>
                <c:choose>
                    <c:when test="${not empty detailProduct.description}">
                        <p class="staff-product-detail__description"><c:out value="${detailProduct.description}" /></p>
                    </c:when>
                    <c:otherwise>
                        <p class="staff-product-detail__description text-muted">No description available.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="staff-product-detail__section">
                <h6 class="staff-product-detail__section-title">Variant Information</h6>
                <c:choose>
                    <c:when test="${empty detailProduct.variants}">
                        <div class="staff-product-detail__empty">
                            No active variants available for this product.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-sm align-middle staff-product-detail__variant-table mb-0">
                                <thead>
                                    <tr>
                                        <th>Variant</th>
                                        <th>Sale Price</th>
                                        <th>Stock</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${detailProduct.variants}" var="variant">
                                        <tr>
                                            <td><c:out value="${variant.variantName}" /></td>
                                            <td><fmt:formatNumber value="${variant.price}" pattern="#,##0" /> VND</td>
                                            <td>${variant.stock}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:otherwise>
</c:choose>
