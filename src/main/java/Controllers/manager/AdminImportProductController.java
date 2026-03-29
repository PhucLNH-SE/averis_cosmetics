package Controllers.manager;

import DALs.ImportProductDAO;
import DALs.ProductDAO;
import DALs.SupplierDAO;
import Model.Brand;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminImportProductController extends HttpServlet {

    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_IMPORT_CONTENT = "/WEB-INF/views/admin/partials/import-product-content.jsp";
    private static final String ADMIN_HISTORY_CONTENT = "/WEB-INF/views/admin/partials/manage-importproduct-content.jsp";
    private static final String STAFF_HISTORY_CONTENT = "/WEB-INF/views/staff/partials/manage-importproduct-content.jsp";

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
            case "create":
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
        PurchaseOrder importOrder = dao.getImportOrderById(orderId);
        List<PurchaseDetail> details = dao.getImportOrderDetail(orderId);
        String status = dao.getImportOrderStatus(orderId);

        request.setAttribute("importOrder", importOrder);
        request.setAttribute("details", details);
        request.setAttribute("orderStatus", status);
        request.setAttribute("orderId", orderId);
        request.setAttribute("currentManagerRole", manager == null ? null : manager.getManagerRole());
        request.setAttribute("importBasePath", isStaffRoute(request)
                ? request.getContextPath() + "/staff/import-product"
                : request.getContextPath() + "/admin/import-product");
        request.getRequestDispatcher("/WEB-INF/views/admin/import-detail.jsp").forward(request, response);
    }

    private void showImportProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isStaffRoute(request)) {
            showHistory(request, response);
            return;
        }

        ProductDAO productDao = new ProductDAO();

        String keyword = trimToNull(request.getParameter("keyword"));
        String brandId = trimToNull(request.getParameter("brandId"));
        String categoryId = trimToNull(request.getParameter("categoryId"));
        String status = trimToNull(request.getParameter("status"));

        List<Product> listP = productDao.getProductsForAdminWithImportPrice(keyword, brandId, categoryId, status);
        List<Brand> brands = productDao.getAllBrands();
        List<Model.Category> categories = productDao.getAllCategories();
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();

        int activeCount = 0;
        for (Product product : listP) {
            if (product.isStatus()) {
                activeCount++;
            }
        }

        request.setAttribute("listP", listP);
        request.setAttribute("listB", brands);
        request.setAttribute("listC", categories);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedBrandId", brandId);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("resultCount", listP.size());
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("inactiveCount", listP.size() - activeCount);
        request.setAttribute("supplierList", suppliers);
        request.setAttribute("nextImportCode", generateImportCode());
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

        double total = 0;
        boolean hasError = false;
        boolean hasProduct = false;

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
            double price;
            try {
                quantity = Integer.parseInt(quantityRaw);
                price = Double.parseDouble(priceRaw);
            } catch (NumberFormatException ex) {
                hasError = true;
                break;
            }

            if (quantity > 0 && price > 0) {
                hasProduct = true;
                total += quantity * price;
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

        int orderId = dao.createPurchaseOrder(supplierId, managerId, importCode, invoiceNo, note);

        for (int i = 0; i < variantIds.length; i++) {
            String variantIdRaw = variantIds[i];
            String quantityRaw = quantities[i];
            String priceRaw = prices[i];

            if (variantIdRaw == null || variantIdRaw.trim().isEmpty()
                    || quantityRaw == null || quantityRaw.trim().isEmpty()) {
                continue;
            }

            int variantId = Integer.parseInt(variantIdRaw);
            int quantity = Integer.parseInt(quantityRaw);
            double price = Double.parseDouble(priceRaw);

            dao.insertPurchaseDetail(orderId, variantId, quantity, price);
        }

        dao.updateTotalAmount(orderId, total);
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

        int orderId = Integer.parseInt(orderIdRaw);
        int[] variantIds = parseIntArray(variantIdRaw);
        int[] receivedQuantities = parseIntArray(receivedQtyRaw);
        boolean ok = dao.confirmReceipt(orderId, manager.getManagerId(), variantIds, receivedQuantities);
        response.sendRedirect(buildHistoryRedirect(request, ok ? null : "importFailed", ok ? "received" : null));
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

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/staff/");
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

    private String generateImportCode() {
        return "IMP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}



