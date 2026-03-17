<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Panel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/admin-brand.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/admin-panel.css" rel="stylesheet">
        <c:if test="${currentView == 'statistic'}">
            <link href="${pageContext.request.contextPath}/assets/css/admin-statistic.css" rel="stylesheet">
        </c:if>
        <c:if test="${currentView == 'products'}">
            <link href="${pageContext.request.contextPath}/assets/css/manage-product.css" rel="stylesheet">
        </c:if>
        <c:if test="${currentView == 'inventory'}">
            <link href="${pageContext.request.contextPath}/css/admin-importproduct.css" rel="stylesheet">
        </c:if>
        <c:if test="${currentView == 'voucher'}">
            <link href="${pageContext.request.contextPath}/assets/css/admin-voucher.css" rel="stylesheet">
        </c:if>
        <c:if test="${currentView == 'staff'}">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link href="${pageContext.request.contextPath}/assets/css/manage-staff.css" rel="stylesheet">
        </c:if>

        <c:if test="${currentView == 'feedback'}">
            <link rel="stylesheet"
                  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link href="${pageContext.request.contextPath}/assets/css/manage-feedback.css"
                  rel="stylesheet">
        </c:if>   
    </head>
    <body class="admin-shell-body">
        <div class="admin-shell">
            <aside class="admin-sidebar">
                <div class="admin-sidebar__brand">
                    <h1>Admin Panel</h1>
                    <p>Menu chuc nang quan tri</p>
                </div>

                <nav class="admin-sidebar__nav">
                    <a class="admin-sidebar__link ${currentView == 'dashboard' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/panel?view=dashboard">
                        <i class="bi bi-grid-1x2-fill"></i>
                        <span>Dashboard</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'users' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/panel?view=users">
                        <i class="bi bi-people-fill"></i>
                        <span>Manage Users</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'brands' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-brand">
                        <i class="bi bi-bookmark-star-fill"></i>
                        <span>Manage Brand</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'categories' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-category">
                        <i class="bi bi-grid-fill"></i>
                        <span>Manage Category</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'products' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-product">
                        <i class="bi bi-box-seam-fill"></i>
                        <span>Manage Product</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'inventory' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/import-product?action=history">
                        <i class="bi bi-boxes"></i>
                        <span>Manage Inventory</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'voucher' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-voucher">
                        <i class="bi bi-ticket-perforated-fill"></i>
                        <span>Manage Voucher</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'statistic' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-statistic">
                        <i class="bi bi-bar-chart-fill"></i>
                        <span>Manage Statistic</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'staff' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-staff">
                        <i class="bi bi-person-badge-fill"></i>
                        <span>Manage Staff</span>
                    </a>

                    <a class="admin-sidebar__link ${currentView == 'orders' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-orders">
                        <i class="bi bi-receipt"></i>
                        <span>Manage Orders</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'feedback' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-feedback">

                        <i class="bi bi-chat-square-text-fill"></i>
                        <span>Manage Feedback</span>

                    </a>
                </nav>

                <div class="admin-sidebar__footer">
                    <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">
                        <i class="bi bi-box-arrow-right me-1"></i>Dang xuat
                    </a>
                </div>
            </aside>

            <main class="admin-content">
                <jsp:include page="${contentPage}" />
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
