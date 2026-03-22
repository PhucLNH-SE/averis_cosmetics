<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold mb-0">Product Management</h3>
            <p class="text-muted small mb-0">Manage your cosmetics inventory</p>
        </div>
        <a class="btn btn-outline-secondary px-3" href="${pageContext.request.contextPath}/admin/panel?view=dashboard">
            Back to Dashboard
        </a>
    </div>

    <c:if test="${not empty successMsg}">
        <c:set var="popupMessage" scope="request" value="${successMsg}" />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>

    <c:if test="${not empty errorMsg}">
        <c:set var="popupMessage" scope="request" value="${errorMsg}" />
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
                <form action="${pageContext.request.contextPath}/admin/manage-product" method="get" class="product-search-form">
                    <div class="product-filter-grid">
                        <div>
                            <label class="form-label fw-semibold mb-2" for="productSearchKeyword">Keyword</label>
                            <div class="product-search-form__input-wrap">
                                <i class="fas fa-search product-search-form__icon"></i>
                                <input
                                    id="productSearchKeyword"
                                    type="text"
                                    name="keyword"
                                    class="form-control product-search-form__input"
                                    placeholder="Search by product ID, name, brand, category..."
                                    value="<c:out value='${searchKeyword}'/>">
                            </div>
                        </div>
                        <div>
                            <label class="form-label fw-semibold mb-2" for="productBrandFilter">Brand</label>
                            <select id="productBrandFilter" name="brandId" class="form-select product-filter-select">
                                <option value="">All brands</option>
                                <c:forEach items="${listB}" var="b">
                                    <option value="${b.brandId}" ${selectedBrandId == b.brandId ? 'selected' : ''}>${b.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <label class="form-label fw-semibold mb-2" for="productCategoryFilter">Category</label>
                            <select id="productCategoryFilter" name="categoryId" class="form-select product-filter-select">
                                <option value="">All categories</option>
                                <c:forEach items="${listC}" var="c">
                                    <option value="${c.categoryId}" ${selectedCategoryId == c.categoryId ? 'selected' : ''}>${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <label class="form-label fw-semibold mb-2" for="productStatusFilter">Status</label>
                            <select id="productStatusFilter" name="status" class="form-select product-filter-select">
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
                        <a class="btn btn-outline-secondary px-4" href="${pageContext.request.contextPath}/admin/manage-product">
                            Reset
                        </a>
                    </div>
                    <p class="text-muted small mb-0 mt-2">
                        Showing ${resultCount} product(s)<c:if test="${not empty searchKeyword}"> for "<c:out value="${searchKeyword}"/>"</c:if>
                    </p>
                </form>

                <div class="product-toolbar__actions">
                    <button class="btn btn-success px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                        <i class="fas fa-plus-circle me-1"></i> Add Product
                    </button>
                </div>
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
                        <th class="text-center">Import Price</th>
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
                            
                            <td>
                                <c:choose>
                                    <c:when test="${empty p.variants}">
                                        <span class="text-muted small text-center d-block">None</span>
                                    </c:when>
                                    <c:otherwise>
                                        <ul class="list-unstyled mb-0 small">
                                            <c:forEach items="${p.variants}" var="variant">
                                                <li class="mb-1">
                                                    <span class="fw-bold">${variant.variantName}</span> - 
                                                    <span class="text-success"><fmt:formatNumber value="${variant.importPrice}" pattern="#,##0"/> VND</span>
                                                </li>
                                            </c:forEach>
                                        </ul>
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
                                <button class="btn btn-info btn-sm px-3 text-white me-1"
                                        data-id="${p.productId}"
                                        data-name="<c:out value='${p.name}' />"
                                        onclick="openVariantModal(this)">
                                    <i class="fas fa-tags me-1"></i> Variants
                                </button>

                                <button class="btn btn-primary btn-sm px-3 me-1"
                                        data-id="${p.productId}"
                                        data-name="<c:out value='${p.name}' />"
                                        data-brand="${p.brand.brandId}"
                                        data-category="${p.category.categoryId}"
                                        data-status="${p.status}"
                                        data-desc="<c:out value='${p.description}' />"
                                        data-image="${not empty p.mainImage ? p.mainImage : 'Logo.png'}"
                                        onclick="openEditModal(this)">
                                    <i class="fas fa-edit me-1"></i> Detail
                                </button>

                                <c:choose>
                                    <c:when test="${p.status}">
                                        <button class="btn btn-danger btn-sm px-3"
                                                data-id="${p.productId}"
                                                data-name="<c:out value='${p.name}' />"
                                                onclick="openHideModal(this)">
                                            <i class="fas fa-eye-slash me-1"></i> Hide
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-success btn-sm px-3"
                                                data-id="${p.productId}"
                                                data-name="<c:out value='${p.name}' />"
                                                onclick="openShowModal(this)">
                                            <i class="fas fa-eye me-1"></i> Show
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty listP}">
                        <tr>
                            <td colspan="9" class="text-center py-5 text-muted">
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

<div id="all-variants-storage" class="d-none">
    <c:forEach items="${listP}" var="p">
        <div id="variants-data-${p.productId}">
            <c:choose>
                <c:when test="${empty p.variants}">
                    <div class="alert alert-warning py-3 text-center mb-0">
                        <i class="fas fa-exclamation-circle me-1"></i> No variants.
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${p.variants}" var="v">
                        <div class="variant-item">
                            <form action="${pageContext.request.contextPath}/admin/manage-variant" method="post" class="variant-item__form">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="variantId" value="${v.variantId}">
                                <input type="hidden" name="productId" value="${p.productId}">
                                <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                                <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                                <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                                <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">

                                <div class="variant-item__field variant-item__field--name">
                                    <label class="small text-muted mb-1 fw-bold">Variant Name</label>
                                    <input type="text" name="variantName" class="form-control form-control-sm text-primary fw-bold" value="${v.variantName}" required>
                                </div>
                                <div class="variant-item__field variant-item__field--price">
                                    <label class="small text-muted mb-1 fw-bold">Price (VND)</label>
                                    <input type="number" step="0.01" name="price" class="form-control form-control-sm text-danger fw-bold" value="${v.price}" required>
                                </div>
                                <div class="variant-item__field variant-item__field--stock">
                                    <label class="small text-muted mb-1 fw-bold text-success">Stock</label>
                                    <input type="number" min="0" name="stock" class="form-control form-control-sm text-center fw-bold text-success" value="${v.stock}" required>
                                </div>

                                <button type="submit" class="variant-action-btn variant-action-btn--save" title="Save changes">
                                    <i class="fas fa-save me-1"></i> Save
                                </button>
                            </form>

                            <form action="${pageContext.request.contextPath}/admin/manage-variant" method="post" class="variant-item__delete-form" onsubmit="return confirm('Do you want to delete this variant?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="variantId" value="${v.variantId}">
                                <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                                <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                                <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                                <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">
                                <button type="submit" class="variant-action-btn variant-action-btn--delete" title="Delete variant">
                                    <i class="fas fa-trash me-1"></i> Delete
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>
</div>

<div class="modal fade" id="variantModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg"> 
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title fw-bold"><i class="fas fa-tags me-2"></i>Type: <span id="varModalProductName"></span></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body bg-light p-4">
                <div class="card shadow-sm mb-4 border-0">
                    <div class="card-body p-3 bg-white rounded">
                        <h6 class="fw-bold mb-3 text-primary border-bottom pb-2">Add new variant</h6>
                        <form action="${pageContext.request.contextPath}/admin/manage-variant" method="post" class="row g-2 align-items-end">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" id="addVarProductId">
                            <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                            <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                            <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                            <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">
                            
                            <div class="col-md-5">
                                <label class="small text-muted mb-1 fw-bold">Variant Name</label>
                                <input type="text" name="variantName" class="form-control form-control-sm" placeholder="e.g., 50ml" required>
                            </div>
                            <div class="col-md-4">
                                <label class="small text-muted mb-1 fw-bold">Price</label>
                                <input type="number" step="0.01" name="price" class="form-control form-control-sm" placeholder="Price" required>
                            </div>
                            <div class="col-md-2">
                                <label class="small text-muted mb-1 fw-bold text-success">Stock</label>
                                <input type="number" min="0" name="stock" class="form-control form-control-sm" placeholder="0" required>
                            </div>
                            <div class="col-md-1">
                                <button type="submit" class="btn btn-primary btn-sm w-100"><i class="fas fa-plus"></i> Add</button>
                            </div>
                        </form>
                    </div>
                </div>

                    <h6 class="fw-bold mb-3 text-secondary">Variant List</h6>
                <div id="variantListContainer" class="bg-white p-3 rounded shadow-sm"></div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/manage-product" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title fw-bold">Add New Product</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Product Image</label>
                        <input type="file" name="image" class="form-control" accept="image/*" required onchange="previewImage(this, 'addPreview')">
                        <img id="addPreview" class="img-preview mt-2 rounded border" src="#">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Product Name</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>

                    <div class="row bg-light p-3 rounded border mb-3 mx-0">
                        <h6 class="fw-bold text-secondary mb-2 border-bottom pb-1">Pricing & Inventory (Default Variant)</h6>
                        <input type="hidden" name="stock" value="0">

                        <div class="col-md-12 mb-2">
                            <label class="form-label text-danger fw-bold small">Sale Price</label>
                            <input type="number" step="0.01" name="price" class="form-control" required placeholder="e.g., 299000">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Description</label>
                        <textarea name="description" class="form-control" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Brand</label>
                            <select name="brandId" class="form-select">
                                <c:forEach items="${listB}" var="b">
                                    <option value="${b.brandId}">${b.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Category</label>
                            <select name="categoryId" class="form-select">
                                <c:forEach items="${listC}" var="c">
                                    <option value="${c.categoryId}">${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-check form-switch mt-2">
                        <input class="form-check-input" type="checkbox" name="status" value="true" checked id="addStatus">
                        <label class="form-check-label fw-bold text-success" for="addStatus">Active Status</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success px-4"><i class="fas fa-check me-1"></i> Save Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/manage-product" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="productId" id="editId">
                <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold">Edit Product Info</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3 text-center">
                         <img id="editImageDisplay" src="" width="100" height="100" class="rounded border mb-2 product-image-current">
                         <p class="text-muted small">Current Image</p>
                         <input type="file" name="image" class="form-control mt-2" accept="image/*" onchange="previewImage(this, 'editImageDisplay')">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Product Name</label>
                        <input type="text" name="name" id="editName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea name="description" id="editDesc" class="form-control" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Brand</label>
                            <select name="brandId" id="editBrand" class="form-select">
                                <c:forEach items="${listB}" var="b"><option value="${b.brandId}">${b.name}</option></c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Category</label>
                            <select name="categoryId" id="editCategory" class="form-select">
                                <c:forEach items="${listC}" var="c"><option value="${c.categoryId}">${c.name}</option></c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-check form-switch mt-2">
                        <input class="form-check-input" type="checkbox" name="status" value="true" id="editStatus">
                        <label class="form-check-label fw-bold" for="editStatus">Active</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary px-4">Update Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="hideModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content text-center border-0 shadow">
            <form action="${pageContext.request.contextPath}/admin/manage-product" method="post">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="productId" id="hideId">
                <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">
                <div class="modal-body p-4">
                    <i class="fas fa-eye-slash text-danger mb-3 product-modal-icon"></i>
                    <h5 class="fw-bold mb-3">Hide Product?</h5>
                    <p class="text-muted small mb-4">Are you sure you want to hide <strong id="hideName" class="text-dark"></strong>?</p>
                    <div class="d-flex justify-content-center gap-2">
                        <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger px-4">Yes, Hide</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="showModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content text-center border-0 shadow">
            <form action="${pageContext.request.contextPath}/admin/manage-product" method="post">
                <input type="hidden" name="action" value="show">
                <input type="hidden" name="productId" id="showId">
                <input type="hidden" name="returnKeyword" value="<c:out value='${searchKeyword}'/>">
                <input type="hidden" name="returnBrandId" value="<c:out value='${selectedBrandId}'/>">
                <input type="hidden" name="returnCategoryId" value="<c:out value='${selectedCategoryId}'/>">
                <input type="hidden" name="returnStatus" value="<c:out value='${selectedStatus}'/>">
                <div class="modal-body p-4">
                    <i class="fas fa-eye text-success mb-3 product-modal-icon"></i>
                    <h5 class="fw-bold mb-3">Show Product?</h5>
                    <p class="text-muted small mb-4">Do you want to make <strong id="showName" class="text-dark"></strong> visible?</p>
                    <div class="d-flex justify-content-center gap-2">
                        <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success px-4">Yes, Show</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openVariantModal(button) {
        const productId = button.getAttribute('data-id');
        const productName = button.getAttribute('data-name');
        document.getElementById('varModalProductName').innerText = productName;
        document.getElementById('addVarProductId').value = productId;
        document.getElementById('variantListContainer').innerHTML = document.getElementById('variants-data-' + productId).innerHTML;
        bootstrap.Modal.getOrCreateInstance(document.getElementById('variantModal')).show();
    }

    function previewImage(input, previewId) {
        const preview = document.getElementById(previewId);
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                preview.src = e.target.result;
                preview.style.display = 'inline-block';
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    function openEditModal(button) {
        document.getElementById('editId').value = button.getAttribute('data-id');
        document.getElementById('editName').value = button.getAttribute('data-name');
        document.getElementById('editBrand').value = button.getAttribute('data-brand');
        document.getElementById('editCategory').value = button.getAttribute('data-category');
        document.getElementById('editStatus').checked = button.getAttribute('data-status') === 'true';
        document.getElementById('editDesc').value = button.getAttribute('data-desc');
        document.getElementById('editImageDisplay').src = '${pageContext.request.contextPath}/assets/img/' + button.getAttribute('data-image');
        bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
    }

    function openHideModal(button) {
        document.getElementById('hideId').value = button.getAttribute('data-id');
        document.getElementById('hideName').innerText = button.getAttribute('data-name');
        bootstrap.Modal.getOrCreateInstance(document.getElementById('hideModal')).show();
    }

    function openShowModal(button) {
        document.getElementById('showId').value = button.getAttribute('data-id');
        document.getElementById('showName').innerText = button.getAttribute('data-name');
        bootstrap.Modal.getOrCreateInstance(document.getElementById('showModal')).show();
    }
</script>



