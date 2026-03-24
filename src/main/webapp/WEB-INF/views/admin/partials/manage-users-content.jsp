<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section admin-page admin-page--users">
    <div class="page-header">
        <div>
            <h4>Manage Users</h4>
            <p class="text-muted mb-0">List of users in the system</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/manage-statistic" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Back to Dashboard
        </a>
    </div>

    <c:if test="${param.success == 'locked'}">
        <c:set var="popupMessage" scope="request" value="User banned successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'unlocked'}">
        <c:set var="popupMessage" scope="request" value="User unlocked successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'lockFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to ban user." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.success == 'unlockFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to unlock user." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'notFound'}">
        <c:set var="popupMessage" scope="request" value="User not found." />
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
                                    <a href="${pageContext.request.contextPath}/admin/manage-users?action=detail&id=${user.customerId}"
                                       class="btn btn-info btn-sm text-white me-1">
                                        <i class="bi bi-eye"></i> View Detail
                                    </a>

                                    <c:choose>
                                        <c:when test="${user.status}">
                                            <form method="post" action="${pageContext.request.contextPath}/admin/manage-users" class="d-inline">
                                                <input type="hidden" name="action" value="ban">
                                                <input type="hidden" name="id" value="${user.customerId}">
                                                <button type="submit" class="btn btn-danger btn-sm text-white">
                                                    <i class="bi bi-lock"></i> Ban
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="post" action="${pageContext.request.contextPath}/admin/manage-users" class="d-inline">
                                                <input type="hidden" name="action" value="unlock">
                                                <input type="hidden" name="id" value="${user.customerId}">
                                                <button type="submit" class="btn btn-success btn-sm text-white">
                                                    <i class="bi bi-unlock"></i> Unlock
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
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

<c:if test="${modalMode == 'detail' and not empty selectedUser}">
    <div class="modal fade show d-block" id="userDetailModal" tabindex="-1" aria-modal="true" role="dialog">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">User Detail</h5>
                    <a href="${pageContext.request.contextPath}/admin/manage-users" class="btn-close" aria-label="Close"></a>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">User ID</label>
                            <input type="text" class="form-control" value="${selectedUser.customerId}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Username</label>
                            <input type="text" class="form-control" value="${selectedUser.username}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Full Name</label>
                            <input type="text" class="form-control" value="${selectedUser.fullName}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Email</label>
                            <input type="text" class="form-control" value="${empty selectedUser.email ? '-' : selectedUser.email}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Gender</label>
                            <input type="text" class="form-control" value="${empty selectedUser.gender ? '-' : selectedUser.gender}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Date of Birth</label>
                            <input type="text" class="form-control" value="${empty selectedUser.dateOfBirth ? '-' : selectedUser.dateOfBirth}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Status</label>
                            <input type="text" class="form-control" value="${selectedUser.status ? 'Active' : 'Locked'}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Email Verified</label>
                            <input type="text" class="form-control" value="${selectedUser.emailVerified ? 'Yes' : 'No'}" readonly>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="${pageContext.request.contextPath}/admin/manage-users" class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Close
                    </a>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-backdrop fade show"></div>
</c:if>
