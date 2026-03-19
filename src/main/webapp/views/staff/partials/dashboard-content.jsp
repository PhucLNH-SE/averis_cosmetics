<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="admin-content__section">
    <div class="admin-content__header">
        <div>
            <h2 class="admin-content__title">Staff Dashboard</h2>
            <p class="admin-content__subtitle">Choose a staff function from the left menu.</p>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Orders</h3>
                    <p>Track and update order statuses from the staff shell.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/staff/manage-orders">Open</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>Manage Feedback</h3>
                    <p>Manage product reviews and customer replies directly in the shell.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/manage-feedback">Open</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>View Brands</h3>
                    <p>View the brand list in the right content area.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/manage-brand">Open</a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card admin-feature-card h-100">
                <div class="card-body">
                    <h3>View Categories</h3>
                    <p>View the category list in the same shell layout.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/staff/manage-category">Open</a>
                </div>
            </div>
        </div>
    </div>
</section>
