package Controllers.admin;

import DALs.CustomerDAO;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminPanelController extends HttpServlet {

    private static final String DEFAULT_VIEW = "statistic";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        if (view == null || view.trim().isEmpty()) {
            view = DEFAULT_VIEW;
        }

        view = view.trim().toLowerCase();
        request.setAttribute("currentView", view);

        switch (view) {
            case "users":
                loadUsers(request);
                request.setAttribute("contentPage", "/views/admin/partials/manage-users-content.jsp");
                break;
            case "products":
                response.sendRedirect(request.getContextPath() + "/admin/manage-product");
                return;
            case "inventory":
                response.sendRedirect(request.getContextPath() + "/admin/import-product?action=history");
                return;
            case "brands":
                response.sendRedirect(request.getContextPath() + "/admin/manage-brand");
                return;
            case "categories":
                response.sendRedirect(request.getContextPath() + "/admin/manage-category");
                return;
            case "staff":
                response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
                return;
            case "voucher":
                response.sendRedirect(request.getContextPath() + "/admin/manage-voucher");
                return;
            case "statistic":
                response.sendRedirect(request.getContextPath() + "/admin/manage-statistic");
                return;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/manage-statistic");
                return;
        }

        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void loadUsers(HttpServletRequest request) {
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> users = customerDAO.getAllCustomers();
        request.setAttribute("users", users);
    }
}
