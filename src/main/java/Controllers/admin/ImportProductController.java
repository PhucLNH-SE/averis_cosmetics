package Controllers.admin;

import DALs.ImportProductDAO;
import Model.Brand;
import Model.Manager;
import Model.ProductVariant;
import Model.PurchaseDetail;
import Model.PurchaseOrder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ImportProductController extends HttpServlet {

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
        importProduct(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        List<PurchaseDetail> details = dao.getImportOrderDetail(orderId);
        request.setAttribute("details", details);
        request.getRequestDispatcher("/views/admin/import-detail.jsp").forward(request, response);
    }

    private void showImportProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Brand> brands = dao.getAllBrands();
        request.setAttribute("brands", brands);

        String brandIdRaw = request.getParameter("brandId");
        if (brandIdRaw != null && !brandIdRaw.trim().isEmpty()) {
            int brandId = Integer.parseInt(brandIdRaw);
            List<ProductVariant> variants = dao.getVariantByBrand(brandId);
            request.setAttribute("variants", variants);
            request.setAttribute("selectedBrand", brandId);
        }

        request.setAttribute("currentView", "inventory");
        request.setAttribute("contentPage", "/views/admin/partials/import-product-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
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
            dao.updateStock(variantId, quantity, price);
        }

        dao.updateTotalAmount(orderId, total);
        response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history&success=import");
    }

    private void showHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<PurchaseOrder> history = dao.getImportHistory();
        request.setAttribute("history", history);
        request.setAttribute("currentView", "inventory");
        request.setAttribute("contentPage", "/views/admin/partials/manage-importproduct-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }
}
