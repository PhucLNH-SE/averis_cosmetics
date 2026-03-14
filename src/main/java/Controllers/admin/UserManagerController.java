package Controllers.admin;

import java.io.IOException;
import java.util.List;

import DALs.CustomerDAO;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserManagerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/dashboard":
                response.sendRedirect(request.getContextPath() + "/admin/panel?view=dashboard");
                break;
            case "/admin/manage-users":
                response.sendRedirect(request.getContextPath() + "/admin/panel?view=users");
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        switch (path) {
            case "/admin/update-user-status":
                updateUserStatus(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void updateUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("status");

        if (idParam == null || statusParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/panel?view=users");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);
            boolean targetStatus = Boolean.parseBoolean(statusParam);
            CustomerDAO customerDAO = new CustomerDAO();
            customerDAO.updateCustomerStatus(userId, targetStatus);
            response.sendRedirect(request.getContextPath() + "/admin/panel?view=users&success=update");
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/panel?view=users&error=updateFailed");
        }
    }
}
