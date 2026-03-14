<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section">
    <div class="container py-4">
        <div class="page-header">
            <div>
                <h4>Manage Categories</h4>
                <p class="text-muted mb-0">List of product categories</p>
            </div>
            <button type="button" class="btn btn-add text-white" data-bs-toggle="modal" data-bs-target="#categoryModal" onclick="openAddModal()">
                Add Category
            </button>
        </div>

        <c:if test="${param.success == 'add'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Category added successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.success == 'update'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Category updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.success == 'delete'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Category deleted successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'addFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to add category!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'updateFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to update category!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'deleteFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to delete category!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Category Name</th>
                                <th>Status</th>
                                <th class="text-end px-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cat" items="${categories}">
                                <tr>
                                    <td class="px-4">${cat.categoryId}</td>
                                    <td><strong>${cat.name}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cat.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end px-4">
                                        <button type="button" class="btn btn-edit btn-sm text-white me-1"
                                                data-bs-toggle="modal" data-bs-target="#categoryModal"
                                                onclick="openEditModal(${cat.categoryId}, '${cat.name}', ${cat.status})">
                                            Edit
                                        </button>
                                        <button type="button" class="btn btn-delete btn-sm text-white"
                                                data-bs-toggle="modal" data-bs-target="#deleteModal"
                                                onclick="openDeleteModal(${cat.categoryId}, '${cat.name}')">
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty categories}">
                                <tr>
                                    <td colspan="4" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No categories found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="categoryModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitle">Add Category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="categoryForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" id="categoryId" name="id">
                        <input type="hidden" id="action" name="action" value="add">

                        <div class="mb-3">
                            <label for="categoryName" class="form-label">Category Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="categoryName" name="name" required maxlength="100"
                                   placeholder="Enter category name">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="categoryStatus" name="status" value="1" checked>
                                <label class="form-check-label" for="categoryStatus">Active</label>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary" id="submitBtn">Add</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" id="deleteCategoryId" name="id">
                        <p>Are you sure you want to delete category <strong id="deleteCategoryName"></strong>?</p>
                        <p class="text-muted small">This action cannot be undone.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<script>
    function openAddModal() {
        document.getElementById('modalTitle').textContent = 'Add Category';
        document.getElementById('submitBtn').textContent = 'Add';
        document.getElementById('action').value = 'add';
        document.getElementById('categoryId').value = '';
        document.getElementById('categoryName').value = '';
        document.getElementById('categoryStatus').checked = true;
        document.getElementById('categoryForm').action = '${pageContext.request.contextPath}/admin/add-category';
    }

    function openEditModal(categoryId, categoryName, categoryStatus) {
        document.getElementById('modalTitle').textContent = 'Update Category';
        document.getElementById('submitBtn').textContent = 'Update';
        document.getElementById('action').value = 'update';
        document.getElementById('categoryId').value = categoryId;
        document.getElementById('categoryName').value = categoryName;
        document.getElementById('categoryStatus').checked = categoryStatus;
        document.getElementById('categoryForm').action = '${pageContext.request.contextPath}/admin/update-category';
    }

    function openDeleteModal(categoryId, categoryName) {
        document.getElementById('deleteCategoryId').value = categoryId;
        document.getElementById('deleteCategoryName').textContent = categoryName;
        document.getElementById('deleteModal').querySelector('form').action =
                '${pageContext.request.contextPath}/admin/delete-category';
    }
</script>
