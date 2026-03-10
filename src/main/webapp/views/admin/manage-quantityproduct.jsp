<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Product Quantity</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-brand.css" rel="stylesheet">
    <style>
        .product-img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 8px;
        }
        .stock-input {
            width: 80px;
            padding: 6px 10px;
            border: 1px solid #ced4da;
            border-radius: 8px;
        }
        .stock-input:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
            outline: none;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <!-- Header -->
        <div class="page-header">
            <div>
                <h4>Manage Product Quantity</h4>
                <p class="text-muted mb-0">List of products with stock management</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                Back
            </a>
        </div>

        <!-- Alerts -->
        <c:if test="${param.success == 'update'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Stock updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.error == 'updateFailed'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Failed to update stock!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Table Card -->
        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Image</th>
                                <th>Product Name</th>
                                <th>Variant</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Status</th>
                                <th class="text-end px-4">Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${list}" var="v">
                                <tr>
                                    <td class="px-4">${v.variantId}</td>
                                    <td>
                                        <c:if test="${not empty v.imageUrl}">
                                            <img src="${pageContext.request.contextPath}/${v.imageUrl}" class="product-img" alt="${v.productName}">
                                        </c:if>
                                    </td>
                                    <td><strong>${v.productName}</strong></td>
                                    <td>${v.variantName}</td>
                                    <td>${v.categoryName}</td>
                                    <td><fmt:formatNumber value="${v.price}" pattern="#,##0"/> ₫</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${v.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end px-4">
                                        <form action="${pageContext.request.contextPath}/PQuantityManagerController" method="post" class="d-flex align-items-center justify-content-end gap-2">
                                            <input type="hidden" name="action" value="updateStock">
                                            <input type="hidden" name="variantId" value="${v.variantId}">
                                            <input type="number" name="stock" value="${v.stock}" min="0" class="stock-input">
                                            <button type="submit" class="btn btn-edit btn-sm text-white">
                                                <i class="bi bi-check2"></i> Save
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty list}">
                                <tr>
                                    <td colspan="8" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No products found
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
