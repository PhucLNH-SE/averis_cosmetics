<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section admin-page admin-page--supplier">
    <div class="container py-4">
        <div class="page-header">
            <div>
                <h4>Manage Suppliers</h4>
                <p class="text-muted mb-0">List of product suppliers</p>
            </div>
            <a class="btn btn-add text-white"
               href="${pageContext.request.contextPath}/admin/manage-supplier?action=add">
                <i class="bi bi-plus-circle"></i> Add Supplier
            </a>
        </div>

        <c:if test="${param.success == 'add'}">
            <c:set var="popupMessage" scope="request" value="Supplier added successfully." />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${param.success == 'update'}">
            <c:set var="popupMessage" scope="request" value="Supplier updated successfully." />
            <c:set var="popupType" scope="request" value="success" />
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
                    <table class="table">
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
                                    <td><c:out value="${supplier.address}" /></td>
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
                    <h5 class="modal-title"><c:out value="${modalTitle}" /></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/admin/manage-supplier">
                    <div class="modal-body">
                        <input type="hidden" name="id" value="${not empty selectedSupplier ? selectedSupplier.supplierId : ''}">
                        <input type="hidden" name="action" value="${actionValue}">

                        <div class="mb-3">
                            <label for="supplierName" class="form-label">Supplier Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="supplierName" name="name"
                                   value="<c:out value='${selectedSupplier.name}' />"
                                   required maxlength="150" placeholder="Enter supplier name">
                        </div>

                        <div class="mb-3">
                            <label for="supplierPhone" class="form-label">Phone <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" id="supplierPhone" name="phone"
                                   value="<c:out value='${selectedSupplier.phone}' />"
                                   required maxlength="20" inputmode="tel" placeholder="Enter supplier phone">
                        </div>

                        <div class="mb-3">
                            <label for="supplierAddress" class="form-label">Address <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="supplierAddress" name="address" rows="3"
                                      required maxlength="255"
                                      placeholder="Enter supplier address"><c:out value="${selectedSupplier.address}" /></textarea>
                        </div>

                        <div class="mb-3">
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
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="bi bi-check2-circle"></i> <c:out value="${submitLabel}" />
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<c:if test="${openModal}">
    <script>
        window.addEventListener('load', function () {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('supplierModal')).show();
        });
    </script>
</c:if>
