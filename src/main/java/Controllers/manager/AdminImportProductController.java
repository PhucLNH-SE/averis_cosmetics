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
        String action = toNullIfBlank(request.getParameter("action"));
        if (action == null) {
            action = "importproduct";
        }

        switch (action) {
            case "history":
                showImportOrderHistory(request, response);
                break;
            case "viewdetail":
                showImportOrderDetail(request, response);
                break;
            case "importproduct":
                if (isStaffImportRoute(request)) {
                    showImportOrderHistory(request, response);
                } else {
                    showCreateImportOrderPage(request, response);
                }
                break;
            default:
                showImportOrderHistory(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = toNullIfBlank(request.getParameter("action"));
        if (action == null) {
            action = "importproduct";
        }

        switch (action) {
            case "receive":
                confirmImportReceipt(request, response);
                break;
            case "importproduct":
            default:
                createImportOrder(request, response);
                break;
        }
    }

    private void showCreateImportOrderPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isStaffImportRoute(request)) {
            showImportOrderHistory(request, response);
            return;
        }

        ProductDAO productDao = new ProductDAO();
        List<Product> listP = productDao.getProductsForAdminWithImportPrice(null, null, null, null);
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();

        request.setAttribute("listP", listP);
        request.setAttribute("supplierList", suppliers);
        request.setAttribute("nextImportCode", generateImportCode());
        request.setAttribute("maxImportTotalAmount", MAX_IMPORT_TOTAL_AMOUNT);
        request.setAttribute("currentView", "import");
        request.setAttribute("contentPage", ADMIN_IMPORT_CONTENT);
        request.getRequestDispatcher(ADMIN_PANEL).forward(request, response);
    }

    private void createImportOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isStaffImportRoute(request)) {
            response.sendRedirect(request.getContextPath() + "/staff/import-product?action=history");
            return;
        }

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        try {
            String supplierIdRaw = toNullIfBlank(request.getParameter("supplierId"));
            String importCode = toNullIfBlank(request.getParameter("importCode"));
            String invoiceNo = toNullIfBlank(request.getParameter("invoiceNo"));
            String note = toNullIfBlank(request.getParameter("note"));
            String[] variantIds = request.getParameterValues("variantId");
            String[] quantities = request.getParameterValues("quantity");
            String[] prices = request.getParameterValues("price");

            ValidationUtil.validateImportOrderInput(supplierIdRaw, variantIds, quantities, prices);

            List<ImportOrderDetail> details = new ArrayList<>();
            for (int i = 0; i < variantIds.length; i++) {
                String variantIdRaw = variantIds[i];
                String quantityRaw = i < quantities.length ? quantities[i] : null;
                String priceRaw = i < prices.length ? prices[i] : null;

                if (toNullIfBlank(variantIdRaw) == null) {
                    continue;
                }

                ValidationUtil.validateImportItemInput(variantIdRaw, quantityRaw, priceRaw);
                if (toNullIfBlank(quantityRaw) == null && toNullIfBlank(priceRaw) == null) {
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

            ImportOrder importOrder = new ImportOrder();
            importOrder.setSupplierId(ValidationUtil.parseImportSupplierId(supplierIdRaw));
            importOrder.setCreatedBy(manager.getManagerId());
            importOrder.setImportCode(importCode == null ? generateImportCode() : importCode);
            importOrder.setInvoiceNo(invoiceNo);
            importOrder.setNote(note);
            importOrder.setStatus("PENDING");
            importOrder.setDetails(details);
            importOrder.setTotalAmount(importOrder.calculateTotalAmount());

            ValidationUtil.validateImportOrder(importOrder, MAX_IMPORT_TOTAL_AMOUNT);

            int orderId = dao.createPurchaseOrderWithDetails(importOrder);
            if (orderId <= 0) {
                request.setAttribute("error", "Failed to create import order.");
                showCreateImportOrderPage(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&success=import");
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            showCreateImportOrderPage(request, response);
            return;
        }
    }

    private void showImportOrderHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean staffRoute = isStaffImportRoute(request);
        String importBasePath = request.getContextPath()
                + (staffRoute ? "/staff/import-product" : "/admin/import-product");
        List<ImportOrder> history = dao.getImportHistory();

        request.setAttribute("history", history);
        request.setAttribute("staffRoute", staffRoute);
        request.setAttribute("canCreateImportOrder", !staffRoute);
        request.setAttribute("importBasePath", importBasePath);
        request.setAttribute("currentView", "import");
        request.setAttribute("contentPage", staffRoute ? STAFF_HISTORY_CONTENT : ADMIN_HISTORY_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private void showImportOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        boolean staffRoute = isStaffImportRoute(request);
        String importBasePath = request.getContextPath()
                + (staffRoute ? "/staff/import-product" : "/admin/import-product");
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

        setImportOrderDetailAttributes(request, orderId, manager, null, null, null, importBasePath);
        request.getRequestDispatcher(IMPORT_DETAIL_VIEW).forward(request, response);
    }

    private void confirmImportReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean staffRoute = isStaffImportRoute(request);
        String importBasePath = request.getContextPath()
                + (staffRoute ? "/staff/import-product" : "/admin/import-product");
        String historyRedirect = importBasePath + "?action=history";
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        String orderIdRaw = toNullIfBlank(request.getParameter("orderId"));
        if (orderIdRaw == null) {
            response.sendRedirect(historyRedirect + "&error=importFailed");
            return;
        }

        String[] variantIdRaw = request.getParameterValues("variantId");
        String[] receivedQtyRaw = request.getParameterValues("receivedQuantity");

        try {
            int orderId = Integer.parseInt(orderIdRaw);
            int[] variantIds = new int[variantIdRaw == null ? 0 : variantIdRaw.length];
            for (int i = 0; i < variantIds.length; i++) {
                if (variantIdRaw[i] == null || variantIdRaw[i].trim().isEmpty()) {
                    variantIds[i] = 0;
                } else {
                    variantIds[i] = Integer.parseInt(variantIdRaw[i].trim());
                }
            }

            int[] receivedQuantities = new int[receivedQtyRaw == null ? 0 : receivedQtyRaw.length];
            for (int i = 0; i < receivedQuantities.length; i++) {
                if (receivedQtyRaw[i] == null || receivedQtyRaw[i].trim().isEmpty()) {
                    receivedQuantities[i] = 0;
                } else {
                    receivedQuantities[i] = ValidationUtil.parseQuantityValue(receivedQtyRaw[i], "Invalid received quantity.");
                }
            }

            List<ImportOrderDetail> details = dao.getImportOrderDetail(orderId);
            BigDecimal receivedTotal = BigDecimal.ZERO;
            for (ImportOrderDetail detail : details) {
                int receivedQty = detail.getQuantity();

                for (int i = 0; i < variantIds.length; i++) {
                    if (variantIds[i] == detail.getVariantId()) {
                        if (i < receivedQuantities.length && receivedQuantities[i] >= 0) {
                            receivedQty = receivedQuantities[i];
                        }
                        break;
                    }
                }

                detail.setReceivedQuantity(receivedQty);
                receivedTotal = receivedTotal.add(detail.calculateSubtotal(receivedQty));
            }

            if (receivedTotal.compareTo(MAX_IMPORT_TOTAL_AMOUNT) > 0) {
                setImportOrderDetailAttributes(request, orderId, manager, variantIds, receivedQuantities,
                        "Total amount cannot exceed 9,999,999,999 VND.", importBasePath);
                request.setAttribute("autoOpenImportDetail", Boolean.TRUE);
                showImportOrderHistory(request, response);
                return;
            }

            boolean ok = dao.confirmReceipt(orderId, manager.getManagerId(), variantIds, receivedQuantities);
            if (!ok) {
                setImportOrderDetailAttributes(request, orderId, manager, variantIds, receivedQuantities,
                        "Failed to confirm import receipt.", importBasePath);
                request.setAttribute("autoOpenImportDetail", Boolean.TRUE);
                showImportOrderHistory(request, response);
                return;
            }

            response.sendRedirect(historyRedirect + "&success=received");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(historyRedirect + "&error=importFailed");
        }
    }

    private void setImportOrderDetailAttributes(HttpServletRequest request, int orderId, Manager manager,
            int[] variantIds, int[] receivedQuantities, String detailError, String importBasePath) {
        ImportOrder importOrder = dao.getImportOrderById(orderId);
        List<ImportOrderDetail> details = dao.getImportOrderDetail(orderId);
        String status = dao.getImportOrderStatus(orderId);

        if (details != null && variantIds != null && receivedQuantities != null) {
            for (ImportOrderDetail detail : details) {
                int receivedQty = detail.getQuantity();

                for (int i = 0; i < variantIds.length; i++) {
                    if (variantIds[i] == detail.getVariantId()) {
                        if (i < receivedQuantities.length && receivedQuantities[i] >= 0) {
                            receivedQty = receivedQuantities[i];
                        }
                        break;
                    }
                }

                detail.setReceivedQuantity(receivedQty);
            }
        }

        if (importOrder != null && details != null && !details.isEmpty() && "PENDING".equalsIgnoreCase(status)) {
            BigDecimal total = BigDecimal.ZERO;
            for (ImportOrderDetail detail : details) {
                int receivedQty = detail.getReceivedQuantity() != null ? detail.getReceivedQuantity() : detail.getQuantity();
                total = total.add(detail.calculateSubtotal(receivedQty));
            }
            importOrder.setTotalAmount(total);
        }

        request.setAttribute("importOrder", importOrder);
        request.setAttribute("details", details);
        request.setAttribute("orderStatus", status);
        request.setAttribute("orderId", orderId);
        request.setAttribute("currentManagerRole", manager == null ? null : manager.getManagerRole());
        request.setAttribute("importBasePath", importBasePath);
        request.setAttribute("maxImportTotalAmount", MAX_IMPORT_TOTAL_AMOUNT);
        request.setAttribute("importDetailError", detailError);
    }

    private String toNullIfBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isStaffImportRoute(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/staff/");
    }

    private String generateImportCode() {
        return "IMP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}



