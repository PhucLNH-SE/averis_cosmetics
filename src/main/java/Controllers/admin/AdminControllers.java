package Controllers.admin;

import DALs.CustomerDAO;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminControllers extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action;
        switch (request.getServletPath()) {
            case "/admin/manage-users":
                action = "manage-users";
                break;
            default:
                action = request.getParameter("action");
                break;
        }
        if (action == null) {
            action = "dashboard";
        }

        switch (action) {
            case "manage-users":
                showManageUsers(request, response);
                break;
            case "dashboard":
            default:
                request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action;
        switch (request.getServletPath()) {
            case "/admin/update-user-status":
                action = "update-user-status";
                break;
            default:
                action = request.getParameter("action");
                break;
        }
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "update-user-status":
                updateUserStatus(request, response);
                break;
            default:
                doGet(request, response);
                break;
        }
    }

    private void showManageUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> users = customerDAO.getAllCustomers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/views/admin/manage-users.jsp").forward(request, response);
    }

    private void updateUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("status");
        if (idParam == null || statusParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(idParam);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users");
            return;
        }

        boolean targetStatus = Boolean.parseBoolean(statusParam);
        CustomerDAO customerDAO = new CustomerDAO();
        customerDAO.updateCustomerStatus(userId, targetStatus);

        response.sendRedirect(request.getContextPath() + "/admin/manage-users");
    }
}
