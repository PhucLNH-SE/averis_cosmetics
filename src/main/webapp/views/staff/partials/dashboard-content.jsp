<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="admin-content__section">
    <div class="admin-content__header">
        <div>
            <h2 class="admin-content__title">Staff Dashboard</h2>
            <p class="admin-content__subtitle">Chon chuc nang nhan vien tu menu ben trai.</p>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Orders</h3>
                    <p>Theo doi va cap nhat trang thai don hang trong staff shell.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-orders">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Feedback</h3>
                    <p>Quan ly review san pham va phan hoi khach hang ngay trong shell.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/manage-feedback">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>View Brands</h3>
                    <p>Xem danh sach thuong hieu trong khu vuc content ben phai.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/manage-brand">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>View Categories</h3>
                    <p>Xem danh sach danh muc trong cung giao dien shell.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/manage-category">Mo chuc nang</a>
                </div>
            </div>
        </div>
    </div>
</section>
