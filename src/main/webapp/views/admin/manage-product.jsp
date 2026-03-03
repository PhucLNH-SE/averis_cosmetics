<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Management - Averis Cosmetics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body { background-color: #f8f9fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card { border: none; border-radius: 15px; box-shadow: 0 0 20px rgba(0,0,0,0.05); }
        .table thead { background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; }
        .btn-success { background-color: #28a745; border: none; }
        .btn-primary { background-color: #0d6efd; border: none; }
        .btn-danger { background-color: #dc3545; border: none; }
        .img-preview { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; border: 1px solid #dee2e6; display: none; margin-top: 10px; }
        .product-img-td { width: 50px; height: 50px; object-fit: cover; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold mb-0">Product Management</h3>
                <p class="text-muted small">Manage your cosmetics inventory</p>
            </div>
            <button class="btn btn-outline-secondary px-3" onclick="history.back()">← Back</button>
        </div>

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
                            <th>Status</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listP}" var="p">
                            <tr>
                                <td class="fw-bold">${p.productId}</td>
                                <td>
                                    <c:set var="imagePath" value="${p.mainImage.contains('-') ? 'products/' : ''}${p.mainImage}" />
                                    <img src="${pageContext.request.contextPath}/assets/img/${imagePath}" class="product-img-td border" onerror="this.src='${pageContext.request.contextPath}/assets/img/default-product.jpg';">
                                </td>
                                <td>${p.name}</td>
                                <td>${p.category.name}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.price == 0}"><span class="text-muted small">No variants</span></c:when>
                                        <c:when test="${p.price == p.maxPrice}">
                                            <span class="text-primary fw-bold"><fmt:formatNumber value="${p.price}" pattern="#,###"/>₫</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-primary fw-bold">
                                                <fmt:formatNumber value="${p.price}" pattern="#,###"/>₫ - <fmt:formatNumber value="${p.maxPrice}" pattern="#,###"/>₫
                                            </span>
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
                                            onclick="openVariantModal('${p.productId}', '${p.name.replace("'", "\\'")}')">
                                        <i class="fas fa-tags me-1"></i> Variants
                                    </button>
                                    
                                    <button class="btn btn-primary btn-sm px-3 me-1" 
                                            onclick="openEditModal('${p.productId}', '${p.name.replace("'", "\\'")}', '${p.brand.brandId}', '${p.category.categoryId}', ${p.status}, '${p.description.replace("'", "\\'")}', '${imagePath}')">
                                        <i class="fas fa-edit me-1"></i> Edit
                                    </button>
                                    <button class="btn btn-danger btn-sm px-3" onclick="openDeleteModal('${p.productId}', '${p.name.replace("'", "\\'")}')">
                                        <i class="fas fa-trash me-1"></i> Delete
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

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
                            <div class="d-flex gap-2 mb-2 p-2 border-bottom align-items-end bg-white">
                                <form action="manage-variant" method="post" class="d-flex gap-2 flex-grow-1 align-items-end mb-0">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="variantId" value="${v.variantId}">
                                    <input type="hidden" name="productId" value="${p.productId}">
                                    <input type="hidden" name="stock" value="${v.stock}">
                                    
                                    <div class="flex-grow-1">
                                        <label class="small text-muted mb-1">ml, type/</label>
                                        <input type="text" name="variantName" class="form-control form-control-sm fw-bold text-primary" value="${v.variantName}" required>
                                    </div>
                                    <div class="flex-grow-1">
                                        <label class="small text-muted mb-1">Price ($)</label>
                                        <input type="number" step="0.01" name="price" class="form-control form-control-sm text-danger fw-bold" value="${v.price}" required>
                                    </div>
                                    
                                    <button type="submit" class="btn btn-success btn-sm px-3" title="Lưu thay đổi"><i class="fas fa-save"></i></button>
                                </form>
                                
                                <form action="manage-variant" method="post" class="mb-0" onsubmit="return confirm('Do you want delete?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="variantId" value="${v.variantId}">
                                    <button type="submit" class="btn btn-danger btn-sm px-3" title="Xóa"><i class="fas fa-trash"></i></button>
                                </form>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:forEach>
    </div>

    <div class="modal fade" id="variantModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title fw-bold"><i class="fas fa-tags me-2"></i>Type: <span id="varModalProductName"></span></h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body bg-light p-4">
                    
                    <div class="card shadow-sm mb-4 border-0">
                        <div class="card-body p-3 bg-white rounded">
                            <h6 class="fw-bold mb-3 text-primary border-bottom pb-2">Add new type</h6>
                            <form action="manage-variant" method="post" class="row g-2 align-items-end">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" id="addVarProductId">
                                <input type="hidden" name="stock" value="100"> <div class="col-md-5">
                                    <label class="small text-muted mb-1">Product Name (Ex: 50ml, Red)</label>
                                    <input type="text" name="variantName" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="small text-muted mb-1">Price ($)</label>
                                    <input type="number" step="0.01" name="price" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-md-3">
                                    <button type="submit" class="btn btn-primary btn-sm w-100"><i class="fas fa-plus"></i> Add</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <h6 class="fw-bold mb-3 text-secondary">Type List</h6>
                    <div id="variantListContainer" class="bg-white p-3 rounded shadow-sm">
                        </div>
                </div>
            </div>
        </div>
    </div>


    <div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="manage-product" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold">Add New Product</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Product Image</label>
                            <input type="file" name="image" class="form-control" accept="image/*" required onchange="previewImage(this, 'addPreview')">
                            <img id="addPreview" class="img-preview" src="#" alt="Preview">
                        </div>
                        
                        <div class="row">
                            <div class="col-md-8 mb-3">
                                <label class="form-label">Product Name</label>
                                <input type="text" name="name" class="form-control" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label text-danger fw-bold">Price ($)</label>
                                <input type="number" step="0.01" name="price" class="form-control" required placeholder="Ex: 29.99">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea name="description" class="form-control" rows="3"></textarea>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Brand</label>
                                <select name="brandId" class="form-select">
                                    <c:forEach items="${listB}" var="b">
                                        <option value="${b.brandId}">${b.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Category</label>
                                <select name="categoryId" class="form-select">
                                    <c:forEach items="${listC}" var="c">
                                        <option value="${c.categoryId}">${c.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="status" value="true" checked id="addStatus">
                            <label class="form-check-label" for="addStatus">Active</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success">Save Product</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="manage-product" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="productId" id="editId">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold">Edit Product</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
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
                                    <c:forEach items="${listB}" var="b">
                                        <option value="${b.brandId}">${b.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Category</label>
                                <select name="categoryId" id="editCategory" class="form-select">
                                    <c:forEach items="${listC}" var="c">
                                        <option value="${c.categoryId}">${c.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="status" value="true" id="editStatus">
                            <label class="form-check-label" for="editStatus">Active</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content text-center">
                <form action="manage-product" method="post">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="productId" id="deleteId">
                    <div class="modal-header border-0">
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <i class="fas fa-exclamation-triangle text-warning mb-3" style="font-size: 3rem;"></i>
                        <p>Are you sure you want to delete product:</p>
                        <h5 id="deleteName" class="fw-bold text-danger mb-4"></h5>
                        <p class="text-muted small">This action cannot be undone and might affect related orders.</p>
                    </div>
                    <div class="modal-footer justify-content-center border-0">
                        <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger px-4">Delete Permanently</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function openVariantModal(productId, productName) {
            document.getElementById('varModalProductName').innerText = productName;
            document.getElementById('addVarProductId').value = productId; // Update ID cho form Add
            const variantHTML = document.getElementById('variants-data-' + productId).innerHTML;
            document.getElementById('variantListContainer').innerHTML = variantHTML;
            new bootstrap.Modal(document.getElementById('variantModal')).show();
        }

        function previewImage(input, previewId) {
            const preview = document.getElementById(previewId);
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    preview.style.display = 'inline-block';
                }
                reader.readAsDataURL(input.files[0]);
            }
        }

        function openEditModal(id, name, brandId, categoryId, status, desc, imagePath) {
            document.getElementById('editId').value = id;
            document.getElementById('editName').value = name;
            document.getElementById('editBrand').value = brandId;
            document.getElementById('editCategory').value = categoryId;
            document.getElementById('editStatus').checked = status;
            document.getElementById('editDesc').value = desc;
            
            const basePath = "${pageContext.request.contextPath}/assets/img/";
            document.getElementById('editImageDisplay').src = basePath + imagePath;
            
            var editModal = new bootstrap.Modal(document.getElementById('editModal'));
            editModal.show();
        }

        function openDeleteModal(id, name) {
            document.getElementById('deleteId').value = id;
            document.getElementById('deleteName').innerText = name;
            var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
    </script>
</body>
</html>