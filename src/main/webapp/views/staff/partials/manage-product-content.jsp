<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold mb-0">Product Management</h3>
            <p class="text-muted small mb-0">Manage your cosmetics inventory</p>
        </div>
        <a class="btn btn-outline-secondary px-3" href="${pageContext.request.contextPath}/staff/panel?view=dashboard">
            Back to Dashboard
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-4">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Image</th>
                        <th>Product Name</th>
                        <th>Category</th>
                        <th>Price Range</th>
                        <th class="text-center">Price Stock (Giá nhập)</th>
                        <th>Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty listP}">
                            <c:forEach items="${listP}" var="p">
                                <tr>
                                    <td class="fw-bold">${p.productId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty p.mainImage}">
                                                <img src="${pageContext.request.contextPath}/assets/img/${p.mainImage}"
                                                     class="product-img-td border"
                                                     width="50"
                                                     height="50"
                                                     style="object-fit:cover;"
                                                     onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/assets/img/Logo.png"
                                                     class="product-img-td border"
                                                     width="50"
                                                     height="50">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${p.name}</td>
                                    <td>${p.category.name}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.price == 0}">
                                                <span class="text-muted small">No variants</span>
                                            </c:when>
                                            <c:when test="${p.price == p.maxPrice}">
                                                <span class="text-primary fw-bold">
                                                    <fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-primary fw-bold">
                                                    <fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND -
                                                    <fmt:formatNumber value="${p.maxPrice}" pattern="#,##0"/> VND
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${empty p.variants}">
                                                <span class="text-muted small text-center d-block">Không có</span>
                                            </c:when>
                                            <c:otherwise>
                                                <ul class="list-unstyled mb-0 small">
                                                    <c:forEach items="${p.variants}" var="variant">
                                                        <li class="mb-1">
                                                            <span class="fw-bold">${variant.variantName}</span> -
                                                            <span class="text-success">
                                                                <fmt:formatNumber value="${variant.importPrice}" pattern="#,##0"/> ₫
                                                            </span>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <span class="${p.status ? 'text-success' : 'text-danger'} fw-bold">
                                            <i class="bi ${p.status ? 'bi-check-circle-fill' : 'bi-x-circle-fill'} me-1"></i>
                                            ${p.status ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td class="text-center">
                                        <button class="btn btn-primary btn-sm px-3"
                                                data-id="${p.productId}"
                                                data-name="<c:out value='${p.name}' />"
                                                onclick="openDetailModal(this)">
                                            Detail
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="text-center text-muted py-4">No products found.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</section>

<div id="all-product-details-storage" class="d-none">
    <c:forEach items="${listP}" var="p">
        <div id="product-detail-data-${p.productId}">
            <div class="mb-3 text-center">
                <c:choose>
                    <c:when test="${not empty p.mainImage}">
                        <img src="${pageContext.request.contextPath}/assets/img/${p.mainImage}"
                             class="staff-detail-preview rounded border"
                             alt="${p.name}"
                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/assets/img/Logo.png"
                             class="staff-detail-preview rounded border"
                             alt="No image">
                    </c:otherwise>
                </c:choose>
                <p class="text-muted small mt-2 mb-0">Current Image</p>
            </div>

            <div class="mb-3">
                <label class="form-label">Product Name</label>
                <div class="form-control staff-readonly-field">
                    <c:out value="${p.name}" />
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Description</label>
                <div class="form-control staff-readonly-field staff-readonly-field--textarea">
                    <c:choose>
                        <c:when test="${not empty p.description}">
                            <c:out value="${p.description}" />
                        </c:when>
                        <c:otherwise>
                            No description available.
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Brand</label>
                    <div class="form-control staff-readonly-field">
                        <c:out value="${p.brand.name}" />
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Category</label>
                    <div class="form-control staff-readonly-field">
                        <c:out value="${p.category.name}" />
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Price Range</label>
                    <div class="form-control staff-readonly-field">
                        <c:choose>
                            <c:when test="${p.price == 0}">
                                No variants
                            </c:when>
                            <c:when test="${p.price == p.maxPrice}">
                                <fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND -
                                <fmt:formatNumber value="${p.maxPrice}" pattern="#,##0"/> VND
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Status</label>
                    <div class="form-control staff-readonly-field ${p.status ? 'staff-status-active' : 'staff-status-inactive'}">
                        ${p.status ? 'Active' : 'Inactive'}
                    </div>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Price Stock (Giá nhập)</label>
                <div class="staff-import-box">
                    <c:choose>
                        <c:when test="${empty p.variants}">
                            <span class="text-muted">Không có</span>
                        </c:when>
                        <c:otherwise>
                            <ul class="list-unstyled mb-0">
                                <c:forEach items="${p.variants}" var="variant">
                                    <li class="mb-2">
                                        <span class="fw-bold">${variant.variantName}</span> -
                                        <span class="text-success">
                                            <fmt:formatNumber value="${variant.importPrice}" pattern="#,##0"/> ₫
                                        </span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="form-check form-switch mt-2">
                <input class="form-check-input" type="checkbox" ${p.status ? 'checked' : ''} disabled>
                <label class="form-check-label fw-bold ${p.status ? 'text-success' : 'text-danger'}">
                    ${p.status ? 'Active' : 'Inactive'}
                </label>
            </div>
        </div>
    </c:forEach>
</div>

<div class="modal fade" id="detailModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold" id="detailModalTitle">Product Detail</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div id="detailModalContent"></div>
            </div>
        </div>
    </div>
</div>

<script>
    function openDetailModal(button) {
        const productId = button.getAttribute('data-id');
        const productName = button.getAttribute('data-name');
        const content = document.getElementById('product-detail-data-' + productId);

        document.getElementById('detailModalTitle').innerText = 'Detail Product: ' + productName;
        document.getElementById('detailModalContent').innerHTML = content ? content.innerHTML : '';

        bootstrap.Modal.getOrCreateInstance(document.getElementById('detailModal')).show();
    }
</script>
