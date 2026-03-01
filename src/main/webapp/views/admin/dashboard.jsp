<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<main class="container py-4">
    <section class="mb-4">
        <div class="card border-0 shadow-sm">
            <div class="card-body d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                <div>
                    <h1 class="h4 mb-1">Admin Dashboard</h1>
                    <p class="text-muted mb-0">Điều hướng nhanh theo screen flow quản trị.</p>
                </div>
                <a class="btn btn-outline-danger" href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-right me-1"></i>Đăng xuất
                </a>
            </div>
        </div>
    </section>

    <section class="row g-3">
        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-people me-2"></i>Manage Users</h2>
                    <p class="text-muted">Quản lý người dùng, cập nhật và xem chi tiết.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-users">Manage Users</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-user">Update User</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/user-detail">View User Detail</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-bookmark-star me-2"></i>Manage Brand</h2>
                    <p class="text-muted">Quản lý thương hiệu, thêm mới và cập nhật.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-brand">Manage Brand</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/add-brand">Add Brand</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-brand">Update Brand</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-grid me-2"></i>Manage Category</h2>
                    <p class="text-muted">Quản lý danh mục, thêm mới và cập nhật danh mục.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-category">Manage Category</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/add-category">Add Category</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-category">Update Category</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-ticket-perforated me-2"></i>Manage Voucher</h2>
                    <p class="text-muted">Quản lý voucher, tạo mới và cập nhật voucher.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-voucher">Manage Voucher</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/create-voucher">Create Voucher</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-voucher">Update Voucher</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-boxes me-2"></i>Manage Inventory</h2>
                    <p class="text-muted">Quản lý số lượng tồn kho sản phẩm.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-inventory">Manage Inventory</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-product-quantity">Update Product Quantity</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-box-seam me-2"></i>Manage Product</h2>
                    <p class="text-muted">Quản lý sản phẩm, thêm mới và cập nhật thông tin.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-product">Manage Product</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/add-product">Add Product</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-product">Update Product</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-bar-chart-line me-2"></i>Manage Statistic</h2>
                    <p class="text-muted">Theo dõi thống kê tháng.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-month-statistic">Manage Month Statistic</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-person-badge me-2"></i>Manage Staff</h2>
                    <p class="text-muted">Quản lý nhân sự: thêm, cập nhật và xem chi tiết.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/manage-staff">Manage Staff</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/add-staff">Add Staff</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/update-staff">Update Staff</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/staff-detail">View Staff Detail</a>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
