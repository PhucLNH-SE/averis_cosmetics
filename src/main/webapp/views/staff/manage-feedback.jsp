<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="en_US"/>

<c:choose>
    <c:when test="${isAjax}">
        <c:choose>
            <c:when test="${empty comments}">
                <div class="alert alert-light text-center py-5">No reviews found for this product.</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="c" items="${comments}">
                    <div class="card mb-4 border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <div>
                                    <div class="d-flex align-items-center gap-2 mb-1">
                                        <h6 class="fw-bold mb-0">${c.customerName}</h6>
                                        <span class="badge bg-secondary" style="font-size: 0.7rem; font-weight: 500;">
                                            Order #${c.orderId}
                                        </span>
                                    </div>
                                    <small class="text-muted">
                                        <fmt:parseDate value="${c.reviewedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" type="both" />
                                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                    </small>
                                </div>
                                <div class="text-warning">
                                    <c:forEach begin="1" end="5" var="i">
                                        <i class="${i <= c.rating ? 'fas' : 'far'} fa-star small"></i>
                                    </c:forEach>
                                </div>
                            </div>
                            <p class="card-text fb-comment-text mb-4">"${c.reviewComment}"</p>
                            <c:if test="${not empty c.responseContent}">
                                <div class="fb-staff-response mb-4">
                                    <div class="small fw-bold text-primary mb-1">Staff (${c.managerName}):</div>
                                    <div class="small text-dark">${c.responseContent}</div>
                                </div>
                            </c:if>
                            <div class="d-flex gap-2">
                                <button class="btn btn-sm btn-primary px-3 rounded-pill" type="button" data-bs-toggle="collapse" data-bs-target="#replyForm${c.orderDetailId}">
                                    <i class="fas fa-reply me-1"></i> ${not empty c.responseContent ? 'Edit' : 'Reply'}
                                </button>
                                <a href="manage-feedback?action=delete&id=${c.orderDetailId}" class="btn btn-sm btn-outline-danger rounded-circle" onclick="return confirm('Delete this review?')">
                                    <i class="fas fa-trash"></i>
                                </a>
                            </div>
                            <div class="collapse mt-3" id="replyForm${c.orderDetailId}">
                                <form action="manage-feedback" method="POST">
                                    <input type="hidden" name="action" value="reply">
                                    <input type="hidden" name="orderDetailId" value="${c.orderDetailId}">
                                    <textarea name="responseContent" class="form-control mb-2 rounded-3" rows="2">${c.responseContent}</textarea>
                                    <div class="text-end">
                                        <button type="submit" class="btn btn-success btn-sm px-4">Save Response</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Feedback Management - Averis Cosmetics</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/manage-feedback.css?v=4">
        </head>
        <body class="bg-light">
            <div class="container fb-main-container">
                <div class="fb-header-flex">
                    <div>
                        <h3 class="fb-title">Feedback Management</h3>
                        <p class="text-muted small">Manage reviews grouped by product</p>
                    </div>
                    <button class="btn btn-outline-secondary px-3" onclick="history.back()">← Back</button>
                </div>
                <c:if test="${not empty successMsg}">
                    <div class="alert alert-success border-0 shadow-sm mb-4">${successMsg}</div>
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
                                            <button class="btn btn-primary btn-sm px-3 rounded-pill" onclick="viewProductReviews(${ps.productId}, '${ps.productName}')">
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
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                function viewProductReviews(productId, productName) {
                    document.getElementById('modalProductName').innerText = productName;
                    document.getElementById('modalCommentsArea').innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div></div>';
                    document.getElementById('feedbackModal').style.display = 'flex';

                    fetch('manage-feedback?action=getComments&productId=' + productId)
                        .then(response => response.text())
                        .then(html => {
                            document.getElementById('modalCommentsArea').innerHTML = html;
                        });
                }
                function closeDetailModal() { document.getElementById('feedbackModal').style.display = 'none'; }
                window.onclick = function(event) { if (event.target == document.getElementById('feedbackModal')) closeDetailModal(); }
            </script>
        </body>
        </html>
    </c:otherwise>
</c:choose>