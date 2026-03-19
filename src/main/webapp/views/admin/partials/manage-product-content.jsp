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
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body p-4">
            <div class="d-flex justify-content-end mb-3">
                <button class="btn btn-success px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                    <i class="fas fa-plus-circle me-1"></i> Add Product
                </button>
            </div>

            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Image</th>
                        <th>Product Name</th>
                        <th>Category</th>
                        <th>Price Range</th>
                        <th class="text-center">Price Stock (Giá nhập)</th>
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
                                             class="product-img-td border" width="50" height="50" style="object-fit:cover;"
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
                                        <span class="text-muted small text-center d-block">Không có</span>
                                    </c:when>
                                    <c:otherwise>
                                        <ul class="list-unstyled mb-0 small">
                                            <c:forEach items="${p.variants}" var="variant">
                                                <li class="mb-1">
                                                    <span class="fw-bold">${variant.variantName}</span> - 
                                                    <span class="text-success"><fmt:formatNumber value="${variant.importPrice}" pattern="#,##0"/> ₫</span>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:otherwise>
                                </c:choose>
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
                </tbody>
            </table>
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
                        <div class="d-flex gap-2 mb-2 p-2 border-bottom align-items-end bg-white rounded">
                            <form action="${pageContext.request.contextPath}/admin/manage-variant" method="post" class="d-flex gap-2 flex-grow-1 align-items-end mb-0">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="variantId" value="${v.variantId}">
                                <input type="hidden" name="productId" value="${p.productId}">
                                <input type="hidden" name="stock" value="${v.stock}">

                                <div class="flex-grow-1">
                                    <label class="small text-muted mb-1 fw-bold">Variant Name</label>
                                    <input type="text" name="variantName" class="form-control form-control-sm text-primary fw-bold" value="${v.variantName}" required>
                                </div>
                                <div class="flex-grow-1" style="max-width: 150px;">
                                    <label class="small text-muted mb-1 fw-bold">Price (VND)</label>
                                    <input type="number" step="0.01" name="price" class="form-control form-control-sm text-danger fw-bold" value="${v.price}" required>
                                </div>
                                <div class="flex-grow-1" style="max-width: 80px;">
                                    <label class="small text-muted mb-1 fw-bold text-success">Stock</label>
                                    <input type="text" class="form-control form-control-sm text-center fw-bold bg-light text-success" value="${v.stock}" readonly title="Stock is read-only">
                                </div>

                                <button type="submit" class="btn btn-success btn-sm px-3 mb-1" title="Save">
                                    <i class="fas fa-save"></i>
                                </button>
                            </form>

                            <form action="${pageContext.request.contextPath}/admin/manage-variant" method="post" class="mb-1" onsubmit="return confirm('Do you want delete?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="variantId" value="${v.variantId}">
                                <button type="submit" class="btn btn-danger btn-sm px-3" title="Delete">
                                    <i class="fas fa-trash"></i>
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
                        <h6 class="fw-bold mb-3 text-primary border-bottom pb-2">Add new type</h6>
                        <form action="${pageContext.request.contextPath}/admin/manage-variant" method="post" class="row g-2 align-items-end">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" id="addVarProductId">
                            <input type="hidden" name="stock" value="0">
                            
                            <div class="col-md-5">
                                <label class="small text-muted mb-1 fw-bold">Variant Name</label>
                                <input type="text" name="variantName" class="form-control form-control-sm" placeholder="Ex: 50ml" required>
                            </div>
                            <div class="col-md-4">
                                <label class="small text-muted mb-1 fw-bold">Price</label>
                                <input type="number" step="0.01" name="price" class="form-control form-control-sm" placeholder="Price" required>
                            </div>
                            <div class="col-md-3">
                                <button type="submit" class="btn btn-primary btn-sm w-100"><i class="fas fa-plus"></i> Add</button>
                            </div>
                        </form>
                    </div>
                </div>

                <h6 class="fw-bold mb-3 text-secondary">Type List</h6>
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
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title fw-bold">Add New Product</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Product Image</label>
                        <input type="file" name="image" class="form-control" accept="image/*" required onchange="previewImage(this, 'addPreview')">
                        <img id="addPreview" class="img-preview mt-2 rounded border" src="#" style="display:none; width:100px; height:100px; object-fit:cover;">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Product Name</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>

                    <div class="row bg-light p-3 rounded border mb-3 mx-0">
                        <h6 class="fw-bold text-secondary mb-2 border-bottom pb-1">Pricing & Inventory (Bản mặc định)</h6>
                        <input type="hidden" name="stock" value="0">

                        <div class="col-md-6 mb-2">
                            <label class="form-label text-danger fw-bold small">Sale Price</label>
                            <input type="number" step="0.01" name="price" class="form-control" required placeholder="Ex: 299000">
                        </div>
                        <div class="col-md-6 mb-2">
                            <label class="form-label text-success fw-bold small">Import Price</label>
                            <input type="number" step="0.01" name="importPrice" class="form-control" required placeholder="Ex: 150000">
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
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold">Edit Product Info</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3 text-center">
                         <img id="editImageDisplay" src="" width="100" height="100" class="rounded border mb-2" style="object-fit: cover;">
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
                <div class="modal-body p-4">
                    <i class="fas fa-eye-slash text-danger mb-3" style="font-size: 3rem;"></i>
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
                <div class="modal-body p-4">
                    <i class="fas fa-eye text-success mb-3" style="font-size: 3rem;"></i>
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