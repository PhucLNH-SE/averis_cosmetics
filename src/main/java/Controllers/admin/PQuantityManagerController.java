package Controllers.admin;

import DALs.ProductDAO;
import Model.ProductVariant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PQuantityManagerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                list(request, response);
                break;

            default:
                list(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "";
        }

        switch (action) {
            case "updateStock":
                updateStock(request, response);
                break;

            default:
                response.sendRedirect("PQuantityManagerController");
                break;
        }
    }

    // =========================
    // Hiển thị danh sách
    // =========================
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();
        List<ProductVariant> list = dao.getAllProductQuantity();

        request.setAttribute("list", list);
        request.getRequestDispatcher("/views/admin/manage-quantityproduct.jsp")
               .forward(request, response);
    }

    // =========================
    // Update stock
    // =========================
    private void updateStock(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int variantId = Integer.parseInt(request.getParameter("variantId"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        ProductDAO dao = new ProductDAO();
        dao.updateStock(variantId, stock);

        response.sendRedirect("PQuantityManagerController?action=list");
    }
}