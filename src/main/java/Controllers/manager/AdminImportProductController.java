package Controllers.manager;

import DALs.ImportProductDAO;
import DALs.ProductDAO;
import DALs.SupplierDAO;
import Model.Manager;
import Model.Product;
import Model.ImportOrder;
import Model.ImportOrderDetail;
import Model.Supplier;
import Utils.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminImportProductController extends HttpServlet {

    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_IMPORT_CONTENT = "/WEB-INF/views/admin/partials/import-product-content.jsp";
    private static final String ADMIN_HISTORY_CONTENT = "/WEB-INF/views/admin/partials/manage-importproduct-content.jsp";
    private static final String STAFF_HISTORY_CONTENT = "/WEB-INF/views/staff/partials/manage-importproduct-content.jsp";
    private static final String IMPORT_DETAIL_VIEW = "/WEB-INF/views/admin/import-detail.jsp";
    private static final BigDecimal MAX_IMPORT_TOTAL_AMOUNT = new BigDecimal("9999999999");

    private final ImportProductDAO dao = new ImportProductDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "importproduct";
        }

        switch (action) {
            case "history":
                showHistory(request, response);
                break;
            case "viewdetail":
                showDetail(request, response);
                break;
            case "importproduct":
                if (isStaffRoute(request)) {
                    showHistory(request, response);
                } else {
                    showImportProduct(request, response);
                }
                break;
            default:
                showHistory(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "importproduct";
        }

        switch (action) {
            case "receive":
                receiveOrder(request, response);
                break;
            case "addsupplier":
                addSupplier(request, response);
                break;
            case "importproduct":
            default:
                importProduct(request, response);
                break;
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        populateImportDetailAttributes(request, orderId, manager, null, null, null);
        request.getRequestDispatcher(IMPORT_DETAIL_VIEW).forward(request, response);
    }

    private void showImportProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isStaffRoute(request)) {
            showHistory(request, response);
            return;
        }

        ProductDAO productDao = new ProductDAO();

        String keyword = trimToNull(request.getParameter("keyword"));
        String categoryId = trimToNull(request.getParameter("categoryId"));
        String status = trimToNull(request.getParameter("status"));

        List<Product> listP = productDao.getProductsForAdminWithImportPrice(keyword, null, categoryId, status);
        List<Model.Category> categories = productDao.getAllCategories();
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();

        int activeCount = 0;
        for (Product product : listP) {
            if (product.isStatus()) {
                activeCount++;
            }
        }

        request.setAttribute("listP", listP);
        request.setAttribute("listC", categories);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("resultCount", listP.size());
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("inactiveCount", listP.size() - activeCount);
        request.setAttribute("supplierList", suppliers);
        request.setAttribute("nextImportCode", generateImportCode());
        request.setAttribute("maxImportTotalAmount", MAX_IMPORT_TOTAL_AMOUNT);
        request.setAttribute("currentView", "import");
        request.setAttribute("contentPage", ADMIN_IMPORT_CONTENT);
        request.getRequestDispatcher(ADMIN_PANEL).forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void importProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isStaffRoute(request)) {
            response.sendRedirect(request.getContextPath() + "/staff/import-product?action=history");
            return;
        }

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        ImportOrder importOrder;
        try {
            importOrder = buildImportOrder(request, manager.getManagerId());
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            showImportProduct(request, response);
            return;
        }

        int orderId = dao.createPurchaseOrderWithDetails(importOrder);
        if (orderId <= 0) {
            request.setAttribute("error", "Failed to create import order.");
            showImportProduct(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&success=import");
    }

    private ImportOrder buildImportOrder(HttpServletRequest request, int managerId) {
        String supplierIdRaw = trimToNull(request.getParameter("supplierId"));
        String[] variantIds = request.getParameterValues("variantId");
        String[] quantities = request.getParameterValues("quantity");
        String[] prices = request.getParameterValues("price");

        ValidationUtil.validateImportOrderInput(supplierIdRaw, variantIds, quantities, prices);

        ImportOrder importOrder = new ImportOrder();
        importOrder.setSupplierId(ValidationUtil.parseImportSupplierId(supplierIdRaw));
        importOrder.setCreatedBy(managerId);
        importOrder.setImportCode(resolveImportCode(trimToNull(request.getParameter("importCode"))));
        importOrder.setInvoiceNo(trimToNull(request.getParameter("invoiceNo")));
        importOrder.setNote(trimToNull(request.getParameter("note")));
        importOrder.setStatus("PENDING");
        importOrder.setDetails(extractImportDetails(variantIds, quantities, prices));
        importOrder.setTotalAmount(importOrder.calculateTotalAmount());

        ValidationUtil.validateImportOrder(importOrder, MAX_IMPORT_TOTAL_AMOUNT);
        return importOrder;
    }

    private List<ImportOrderDetail> extractImportDetails(String[] variantIds, String[] quantities, String[] prices) {
        List<ImportOrderDetail> details = new ArrayList<>();

        for (int i = 0; i < variantIds.length; i++) {
            String variantIdRaw = variantIds[i];
            String quantityRaw = i < quantities.length ? quantities[i] : null;
            String priceRaw = i < prices.length ? prices[i] : null;

            if (trimToNull(variantIdRaw) == null) {
                continue;
            }

            ValidationUtil.validateImportItemInput(variantIdRaw, quantityRaw, priceRaw);
            if (trimToNull(quantityRaw) == null && trimToNull(priceRaw) == null) {
                continue;
            }

            ImportOrderDetail detail = new ImportOrderDetail();
            detail.setVariantId(ValidationUtil.parseImportVariantId(variantIdRaw));
            detail.setQuantity(ValidationUtil.parseImportQuantity(quantityRaw));
            detail.setImportPrice(ValidationUtil.parseImportPrice(priceRaw));

            if (ValidationUtil.isValidImportItem(detail)) {
                details.add(detail);
            }
        }

        return details;
    }

    private String resolveImportCode(String importCode) {
        return importCode == null ? generateImportCode() : importCode;
    }

    private void addSupplier(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (isStaffRoute(request)) {
            response.sendRedirect(request.getContextPath() + "/staff/import-product?action=history");
            return;
        }

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        Supplier supplier = ValidationUtil.normalizeSupplier(
                0,
                request.getParameter("supplierName"),
                request.getParameter("supplierPhone"),
                request.getParameter("supplierAddress"),
                true);
        String name = supplier.getName();

        request.setAttribute("supplierForm", supplier);
        request.setAttribute("openSupplierModal", Boolean.TRUE);

        try {
            ValidationUtil.validateSupplierInput(supplier);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            showImportProduct(request, response);
            return;
        }

        if (supplierDAO.existsByName(name)) {
            request.setAttribute("error", "Supplier name already exists.");
            showImportProduct(request, response);
            return;
        }

        if (supplierDAO.insertSupplier(supplier)) {
            response.sendRedirect(request.getContextPath() + "/admin/import-product?action=importproduct&success=supplierAdded");
        } else {
            request.setAttribute("error", "Failed to add supplier.");
            showImportProduct(request, response);
        }
    }

    private void showHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean staffRoute = isStaffRoute(request);
        List<ImportOrder> history = dao.getImportHistory();
        request.setAttribute("history", history);
        request.setAttribute("staffRoute", staffRoute);
        request.setAttribute("canCreateImportOrder", !staffRoute);
        request.setAttribute("importBasePath", request.getContextPath()
                + (staffRoute ? "/staff/import-product" : "/admin/import-product"));
        request.setAttribute("currentView", "import");
        request.setAttribute("contentPage", staffRoute ? STAFF_HISTORY_CONTENT : ADMIN_HISTORY_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private void receiveOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        String orderIdRaw = trimToNull(request.getParameter("orderId"));
        if (orderIdRaw == null) {
            response.sendRedirect(buildHistoryRedirect(request, "importFailed", null));
            return;
        }

        String[] variantIdRaw = request.getParameterValues("variantId");
        String[] receivedQtyRaw = request.getParameterValues("receivedQuantity");
        try {
            int orderId = Integer.parseInt(orderIdRaw);
            int[] variantIds = parseIntArray(variantIdRaw);
            int[] receivedQuantities = parseQuantityArray(receivedQtyRaw);

            BigDecimal receivedTotal = calculateReceiptTotal(orderId, variantIds, receivedQuantities);
            if (receivedTotal.compareTo(MAX_IMPORT_TOTAL_AMOUNT) > 0) {
                showHistoryWithDetail(request, response, orderId, manager, variantIds, receivedQuantities,
                        "Total amount cannot exceed 9,999,999,999 VND.");
                return;
            }

            boolean ok = dao.confirmReceipt(orderId, manager.getManagerId(), variantIds, receivedQuantities);
            if (!ok) {
                showHistoryWithDetail(request, response, orderId, manager, variantIds, receivedQuantities,
                        "Failed to confirm import receipt.");
                return;
            }

            response.sendRedirect(buildHistoryRedirect(request, null, "received"));
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(buildHistoryRedirect(request, "importFailed", null));
        }
    }

    private int[] parseIntArray(String[] values) {
        if (values == null) {
            return new int[0];
        }

        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null || values[i].trim().isEmpty()) {
                result[i] = 0;
            } else {
                result[i] = Integer.parseInt(values[i].trim());
            }
        }
        return result;
    }

    private int[] parseQuantityArray(String[] values) {
        if (values == null) {
            return new int[0];
        }

        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null || values[i].trim().isEmpty()) {
                result[i] = 0;
            } else {
                result[i] = ValidationUtil.parseQuantityValue(values[i], "Invalid received quantity.");
            }
        }
        return result;
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/staff/");
    }

    private BigDecimal calculateReceiptTotal(int orderId, int[] variantIds, int[] receivedQuantities) {
        List<ImportOrderDetail> details = dao.getImportOrderDetail(orderId);
        applyReceivedQuantities(details, variantIds, receivedQuantities);
        return calculateReceiptTotal(details);
    }

    private BigDecimal calculateReceiptTotal(List<ImportOrderDetail> details) {
        BigDecimal total = BigDecimal.ZERO;
        for (ImportOrderDetail detail : details) {
            int receivedQty = detail.getReceivedQuantity() != null ? detail.getReceivedQuantity() : detail.getQuantity();
            total = total.add(detail.calculateSubtotal(receivedQty));
        }

        return total;
    }

    private void applyReceivedQuantities(List<ImportOrderDetail> details, int[] variantIds, int[] receivedQuantities) {
        if (details == null || details.isEmpty() || variantIds == null || receivedQuantities == null) {
            return;
        }

        for (ImportOrderDetail detail : details) {
            detail.setReceivedQuantity(resolveReceivedQuantity(
                    detail.getVariantId(),
                    detail.getQuantity(),
                    variantIds,
                    receivedQuantities));
        }
    }

    private int resolveReceivedQuantity(int variantId, int fallbackQuantity, int[] variantIds, int[] receivedQuantities) {
        if (variantIds == null || receivedQuantities == null) {
            return fallbackQuantity;
        }

        for (int i = 0; i < variantIds.length; i++) {
            if (variantIds[i] == variantId) {
                if (i < receivedQuantities.length && receivedQuantities[i] >= 0) {
                    return receivedQuantities[i];
                }
                break;
            }
        }

        return fallbackQuantity;
    }

    private String buildHistoryRedirect(HttpServletRequest request, String error, String success) {
        StringBuilder redirect = new StringBuilder();
        redirect.append(request.getContextPath())
                .append(isStaffRoute(request) ? "/staff/import-product?action=history" : "/admin/import-product?action=history");
        if (error != null) {
            redirect.append("&error=").append(error);
        }
        if (success != null) {
            redirect.append("&success=").append(success);
        }
        return redirect.toString();
    }

    private void showHistoryWithDetail(HttpServletRequest request, HttpServletResponse response,
            int orderId, Manager manager, int[] variantIds, int[] receivedQuantities, String detailError)
            throws ServletException, IOException {
        populateImportDetailAttributes(request, orderId, manager, variantIds, receivedQuantities, detailError);
        request.setAttribute("autoOpenImportDetail", Boolean.TRUE);
        request.setAttribute("autoOpenImportOrderId", orderId);
        showHistory(request, response);
    }

    private void populateImportDetailAttributes(HttpServletRequest request, int orderId, Manager manager,
            int[] variantIds, int[] receivedQuantities, String detailError) {
        ImportOrder importOrder = dao.getImportOrderById(orderId);
        List<ImportOrderDetail> details = dao.getImportOrderDetail(orderId);
        String status = dao.getImportOrderStatus(orderId);

        applyReceivedQuantities(details, variantIds, receivedQuantities);
        if (importOrder != null && details != null && !details.isEmpty() && "PENDING".equalsIgnoreCase(status)) {
            importOrder.setTotalAmount(calculateReceiptTotal(details));
        }

        request.setAttribute("importOrder", importOrder);
        request.setAttribute("details", details);
        request.setAttribute("orderStatus", status);
        request.setAttribute("orderId", orderId);
        request.setAttribute("currentManagerRole", manager == null ? null : manager.getManagerRole());
        request.setAttribute("importBasePath", isStaffRoute(request)
                ? request.getContextPath() + "/staff/import-product"
                : request.getContextPath() + "/admin/import-product");
        request.setAttribute("maxImportTotalAmount", MAX_IMPORT_TOTAL_AMOUNT);
        request.setAttribute("importDetailError", detailError);
    }

    private String generateImportCode() {
        return "IMP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}



