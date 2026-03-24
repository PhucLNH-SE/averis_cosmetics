<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="admin-content__section admin-page admin-page--staff-detail">
    <div class="container-fluid staff-main-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold mb-0">Staff Detail</h3>
                <p class="text-muted small">Thông tin nhân viên và đơn hàng đã xử lý</p>
            </div>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/manage-staff">
                <i class="fas fa-arrow-left me-1"></i> Quay lại
            </a>
        </div>

        <div class="card staff-detail-card mb-4">
            <div class="card-body p-4">
                <div class="row g-3">
                    <div class="col-md-4">
                        <div class="staff-detail-label">Staff ID</div>
                        <div class="staff-detail-value">#${staff.managerId}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="staff-detail-label">Họ tên</div>
                        <div class="staff-detail-value">${staff.fullName}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="staff-detail-label">Email</div>
                        <div class="staff-detail-value">${staff.email}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="staff-detail-label">Role</div>
                        <div class="staff-detail-value">
                            <span class="role-badge-staff">${staff.managerRole}</span>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="staff-detail-label">Trạng thái</div>
                        <div class="staff-detail-value">
                            <span class="${staff.status ? 'status-active' : 'status-inactive'}">
                                <i class="fas ${staff.status ? 'fa-check-circle' : 'fa-ban'} me-1"></i>
                                ${staff.status ? 'Active' : 'Banned'}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="card staff-table-card">
            <div class="card-body p-4">
                <h5 class="fw-bold mb-3">Đơn hàng đã xử lý</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Mã đơn</th>
                                <th>Người nhận</th>
                                <th>Trạng thái</th>
                                <th>Thanh toán</th>
                                <th>Tổng tiền</th>
                                <th>Ngày tạo</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty handledOrders}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted">Chưa có đơn hàng</td>
                                </tr>
                            </c:if>
                            <c:forEach items="${handledOrders}" var="o">
                                <tr>
                                    <td>#${o.orderId}</td>
                                    <td>${o.receiverName}</td>
                                    <td>${o.orderStatus}</td>
                                    <td>${o.paymentStatus}</td>
                                    <td>
                                        <fmt:formatNumber value="${o.totalAmount}" pattern="#,##0" /> VND
                                    </td>
                                    <td>${o.createdAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        
        <div class="card staff-table-card mt-4">
            <div class="card-body p-4">
                <h5 class="fw-bold mb-3">Feedback đã phản hồi</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Mã đơn</th>
                                <th>Sản phẩm</th>
                                <th>Khách hàng</th>
                                <th>Rating</th>
                                <th>Nội dung phản hồi</th>
                                <th>Ngày phản hồi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty feedbacks}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted">Chưa có phản hồi</td>
                                </tr>
                            </c:if>
                            <c:forEach items="${feedbacks}" var="f">
                                <tr>
                                    <td>#${f.orderId}</td>
                                    <td>${f.productName}</td>
                                    <td>${f.customerName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${f.rating != null}">${f.rating}</c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${f.responseContent}</td>
                                    <td>${f.respondedAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
