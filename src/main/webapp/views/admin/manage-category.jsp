<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Categories - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        .table-hover tbody tr:hover {
            background-color: #f5f5f5;
        }
    </style>
</head>
<body class="bg-light">
<main class="container py-4">
    <section class="mb-4">
        <div class="card border-0 shadow-sm">
            <div class="card-body d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                <div>
                    <h1 class="h4 mb-1">Manage Categories</h1>
                    <p class="text-muted mb-0">Manage product categories.</p>
                </div>
                <a class="btn btn-success" href="${pageContext.request.contextPath}/admin/category?action=add">
                    <i class="bi bi-plus-circle me-1"></i>Add Category
                </a>
            </div>
        </div>
    </section>

    <c:if test="${param.success == 1}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle me-2"></i>Operation successful!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${param.error == 1}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-circle me-2"></i>An error occurred. Please try again!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty categories}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th style="width: 10%">ID</th>
                                    <th style="width: 50%">Category Name</th>
                                    <th style="width: 15%">Status</th>
                                    <th style="width: 25%">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="category" items="${categories}">
                                    <tr>
                                        <td><strong>${category.categoryId}</strong></td>
                                        <td>${category.name}</td>
                                        <td>
                                            <c:if test="${category.status}">
                                                <span class="badge bg-success">Active</span>
                                            </c:if>
                                            <c:if test="${!category.status}">
                                                <span class="badge bg-secondary">Inactive</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/category?action=edit&id=${category.categoryId}" class="btn btn-sm btn-primary">
                                                <i class="bi bi-pencil me-1"></i>Edit
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/category?action=delete&id=${category.categoryId}" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete?');">
                                                <i class="bi bi-trash me-1"></i>Delete
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info" role="alert">
                        <i class="bi bi-info-circle me-2"></i>No categories found. <a href="${pageContext.request.contextPath}/admin/category?action=add">Add a new category</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/views/admin/dashboard.jsp" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
        </a>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
