<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Categories</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-brand.css" rel="stylesheet">
    <style>
        .empty-state { padding: 40px 0; }
    </style>
</head>
<body>
    <div class="container py-4">
        <div class="page-header d-flex justify-content-between align-items-center mb-3">
            <div>
                <h4>Manage Categories</h4>
                <p class="text-muted mb-0">Category list</p>
            </div>
            <a href="${pageContext.request.contextPath}/staff/dashboard" class="btn btn-outline-secondary">Back</a>
        </div>

        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Category Name</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cat" items="${categories}">
                                <tr>
                                    <td class="px-4">${cat.categoryId}</td>
                                    <td><strong>${cat.name}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cat.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty categories}">
                                <tr>
                                    <td colspan="3" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No categories found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
