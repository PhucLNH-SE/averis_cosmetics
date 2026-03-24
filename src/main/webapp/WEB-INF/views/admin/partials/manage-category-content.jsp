<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section admin-page admin-page--category">
    <div class="container py-4">
        <div class="page-header">
            <div>
                <h4>Manage Categories</h4>
                <p class="text-muted mb-0">List of product categories</p>
            </div>
            <button type="button" class="btn btn-add text-white" data-bs-toggle="modal" data-bs-target="#categoryModal" onclick="openAddModal()">
                <i class="bi bi-plus-circle"></i> Add Category
            </button>
        </div>

        <c:if test="${param.success == 'add'}">
            <c:set var="popupMessage" scope="request" value="Category added successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.success == 'update'}">
            <c:set var="popupMessage" scope="request" value="Category updated successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.error == 'addFailed'}">
            <c:set var="popupMessage" scope="request" value="Failed to add category." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${param.error == 'updateFailed'}">
            <c:set var="popupMessage" scope="request" value="Failed to update category." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${param.error == 'notFound'}">
            <c:set var="popupMessage" scope="request" value="Category not found." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${not empty error}">
            <c:set var="popupMessage" scope="request" value="${error}" />
            <c:set var="popupType" scope="request" value="error" />
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
                                        <a href="${pageContext.request.contextPath}/admin/manage-category?action=edit&id=${cat.categoryId}"
                                           class="btn btn-edit btn-sm text-white me-1">
                                            <i class="bi bi-pencil-square"></i> Edit
                                        </a>
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
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle"></i> Cancel
                        </button>
                        <button type="submit" class="btn btn-primary px-4" id="submitBtn">
                            <i class="bi bi-check2-circle"></i> Add
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</section>

<script>
    function openAddModal() {
        document.getElementById('modalTitle').textContent = 'Add Category';
        document.getElementById('submitBtn').innerHTML = '<i class="bi bi-check2-circle"></i> Add';
        document.getElementById('action').value = 'add';
        document.getElementById('categoryId').value = '';
        document.getElementById('categoryName').value = '';
        document.getElementById('categoryStatus').checked = true;
        document.getElementById('categoryForm').action = '${pageContext.request.contextPath}/admin/add-category';
    }

    <c:if test="${formMode == 'update' and not empty selectedCategory}">
    window.addEventListener('load', function () {
        document.getElementById('modalTitle').textContent = 'Update Category';
        document.getElementById('submitBtn').innerHTML = '<i class="bi bi-check2-circle"></i> Update Changes';
        document.getElementById('action').value = 'update';
        document.getElementById('categoryId').value = '${selectedCategory.categoryId}';
        document.getElementById('categoryName').value = '${selectedCategory.name}';
        document.getElementById('categoryStatus').checked = ${selectedCategory.status};
        document.getElementById('categoryForm').action = '${pageContext.request.contextPath}/admin/update-category';
        bootstrap.Modal.getOrCreateInstance(document.getElementById('categoryModal')).show();
    });
    </c:if>
</script>
