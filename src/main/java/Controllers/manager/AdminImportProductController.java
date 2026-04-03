package Controllers.manager;

import DALs.ImportProductDAO;
import DALs.ProductDAO;
import DALs.SupplierDAO;
import Model.Manager;
import Model.Product;
import Model.PurchaseDetail;
import Model.PurchaseOrder;
import Model.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    private static final BigInteger MAX_QUANTITY_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

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

        int managerId = manager.getManagerId();
        String[] variantIds = request.getParameterValues("variantId");
        String[] quantities = request.getParameterValues("quantity");
        String[] prices = request.getParameterValues("price");
        String supplierIdRaw = trimToNull(request.getParameter("supplierId"));
        String invoiceNo = trimToNull(request.getParameter("invoiceNo"));
        String note = trimToNull(request.getParameter("note"));
        String importCode = trimToNull(request.getParameter("importCode"));

        BigDecimal total = BigDecimal.ZERO;
        boolean hasError = false;
        boolean hasProduct = false;
        List<Integer> parsedVariantIds = new ArrayList<>();
        List<Integer> parsedQuantities = new ArrayList<>();
        List<BigDecimal> parsedPrices = new ArrayList<>();

        if (supplierIdRaw == null) {
            request.setAttribute("error", "Please select a supplier.");
            showImportProduct(request, response);
            return;
        }

        if (variantIds == null || quantities == null || prices == null) {
            request.setAttribute("error", "Please add at least one import item.");
            showImportProduct(request, response);
            return;
        }

        Integer supplierId = Integer.valueOf(supplierIdRaw);
        if (importCode == null) {
            importCode = generateImportCode();
        }

        for (int i = 0; i < variantIds.length; i++) {
            String variantIdRaw = variantIds[i];
            String quantityRaw = quantities[i];
            String priceRaw = prices[i];

            if (variantIdRaw == null || variantIdRaw.trim().isEmpty()) {
                continue;
            }

            if ((quantityRaw != null && !quantityRaw.trim().isEmpty() && (priceRaw == null || priceRaw.trim().isEmpty()))
                    || (priceRaw != null && !priceRaw.trim().isEmpty() && (quantityRaw == null || quantityRaw.trim().isEmpty()))) {
                hasError = true;
                break;
            }

            if (quantityRaw == null || quantityRaw.trim().isEmpty()) {
                continue;
            }

            int quantity;
            BigDecimal price;
            try {
                quantity = parseQuantityValue(quantityRaw);
                price = parseImportPrice(priceRaw);
            } catch (NumberFormatException ex) {
                hasError = true;
                break;
            }

            if (quantity > 0 && price.compareTo(BigDecimal.ZERO) > 0) {
                hasProduct = true;
                total = total.add(price.multiply(BigDecimal.valueOf(quantity)));
                parsedVariantIds.add(Integer.parseInt(variantIdRaw.trim()));
                parsedQuantities.add(quantity);
                parsedPrices.add(price);
                if (total.compareTo(MAX_IMPORT_TOTAL_AMOUNT) > 0) {
                    request.setAttribute("error", "Total amount cannot exceed 9,999,999,999 VND.");
                    showImportProduct(request, response);
                    return;
                }
            }
        }

        if (hasError) {
            request.setAttribute("error", "Please enter valid quantity and import price for each selected item.");
            showImportProduct(request, response);
            return;
        }

        if (!hasProduct) {
            request.setAttribute("error", "Please add at least one valid import item.");
            showImportProduct(request, response);
            return;
        }

        int orderId = dao.createPurchaseOrderWithDetails(
                supplierId,
                managerId,
                importCode,
                invoiceNo,
                note,
                parsedVariantIds,
                parsedQuantities,
                parsedPrices,
                total);
        if (orderId <= 0) {
            request.setAttribute("error", "Failed to create import order.");
            showImportProduct(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&success=import");
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

        String name = trimToNull(request.getParameter("supplierName"));
        String phone = trimToNull(request.getParameter("supplierPhone"));
        String address = trimToNull(request.getParameter("supplierAddress"));

        if (name == null || phone == null || address == null) {
            request.setAttribute("error", "Please enter full supplier information.");
            showImportProduct(request, response);
            return;
        }

        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setPhone(phone);
        supplier.setAddress(address);

        if (supplierDAO.insert(supplier)) {
            response.sendRedirect(request.getContextPath() + "/admin/import-product?action=importproduct&success=supplierAdded");
        } else {
            request.setAttribute("error", "Failed to add supplier. Please check duplicate name.");
            showImportProduct(request, response);
        }
    }

    private void showHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean staffRoute = isStaffRoute(request);
        List<PurchaseOrder> history = staffRoute ? dao.getPendingImportOrders() : dao.getImportHistory();
        request.setAttribute("history", history);
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
        } catch (NumberFormatException ex) {
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
                result[i] = parseQuantityValue(values[i]);
            }
        }
        return result;
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/staff/");
    }

    private BigDecimal parseImportPrice(String rawValue) {
        if (rawValue == null) {
            throw new NumberFormatException("Price is required");
        }

        String cleaned = rawValue.replace(",", "").trim();
        int decimalIndex = cleaned.indexOf('.');
        if (decimalIndex >= 0) {
            String fractional = cleaned.substring(decimalIndex + 1);
            if (!fractional.replace("0", "").isEmpty()) {
                throw new NumberFormatException("Price must be a whole number");
            }
            cleaned = cleaned.substring(0, decimalIndex);
        }

        cleaned = cleaned.replaceAll("\\s+", "");
        if (cleaned.isEmpty() || !cleaned.matches("\\d+")) {
            throw new NumberFormatException("Price must contain digits only");
        }

        return new BigDecimal(cleaned);
    }

    private int parseQuantityValue(String rawValue) {
        if (rawValue == null) {
            throw new NumberFormatException("Quantity is required");
        }

        String cleaned = rawValue.replace(",", "").trim().replaceAll("\\s+", "");
        if (cleaned.isEmpty() || !cleaned.matches("\\d+")) {
            throw new NumberFormatException("Quantity must contain digits only");
        }

        BigInteger quantityValue = new BigInteger(cleaned);
        if (quantityValue.compareTo(MAX_QUANTITY_VALUE) > 0) {
            throw new NumberFormatException("Quantity exceeds supported range");
        }

        return quantityValue.intValue();
    }

    private BigDecimal calculateReceiptTotal(int orderId, int[] variantIds, int[] receivedQuantities) {
        List<PurchaseDetail> details = dao.getImportOrderDetail(orderId);
        applyReceivedQuantities(details, variantIds, receivedQuantities);
        return calculateReceiptTotal(details);
    }

    private BigDecimal calculateReceiptTotal(List<PurchaseDetail> details) {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseDetail detail : details) {
            int receivedQty = detail.getReceivedQuantity() != null ? detail.getReceivedQuantity() : detail.getQuantity();
            BigDecimal importPrice = detail.getImportPrice() == null ? BigDecimal.ZERO : detail.getImportPrice();
            total = total.add(importPrice.multiply(BigDecimal.valueOf(receivedQty)));
        }

        return total;
    }

    private void applyReceivedQuantities(List<PurchaseDetail> details, int[] variantIds, int[] receivedQuantities) {
        if (details == null || details.isEmpty() || variantIds == null || receivedQuantities == null) {
            return;
        }

        for (PurchaseDetail detail : details) {
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
        PurchaseOrder importOrder = dao.getImportOrderById(orderId);
        List<PurchaseDetail> details = dao.getImportOrderDetail(orderId);
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



