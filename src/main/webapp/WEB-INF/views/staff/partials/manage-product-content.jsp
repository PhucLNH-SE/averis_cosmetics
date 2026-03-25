<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setLocale value="vi_VN" />

<section class="admin-content__section admin-page admin-page--product staff-page staff-page--product staff-product-page">
    <div class="card shadow-sm staff-product-page__hero">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-start gap-3 flex-wrap mb-4">
                <div>
                    <h3 class="fw-bold mb-1">View Product List</h3>
                    <p class="text-muted mb-0">Staff can review product information here without edit permissions.</p>
                </div>
                <a class="btn btn-outline-secondary px-3" href="${pageContext.request.contextPath}/staff/dashboard">
                    <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
                </a>
            </div>

            <div class="product-overview">
                <div class="product-overview__card product-overview__card--results">
                    <span class="product-overview__label">Results</span>
                    <strong class="product-overview__value">${resultCount}</strong>
                </div>
                <div class="product-overview__card product-overview__card--active">
                    <span class="product-overview__label">Active</span>
                    <strong class="product-overview__value">${activeCount}</strong>
                </div>
                <div class="product-overview__card product-overview__card--inactive">
                    <span class="product-overview__label">Inactive</span>
                    <strong class="product-overview__value">${inactiveCount}</strong>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/staff/manage-product" method="get" class="product-search-form">
                <div class="product-filter-grid">
                    <div>
                        <label class="form-label fw-semibold mb-2" for="staffProductKeyword">Keyword</label>
                        <div class="product-search-form__input-wrap">
                            <i class="bi bi-search product-search-form__icon"></i>
                            <input
                                id="staffProductKeyword"
                                type="text"
                                name="keyword"
                                class="form-control product-search-form__input"
                                value="<c:out value='${searchKeyword}'/>"
                                autocomplete="off">
                        </div>
                    </div>
                    <div>
                        <label class="form-label fw-semibold mb-2" for="staffProductBrand">Brand</label>
                        <select id="staffProductBrand" name="brandId" class="form-select product-filter-select">
                            <option value="">All brands</option>
                            <c:forEach items="${listB}" var="brand">
                                <option value="${brand.brandId}" <c:if test="${selectedBrandId == brand.brandId}">selected</c:if>>
                                    ${brand.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label class="form-label fw-semibold mb-2" for="staffProductCategory">Category</label>
                        <select id="staffProductCategory" name="categoryId" class="form-select product-filter-select">
                            <option value="">All categories</option>
                            <c:forEach items="${listC}" var="category">
                                <option value="${category.categoryId}" <c:if test="${selectedCategoryId == category.categoryId}">selected</c:if>>
                                    ${category.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label class="form-label fw-semibold mb-2" for="staffProductStatus">Status</label>
                        <select id="staffProductStatus" name="status" class="form-select product-filter-select">
                            <option value="">All status</option>
                            <option value="active" <c:if test="${selectedStatus == 'active'}">selected</c:if>>Active</option>
                            <option value="inactive" <c:if test="${selectedStatus == 'inactive'}">selected</c:if>>Inactive</option>
                        </select>
                    </div>
                </div>

                <div class="product-search-form__row">
                    <button type="submit" class="btn btn-primary px-4">
                        <i class="bi bi-funnel me-1"></i>Apply filters
                    </button>
                    <a class="btn btn-outline-secondary px-4" href="${pageContext.request.contextPath}/staff/manage-product">
                        Reset
                    </a>
                    <p class="text-muted mb-0 staff-product-page__result-text">
                        Showing ${resultCount} of ${totalProductCount} products
                    </p>
                </div>
            </form>
        </div>

        <div class="card-body pt-0 px-4 pb-4">
            <div class="table-responsive">
                <table class="table table-hover align-middle staff-product-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Image</th>
                            <th>Product</th>
                            <th>Brand</th>
                            <th>Category</th>
                            <th>Price Range</th>
                            <th>Variants</th>
                            <th>Status</th>
                            <th class="text-center">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listP}" var="product">
                            <c:url var="staffProductDetailUrl" value="/staff/manage-product">
                                <c:param name="action" value="detail" />
                                <c:param name="id" value="${product.productId}" />
                                <c:param name="keyword" value="${searchKeyword}" />
                                <c:param name="brandId" value="${selectedBrandId}" />
                                <c:param name="categoryId" value="${selectedCategoryId}" />
                                <c:param name="status" value="${selectedStatus}" />
                            </c:url>
                            <tr>
                                <td class="fw-semibold">#${product.productId}</td>
                                <td>
                                    <div class="staff-product-table__image-wrap">
                                        <c:choose>
                                            <c:when test="${not empty product.mainImage}">
                                                <img
                                                    src="${pageContext.request.contextPath}/assets/img/${product.mainImage}"
                                                    alt="${product.name}"
                                                    class="staff-product-table__image">
                                            </c:when>
                                            <c:otherwise>
                                                <img
                                                    src="${pageContext.request.contextPath}/assets/img/Logo.png"
                                                    alt="${product.name}"
                                                    class="staff-product-table__image">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                                <td>
                                    <div class="staff-product-table__name"><c:out value="${product.name}" /></div>
                                </td>
                                <td><c:out value="${product.brand.name}" /></td>
                                <td><c:out value="${product.category.name}" /></td>
                                <td class="staff-product-table__price">
                                    <c:choose>
                                        <c:when test="${empty product.variants}">
                                            <span class="text-muted">No variants</span>
                                        </c:when>
                                        <c:when test="${product.price == product.maxPrice}">
                                            <fmt:formatNumber value="${product.price}" pattern="#,##0" /> VND
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${product.price}" pattern="#,##0" />
                                            -
                                            <fmt:formatNumber value="${product.maxPrice}" pattern="#,##0" /> VND
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="staff-product-table__variant-count">
                                        ${fn:length(product.variants)} variant(s)
                                    </span>
                                </td>
                                <td>
                                    <span class="staff-product-table__status ${product.status ? 'staff-product-table__status--active' : 'staff-product-table__status--inactive'}">
                                        <i class="bi ${product.status ? 'bi-check-circle-fill' : 'bi-x-circle-fill'}"></i>
                                        ${product.status ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td class="text-center">
                                    <a
                                        class="btn btn-primary btn-sm staff-product-table__detail-btn"
                                        href="${staffProductDetailUrl}">
                                        <i class="bi bi-eye me-1"></i>View Detail
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty listP}">
                            <tr>
                                <td colspan="9" class="text-center py-5 text-muted">
                                    No products matched your filters.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<div class="modal fade" id="staffProductDetailModal" tabindex="-1" aria-labelledby="staffProductDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content staff-product-detail-modal">
            <div class="modal-header staff-product-detail-modal__header">
                <h5 class="modal-title fw-bold" id="staffProductDetailModalLabel">
                    <c:out value="${not empty selectedDetailProduct ? selectedDetailProduct.name : 'Product Detail'}" />
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body staff-product-detail-modal__body" id="staffProductDetailModalBody">
                <c:choose>
                    <c:when test="${not empty selectedDetailProduct}">
                        <c:set var="detailProduct" value="${selectedDetailProduct}" scope="request" />
                        <jsp:include page="/WEB-INF/views/common/product-detail-content.jsp" />
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted mb-0">Select a product to view more information.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<c:if test="${detailMode and not empty selectedDetailProduct}">
<script>
    window.addEventListener('load', function () {
        bootstrap.Modal.getOrCreateInstance(document.getElementById('staffProductDetailModal')).show();
    });
</script>
</c:if>

