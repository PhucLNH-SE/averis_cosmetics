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
import org.mindrot.jbcrypt.BCrypt;

public class AdminStaffController extends HttpServlet {

    private boolean isUnauthorizedAdmin(Manager admin) {
        return admin == null || !"ADMIN".equals(admin.getManagerRole());
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
        if (staff == null) {
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
            if (selectedStaff == null) {
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
        Boolean status = Boolean.parseBoolean(request.getParameter("status"));

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

    private void updateStaff(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Manager admin, ManagerDAO dao)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("managerId"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        String password = request.getParameter("password");
        Boolean status = Boolean.parseBoolean(request.getParameter("status"));

        Manager selectedStaff = new Manager();
        selectedStaff.setManagerId(id);
        selectedStaff.setFullName(name);
        selectedStaff.setEmail(email);
        selectedStaff.setManagerRole(role);
        selectedStaff.setStatus(status);

        if (id == admin.getManagerId() && (!"ADMIN".equals(role) || !status)) {
            forwardManageStaffPage(request, response, session, dao, selectedStaff, "update",
                    "You cannot demote or ban yourself!");
            return;
        }

        if (dao.isEmailExist(email, id)) {
            forwardManageStaffPage(request, response, session, dao, selectedStaff, "update",
                    "Email already exists in another account!");
            return;
        }

        if (dao.isNameExist(name, id)) {
            forwardManageStaffPage(request, response, session, dao, selectedStaff, "update",
                    "Staff name already exists in another account!");
            return;
        }

        Manager manager = new Manager();
        manager.setManagerId(id);
        manager.setFullName(name);
        manager.setEmail(email);
        manager.setManagerRole(role);
        manager.setStatus(status);

        if (password != null && !password.trim().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            manager.setPassword(hashedPassword);
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


