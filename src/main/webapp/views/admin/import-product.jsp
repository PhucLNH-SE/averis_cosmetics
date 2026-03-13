<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Import Product</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-importproduct.css" rel="stylesheet">
    <style>
        body {
            background-color: #f5f6fa;
        }
        .form-select, .form-control {
            border-radius: 8px;
            padding: 10px 14px;
            border: 1px solid #ced4da;
        }
        .form-select:focus, .form-control:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
        }
        .btn-import {
            background-color: #28a745;
            border-color: #28a745;
            border-radius: 8px;
            padding: 10px 24px;
            font-weight: 500;
        }
        .btn-import:hover {
            background-color: #218838;
            border-color: #218838;
        }
        .btn-load {
            background-color: #0d6efd;
            border-color: #0d6efd;
            border-radius: 8px;
            padding: 10px 20px;
            font-weight: 500;
        }
        .btn-load:hover {
            background-color: #0b5ed7;
            border-color: #0b5ed7;
        }
        .btn-back {
            background-color: #6c757d;
            border-color: #6c757d;
            border-radius: 8px;
            padding: 10px 20px;
            font-weight: 500;
            color: white;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        .btn-back:hover {
            background-color: #5a6268;
            border-color: #5a6268;
            color: white;
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }
        .page-header h4 {
            color: #2c3e50;
            font-weight: 600;
            margin-bottom: 4px;
        }
        .page-header .text-muted {
            color: #6c757d;
            font-size: 14px;
        }
        .table-card {
            border: none;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            overflow: hidden;
        }
        .table thead th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #e9ecef;
            color: #495057;
            font-weight: 600;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 16px;
        }
        .table tbody td {
            padding: 16px;
            vertical-align: middle;
            border-bottom: 1px solid #f0f0f0;
        }
        .table tbody tr:hover {
            background-color: #f8f9fa;
        }
        .alert {
            border-radius: 8px;
            border: none;
            padding: 12px 16px;
        }
        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
        }
        .stock-cell {
            font-weight: 600;
        }
        .stock-low {
            color: #dc3545;
        }
        .stock-ok {
            color: #28a745;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <!-- Header -->
        <div class="page-header">
            <div>
                <h4>Import Product</h4>
                <p class="text-muted mb-0">Create new import order</p>
            </div>
            <a href="${pageContext.request.contextPath}/ImportProductController?action=history" class="btn btn-back">
                <i class="bi bi-arrow-left"></i> Back to History
            </a>
        </div>

        <!-- Error Alert -->
        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Brand Selection -->
        <div class="card table-card mb-4">
            <div class="card-body">
                <form action="ImportProductController" method="get" class="d-flex align-items-center gap-3">
                    <div class="flex-grow-1">
                        <label for="brandId" class="form-label fw-semibold">Choose Brand</label>
                        <select name="brandId" id="brandId" class="form-select">
                            <option value="" disabled ${empty selectedBrand ? 'selected' : ''}>-- Select Brand --</option>
                            <c:forEach items="${brands}" var="b">
                                <option value="${b.brandId}" ${selectedBrand == b.brandId ? 'selected' : ''}>
                                    ${b.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mt-4">
                        <button type="submit" class="btn btn-load text-white">
                             <i class="bi-repeat me-1"></i> Load Products
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Import Form -->
        <c:if test="${not empty variants}">
            <div class="card table-card">
                <div class="card-body p-0">
                    <form action="ImportProductController" method="post">
                        <input type="hidden" name="brandId" value="${selectedBrand}">
                        
                        <div class="p-3 bg-light border-bottom d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">
                                <i class="bi bi-box-seam me-2"></i>Product Variants
                            </h5>
                            <button type="submit" class="btn btn-import text-white">
                                <i class="bi bi-check-circle me-1"></i> Import Products
                            </button>
                        </div>

                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>Variant</th>
                                        <th>Current Stock</th>
                                        <th style="width: 180px;">Import Price (VNĐ)</th>
                                        <th style="width: 150px;">Quantity</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${variants}" var="v">
                                        <tr>
                                            <td><strong>${v.productName}</strong></td>
                                            <td>${v.variantName}</td>
                                            <td>
                                                <span class="stock-cell ${v.stock > 10 ? 'stock-ok' : 'stock-low'}">
                                                    ${v.stock}
                                                </span>
                                            </td>
                                            <td>
                                                <input type="number" class="form-control" name="price" 
                                                       placeholder="0" step="1000" min="0">
                                            </td>
                                            <td>
                                                <input type="number" class="form-control" name="quantity" 
                                                       placeholder="0" min="0">
                                            </td>
                                            <td style="display: none;">
                                                <input type="hidden" name="variantId" value="${v.variantId}">
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
