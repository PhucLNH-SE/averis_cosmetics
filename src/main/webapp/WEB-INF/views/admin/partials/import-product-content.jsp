<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section admin-page admin-page--product">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold mb-0">Create Import Order</h3>
            <p class="text-muted small mb-0">Create one import order with multiple products, and use brand only as a filter</p>
        </div>
        <div class="d-flex gap-2">
            <button type="button" class="btn btn-outline-primary px-3" data-bs-toggle="modal" data-bs-target="#supplierModal">
                <i class="bi bi-building-add"></i> Add Supplier
            </button>
            <a class="btn btn-outline-secondary px-3" href="${pageContext.request.contextPath}/admin/import-product?action=history">
                <i class="bi bi-arrow-left"></i> Back to History
            </a>
        </div>
    </div>

    <c:if test="${not empty error}">
        <c:set var="popupMessage" scope="request" value="${error}" />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.success == 'supplierAdded'}">
        <c:set var="popupMessage" scope="request" value="Supplier added successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/import-product" method="post" id="createImportOrderForm">
        <input type="hidden" name="action" value="importproduct">

        <div class="card shadow-sm mb-4">
            <div class="card-header bg-white border-0 pt-4 px-4">
                <h5 class="fw-bold mb-1">Import Order Information</h5>
                <p class="text-muted small mb-0">Fill in the import order header before adding items</p>
            </div>
            <div class="card-body p-4">
                <div class="row g-3">
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Import Code</label>
                        <input type="text" class="form-control" name="importCode" value="${nextImportCode}" readonly>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Supplier</label>
                        <select name="supplierId" class="form-select" required>
                            <option value="">Select supplier</option>
                            <c:forEach items="${supplierList}" var="supplier">
                                <option value="${supplier.supplierId}">${supplier.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Brand Filter</label>
                        <select id="importOrderBrandId" class="form-select" onchange="handleBrandChange()">
                            <option value="">All brands</option>
                            <c:forEach items="${listB}" var="b">
                                <option value="${b.brandId}">${b.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Invoice No</label>
                        <input type="text" class="form-control" name="invoiceNo" placeholder="Optional">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Note</label>
                        <textarea class="form-control" name="note" rows="2" placeholder="Import note"></textarea>
                    </div>
                </div>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-header bg-white border-0 pt-4 px-4 d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="fw-bold mb-1">Import Items</h5>
                    <p class="text-muted small mb-0">Add multiple products and variants. Brand only helps filter product choices</p>
                </div>
                <button type="button" class="btn btn-primary" onclick="addImportRow()">
                    <i class="bi bi-plus-circle me-1"></i> Add Item
                </button>
            </div>
            <div class="card-body p-4">
                <div class="table-responsive">
                    <table class="table align-middle" id="importItemsTable">
                        <thead>
                            <tr>
                                <th style="min-width: 220px;">Product</th>
                                <th style="min-width: 220px;">Variant</th>
                                <th class="text-center" style="min-width: 120px;">Current Stock</th>
                                <th class="text-center" style="min-width: 140px;">Quantity</th>
                                <th class="text-center" style="min-width: 160px;">Import Price</th>
                                <th class="text-end" style="min-width: 160px;">Subtotal</th>
                                <th class="text-center" style="width: 80px;">Remove</th>
                            </tr>
                        </thead>
                        <tbody id="importItemsBody">
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <p class="text-muted small mb-0">One import order can include products from multiple brands.</p>
                    <div class="fw-bold fs-5">
                        Total:
                        <span id="importOrderGrandTotal">0 VND</span>
                    </div>
                </div>
            </div>
            <div class="card-footer bg-white border-0 px-4 pb-4 d-flex justify-content-end gap-2">
                <a class="btn btn-outline-secondary px-4" href="${pageContext.request.contextPath}/admin/import-product?action=history">Cancel</a>
                <button type="submit" class="btn btn-primary px-4">
                    <i class="bi bi-check2-circle me-1"></i> Save Import Order
                </button>
            </div>
        </div>
    </form>
</section>

<div id="product-options-storage" class="d-none">
    <select id="brand-products-all">
        <option value="">Select product</option>
        <c:forEach items="${listP}" var="product">
            <option value="${product.productId}">${product.name}</option>
        </c:forEach>
    </select>
    <c:forEach items="${listB}" var="brand">
        <select id="brand-products-${brand.brandId}">
            <option value="">Select product</option>
            <c:forEach items="${listP}" var="product">
                <c:if test="${product.brand.brandId == brand.brandId}">
                    <option value="${product.productId}">${product.name}</option>
                </c:if>
            </c:forEach>
        </select>
    </c:forEach>
</div>

<div id="variant-options-storage" class="d-none">
    <c:forEach items="${listP}" var="product">
        <select id="product-variants-${product.productId}">
            <option value="">Select variant</option>
            <c:forEach items="${product.variants}" var="variant">
                <option value="${variant.variantId}"
                        data-stock="${variant.stock}"
                        data-default-price="${variant.importPrice}"
                        data-label="${product.name} - ${variant.variantName}">
                    ${variant.variantName}
                </option>
            </c:forEach>
        </select>
    </c:forEach>
</div>

<div class="modal fade" id="supplierModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/import-product" method="post">
                <input type="hidden" name="action" value="addsupplier">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title fw-bold">
                        <i class="bi bi-building-add me-2"></i>Add Supplier
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Supplier Name</label>
                        <input type="text" class="form-control" name="supplierName" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Phone</label>
                        <input type="text" class="form-control" name="supplierPhone" required>
                    </div>
                    <div class="mb-0">
                        <label class="form-label fw-semibold">Address</label>
                        <textarea class="form-control" name="supplierAddress" rows="3" required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary px-4">Save Supplier</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function getBrandId() {
        const brandSelect = document.getElementById('importOrderBrandId');
        return brandSelect ? brandSelect.value : '';
    }

    function buildProductOptionsHtml(brandId) {
        const source = document.getElementById(brandId ? 'brand-products-' + brandId : 'brand-products-all');
        return source ? source.innerHTML : '<option value="">Select product</option>';
    }

    function buildVariantOptionsHtml(productId) {
        if (!productId) {
            return '<option value="">Select variant</option>';
        }
        const source = document.getElementById('product-variants-' + productId);
        return source ? source.innerHTML : '<option value="">Select variant</option>';
    }

    function addImportRow(silent) {
        const brandId = getBrandId();
        const tbody = document.getElementById('importItemsBody');
        const row = document.createElement('tr');
        row.innerHTML = ""
                + "<td>"
                + "    <select class=\"form-select import-product-select\" onchange=\"handleProductChange(this)\" required>"
                + buildProductOptionsHtml(brandId)
                + "    </select>"
                + "</td>"
                + "<td>"
                + "    <select class=\"form-select import-variant-select\" name=\"variantId\" onchange=\"handleVariantChange(this)\" required>"
                + "        <option value=\"\">Select variant</option>"
                + "    </select>"
                + "</td>"
                + "<td class=\"text-center\">"
                + "    <span class=\"badge bg-light text-dark border import-current-stock\">0</span>"
                + "</td>"
                + "<td>"
                + "    <input type=\"number\" class=\"form-control text-end\" name=\"quantity\" min=\"1\" value=\"1\" onchange=\"updateRowSubtotal(this)\" oninput=\"updateRowSubtotal(this)\" required>"
                + "</td>"
                + "<td>"
                + "    <input type=\"number\" class=\"form-control text-end\" name=\"price\" min=\"0\" step=\"1000\" value=\"0\" onchange=\"updateRowSubtotal(this)\" oninput=\"updateRowSubtotal(this)\" required>"
                + "</td>"
                + "<td class=\"text-end fw-bold import-row-subtotal\">0 VND</td>"
                + "<td class=\"text-center\">"
                + "    <button type=\"button\" class=\"btn btn-outline-danger btn-sm\" onclick=\"removeImportRow(this)\">"
                + "        <i class=\"bi bi-trash\"></i>"
                + "    </button>"
                + "</td>";
        tbody.appendChild(row);
        updateGrandTotal();
    }

    function handleBrandChange() {
        // Brand works only as a filter for the next item rows.
    }

    function handleProductChange(selectEl) {
        const row = selectEl.closest('tr');
        const variantSelect = row.querySelector('.import-variant-select');
        variantSelect.innerHTML = buildVariantOptionsHtml(selectEl.value);
        row.querySelector('.import-current-stock').textContent = '0';
        const priceInput = row.querySelector('input[name="price"]');
        priceInput.value = 0;
        updateRowSubtotal(priceInput);
    }

    function handleVariantChange(selectEl) {
        const row = selectEl.closest('tr');
        const selectedOption = selectEl.options[selectEl.selectedIndex];
        const stock = selectedOption ? (selectedOption.getAttribute('data-stock') || '0') : '0';
        const defaultPrice = selectedOption ? (selectedOption.getAttribute('data-default-price') || '0') : '0';
        row.querySelector('.import-current-stock').textContent = stock;
        row.querySelector('input[name="price"]').value = defaultPrice;
        updateRowSubtotal(selectEl);
    }

    function updateRowSubtotal(sourceEl) {
        const row = sourceEl.closest('tr');
        const qty = Number(row.querySelector('input[name="quantity"]').value || 0);
        const price = Number(row.querySelector('input[name="price"]').value || 0);
        const subtotal = qty * price;
        row.querySelector('.import-row-subtotal').textContent = subtotal.toLocaleString('vi-VN') + ' VND';
        updateGrandTotal();
    }

    function updateGrandTotal() {
        let total = 0;
        document.querySelectorAll('#importItemsBody tr').forEach(function (row) {
            const qty = Number(row.querySelector('input[name="quantity"]').value || 0);
            const price = Number(row.querySelector('input[name="price"]').value || 0);
            total += qty * price;
        });
        document.getElementById('importOrderGrandTotal').textContent = total.toLocaleString('vi-VN') + ' VND';
    }

    function removeImportRow(button) {
        const row = button.closest('tr');
        row.remove();
        if (!document.querySelector('#importItemsBody tr')) {
            updateGrandTotal();
            return;
        }
        updateGrandTotal();
    }

    document.addEventListener('DOMContentLoaded', function () {
        addImportRow(true);
    });
</script>


