<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section admin-page admin-page--product">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold mb-0">Import Products</h3>
            <p class="text-muted small mb-0">Select a product and import its variants</p>
        </div>
        <a class="btn btn-outline-secondary px-3" href="${pageContext.request.contextPath}/admin/import-product?action=history">
            <i class="bi bi-arrow-left"></i> Back to History
        </a>
    </div>

    <c:if test="${not empty error}">
        <c:set var="popupMessage" scope="request" value="${error}" />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body p-4 pb-0">
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
        <div class="card-body p-4">
            <div class="product-toolbar mb-4">
                <form action="${pageContext.request.contextPath}/admin/import-product" method="get" class="product-search-form">
                    <input type="hidden" name="action" value="importproduct">
                    <div class="product-filter-grid">
                        <div>
                            <label class="form-label fw-semibold mb-2" for="importSearchKeyword">Keyword</label>
                            <div class="product-search-form__input-wrap">
                                <i class="fas fa-search product-search-form__icon"></i>
                                <input
                                    id="importSearchKeyword"
                                    type="text"
                                    name="keyword"
                                    class="form-control product-search-form__input"
                                    value="<c:out value='${searchKeyword}'/>">
                            </div>
                        </div>
                        <div>
                            <label class="form-label fw-semibold mb-2" for="importBrandFilter">Brand</label>
                            <select id="importBrandFilter" name="brandId" class="form-select product-filter-select">
                                <option value="">All brands</option>
                                <c:forEach items="${listB}" var="b">
                                    <option value="${b.brandId}" ${selectedBrandId == b.brandId ? 'selected' : ''}>${b.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <label class="form-label fw-semibold mb-2" for="importCategoryFilter">Category</label>
                            <select id="importCategoryFilter" name="categoryId" class="form-select product-filter-select">
                                <option value="">All categories</option>
                                <c:forEach items="${listC}" var="c">
                                    <option value="${c.categoryId}" ${selectedCategoryId == c.categoryId ? 'selected' : ''}>${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <label class="form-label fw-semibold mb-2" for="importStatusFilter">Status</label>
                            <select id="importStatusFilter" name="status" class="form-select product-filter-select">
                                <option value="">All status</option>
                                <option value="active" ${selectedStatus == 'active' ? 'selected' : ''}>Active</option>
                                <option value="inactive" ${selectedStatus == 'inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                    </div>
                    <div class="product-search-form__row">
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="fas fa-filter me-1"></i> Search
                        </button>
                        <a class="btn btn-outline-secondary px-4" href="${pageContext.request.contextPath}/admin/import-product?action=importproduct">
                            Reset
                        </a>
                    </div>
                    <p class="text-muted small mb-0 mt-2">
                        Showing ${resultCount} product(s)<c:if test="${not empty searchKeyword}"> for "<c:out value="${searchKeyword}"/>"</c:if>
                    </p>
                </form>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Image</th>
                            <th>Product Name</th>
                            <th>Category</th>
                            <th>Price Range</th>
                            <th class="text-center">Total Stock</th>
                            <th>Status</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listP}" var="p">
                            <tr>
                                <td class="fw-bold">${p.productId}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.mainImage}">
                                            <img src="${pageContext.request.contextPath}/assets/img/${p.mainImage}"
                                                 class="product-img-td product-img-td--cover border" width="50" height="50"
                                                 onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/assets/img/Logo.png" class="product-img-td border" width="50" height="50">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${p.name}</td>
                                <td>${p.category.name}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.price == 0}">
                                            <span class="text-muted small">No variants</span>
                                        </c:when>
                                        <c:when test="${p.price == p.maxPrice}">
                                            <span class="text-primary fw-bold">
                                                <fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-primary fw-bold">
                                                <fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND -
                                                <fmt:formatNumber value="${p.maxPrice}" pattern="#,##0"/> VND
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <span class="fw-bold ${p.totalStock > 0 ? 'text-dark' : 'text-danger'}">
                                        ${p.totalStock}
                                    </span>
                                </td>
                                <td>
                                    <span class="${p.status ? 'text-success' : 'text-danger'} fw-bold">
                                        <i class="fas ${p.status ? 'fa-check-circle' : 'fa-times-circle'} me-1"></i>${p.status ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td class="text-center">
                                    <button class="btn btn-primary btn-sm px-3"
                                            data-id="${p.productId}"
                                            data-name="<c:out value='${p.name}' />"
                                            data-brand-id="${p.brand.brandId}"
                                            onclick="openImportModal(this)">
                                        <i class="fas fa-truck-loading me-1"></i> Import
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty listP}">
                            <tr>
                                <td colspan="8" class="text-center py-5 text-muted">
                                    No products matched your search.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<div id="import-variants-storage" class="d-none">
    <c:forEach items="${listP}" var="p">
        <div id="import-variants-${p.productId}">
            <c:choose>
                <c:when test="${empty p.variants}">
                    <div class="alert alert-warning py-3 text-center mb-0">
                        <i class="fas fa-exclamation-circle me-1"></i> No variants.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-bordered align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Variant</th>
                                    <th class="text-center">Stock</th>
                                    <th class="text-center">Import Price</th>
                                    <th class="text-center">Quantity</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${p.variants}" var="v">
                                    <tr>
                                        <td class="fw-semibold">${v.variantName}</td>
                                        <td class="text-center">${v.stock}</td>
                                        <td class="text-center">
                                            <input type="number" class="form-control form-control-sm text-end"
                                                   name="price" placeholder="0" step="1000" min="0">
                                        </td>
                                        <td class="text-center">
                                            <input type="number" class="form-control form-control-sm text-end"
                                                   name="quantity" placeholder="0" min="0">
                                            <input type="hidden" name="variantId" value="${v.variantId}">
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>
</div>

<div class="modal fade" id="importModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/import-product" method="post">
                <input type="hidden" name="action" value="importproduct">
                <input type="hidden" name="brandId" id="importBrandId">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold">
                        <i class="fas fa-truck-loading me-2"></i>Import Variants: <span id="importProductName"></span>
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body bg-light p-4">
                    <div id="importVariantList" class="bg-white p-3 rounded shadow-sm"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-primary px-4">
                        <i class="bi bi-check2-circle"></i> Create Import Order
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openImportModal(button) {
        const productId = button.getAttribute('data-id');
        const productName = button.getAttribute('data-name');
        const brandId = button.getAttribute('data-brand-id');

        document.getElementById('importProductName').innerText = productName;
        document.getElementById('importBrandId').value = brandId;

        const variantHtml = document.getElementById('import-variants-' + productId).innerHTML;
        document.getElementById('importVariantList').innerHTML = variantHtml;

        bootstrap.Modal.getOrCreateInstance(document.getElementById('importModal')).show();
    }
</script>
