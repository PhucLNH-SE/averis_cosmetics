<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Brands</title>
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
                <h4>Manage Brands</h4>
                <p class="text-muted mb-0">Brand list</p>
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
                                <th>Brand Name</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="brand" items="${brands}">
                                <tr>
                                    <td class="px-4">${brand.brandId}</td>
                                    <td><strong>${brand.name}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${brand.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty brands}">
                                <tr>
                                    <td colspan="3" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No brands found
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
