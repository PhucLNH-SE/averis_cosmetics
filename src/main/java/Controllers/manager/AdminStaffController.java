package Controllers.manager;

import DALs.FeedbackDAO;
import DALs.ManagerDAO;
import DALs.OrderDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.Orders;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminStaffController extends HttpServlet {

    private ManagerDAO managerDAO = new ManagerDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Manager admin = (Manager) session.getAttribute("manager");
        if (isUnauthorizedAdmin(admin)) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action.toLowerCase()) {
            case "detail":
                viewStaffDetail(request, response, session);
                break;
            case "edit":
                loadStaffForUpdate(request, response, session);
                break;
            case "list":
            default:
                viewStaffList(request, response, session);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Manager admin = (Manager) session.getAttribute("manager");
        if (isUnauthorizedAdmin(admin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action.toLowerCase()) {
                case "add":
                    addStaff(request, session);
                    break;
                case "update":
                    updateStaff(request, response, session);
                    break;
                case "list":
                default:
                    break;
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "System error occurred!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
    }

    private boolean isUnauthorizedAdmin(Manager admin) {
        return admin == null || !"ADMIN".equals(admin.getManagerRole());
    }

    private boolean isInvalidStaff(Manager manager) {
        return manager == null || !"STAFF".equals(manager.getManagerRole());
    }

    private boolean resolveStatus(HttpServletRequest request) {
        String[] values = request.getParameterValues("status");
        if (values == null) {
            return false;
        }

        for (String value : values) {
            if (Boolean.parseBoolean(value)) {
                return true;
            }
        }

        return false;
    }

    private void forwardManageStaffPage(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Manager selectedStaff, String formMode, String inlineError)
            throws ServletException, IOException {

        List<Manager> listStaff = managerDAO.getAllStaff();
        request.setAttribute("listStaff", listStaff);

        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }
        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }
        if (inlineError != null && !inlineError.trim().isEmpty()) {
            request.setAttribute("errorMsg", inlineError);
        }

        request.setAttribute("selectedStaff", selectedStaff);
        request.setAttribute("formMode", formMode);
        request.setAttribute("currentView", "staff");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-staff-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void viewStaffList(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        forwardManageStaffPage(request, response, session, null, null, null);
    }

    private void viewStaffDetail(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        String managerIdRaw = request.getParameter("managerId");
        if (managerIdRaw == null || managerIdRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
            return;
        }

        int managerId;
        try {
            managerId = Integer.parseInt(managerIdRaw);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
            return;
        }

        Manager staff = managerDAO.getById(managerId);
        if (isInvalidStaff(staff)) {
            session.setAttribute("errorMsg", "Staff not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
            return;
        }

        List<Orders> handledOrders = orderDAO.getOrdersHandledByManager(managerId);
        List<OrderDetail> feedbacks = feedbackDAO.getFeedbacksByManagerResponse(managerId);

        request.setAttribute("staff", staff);
        request.setAttribute("handledOrders", handledOrders);
        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("currentView", "staff");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-staff-detail.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void loadStaffForUpdate(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        String managerIdRaw = request.getParameter("managerId");
        if (managerIdRaw == null || managerIdRaw.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Staff not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
            return;
        }

        try {
            int managerId = Integer.parseInt(managerIdRaw);
            Manager selectedStaff = managerDAO.getById(managerId);
            if (isInvalidStaff(selectedStaff)) {
                session.setAttribute("errorMsg", "Staff not found.");
                response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
                return;
            }

            forwardManageStaffPage(request, response, session, selectedStaff, "update", null);
        } catch (NumberFormatException ex) {
            session.setAttribute("errorMsg", "Staff not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
        }
    }

    private void addStaff(HttpServletRequest request, HttpSession session) {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        Boolean status = resolveStatus(request);

        if (name == null || name.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Staff name is required.");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Email is required.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Password is required.");
            return;
        }
        if (role == null || role.trim().isEmpty() || !"STAFF".equals(role.trim())) {
            session.setAttribute("errorMsg", "Invalid staff role.");
            return;
        }

        name = name.trim();
        email = email.trim();
        password = password.trim();
        role = role.trim();

        if (managerDAO.isEmailExist(email, 0)) {
            session.setAttribute("errorMsg", "Email already exists!");
            return;
        }

        if (managerDAO.addManager(name, email, password, role, status)) {
            session.setAttribute("successMsg", "Staff added successfully!");
        } else {
            session.setAttribute("errorMsg", "Failed to add staff.");
        }
    }

    private String validateStaffUpdate(Manager selectedStaff) {
        Manager existingStaff = managerDAO.getById(selectedStaff.getManagerId());
        if (isInvalidStaff(existingStaff)) {
            return "Staff not found.";
        }

        if (!"STAFF".equals(selectedStaff.getManagerRole())) {
            return "Invalid staff role.";
        }

        if (managerDAO.isEmailExist(selectedStaff.getEmail(), selectedStaff.getManagerId())) {
            return "Email already exists in another account!";
        }

        return null;
    }

    private void forwardUpdateStaffError(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Manager selectedStaff, String error)
            throws ServletException, IOException {
        forwardManageStaffPage(request, response, session, selectedStaff, "update", error);
    }

    private void updateStaff(HttpServletRequest request, HttpServletResponse response,
            HttpSession session)
            throws ServletException, IOException {
        String managerIdRaw = request.getParameter("managerId");
        if (managerIdRaw == null || managerIdRaw.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Staff not found.");
            return;
        }

        int managerId;
        try {
            managerId = Integer.parseInt(managerIdRaw.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("errorMsg", "Staff not found.");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        String password = request.getParameter("password");

        if (name == null || name.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Staff name is required.");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Email is required.");
            return;
        }
        if (role == null || role.trim().isEmpty() || !"STAFF".equals(role.trim())) {
            session.setAttribute("errorMsg", "Invalid staff role.");
            return;
        }

        Manager selectedStaff = new Manager();
        selectedStaff.setManagerId(managerId);
        selectedStaff.setFullName(name.trim());
        selectedStaff.setEmail(email.trim());
        selectedStaff.setManagerRole(role.trim());
        selectedStaff.setStatus(resolveStatus(request));

        String validationError = validateStaffUpdate(selectedStaff);
        if (validationError != null) {
            if ("Staff not found.".equals(validationError)) {
                session.setAttribute("errorMsg", validationError);
                return;
            }
            forwardUpdateStaffError(request, response, session, selectedStaff, validationError);
            return;
        }

        String newPassword = null;
        if (password != null && !password.trim().isEmpty()) {
            newPassword = password.trim();
        }

        if (managerDAO.updateManager(
                selectedStaff.getManagerId(),
                selectedStaff.getFullName(),
                selectedStaff.getEmail(),
                selectedStaff.getManagerRole(),
                selectedStaff.getStatus(),
                newPassword)) {
            session.setAttribute("successMsg", "Staff updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Failed to update staff.");
        }
    }

}
