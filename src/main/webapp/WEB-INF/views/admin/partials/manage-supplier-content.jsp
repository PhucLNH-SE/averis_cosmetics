<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section admin-page admin-page--supplier">
    <div class="container py-4">
        <div class="page-header">
            <div>
                <h4>Manage Suppliers</h4>
                <p class="text-muted mb-0">List of suppliers for import management</p>
            </div>
            <button type="button" class="btn btn-add text-white" data-bs-toggle="modal" data-bs-target="#supplierModal" onclick="openAddModal()">
                <i class="bi bi-plus-circle"></i> Add Supplier
            </button>
        </div>

        <c:if test="${param.success == 'add'}">
            <c:set var="popupMessage" scope="request" value="Supplier added successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.success == 'update'}">
            <c:set var="popupMessage" scope="request" value="Supplier updated successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.error == 'updateFailed'}">
            <c:set var="popupMessage" scope="request" value="Failed to update supplier." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${param.error == 'notFound'}">
            <c:set var="popupMessage" scope="request" value="Supplier not found." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${not empty error}">
            <c:set var="popupMessage" scope="request" value="${error}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Supplier Name</th>
                                <th>Phone</th>
                                <th>Address</th>
                                <th>Status</th>
                                <th class="text-end px-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="supplier" items="${suppliers}">
                                <tr>
                                    <td class="px-4">${supplier.supplierId}</td>
                                    <td><strong><c:out value="${supplier.name}" /></strong></td>
                                    <td><c:out value="${supplier.phone}" /></td>
                                    <td class="text-wrap"><c:out value="${supplier.address}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${supplier.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end px-4">
                                        <a href="${pageContext.request.contextPath}/admin/manage-supplier?action=edit&id=${supplier.supplierId}"
                                           class="btn btn-edit btn-sm text-white me-1">
                                            <i class="bi bi-pencil-square"></i> Edit
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty suppliers}">
                                <tr>
                                    <td colspan="6" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No suppliers found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="supplierModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitle">
                        <c:choose>
                            <c:when test="${formMode == 'update'}">Update Supplier</c:when>
                            <c:otherwise>Add Supplier</c:otherwise>
                        </c:choose>
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="supplierForm" method="post" action="${pageContext.request.contextPath}/admin/manage-supplier"
                      data-form-mode="${formMode}">
                    <div class="modal-body">
                        <input type="hidden" id="supplierId" name="id" value="${not empty selectedSupplier ? selectedSupplier.supplierId : ''}">
                        <input type="hidden" id="action" name="action" value="${formMode == 'update' ? 'update' : 'add'}">

                        <div class="mb-3">
                            <label for="supplierName" class="form-label">Supplier Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="supplierName" name="name" required maxlength="150"
                                   value="<c:out value='${selectedSupplier.name}'/>"
                                   placeholder="Enter supplier name">
                        </div>

                        <div class="mb-3">
                            <label for="supplierPhone" class="form-label">Phone <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="supplierPhone" name="phone" required maxlength="20" inputmode="tel"
                                   value="<c:out value='${selectedSupplier.phone}'/>"
                                   placeholder="Enter supplier phone">
                        </div>

                        <div class="mb-3">
                            <label for="supplierAddress" class="form-label">Address <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="supplierAddress" name="address" rows="3" required maxlength="255"
                                      placeholder="Enter supplier address"><c:out value="${selectedSupplier.address}" /></textarea>
                        </div>

                        <div class="mb-0">
                            <label class="form-label">Status</label>
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="supplierStatus" name="status" value="1"
                                       ${empty selectedSupplier || selectedSupplier.status ? 'checked' : ''}>
                                <label class="form-check-label" for="supplierStatus">Active</label>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle"></i> Cancel
                        </button>
                        <button type="submit" class="btn btn-primary px-4" id="submitBtn">
                            <c:choose>
                                <c:when test="${formMode == 'update'}">
                                    <i class="bi bi-check2-circle"></i> Update Changes
                                </c:when>
                                <c:otherwise>
                                    <i class="bi bi-check2-circle"></i> Add
                                </c:otherwise>
                            </c:choose>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<script>
    function openAddModal() {
        document.getElementById('modalTitle').textContent = 'Add Supplier';
        document.getElementById('submitBtn').innerHTML = '<i class="bi bi-check2-circle"></i> Add';
        document.getElementById('action').value = 'add';
        document.getElementById('supplierId').value = '';
        document.getElementById('supplierName').value = '';
        document.getElementById('supplierPhone').value = '';
        document.getElementById('supplierAddress').value = '';
        document.getElementById('supplierStatus').checked = true;
    }

    window.addEventListener('load', function () {
        const form = document.getElementById('supplierForm');
        const formMode = form.getAttribute('data-form-mode');

        if (!formMode) {
            return;
        }

        bootstrap.Modal.getOrCreateInstance(document.getElementById('supplierModal')).show();
    });
</script>
