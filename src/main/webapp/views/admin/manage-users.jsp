<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="admin-users-body">
<main class="admin-users-wrapper">
    <section class="admin-users-header">
        <div>
            <h1>Quản lý người dùng</h1>
            <p>Danh sách tài khoản user trong hệ thống</p>
        </div>
        <a class="admin-users-back-btn" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="bi bi-arrow-left"></i> Quay lại
        </a>
    </section>

    <section class="admin-users-panel">
        <div class="admin-users-table-wrap">
            <table class="admin-users-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Họ tên</th>
                    <th>Email</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty users}">
                        <tr>
                            <td colspan="6" class="admin-users-empty">Không có user nào.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.customerId}</td>
                                <td>${user.username}</td>
                                <td>${user.fullName}</td>
                                <td>${empty user.email ? '-' : user.email}</td>
                                <td>
                                    <span class="${user.status ? 'user-status-active' : 'user-status-inactive'}">
                                        <i class="bi ${user.status ? 'bi-check-circle-fill' : 'bi-slash-circle-fill'}"></i>
                                        ${user.status ? 'Hoạt động' : 'Đã khóa'}
                                    </span>
                                </td>
                                <td>
                                    <div class="admin-users-actions">
                                        <button type="button"
                                           class="admin-users-btn-detail"
                                           data-id="${user.customerId}"
                                           data-username="${user.username}"
                                           data-fullname="${user.fullName}"
                                           data-email="${empty user.email ? '-' : user.email}"
                                           data-gender="${empty user.gender ? '-' : user.gender}"
                                           data-dob="${empty user.dateOfBirth ? '-' : user.dateOfBirth}"
                                           data-status="${user.status}"
                                           data-verified="${user.emailVerified}"
                                           onclick="openUserPopup(this)">
                                            <i class="bi bi-eye"></i> Chi tiết
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/update-user-status" method="post">
                                            <input type="hidden" name="id" value="${user.customerId}">
                                            <input type="hidden" name="status" value="${!user.status}">
                                            <button type="submit" class="${user.status ? 'admin-users-btn-lock' : 'admin-users-btn-unlock'}">
                                                <i class="bi ${user.status ? 'bi-lock' : 'bi-unlock'}"></i>
                                                ${user.status ? 'Khóa' : 'Mở khóa'}
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>

<div id="userDetailPopup" class="admin-popup-overlay" onclick="closeUserPopup(event)">
    <div class="admin-popup-card">
        <div class="admin-popup-head">
            <h3>Chi tiết người dùng</h3>
            <button type="button" class="admin-popup-close" onclick="closeUserPopup()">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>

        <div class="admin-popup-body">
            <div class="admin-popup-row"><label>ID</label><span id="popupUserId"></span></div>
            <div class="admin-popup-row"><label>Username</label><span id="popupUsername"></span></div>
            <div class="admin-popup-row"><label>Họ tên</label><span id="popupFullName"></span></div>
            <div class="admin-popup-row"><label>Email</label><span id="popupEmail"></span></div>
            <div class="admin-popup-row"><label>Giới tính</label><span id="popupGender"></span></div>
            <div class="admin-popup-row"><label>Ngày sinh</label><span id="popupDob"></span></div>
            <div class="admin-popup-row"><label>Email verified</label><span id="popupVerified"></span></div>
            <div class="admin-popup-row"><label>Trạng thái</label><span id="popupStatus"></span></div>
        </div>

        <div class="admin-popup-actions">
            <form action="${pageContext.request.contextPath}/admin/update-user-status" method="post">
                <input type="hidden" id="popupFormUserId" name="id">
                <input type="hidden" id="popupFormStatus" name="status">
                <button type="submit" id="popupActionBtn" class="admin-users-btn-lock">
                    <i class="bi bi-lock"></i> Khóa tài khoản
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    function openUserPopup(button) {
        var id = button.getAttribute('data-id');
        var username = button.getAttribute('data-username');
        var fullName = button.getAttribute('data-fullname');
        var email = button.getAttribute('data-email');
        var gender = button.getAttribute('data-gender');
        var dob = button.getAttribute('data-dob');
        var status = button.getAttribute('data-status') === 'true';
        var verified = button.getAttribute('data-verified') === 'true';

        document.getElementById('popupUserId').textContent = id;
        document.getElementById('popupUsername').textContent = username;
        document.getElementById('popupFullName').textContent = fullName;
        document.getElementById('popupEmail').textContent = email;
        document.getElementById('popupGender').textContent = gender;
        document.getElementById('popupDob').textContent = dob;
        document.getElementById('popupVerified').textContent = verified ? 'Yes' : 'No';
        document.getElementById('popupStatus').textContent = status ? 'Hoạt động' : 'Đã khóa';
        document.getElementById('popupFormUserId').value = id;
        document.getElementById('popupFormStatus').value = (!status).toString();

        var btn = document.getElementById('popupActionBtn');
        if (status) {
            btn.className = 'admin-users-btn-lock';
            btn.innerHTML = '<i class="bi bi-lock"></i> Khóa tài khoản';
        } else {
            btn.className = 'admin-users-btn-unlock';
            btn.innerHTML = '<i class="bi bi-unlock"></i> Mở khóa tài khoản';
        }

        document.getElementById('userDetailPopup').classList.add('show');
    }

    function closeUserPopup(event) {
        if (!event || event.target.id === 'userDetailPopup') {
            document.getElementById('userDetailPopup').classList.remove('show');
        }
    }
</script>
</body>
</html>
