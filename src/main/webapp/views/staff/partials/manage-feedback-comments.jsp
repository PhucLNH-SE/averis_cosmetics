<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>
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
                        <a href="${pageContext.request.contextPath}/staff/manage-feedback?action=delete&id=${c.orderDetailId}" class="btn btn-sm btn-outline-danger rounded-circle" onclick="return confirm('Delete this review?')">
                            <i class="fas fa-trash"></i>
                        </a>
                    </div>
                    <div class="collapse mt-3" id="replyForm${c.orderDetailId}">
                        <form action="${pageContext.request.contextPath}/staff/manage-feedback" method="post">
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

