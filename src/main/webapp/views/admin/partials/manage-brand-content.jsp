<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section">
    <div class="container py-4">
        <div class="page-header">
            <div>
                <h4>Manage Brands</h4>
                <p class="text-muted mb-0">List of product brands</p>
            </div>
            <button type="button" class="btn btn-add text-white" data-bs-toggle="modal" data-bs-target="#brandModal" onclick="openAddModal()">
                Add Brand
            </button>
        </div>

        <c:if test="${param.success == 'add'}">
            <c:set var="popupMessage" scope="request" value="Brand added successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.success == 'update'}">
            <c:set var="popupMessage" scope="request" value="Brand updated successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.success == 'delete'}">
            <c:set var="popupMessage" scope="request" value="Brand deleted successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.error == 'addFailed'}">
            <c:set var="popupMessage" scope="request" value="Failed to add brand." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${param.error == 'updateFailed'}">
            <c:set var="popupMessage" scope="request" value="Failed to update brand." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${param.error == 'deleteFailed'}">
            <c:set var="popupMessage" scope="request" value="Failed to delete brand." />
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
                                <th>Brand Name</th>
                                <th>Status</th>
                                <th class="text-end px-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="brand" items="${brands}">
                                <tr>
                                    <td class="px-4">${brand.brandId}</td>
                                    <td><strong>${brand.name}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${brand.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end px-4">
                                        <button type="button" class="btn btn-edit btn-sm text-white me-1"
                                                data-bs-toggle="modal" data-bs-target="#brandModal"
                                                onclick="openEditModal(${brand.brandId}, '${brand.name}', ${brand.status})">
                                            Edit
                                        </button>
                                        <button type="button" class="btn btn-delete btn-sm text-white"
                                                data-bs-toggle="modal" data-bs-target="#deleteModal"
                                                onclick="openDeleteModal(${brand.brandId}, '${brand.name}')">
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty brands}">
                                <tr>
                                    <td colspan="4" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No brands found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="brandModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitle">Add Brand</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="brandForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" id="brandId" name="brandId">
                        <input type="hidden" id="action" name="action" value="add">

                        <div class="mb-3">
                            <label for="brandName" class="form-label">Brand Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="brandName" name="name" required maxlength="100"
                                   placeholder="Enter brand name">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="brandStatus" name="status" value="1" checked>
                                <label class="form-check-label" for="brandStatus">Active</label>
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
                        <input type="hidden" id="deleteBrandId" name="brandId">
                        <p>Are you sure you want to delete brand <strong id="deleteBrandName"></strong>?</p>
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
        document.getElementById('modalTitle').textContent = 'Add Brand';
        document.getElementById('submitBtn').textContent = 'Add';
        document.getElementById('action').value = 'add';
        document.getElementById('brandId').value = '';
        document.getElementById('brandName').value = '';
        document.getElementById('brandStatus').checked = true;
        document.getElementById('brandForm').action = '${pageContext.request.contextPath}/admin/brand?action=add';
    }

    function openEditModal(brandId, brandName, brandStatus) {
        document.getElementById('modalTitle').textContent = 'Update Brand';
        document.getElementById('submitBtn').textContent = 'Update';
        document.getElementById('action').value = 'update';
        document.getElementById('brandId').value = brandId;
        document.getElementById('brandName').value = brandName;
        document.getElementById('brandStatus').checked = brandStatus;
        document.getElementById('brandForm').action = '${pageContext.request.contextPath}/admin/brand?action=update';
    }

    function openDeleteModal(brandId, brandName) {
        document.getElementById('deleteBrandId').value = brandId;
        document.getElementById('deleteBrandName').textContent = brandName;
        document.getElementById('deleteModal').querySelector('form').action =
                '${pageContext.request.contextPath}/admin/brand?action=delete';
    }
</script>
