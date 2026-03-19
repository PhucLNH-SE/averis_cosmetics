package Controllers.staff;

import DALs.ProductDAO;
import Model.Manager;
import Model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StaffProductController", urlPatterns = {"/staff/manage-product"})
public class StaffProductController extends HttpServlet {

    private static final String STAFF_ROLE = "STAFF";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

        if (manager == null || manager.getManagerRole() == null
                || !STAFF_ROLE.equalsIgnoreCase(manager.getManagerRole())) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        ProductDAO dao = new ProductDAO();
        List<Product> listP = dao.getAllProductsWithImportPrice();

        request.setAttribute("listP", listP);
        request.setAttribute("currentView", "products");
        request.setAttribute("contentPage", "/views/staff/partials/manage-product-content.jsp");

        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Staff only has read access.");
    }
}
