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
            HttpSession session, ManagerDAO dao, Manager selectedStaff, String formMode, String inlineError)
            throws ServletException, IOException {

        List<Manager> listStaff = dao.getAllStaff();
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
            HttpSession session, ManagerDAO dao) throws ServletException, IOException {
        forwardManageStaffPage(request, response, session, dao, null, null, null);
    }

    private void viewStaffDetail(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, ManagerDAO dao) throws ServletException, IOException {
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

        Manager staff = dao.getById(managerId);
        if (isInvalidStaff(staff)) {
            session.setAttribute("errorMsg", "Staff not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
            return;
        }

        OrderDAO orderDao = new OrderDAO();
        FeedbackDAO feedbackDao = new FeedbackDAO();
        List<Orders> handledOrders = orderDao.getOrdersHandledByManager(managerId);
        List<OrderDetail> feedbacks = feedbackDao.getFeedbacksByManagerResponse(managerId);

        request.setAttribute("staff", staff);
        request.setAttribute("handledOrders", handledOrders);
        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("currentView", "staff");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-staff-detail.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void loadStaffForUpdate(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, ManagerDAO dao) throws ServletException, IOException {
        String managerIdRaw = request.getParameter("managerId");
        if (managerIdRaw == null || managerIdRaw.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Staff not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
            return;
        }

        try {
            int managerId = Integer.parseInt(managerIdRaw);
            Manager selectedStaff = dao.getById(managerId);
            if (isInvalidStaff(selectedStaff)) {
                session.setAttribute("errorMsg", "Staff not found.");
                response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
                return;
            }

            forwardManageStaffPage(request, response, session, dao, selectedStaff, "update", null);
        } catch (NumberFormatException ex) {
            session.setAttribute("errorMsg", "Staff not found.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
        }
    }

    private void addStaff(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, ManagerDAO dao) {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        Boolean status = resolveStatus(request);

        if (dao.isEmailExist(email, 0)) {
            session.setAttribute("errorMsg", "Email already exists!");
            return;
        }

        if (dao.isNameExist(name, 0)) {
            session.setAttribute("errorMsg", "Staff name already exists! Please choose another name.");
            return;
        }

        Manager manager = new Manager();
        manager.setFullName(name);
        manager.setEmail(email);
        manager.setPassword(password);
        manager.setManagerRole(role);
        manager.setStatus(status);

        if (dao.addManager(manager)) {
            session.setAttribute("successMsg", "Staff added successfully!");
        } else {
            session.setAttribute("errorMsg", "Failed to add staff.");
        }
    }

    private String validateStaffUpdate(Manager admin, ManagerDAO dao, Manager selectedStaff) {
        Manager existingStaff = dao.getById(selectedStaff.getManagerId());
        if (isInvalidStaff(existingStaff)) {
            return "Staff not found.";
        }

        if (selectedStaff.getManagerId() == admin.getManagerId()
                && (!"ADMIN".equals(selectedStaff.getManagerRole()) || !selectedStaff.getStatus())) {
            return "You cannot demote or ban yourself!";
        }

        if (!"STAFF".equals(selectedStaff.getManagerRole())) {
            return "Invalid staff role.";
        }

        if (dao.isEmailExist(selectedStaff.getEmail(), selectedStaff.getManagerId())) {
            return "Email already exists in another account!";
        }

        if (dao.isNameExist(selectedStaff.getFullName(), selectedStaff.getManagerId())) {
            return "Staff name already exists in another account!";
        }

        return null;
    }

    private void forwardUpdateStaffError(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, ManagerDAO dao, Manager selectedStaff, String error)
            throws ServletException, IOException {
        forwardManageStaffPage(request, response, session, dao, selectedStaff, "update", error);
    }

    private void updateStaff(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Manager admin, ManagerDAO dao)
            throws ServletException, IOException {
        String password = request.getParameter("password");
        Manager selectedStaff = new Manager();
        selectedStaff.setManagerId(Integer.parseInt(request.getParameter("managerId")));
        selectedStaff.setFullName(request.getParameter("name"));
        selectedStaff.setEmail(request.getParameter("email"));
        selectedStaff.setManagerRole(request.getParameter("role"));
        selectedStaff.setStatus(resolveStatus(request));

        String validationError = validateStaffUpdate(admin, dao, selectedStaff);
        if (validationError != null) {
            if ("Staff not found.".equals(validationError)) {
                session.setAttribute("errorMsg", validationError);
                return;
            }
            forwardUpdateStaffError(request, response, session, dao, selectedStaff, validationError);
            return;
        }

        Manager manager = new Manager();
        manager.setManagerId(selectedStaff.getManagerId());
        manager.setFullName(selectedStaff.getFullName());
        manager.setEmail(selectedStaff.getEmail());
        manager.setManagerRole(selectedStaff.getManagerRole());
        manager.setStatus(selectedStaff.getStatus());

        if (password != null && !password.trim().isEmpty()) {
            manager.setPassword(password);
        } else {
            manager.setPassword(null);
        }

        if (dao.updateManager(manager)) {
            session.setAttribute("successMsg", "Staff updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Failed to update staff.");
        }
    }

    private void updateStaffStatus(HttpServletRequest request, HttpSession session,
            Manager admin, ManagerDAO dao, boolean active) {
        int id = Integer.parseInt(request.getParameter("managerId"));
        Manager targetStaff = dao.getById(id);

        if (isInvalidStaff(targetStaff)) {
            session.setAttribute("errorMsg", "Staff not found.");
            return;
        }

        if (!active && id == admin.getManagerId()) {
            session.setAttribute("errorMsg", "You cannot ban yourself!");
            return;
        }

        boolean success = active ? dao.unbanManager(id) : dao.banManager(id);

        if (success) {
            session.setAttribute("successMsg", active
                    ? "Staff account unlocked successfully!"
                    : "Staff account banned successfully!");
        } else {
            session.setAttribute("errorMsg", active
                    ? "Failed to unlock staff."
                    : "Failed to ban staff.");
        }
    }

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
        ManagerDAO dao = new ManagerDAO();

        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action.toLowerCase()) {
            case "detail":
                viewStaffDetail(request, response, session, dao);
                break;
            case "edit":
                loadStaffForUpdate(request, response, session, dao);
                break;
            case "list":
            default:
                viewStaffList(request, response, session, dao);
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
        ManagerDAO dao = new ManagerDAO();

        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action.toLowerCase()) {
                case "add":
                    addStaff(request, response, session, dao);
                    break;
                case "update":
                    updateStaff(request, response, session, admin, dao);
                    break;
                case "ban":
                    updateStaffStatus(request, session, admin, dao, false);
                    break;
                case "unban":
                    updateStaffStatus(request, session, admin, dao, true);
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
}


