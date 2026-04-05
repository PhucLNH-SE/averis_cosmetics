<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="admin-content__section admin-page admin-page--staff">
    <div class="page-header">
        <div>
            <h4>Manage Staff</h4>
            <p class="text-muted mb-0">Manage system staff accounts</p>
        </div>
        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addModal">
            <i class="fas fa-user-plus me-1"></i> Add New Staff
        </button>
    </div>

        <c:if test="${not empty successMsg}">
            <c:set var="popupMessage" scope="request" value="${successMsg}" />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${not empty errorMsg}">
            <c:set var="popupMessage" scope="request" value="${errorMsg}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

    <div class="card table-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th class="px-4">ID</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th class="text-end px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty listStaff}">
                                <tr>
                                    <td colspan="6" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No staff found
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${listStaff}" var="s">
                                    <tr>
                                        <td class="px-4">${s.managerId}</td>
                                        <td><strong>${s.fullName}</strong></td>
                                        <td>${s.email}</td>
                                        <td>
                                            <span class="role-badge-staff">
                                                ${s.managerRole}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="${s.status ? 'status-active' : 'status-inactive'}">
                                                <i class="fas ${s.status ? 'fa-check-circle' : 'fa-ban'} me-1"></i>
                                                ${s.status ? 'Active' : 'Banned'}
                                            </span>
                                        </td>
                                        <td class="text-end px-4">
                                            <a class="btn btn-info btn-sm text-white me-1"
                                               href="${pageContext.request.contextPath}/admin/manage-staff?action=detail&managerId=${s.managerId}">
                                                <i class="fas fa-eye"></i> View
                                            </a>

                                            <a class="btn btn-primary btn-sm"
                                               href="${pageContext.request.contextPath}/admin/manage-staff?action=edit&managerId=${s.managerId}">
                                                <i class="fas fa-edit"></i> Edit
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="addModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content border-0 shadow">
                <form action="${pageContext.request.contextPath}/admin/manage-staff" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="role" value="STAFF"> 
                    
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title fw-bold">Add New Staff</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Staff Name</label>
                            <input type="text" name="name" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Email</label>
                            <input type="email" name="email" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Password</label>
                            <input type="password" name="password" class="form-control" required minlength="6">
                        </div>
                        <div class="form-check form-switch mt-3">
                            <input type="hidden" name="status" value="false">
                            <input class="form-check-input" type="checkbox" name="status" value="true" checked id="addStatus">
                            <label class="form-check-label fw-bold" for="addStatus">Account is Active</label>
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle"></i> Cancel
                        </button>
                        <button type="submit" class="btn btn-success px-4">
                            <i class="bi bi-check2-circle"></i> Save Staff
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="editModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content border-0 shadow">
                <form action="${pageContext.request.contextPath}/admin/manage-staff" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="managerId" id="editId" value="${selectedStaff.managerId}">
                    <input type="hidden" name="role" id="editRole" value="${not empty selectedStaff.managerRole ? selectedStaff.managerRole : 'STAFF'}">

                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title fw-bold">Edit Staff Profile</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Staff Name</label>
                            <input type="text" name="name" id="editName" class="form-control" required value="<c:out value='${selectedStaff.fullName}'/>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Email</label>
                            <input type="email" name="email" id="editEmail" class="form-control" required value="<c:out value='${selectedStaff.email}'/>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">New Password</label>
                            <input type="password" name="password" class="form-control" minlength="6" placeholder="Leave blank to keep current password">
                            <small class="text-primary mt-1 d-block">Leave blank if you do not want to change the password.</small>
                        </div>
                        <div class="form-check form-switch mt-3">
                            <input type="hidden" name="status" value="false">
                            <input class="form-check-input" type="checkbox" name="status" value="true" id="editStatus"
                                   ${not empty selectedStaff and selectedStaff.status ? 'checked' : ''}>
                            <label class="form-check-label fw-bold" for="editStatus">Account is Active</label>
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle"></i> Cancel
                        </button>
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="bi bi-check2-circle"></i> Update Changes
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</section>

<script>
    <c:if test="${formMode == 'update' and not empty selectedStaff}">
    window.addEventListener('load', function () {
        bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
    });
    </c:if>

</script>
