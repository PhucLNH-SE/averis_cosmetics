<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="admin-content__section">
    <div class="container-fluid staff-main-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold mb-0">Staff Management</h3>
                <p class="text-muted small">Manage system staff accounts</p>
            </div>
            <button class="btn btn-success px-3" data-bs-toggle="modal" data-bs-target="#addModal">
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

        <div class="card staff-table-card">
            <div class="card-body p-4">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Staff Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listStaff}" var="s">
                            <%-- Only show STAFF, hide ADMIN --%>
                            <c:if test="${s.managerRole == 'STAFF'}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="staff-avatar-placeholder me-3">
                                                ${fn:substring(s.fullName, 0, 1)}
                                            </div>
                                            <span class="fw-bold text-dark">${s.fullName}</span>
                                        </div>
                                    </td>
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
                                    <td class="text-center">
                                        <button class="btn btn-info btn-sm text-white px-2 me-1"
                                                onclick="openViewModal('${s.managerId}', '${fn:escapeXml(s.fullName)}', '${s.email}', '${s.managerRole}', '${s.status}')">
                                            <i class="fas fa-eye"></i>
                                        </button>

                                        <button class="btn btn-primary btn-sm px-2 me-1"
                                                onclick="openEditModal('${s.managerId}', '${fn:escapeXml(s.fullName)}', '${s.email}', '${s.managerRole}', '${s.status}')">
                                            <i class="fas fa-edit"></i>
                                        </button>

                                        <c:choose>
                                            <c:when test="${s.status}">
                                                <button class="btn btn-danger btn-sm px-2" title="Ban Account"
                                                        onclick="openDeleteModal('${s.managerId}', '${fn:escapeXml(s.fullName)}')">
                                                    <i class="fas fa-ban"></i>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-success btn-sm px-2" title="Unlock Account"
                                                        onclick="openUnbanModal('${s.managerId}', '${fn:escapeXml(s.fullName)}')">
                                                    <i class="fas fa-unlock"></i>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
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
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success px-4">Save Staff</button>
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
                    <input type="hidden" name="managerId" id="editId">
                    <input type="hidden" name="role" id="editRole" value="STAFF">

                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title fw-bold">Edit Staff Profile</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Staff Name</label>
                            <input type="text" name="name" id="editName" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Email</label>
                            <input type="email" name="email" id="editEmail" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">New Password</label>
                            <input type="password" name="password" class="form-control" minlength="6" placeholder="Leave blank to keep current password">
                            <small class="text-primary mt-1 d-block">Leave blank if you do not want to change the password.</small>
                        </div>
                        <div class="form-check form-switch mt-3">
                            <input type="hidden" name="status" value="false">
                            <input class="form-check-input" type="checkbox" name="status" value="true" id="editStatus">
                            <label class="form-check-label fw-bold" for="editStatus">Account is Active</label>
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary px-4">Update Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="viewModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title fw-bold"><i class="fas fa-id-card me-2"></i>Staff Details</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="view-detail-row">
                        <div class="view-detail-label">Staff ID:</div>
                        <div class="view-detail-value" id="viewId"></div>
                    </div>
                    <div class="view-detail-row">
                        <div class="view-detail-label">Staff Name:</div>
                        <div class="view-detail-value" id="viewName"></div>
                    </div>
                    <div class="view-detail-row">
                        <div class="view-detail-label">Email:</div>
                        <div class="view-detail-value" id="viewEmail"></div>
                    </div>
                    <div class="view-detail-row">
                        <div class="view-detail-label">Role:</div>
                        <div class="view-detail-value" id="viewRole"></div>
                    </div>
                    <div class="view-detail-row border-0 mb-0 pb-0">
                        <div class="view-detail-label">Status:</div>
                        <div class="view-detail-value" id="viewStatus"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog modal-sm modal-dialog-centered">
            <div class="modal-content text-center border-0 shadow">
                <form action="${pageContext.request.contextPath}/admin/manage-staff" method="post">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="managerId" id="deleteId">
                    <div class="modal-body p-4">
                        <i class="fas fa-user-slash text-danger mb-3" style="font-size: 3rem;"></i>
                        <h5 class="fw-bold mb-3">Ban Account?</h5>
                        <p class="text-muted small mb-4">Are you sure you want to deactivate <strong id="deleteName" class="text-dark"></strong>? They will no longer be able to log in.</p>
                        <div class="d-flex justify-content-center gap-2">
                            <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-danger px-4">Yes, Ban</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="unbanModal" tabindex="-1">
        <div class="modal-dialog modal-sm modal-dialog-centered">
            <div class="modal-content text-center border-0 shadow">
                <form action="${pageContext.request.contextPath}/admin/manage-staff" method="post">
                    <input type="hidden" name="action" value="unban">
                    <input type="hidden" name="managerId" id="unbanId">
                    <div class="modal-body p-4">
                        <i class="fas fa-unlock text-success mb-3" style="font-size: 3rem;"></i>
                        <h5 class="fw-bold mb-3">Unlock Account?</h5>
                        <p class="text-muted small mb-4">Are you sure you want to restore access for <strong id="unbanName" class="text-dark"></strong>? They will be able to log in again.</p>
                        <div class="d-flex justify-content-center gap-2">
                            <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-success px-4">Yes, Unlock</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<script>
    function openEditModal(id, name, email, role, status) {
        document.getElementById('editId').value = id;
        document.getElementById('editName').value = name;
        document.getElementById('editEmail').value = email;
        // The role value is still assigned to the hidden <input type="hidden" id="editRole">
        document.getElementById('editRole').value = role; 
        document.getElementById('editStatus').checked = (status === 'true');
        new bootstrap.Modal(document.getElementById('editModal')).show();
    }

    function openViewModal(id, name, email, role, status) {
        document.getElementById('viewId').innerText = '#' + id;
        document.getElementById('viewName').innerText = name;
        document.getElementById('viewEmail').innerText = email;
        document.getElementById('viewRole').innerHTML = role === 'ADMIN'
                ? '<span class="role-badge-admin">ADMIN</span>'
                : '<span class="role-badge-staff">STAFF</span>';
        document.getElementById('viewStatus').innerHTML = status === 'true'
                ? '<span class="status-active"><i class="fas fa-check-circle"></i> Active</span>'
                : '<span class="status-inactive"><i class="fas fa-ban"></i> Banned</span>';
        new bootstrap.Modal(document.getElementById('viewModal')).show();
    }

    function openDeleteModal(id, name) {
        document.getElementById('deleteId').value = id;
        document.getElementById('deleteName').innerText = name;
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }

    function openUnbanModal(id, name) {
        document.getElementById('unbanId').value = id;
        document.getElementById('unbanName').innerText = name;
        new bootstrap.Modal(document.getElementById('unbanModal')).show();
    }
</script>
