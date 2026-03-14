<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="admin-content__section">
    <div class="admin-content__header">
        <div>
            <h2 class="admin-content__title">Dashboard</h2>
            <p class="admin-content__subtitle">Chon chuc nang quan tri tu menu ben trai.</p>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Users</h3>
                    <p>Xem danh sach nguoi dung va cap nhat trang thai tai khoan.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/panel?view=users">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Statistic</h3>
                    <p>Khu vuc thong ke theo thang se duoc dua vao shell nay.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/manage-statistic">Mo thong ke</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Product</h3>
                    <p>Cac module admin khac se duoc migrate dan vao khu vuc content ben phai.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/manage-product">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Inventory</h3>
                    <p>Theo doi lich su nhap hang va cap nhat ton kho trong admin shell.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/import-product?action=history">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Voucher</h3>
                    <p>Quan ly voucher trong cung admin shell, giu nguyen sidebar ben trai.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/manage-voucher">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Brand</h3>
                    <p>Quan ly thuong hieu trong admin shell va tai su dung form modal hien co.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/manage-brand">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Category</h3>
                    <p>Quan ly danh muc trong admin shell voi luong CRUD giong manage brand.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/manage-category">Mo chuc nang</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Staff</h3>
                    <p>Quan ly tai khoan staff va admin trong cung shell, khong roi khoi sidebar.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/manage-staff">Mo chuc nang</a>
                </div>
            </div>
        </div>
    </div>
</section>
