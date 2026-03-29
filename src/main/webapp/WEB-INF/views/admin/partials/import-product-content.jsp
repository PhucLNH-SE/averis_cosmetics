<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setLocale value="vi_VN"/>

<section class="admin-content__section admin-page admin-page--product">
    <div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
        <div>
            <h3 class="fw-bold mb-0">Create Import Order</h3>
            <p class="text-muted small mb-0">Create one import order with multiple products and variants from the current catalog.</p>
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
                <p class="text-muted small mb-0">Fill in the order header before adding line items.</p>
            </div>
            <div class="card-body p-4">
                <div class="row g-3">
                    <div class="col-lg-4 col-md-6">
                        <label class="form-label fw-semibold">Import Code</label>
                        <input type="text" class="form-control" name="importCode" value="${empty param.importCode ? nextImportCode : param.importCode}" readonly>
                    </div>
                    <div class="col-lg-4 col-md-6">
                        <label class="form-label fw-semibold">Supplier</label>
                        <select name="supplierId" class="form-select" required>
                            <option value="">Select supplier</option>
                            <c:forEach items="${supplierList}" var="supplier">
                                <option value="${supplier.supplierId}" ${param.supplierId == supplier.supplierId ? 'selected' : ''}>${supplier.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-lg-4 col-md-6">
                        <label class="form-label fw-semibold">Invoice No</label>
                        <input type="text" class="form-control" name="invoiceNo" placeholder="Optional" value="${fn:escapeXml(param.invoiceNo)}">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Note</label>
                        <textarea class="form-control" name="note" rows="2" placeholder="Import note"><c:out value="${param.note}" /></textarea>
                    </div>
                </div>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-header bg-white border-0 pt-4 px-4 d-flex flex-wrap justify-content-between align-items-center gap-3">
                <div>
                    <h5 class="fw-bold mb-1">Import Items</h5>
                    <p class="text-muted small mb-0">Products are shown as <strong>Brand - Product</strong>. Total import amount is limited to <fmt:formatNumber value="${maxImportTotalAmount}" pattern="#,##0"/> VND.</p>
                </div>
                <button type="button" class="btn btn-primary" onclick="addImportRow()">
                    <i class="bi bi-plus-circle me-1"></i> Add Item
                </button>
            </div>
            <div class="card-body p-4">
                <div class="table-responsive import-items-table-wrap">
                    <table class="table align-middle import-items-table" id="importItemsTable">
                        <thead>
                            <tr>
                                <th class="import-items-table__product">Product</th>
                                <th class="import-items-table__variant">Variant</th>
                                <th class="text-center import-items-table__stock">Current Stock</th>
                                <th class="text-center import-items-table__qty">Quantity</th>
                                <th class="text-center import-items-table__price">Import Price</th>
                                <th class="text-end import-items-table__subtotal">Subtotal</th>
                                <th class="text-center import-items-table__remove">Remove</th>
                            </tr>
                        </thead>
                        <tbody id="importItemsBody">
                        </tbody>
                    </table>
                </div>

                <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mt-3">
                    <div>
                        <p class="text-muted small mb-1">One import order can include products from multiple brands and multiple variants.</p>
                        <div class="text-danger small d-none import-total-limit-feedback" id="importTotalLimitAlert"></div>
                    </div>
                    <div class="fw-bold fs-5">
                        Total:
                        <span id="importOrderGrandTotal">0 VND</span>
                    </div>
                </div>
            </div>
            <div class="card-footer bg-white border-0 px-4 pb-4 d-flex justify-content-end gap-2">
                <a class="btn btn-outline-secondary px-4" href="${pageContext.request.contextPath}/admin/import-product?action=history">Cancel</a>
                <button type="submit" class="btn btn-primary px-4" id="saveImportOrderBtn">
                    <i class="bi bi-check2-circle me-1"></i> Save Import Order
                </button>
            </div>
        </div>
    </form>
</section>

<div id="product-options-storage" class="d-none">
    <select id="all-products-template">
        <option value="">Select product</option>
        <c:forEach items="${listP}" var="product">
            <option value="${product.productId}">${product.brand.name} - ${product.name}</option>
        </c:forEach>
    </select>
</div>

<div id="variant-options-storage" class="d-none">
    <c:forEach items="${listP}" var="product">
        <select id="product-variants-${product.productId}">
            <option value="">Select variant</option>
            <c:forEach items="${product.variants}" var="variant">
                <option value="${variant.variantId}"
                        data-stock="${variant.stock}"
                        data-default-price="${variant.importPrice}">
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
    const MAX_IMPORT_TOTAL = BigInt(String('${maxImportTotalAmount}'));
    const MAX_IMPORT_QUANTITY = 2147483647n;
    const submittedProductIds = [
        <c:forEach items="${paramValues.productId}" var="value" varStatus="status">'${value}'${status.last ? '' : ','}</c:forEach>
    ];
    const submittedVariantIds = [
        <c:forEach items="${paramValues.variantId}" var="value" varStatus="status">'${value}'${status.last ? '' : ','}</c:forEach>
    ];
    const submittedQuantities = [
        <c:forEach items="${paramValues.quantity}" var="value" varStatus="status">'${value}'${status.last ? '' : ','}</c:forEach>
    ];
    const submittedPrices = [
        <c:forEach items="${paramValues.price}" var="value" varStatus="status">'${value}'${status.last ? '' : ','}</c:forEach>
    ];

    function normalizeMoneyDigits(raw) {
        const text = String(raw == null ? '' : raw).replace(/,/g, '').trim();
        if (!text) {
            return '';
        }

        const dotParts = text.split('.');
        if (dotParts.length === 2 && dotParts[1].length <= 2) {
            return (dotParts[0] || '').replace(/\D/g, '');
        }

        return text.replace(/\D/g, '');
    }

    function parseWholeNumber(raw) {
        const normalized = normalizeMoneyDigits(raw);
        return normalized ? BigInt(normalized) : 0n;
    }

    function normalizeQuantityDigits(raw) {
        return String(raw == null ? '' : raw).replace(/\D/g, '');
    }

    function formatMoney(value) {
        const digits = String(value == null ? 0 : value).replace(/^0+(?=\d)/, '') || '0';
        return digits.replace(/\B(?=(\d{3})+(?!\d))/g, '.') + ' VND';
    }

    function updateLimitState(total, quantityOverflow) {
        const alertEl = document.getElementById('importTotalLimitAlert');
        const submitBtn = document.getElementById('saveImportOrderBtn');
        if (quantityOverflow) {
            alertEl.textContent = 'Quantity cannot exceed 2.147.483.647.';
            alertEl.classList.remove('d-none');
            submitBtn.disabled = true;
            return;
        }

        if (total > MAX_IMPORT_TOTAL) {
            alertEl.textContent = 'Total amount cannot exceed ' + formatMoney(MAX_IMPORT_TOTAL) + '.';
            alertEl.classList.remove('d-none');
            submitBtn.disabled = true;
            return;
        }

        alertEl.textContent = '';
        alertEl.classList.add('d-none');
        submitBtn.disabled = false;
    }

    function buildProductOptionsHtml() {
        const source = document.getElementById('all-products-template');
        return source ? source.innerHTML : '<option value="">Select product</option>';
    }

    function buildVariantOptionsHtml(productId) {
        if (!productId) {
            return '<option value="">Select variant</option>';
        }
        const source = document.getElementById('product-variants-' + productId);
        return source ? source.innerHTML : '<option value="">Select variant</option>';
    }

    function buildVariantProductMap() {
        const map = {};
        document.querySelectorAll('#variant-options-storage select').forEach(function (selectEl) {
            const productId = selectEl.id.replace('product-variants-', '');
            selectEl.querySelectorAll('option').forEach(function (optionEl) {
                if (optionEl.value) {
                    map[optionEl.value] = productId;
                }
            });
        });
        return map;
    }

    function addImportRow() {
        const tbody = document.getElementById('importItemsBody');
        const row = document.createElement('tr');
        row.innerHTML = ''
                + '<td class="import-items-table__product">'
                + '    <select class="form-select import-product-select" name="productId" onchange="handleProductChange(this)" required>'
                + buildProductOptionsHtml()
                + '    </select>'
                + '</td>'
                + '<td class="import-items-table__variant">'
                + '    <select class="form-select import-variant-select" name="variantId" onchange="handleVariantChange(this)" required>'
                + '        <option value="">Select variant</option>'
                + '    </select>'
                + '</td>'
                + '<td class="text-center import-items-table__stock">'
                + '    <span class="badge bg-light text-dark border import-current-stock">0</span>'
                + '</td>'
                + '<td class="import-items-table__qty">'
                + '    <input type="text" class="form-control text-end" name="quantity" inputmode="numeric" autocomplete="off" value="1" oninput="handleQuantityInput(this)" required>'
                + '</td>'
                + '<td class="import-items-table__price">'
                + '    <input type="text" class="form-control text-end import-items-table__price-input" name="price" inputmode="numeric" autocomplete="off" value="0" oninput="handlePriceInput(this)" required>'
                + '</td>'
                + '<td class="text-end fw-bold import-row-subtotal import-items-table__subtotal">0 VND</td>'
                + '<td class="text-center import-items-table__remove">'
                + '    <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeImportRow(this)">'
                + '        <i class="bi bi-trash"></i>'
                + '    </button>'
                + '</td>';

        tbody.appendChild(row);
        updateGrandTotal();
        return row;
    }

    function handlePriceInput(input) {
        input.value = normalizeMoneyDigits(input.value);
        updateRowSubtotal(input);
    }

    function handleQuantityInput(input) {
        input.value = normalizeQuantityDigits(input.value);
        updateRowSubtotal(input);
    }

    function handleProductChange(selectEl) {
        const row = selectEl.closest('tr');
        const variantSelect = row.querySelector('.import-variant-select');
        const priceInput = row.querySelector('input[name="price"]');
        variantSelect.innerHTML = buildVariantOptionsHtml(selectEl.value);
        variantSelect.value = '';
        row.querySelector('.import-current-stock').textContent = '0';
        priceInput.value = '0';
        updateRowSubtotal(priceInput);
    }

    function handleVariantChange(selectEl) {
        const row = selectEl.closest('tr');
        const selectedOption = selectEl.options[selectEl.selectedIndex];
        const stock = selectedOption ? (selectedOption.getAttribute('data-stock') || '0') : '0';
        const defaultPrice = selectedOption ? normalizeMoneyDigits(selectedOption.getAttribute('data-default-price') || '0') : '0';
        row.querySelector('.import-current-stock').textContent = stock;
        row.querySelector('input[name="price"]').value = defaultPrice || '0';
        updateRowSubtotal(selectEl);
    }

    function updateRowSubtotal(sourceEl) {
        const row = sourceEl.closest('tr');
        const qty = parseWholeNumber(row.querySelector('input[name="quantity"]').value);
        const price = parseWholeNumber(row.querySelector('input[name="price"]').value);
        const subtotal = qty * price;
        row.querySelector('.import-row-subtotal').textContent = formatMoney(subtotal);
        updateGrandTotal();
    }

    function updateGrandTotal() {
        let total = 0n;
        let quantityOverflow = false;
        document.querySelectorAll('#importItemsBody tr').forEach(function (row) {
            const qty = parseWholeNumber(row.querySelector('input[name="quantity"]').value);
            const price = parseWholeNumber(row.querySelector('input[name="price"]').value);
            if (qty > MAX_IMPORT_QUANTITY) {
                quantityOverflow = true;
            }
            total += qty * price;
        });
        document.getElementById('importOrderGrandTotal').textContent = formatMoney(total);
        updateLimitState(total, quantityOverflow);
    }

    function removeImportRow(button) {
        const row = button.closest('tr');
        row.remove();
        updateGrandTotal();
    }

    function buildSubmittedItems() {
        const items = [];
        const rowCount = Math.max(
                submittedProductIds.length,
                submittedVariantIds.length,
                submittedQuantities.length,
                submittedPrices.length
                );

        for (let i = 0; i < rowCount; i++) {
            items.push({
                productId: submittedProductIds[i] || '',
                variantId: submittedVariantIds[i] || '',
                quantity: submittedQuantities[i] || '',
                price: submittedPrices[i] || ''
            });
        }

        return items;
    }

    function restoreSubmittedRows(items, variantProductMap) {
        if (!items.length) {
            addImportRow();
            return;
        }

        items.forEach(function (item) {
            const row = addImportRow();
            const productSelect = row.querySelector('.import-product-select');
            const variantSelect = row.querySelector('.import-variant-select');
            const quantityInput = row.querySelector('input[name="quantity"]');
            const priceInput = row.querySelector('input[name="price"]');
            const productId = item.productId || variantProductMap[item.variantId] || '';

            productSelect.value = productId;
            variantSelect.innerHTML = buildVariantOptionsHtml(productId);

            if (item.variantId) {
                variantSelect.value = item.variantId;
            }

            const selectedOption = variantSelect.options[variantSelect.selectedIndex];
            row.querySelector('.import-current-stock').textContent = selectedOption
                    ? (selectedOption.getAttribute('data-stock') || '0')
                    : '0';
            quantityInput.value = normalizeQuantityDigits(item.quantity) || '';
            priceInput.value = normalizeMoneyDigits(item.price);
            updateRowSubtotal(priceInput);
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        const variantProductMap = buildVariantProductMap();
        restoreSubmittedRows(buildSubmittedItems(), variantProductMap);

        document.getElementById('createImportOrderForm').addEventListener('submit', function (event) {
            let total = 0n;
            let quantityOverflow = false;
            document.querySelectorAll('#importItemsBody tr').forEach(function (row) {
                const qty = parseWholeNumber(row.querySelector('input[name="quantity"]').value);
                const price = parseWholeNumber(row.querySelector('input[name="price"]').value);
                if (qty > MAX_IMPORT_QUANTITY) {
                    quantityOverflow = true;
                }
                total += qty * price;
            });

            if (quantityOverflow || total > MAX_IMPORT_TOTAL) {
                event.preventDefault();
                updateLimitState(total, quantityOverflow);
            }
        });
    });
</script>
