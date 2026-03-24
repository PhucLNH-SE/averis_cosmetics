<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>
<section class="admin-content__section admin-page admin-page--feedback staff-page staff-page--feedback">
    <div class="fb-main-container">
        <div class="fb-header-flex">
            <div>
                <h3 class="fb-title">Feedback Management</h3>
                <p class="text-muted small">Manage reviews grouped by product</p>
            </div>
        </div>
        <c:if test="${not empty successMsg}">
            <c:set var="popupMessage" scope="request" value="${successMsg}" />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>
        <c:if test="${not empty errorMsg}">
            <c:set var="popupMessage" scope="request" value="${errorMsg}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

        <div class="card fb-table-card">
            <div class="card-body p-0">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-4">ID</th>
                            <th>Image</th>
                            <th>Product Name</th>
                            <th>Avg. Rating</th>
                            <th class="text-center">Total Reviews</th>
                            <th class="text-end pe-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${productSummaries}" var="ps">
                            <tr>
                                <td class="ps-4 fw-bold text-muted">${ps.productId}</td>
                                <td><img src="${pageContext.request.contextPath}/${ps.productImageUrl}" class="fb-product-img" onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';"></td>
                                <td class="fw-bold">${ps.productName}</td>
                                <td>
                                    <span class="fb-rating-badge">
                                        <i class="fas fa-star me-1"></i>
                                        <fmt:formatNumber value="${ps.averageRating}" maxFractionDigits="1" minFractionDigits="1"/>
                                    </span>
                                </td>
                                <td class="text-center"><span class="fb-count-badge">${ps.totalFeedbacks}</span></td>
                                <td class="text-end pe-4">
                                    <button class="btn btn-primary btn-sm px-3" onclick="viewProductReviews(${ps.productId}, '${ps.productName}')">
                                        <i class="fas fa-eye me-1"></i> View
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="fb-modal-overlay" id="feedbackModal">
        <div class="fb-modal-card">
            <div class="fb-modal-header">
                <h5 class="fw-bold mb-0">Reviews: <span id="modalProductName" class="text-primary"></span></h5>
                <button type="button" class="btn-close" onclick="closeDetailModal()"></button>
            </div>
            <div class="fb-modal-body" id="modalCommentsArea"></div>
        </div>
    </div>
</section>

<script>
    function viewProductReviews(productId, productName) {
        document.getElementById('modalProductName').innerText = productName;
        document.getElementById('modalCommentsArea').innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div></div>';
        document.getElementById('feedbackModal').style.display = 'flex';

        fetch('${pageContext.request.contextPath}/staff/manage-feedback?action=getComments&productId=' + productId)
            .then(function (response) { return response.text(); })
            .then(function (html) {
                document.getElementById('modalCommentsArea').innerHTML = html;
            });
    }

    function closeDetailModal() {
        document.getElementById('feedbackModal').style.display = 'none';
    }

    window.onclick = function (event) {
        if (event.target === document.getElementById('feedbackModal')) {
            closeDetailModal();
        }
    };
</script>

