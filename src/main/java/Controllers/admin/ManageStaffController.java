package Controllers.admin;

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

public class ManageStaffController extends HttpServlet {

    private boolean isUnauthorizedAdmin(Manager admin) {
        return admin == null || !"ADMIN".equals(admin.getManagerRole());
    }

    private void forwardManageStaffPage(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, ManagerDAO dao, Manager selectedStaff, String formMode, String inlineError)
            throws ServletException, IOException {

        List<Manager> listStaff = dao.getAllManagers();
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

        if ("detail".equalsIgnoreCase(action)) {
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
            return;
        }

        if ("edit".equalsIgnoreCase(action)) {
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
                return;
            } catch (NumberFormatException ex) {
                session.setAttribute("errorMsg", "Staff not found.");
                response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
                return;
            }
        }

        forwardManageStaffPage(request, response, session, dao, null, null, null);
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

        try {
            if ("add".equals(action)) {
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                String role = request.getParameter("role");
                Boolean status = Boolean.parseBoolean(request.getParameter("status"));

                if (dao.isEmailExist(email, 0)) {
                    session.setAttribute("errorMsg", "Email already exists!");
                } else if (dao.isNameExist(name, 0)) {
                    session.setAttribute("errorMsg", "Staff name already exists! Please choose another name.");
                } else {
                    Manager m = new Manager();
                    m.setFullName(name);
                    m.setEmail(email);
                    m.setPassword(password);
                    m.setManagerRole(role);
                    m.setStatus(status);

                    if (dao.addManager(m)) {
                        session.setAttribute("successMsg", "Staff added successfully!");
                    } else {
                        session.setAttribute("errorMsg", "Failed to add staff.");
                    }
                }

            } else if ("update".equals(action)) {
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
                } else if (dao.isEmailExist(email, id)) {
                    forwardManageStaffPage(request, response, session, dao, selectedStaff, "update",
                            "Email already exists in another account!");
                    return;
                } else if (dao.isNameExist(name, id)) {
                    forwardManageStaffPage(request, response, session, dao, selectedStaff, "update",
                            "Staff name already exists in another account!");
                    return;
                } else {
                    Manager m = new Manager();
                    m.setManagerId(id);
                    m.setFullName(name);
                    m.setEmail(email);
                    m.setManagerRole(role);
                    m.setStatus(status);

                    if (password != null && !password.trim().isEmpty()) {
                        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                        m.setPassword(hashedPassword);
                    } else {
                        m.setPassword(null);
                    }

                    if (dao.updateManager(m)) {
                        session.setAttribute("successMsg", "Staff updated successfully!");
                    } else {
                        session.setAttribute("errorMsg", "Failed to update staff.");
                    }
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("managerId"));
                if (id == admin.getManagerId()) {
                    session.setAttribute("errorMsg", "You cannot ban yourself!");
                } else {
                    if (dao.banManager(id)) {
                        session.setAttribute("successMsg", "Staff account banned successfully!");
                    } else {
                        session.setAttribute("errorMsg", "Failed to ban staff.");
                    }
                }

            } else if ("unban".equals(action)) {
                int id = Integer.parseInt(request.getParameter("managerId"));
                if (dao.unbanManager(id)) {
                    session.setAttribute("successMsg", "Staff account unlocked successfully!");
                } else {
                    session.setAttribute("errorMsg", "Failed to unlock staff.");
                }
            }

        } catch (Exception e) {
            session.setAttribute("errorMsg", "System error occurred!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/manage-staff");
    }
}
