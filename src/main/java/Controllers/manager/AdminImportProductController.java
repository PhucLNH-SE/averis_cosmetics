package Controllers.manager;

import DALs.ImportProductDAO;
import Model.Brand;
import Model.Manager;
import DALs.ProductDAO;
import Model.Product;
import Model.PurchaseDetail;
import Model.PurchaseOrder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class AdminImportProductController extends HttpServlet {

    private final ImportProductDAO dao = new ImportProductDAO();

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
            default:
                showImportProduct(request, response);
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
            case "importproduct":
            default:
                importProduct(request, response);
                break;
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        List<PurchaseDetail> details = dao.getImportOrderDetail(orderId);
        String status = dao.getImportOrderStatus(orderId);
        request.setAttribute("details", details);
        request.setAttribute("orderStatus", status);
        request.setAttribute("orderId", orderId);
        request.getRequestDispatcher("/WEB-INF/views/admin/import-detail.jsp").forward(request, response);
    }

    private void showImportProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO productDao = new ProductDAO();

        String keyword = trimToNull(request.getParameter("keyword"));
        String brandId = trimToNull(request.getParameter("brandId"));
        String categoryId = trimToNull(request.getParameter("categoryId"));
        String status = trimToNull(request.getParameter("status"));

        List<Product> listP = productDao.getProductsForAdminWithImportPrice(keyword, brandId, categoryId, status);
        List<Brand> brands = productDao.getAllBrands();
        List<Model.Category> categories = productDao.getAllCategories();

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

        request.setAttribute("currentView", "inventory");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/import-product-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
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
        int brandId = Integer.parseInt(request.getParameter("brandId"));
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

        double total = 0;
        boolean hasError = false;
        boolean hasProduct = false;

        for (int i = 0; i < variantIds.length; i++) {
            String quantityRaw = quantities[i];
            String priceRaw = prices[i];

            if ((quantityRaw != null && !quantityRaw.trim().isEmpty() && (priceRaw == null || priceRaw.trim().isEmpty()))
                    || (priceRaw != null && !priceRaw.trim().isEmpty() && (quantityRaw == null || quantityRaw.trim().isEmpty()))) {
                hasError = true;
                break;
            }

            if (quantityRaw == null || quantityRaw.trim().isEmpty()) {
                continue;
            }

            int quantity = Integer.parseInt(quantityRaw);
            double price = Double.parseDouble(priceRaw);

            if (quantity > 0 && price > 0) {
                hasProduct = true;
                total += quantity * price;
            }
        }

        if (hasError) {
            request.setAttribute("error", "Phai nhap ca Quantity va Import Price.");
            request.setAttribute("selectedBrand", brandId);
            showImportProduct(request, response);
            return;
        }

        if (!hasProduct) {
            request.setAttribute("error", "Ban chua nhap san pham nao.");
            request.setAttribute("selectedBrand", brandId);
            showImportProduct(request, response);
            return;
        }

        int orderId = dao.createPurchaseOrder(brandId, managerId);

        for (int i = 0; i < variantIds.length; i++) {
            String quantityRaw = quantities[i];
            String priceRaw = prices[i];

            if (quantityRaw == null || quantityRaw.trim().isEmpty()) {
                continue;
            }

            int variantId = Integer.parseInt(variantIds[i]);
            int quantity = Integer.parseInt(quantityRaw);
            double price = Double.parseDouble(priceRaw);

            dao.insertPurchaseDetail(orderId, variantId, quantity, price);
        }

        dao.updateTotalAmount(orderId, total);
        response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&success=import");
    }

    private void showHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<PurchaseOrder> history = dao.getImportHistory();
        request.setAttribute("history", history);
        request.setAttribute("currentView", "inventory");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-importproduct-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
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

            response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&error=importFailed");
            return;
        }

        int orderId = Integer.parseInt(orderIdRaw);
        boolean ok = dao.confirmReceipt(orderId, manager.getManagerId());
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&success=received");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&error=importFailed");
        }
    }
}



