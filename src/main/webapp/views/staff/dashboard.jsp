<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<main class="container py-4">
    <section class="mb-4">
        <div class="card border-0 shadow-sm">
            <div class="card-body d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                <div>
                    <h1 class="h4 mb-1">Staff Dashboard</h1>
                    <p class="text-muted mb-0">Chọn chức năng quản lý theo screen flow.</p>
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
                    <h2 class="h5"><i class="bi bi-receipt me-2"></i>Manage Orders</h2>
                    <p class="text-muted">Quản lý đơn hàng và xem chi tiết đơn hàng.</p>
                    <div class="d-flex gap-2 flex-wrap">
                    <a class="btn btn-primary" 
   href="${pageContext.request.contextPath}/ManageOrderController?action=list">
   Manage Orders
</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/order-detail">View Order Detail</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-chat-square-text me-2"></i>Manage Feedback</h2>
                    <p class="text-muted">Quản lý phản hồi và xem chi tiết phản hồi.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-feedback">Manage Feedback</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/feedback-detail">View Feedback Detail</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-bookmark-star me-2"></i>Manage Brand</h2>
                    <p class="text-muted">Quản lý thương hiệu và xem chi tiết thương hiệu.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-brand">Manage Brand</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/brand-detail">View Brand Detail</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-ticket-perforated me-2"></i>Manage Voucher</h2>
                    <p class="text-muted">Quản lý voucher và xem chi tiết voucher.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-voucher">Manage Voucher</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/voucher-detail">View Voucher Detail</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-grid me-2"></i>Manage Category</h2>
                    <p class="text-muted">Quản lý danh mục và xem danh sách danh mục.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-category">Manage Category</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/category-list">View Category List</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-6">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5"><i class="bi bi-box-seam me-2"></i>Manage Product</h2>
                    <p class="text-muted">Quản lý sản phẩm và xem chi tiết sản phẩm.</p>
                    <div class="d-flex gap-2 flex-wrap">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-product">Manage Product</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/product-detail">View Product Detail</a>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
