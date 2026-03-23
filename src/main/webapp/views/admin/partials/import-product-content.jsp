<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="admin-content__section">
    <div class="page-header">
        <div>
            <h4>Import Product</h4>
            <p class="text-muted mb-0">Create new import order</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/import-product?action=history" class="btn btn-back">
            <i class="bi bi-arrow-left"></i> Back to History
        </a>
    </div>

    <c:if test="${not empty error}">
        <c:set var="popupMessage" scope="request" value="${error}" />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card table-card mb-4">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/admin/import-product" method="get" class="d-flex align-items-center gap-3">
                <input type="hidden" name="action" value="importproduct">
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
                    <button type="submit" class="btn btn-primary text-white">
                        <i class="bi bi-repeat me-1"></i> Load Products
                    </button>
                </div>
            </form>
        </div>
    </div>

    <c:if test="${not empty variants}">
        <div class="card table-card">
            <div class="card-body p-0">
                <form action="${pageContext.request.contextPath}/admin/import-product" method="post">
                    <input type="hidden" name="brandId" value="${selectedBrand}">

                    <div class="p-3 bg-light border-bottom d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="bi bi-box-seam me-2"></i>Product Variants
                        </h5>
                        <button type="submit" class="btn btn-add text-white">
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
                                    <th class="import-price-col">Import Price</th>
                                    <th class="import-quantity-col">Quantity</th>
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
                                            <input type="number" class="form-control" name="price" placeholder="0" step="1000" min="0">
                                        </td>
                                        <td>
                                            <input type="number" class="form-control" name="quantity" placeholder="0" min="0">
                                        </td>
                                        <td class="import-hidden-cell">
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
</section>
