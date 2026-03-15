<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Staff Panel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/admin-brand.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/admin-panel.css" rel="stylesheet">
        <c:if test="${currentView == 'orders'}">
            <link href="${pageContext.request.contextPath}/assets/css/staff-orders.css" rel="stylesheet">
        </c:if>
        <c:if test="${currentView == 'feedback'}">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link href="${pageContext.request.contextPath}/assets/css/manage-feedback.css" rel="stylesheet">
        </c:if>
    </head>
    <body class="admin-shell-body">
        <div class="admin-shell">
            <aside class="admin-sidebar">
                <div class="admin-sidebar__brand">
                    <h1>Staff Panel</h1>
                    <p>Menu chuc nang nhan vien</p>
                </div>

                <nav class="admin-sidebar__nav">
                    <a class="admin-sidebar__link ${currentView == 'dashboard' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/panel?view=dashboard">
                        <i class="bi bi-grid-1x2-fill"></i>
                        <span>Dashboard</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'orders' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/manage-orders">
                        <i class="bi bi-receipt-cutoff"></i>
                        <span>Manage Orders</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'feedback' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/manage-feedback">
                        <i class="bi bi-chat-square-text-fill"></i>
                        <span>Manage Feedback</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'brands' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/manage-brand">
                        <i class="bi bi-bookmark-star-fill"></i>
                        <span>View Brands</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'categories' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/manage-category">
                        <i class="bi bi-grid-fill"></i>
                        <span>View Categories</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'products' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/manage-product">
                        <i class="bi bi-box-seam-fill"></i>
                        <span>Manage Product</span>
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
