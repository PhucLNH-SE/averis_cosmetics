<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Panel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
        <c:if test="${currentView == 'staff'}">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        </c:if>

        <c:if test="${currentView == 'feedback'}">
            <link rel="stylesheet"
                  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        </c:if>   
    </head>
    <body class="admin-shell-body">
        <div class="admin-shell">
            <aside class="admin-sidebar">
                <div class="admin-sidebar__brand">
                    <div class="admin-sidebar__brand-mark" aria-hidden="true">A</div>
                    <div class="admin-sidebar__brand-copy">
                        <h1>Admin Panel</h1>
                        <p>Administration menu</p>
                    </div>
                </div>

                <nav class="admin-sidebar__nav">
                    <a class="admin-sidebar__link ${currentView == 'statistic' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-statistic"
                       title="Manage Statistics">
                        <i class="bi bi-bar-chart-fill"></i>
                        <span>Dash Board</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'statistic-report' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-statistic-report"
                       title="Manage Statistic Reports">
                        <i class="bi bi-file-earmark-bar-graph-fill"></i>
                        <span>Statistic Reports</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'users' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-users"
                       title="Manage Users">
                        <i class="bi bi-people-fill"></i>
                        <span>Manage Users</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'brands' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-brand"
                       title="Manage Brands">
                        <i class="bi bi-bookmark-star-fill"></i>
                        <span>Manage Brands</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'categories' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-category"
                       title="Manage Categories">
                        <i class="bi bi-grid-fill"></i>
                        <span>Manage Categories</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'products' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-product"
                       title="Manage Products">
                        <i class="bi bi-box-seam-fill"></i>
                        <span>Manage Products</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'import' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/import-product?action=history"
                       title="Manage Import">
                        <i class="bi bi-boxes"></i>
                        <span>Manage Import</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'voucher' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-voucher"
                       title="Manage Vouchers">
                        <i class="bi bi-ticket-perforated-fill"></i>
                        <span>Manage Vouchers</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'staff' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-staff"
                       title="Manage Staff">
                        <i class="bi bi-person-badge-fill"></i>
                        <span>Manage Staff</span>
                    </a>

                    <a class="admin-sidebar__link ${currentView == 'orders' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-orders"
                       title="Manage Orders">
                        <i class="bi bi-receipt"></i>
                        <span>Manage Orders</span>
                    </a>
                    <a class="admin-sidebar__link ${currentView == 'feedback' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/manage-feedback"
                       title="Manage Feedback">

                        <i class="bi bi-chat-square-text-fill"></i>
                        <span>Manage Feedback</span>

                    </a>
                </nav>
                <div class="admin-sidebar__footer">
                    <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout" title="Sign out">
                        <i class="bi bi-box-arrow-right"></i>
                        <span class="admin-sidebar__footer-text">Sign out</span>
                    </a>
                </div>
            </aside>

            <main class="admin-content">
                <jsp:include page="${contentPage}" />
            </main>
        </div>

        <jsp:include page="/WEB-INF/views/common/popup.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>





