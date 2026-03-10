<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en_US"/>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Manage Feedback - Staff Dashboard</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/manage-feedback.css">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body style="background-color: #f3f4f6;">

<%-- <%@include file="/views/staff/header.jsp" %> --%>

<div class="container" style="margin-top: 40px; margin-bottom: 40px;">
    <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>Manage Reviews (Feedback)</h2>
    </div>

    <c:if test="${not empty successMsg}">
        <div style="background: #d1fae5; color: #065f46; padding: 12px; border-radius: 4px; margin-top: 16px;">
            <i class="fas fa-check-circle"></i> ${successMsg}
        </div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div style="background: #fee2e2; color: #b91c1c; padding: 12px; border-radius: 4px; margin-top: 16px;">
            <i class="fas fa-exclamation-circle"></i> ${errorMsg}
        </div>
    </c:if>

    <table class="feedback-table">
        <thead>
            <tr>
                <th>Customer</th>
                <th>Product</th>
                <th>Rating</th>
                <th>Status</th>
                <th>Review Date</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty feedbacks}">
                    <tr>
                        <td colspan="6" style="text-align: center; padding: 30px;">No reviews from customers found.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="fb" items="${feedbacks}">
                        <tr>
                            <td><strong>${fb.customerName}</strong></td>
                            <td>${fb.productName}</td>
                            <td class="stars">
                                <c:forEach begin="1" end="5" var="i">
                                    <i class="fas fa-star" style="${i <= fb.rating ? '' : 'color: #e5e7eb;'}"></i>
                                </c:forEach>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty fb.responseContent}">
                                        <span class="status-badge status-replied">Responded</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge status-pending">Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <fmt:parseDate value="${fb.reviewedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" type="both" />
                                <fmt:formatDate value="${parsedDate}" pattern="MMM dd, yyyy" />
                            </td>
                            <td>
                                <button class="action-btn btn-view" 
                                        onclick="openDetailModal(${fb.orderDetailId}, '${fb.customerName}', '${fb.productName}', ${fb.rating}, '${fb.reviewComment}', '${fb.responseContent}', '${fb.managerName}')">
                                    <i class="fas fa-reply"></i> Details & Reply
                                </button>
                                
                                <button class="action-btn btn-delete" onclick="confirmDelete(${fb.orderDetailId})">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<div class="modal-overlay" id="feedbackDetailModal">
    <div class="modal-card">
        <div class="modal-header">
            <h3 style="margin: 0;">Review Details</h3>
            <button type="button" onclick="closeDetailModal()" style="background: none; border: none; font-size: 1.5rem; cursor: pointer;">&times;</button>
        </div>
        
        <div style="margin-bottom: 20px; background: #f9fafb; padding: 15px; border-radius: 6px;">
            <p><strong>Customer:</strong> <span id="mdCustomerName"></span></p>
            <p><strong>Product:</strong> <span id="mdProductName"></span></p>
            <p><strong>Rating:</strong> <span id="mdRating" class="stars"></span></p>
            <p style="margin-top: 10px;"><strong>Customer Comment:</strong></p>
            <p id="mdComment" style="font-style: italic; color: #4b5563;"></p>
        </div>

        <form action="${pageContext.request.contextPath}/staff/manage-feedback" method="POST">
            <input type="hidden" name="action" value="reply">
            <input type="hidden" name="orderDetailId" id="mdOrderDetailId">
            
            <div style="margin-bottom: 10px;">
                <label style="font-weight: 600; display: block; margin-bottom: 8px;">Store Response (Staff):</label>
                <textarea name="responseContent" id="mdResponseContent" rows="4" style="width: 100%; padding: 10px; border: 1px solid #d1d5db; border-radius: 4px;" placeholder="Type your answer here..." required></textarea>
                <p id="mdStaffNameInfo" style="font-size: 12px; color: #6b7280; margin-top: 5px; display: none;">
                    Responded by: <strong id="mdStaffName"></strong>
                </p>
            </div>
            
            <div style="text-align: right; margin-top: 20px;">
                <button type="button" onclick="closeDetailModal()" style="padding: 8px 16px; border: 1px solid #d1d5db; background: #fff; border-radius: 4px; cursor: pointer;">Close</button>
                <button type="submit" id="btnSubmitReply" style="padding: 8px 16px; border: none; background: #111827; color: #fff; border-radius: 4px; cursor: pointer;">Save Response</button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    // Populates and opens the modal
    function openDetailModal(id, customer, product, rating, comment, response, staffName) {
        document.getElementById('mdOrderDetailId').value = id;
        document.getElementById('mdCustomerName').textContent = customer;
        document.getElementById('mdProductName').textContent = product;
        
        let starsHtml = '';
        for(let i=1; i<=5; i++) {
            starsHtml += `<i class="fas fa-star" style="\${i <= rating ? '' : 'color: #e5e7eb;'}"></i>`;
        }
        document.getElementById('mdRating').innerHTML = starsHtml;
        
        document.getElementById('mdComment').textContent = (comment && comment !== 'null' && comment !== '') ? comment : "(No comment provided)";
        
        const responseInput = document.getElementById('mdResponseContent');
        const staffInfo = document.getElementById('mdStaffNameInfo');
        const btnSubmit = document.getElementById('btnSubmitReply');
        
        if (response && response !== 'null' && response !== '') {
            responseInput.value = response;
            document.getElementById('mdStaffName').textContent = staffName;
            staffInfo.style.display = 'block';
            btnSubmit.textContent = "Update Response";
        } else {
            responseInput.value = '';
            staffInfo.style.display = 'none';
            btnSubmit.textContent = "Send Response";
        }
        
        document.getElementById('feedbackDetailModal').style.display = 'flex';
    }

    function closeDetailModal() {
        document.getElementById('feedbackDetailModal').style.display = 'none';
    }

    // Handles Soft Delete with English Confirmation
    function confirmDelete(id) {
        Swal.fire({
            title: 'Delete this review?',
            text: "The review will be hidden from customers, but purchase records will remain.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ef4444',
            cancelButtonColor: '#6b7280',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'Cancel'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '${pageContext.request.contextPath}/staff/manage-feedback?action=delete&id=' + id;
            }
        });
    }
</script>

</body>
</html>