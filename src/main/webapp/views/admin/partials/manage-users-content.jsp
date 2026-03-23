<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section admin-page admin-page--users">
    <div class="page-header">
        <div>
            <h4>Manage Users</h4>
            <p class="text-muted mb-0">List of users in the system</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/panel?view=dashboard" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Back to Dashboard
        </a>
    </div>

    <c:if test="${param.success == 'update'}">
        <c:set var="popupMessage" scope="request" value="User status updated successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.error == 'updateFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to update user status." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card table-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th class="px-4">ID</th>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Status</th>
                            <th class="text-end px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td class="px-4">${user.customerId}</td>
                                <td><strong>${user.username}</strong></td>
                                <td>${user.fullName}</td>
                                <td>${empty user.email ? '-' : user.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.status}">
                                            <span class="status-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-inactive">Locked</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end px-4">
                                    <button type="button" class="btn btn-edit btn-sm text-white me-1"
                                            data-bs-toggle="modal" data-bs-target="#userDetailModal"
                                            onclick="openUserDetail(${user.customerId}, '${user.username}', '${user.fullName}', '${empty user.email ? '-' : user.email}', '${empty user.gender ? '-' : user.gender}', '${empty user.dateOfBirth ? '-' : user.dateOfBirth}', ${user.status}, ${user.emailVerified})">
                                        <i class="bi bi-eye"></i> View
                                    </button>
                                    <button type="button" class="btn btn-delete btn-sm text-white"
                                            data-bs-toggle="modal" data-bs-target="#lockModal"
                                            onclick="openLockModal(${user.customerId}, '${user.username}', ${user.status})">
                                        <i class="bi ${user.status ? 'bi-lock' : 'bi-unlock'}"></i> ${user.status ? 'Lock' : 'Unlock'}
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty users}">
                            <tr>
                                <td colspan="6" class="text-center empty-state">
                                    <i class="bi bi-inbox d-block"></i>
                                    No users found
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<div class="modal fade" id="userDetailModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">User Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="row mb-2">
                    <div class="col-4 fw-bold">ID:</div>
                    <div class="col-8" id="detailId"></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Username:</div>
                    <div class="col-8" id="detailUsername"></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Full Name:</div>
                    <div class="col-8" id="detailFullName"></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Email:</div>
                    <div class="col-8" id="detailEmail"></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Gender:</div>
                    <div class="col-8" id="detailGender"></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Date of Birth:</div>
                    <div class="col-8" id="detailDob"></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 fw-bold">Email Verified:</div>
                    <div class="col-8" id="detailVerified"></div>
                </div>
                <div class="row">
                    <div class="col-4 fw-bold">Status:</div>
                    <div class="col-8" id="detailStatus"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-circle"></i> Close
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="lockModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="lockModalTitle">Confirm Action</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/admin/update-user-status">
                <div class="modal-body">
                    <input type="hidden" name="id" id="lockUserId">
                    <input type="hidden" name="status" id="lockUserStatus">
                    <p id="lockMessage"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-danger" id="lockConfirmBtn">
                        <i class="bi bi-check2-circle"></i> Confirm
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openUserDetail(id, username, fullName, email, gender, dob, status, verified) {
        document.getElementById('detailId').textContent = id;
        document.getElementById('detailUsername').textContent = username;
        document.getElementById('detailFullName').textContent = fullName;
        document.getElementById('detailEmail').textContent = email;
        document.getElementById('detailGender').textContent = gender;
        document.getElementById('detailDob').textContent = dob;
        document.getElementById('detailVerified').textContent = verified ? 'Yes' : 'No';
        document.getElementById('detailStatus').textContent = status ? 'Active' : 'Locked';
    }

    function openLockModal(userId, username, currentStatus) {
        document.getElementById('lockUserId').value = userId;
        document.getElementById('lockUserStatus').value = !currentStatus;

        if (currentStatus) {
            document.getElementById('lockModalTitle').textContent = 'Lock User';
            document.getElementById('lockMessage').innerHTML = 'Are you sure you want to lock user <strong>' + username + '</strong>?';
            document.getElementById('lockConfirmBtn').innerHTML = '<i class="bi bi-lock"></i> Lock';
        } else {
            document.getElementById('lockModalTitle').textContent = 'Unlock User';
            document.getElementById('lockMessage').innerHTML = 'Are you sure you want to unlock user <strong>' + username + '</strong>?';
            document.getElementById('lockConfirmBtn').innerHTML = '<i class="bi bi-unlock"></i> Unlock';
        }
    }
</script>
