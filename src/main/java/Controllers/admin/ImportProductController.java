package Controllers.admin;

import DALs.ImportProductDAO;
import Model.Brand;
import Model.ProductVariant;
import Model.Manager;
import Model.PurchaseDetail;
import Model.PurchaseOrder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class ImportProductController extends HttpServlet {

    ImportProductDAO dao = new ImportProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "importproduct";
        }

        switch (action) {

            case "importproduct":
                showImportProduct(request, response);
                break;

            case "history":
                showHistory(request, response);
                break;
                   case "viewdetail":
        showDetail(request, response);
        break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        importProduct(request, response);
    }
// =========================
// VIEW DETAIL PAGE
// =========================
private void showDetail(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    int orderId = Integer.parseInt(request.getParameter("orderId"));

    List<PurchaseDetail> details = dao.getImportOrderDetail(orderId);

    request.setAttribute("details", details);

    request.getRequestDispatcher("/views/admin/import-detail.jsp")
            .forward(request, response);
}
    // =========================
    // IMPORT PRODUCT PAGE
    // =========================
    private void showImportProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Brand> brands = dao.getAllBrands();
        request.setAttribute("brands", brands);

        String brandIdRaw = request.getParameter("brandId");

        if (brandIdRaw != null) {

            int brandId = Integer.parseInt(brandIdRaw);

            List<ProductVariant> variants = dao.getVariantByBrand(brandId);

            request.setAttribute("variants", variants);
            request.setAttribute("selectedBrand", brandId);
        }

        request.getRequestDispatcher("/views/admin/import-product.jsp")
                .forward(request, response);
    }

    // =========================
    // IMPORT PRODUCT ACTION
    // =========================
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

            String q = quantities[i];
            String p = prices[i];

            // nếu nhập 1 cái mà thiếu cái còn lại
            if ((q != null && !q.trim().isEmpty() && (p == null || p.trim().isEmpty())) ||
                (p != null && !p.trim().isEmpty() && (q == null || q.trim().isEmpty()))) {

                hasError = true;
                break;
            }

            if (q == null || q.trim().isEmpty()) {
                continue;
            }

            int quantity = Integer.parseInt(q);
            double price = Double.parseDouble(p);
            int variantId = Integer.parseInt(variantIds[i]);

            if (quantity > 0 && price > 0) {
                hasProduct = true;
                total += quantity * price;
            }
        }

        // nếu lỗi nhập thiếu field
        if (hasError) {
            request.setAttribute("error", "Phải nhập cả Quantity và Import Price.");
            showImportProduct(request, response);
            return;
        }

        // nếu không nhập sản phẩm nào
        if (!hasProduct) {
            request.setAttribute("error", "Bạn chưa nhập sản phẩm nào.");
            showImportProduct(request, response);
            return;
        }

        // tạo order sau khi đã validate
        int orderId = dao.createPurchaseOrder(brandId, managerId);

        for (int i = 0; i < variantIds.length; i++) {

            String q = quantities[i];
            String p = prices[i];

            if (q == null || q.trim().isEmpty()) {
                continue;
            }

            int variantId = Integer.parseInt(variantIds[i]);
            int quantity = Integer.parseInt(q);
            double price = Double.parseDouble(p);

            dao.insertPurchaseDetail(orderId, variantId, quantity, price);
            dao.updateStock(variantId, quantity);
        }

        dao.updateTotalAmount(orderId, total);

        response.sendRedirect("ImportProductController?action=importproduct");
    }

    // =========================
    // HISTORY PAGE
    // =========================
private void showHistory(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    List<PurchaseOrder> history = dao.getImportHistory();

    request.setAttribute("history", history);

    request.getRequestDispatcher("/views/admin/manage-importproduct.jsp")
            .forward(request, response);
}

}