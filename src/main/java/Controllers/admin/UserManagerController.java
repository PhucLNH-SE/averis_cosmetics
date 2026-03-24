package Controllers.admin;

import DALs.CustomerDAO;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserManagerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = trimToNull(request.getParameter("action"));
        if (action == null) {
            showUserList(request, response, null, null);
            return;
        }

        switch (action.toLowerCase()) {
            case "detail":
                showDetail(request, response);
                break;
            default:
                showUserList(request, response, null, null);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = trimToNull(request.getParameter("action"));

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users");
            return;
        }

        switch (action.toLowerCase()) {
            case "ban":
                updateUserStatus(request, response, false, "locked", "lockFailed");
                break;
            case "unlock":
                updateUserStatus(request, response, true, "unlocked", "unlockFailed");
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = trimToNull(request.getParameter("id"));
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users?error=notFound");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);
            CustomerDAO customerDAO = new CustomerDAO();
            Customer selectedUser = customerDAO.getCustomerById(userId);
            if (selectedUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-users?error=notFound");
                return;
            }

            showUserList(request, response, selectedUser, "detail");
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users?error=notFound");
        }
    }

    private void updateUserStatus(HttpServletRequest request, HttpServletResponse response,
            boolean status, String successCode, String failedCode)
            throws IOException {
        String idParam = trimToNull(request.getParameter("id"));
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users?error=notFound");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);
            CustomerDAO customerDAO = new CustomerDAO();
            Customer existingUser = customerDAO.getCustomerById(userId);
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-users?error=notFound");
                return;
            }

            boolean updated = customerDAO.updateCustomerStatus(userId, status);
            response.sendRedirect(request.getContextPath() + "/admin/manage-users?success="
                    + (updated ? successCode : failedCode));
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-users?error=notFound");
        }
    }

    private void showUserList(HttpServletRequest request, HttpServletResponse response,
            Customer selectedUser, String modalMode)
            throws ServletException, IOException {
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> users = customerDAO.getAllCustomers();

        request.setAttribute("users", users);
        request.setAttribute("selectedUser", selectedUser);
        request.setAttribute("modalMode", modalMode);
        request.setAttribute("currentView", "users");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-users-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
