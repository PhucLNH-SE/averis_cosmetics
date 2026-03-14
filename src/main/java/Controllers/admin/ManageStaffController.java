package Controllers.admin;

import DALs.ManagerDAO;
import Model.Manager;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

public class ManageStaffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Manager admin = (Manager) session.getAttribute("manager");
        
        // Block nếu không phải ADMIN
        if (admin == null || !"ADMIN".equals(admin.getManagerRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        ManagerDAO dao = new ManagerDAO();
        List<Manager> listStaff = dao.getAllManagers();
        request.setAttribute("listStaff", listStaff);

        // Đẩy Flash Message
        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }
        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }

        request.setAttribute("currentView", "staff");
        request.setAttribute("contentPage", "/views/admin/partials/manage-staff-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Manager admin = (Manager) session.getAttribute("manager");
        if (admin == null || !"ADMIN".equals(admin.getManagerRole())) {
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

                // Kiểm tra trùng Email và trùng Tên
                if (dao.isEmailExist(email, 0)) {
                    session.setAttribute("errorMsg", "Email already exists!");
                } else if (dao.isNameExist(name, 0)) {
                    session.setAttribute("errorMsg", "Staff name already exists! Please choose another name.");
                } else {
                    Manager m = new Manager();
                    m.setFullName(name);
                    m.setEmail(email);
                    m.setPassword(password); // Mật khẩu sẽ được Hash trong DAO
                    m.setManagerRole(role);
                    m.setStatus(status); 
                    
                    if (dao.addManager(m)) session.setAttribute("successMsg", "Staff added successfully!");
                    else session.setAttribute("errorMsg", "Failed to add staff.");
                }

            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("managerId"));
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String role = request.getParameter("role");
                String password = request.getParameter("password"); 
                Boolean status = Boolean.parseBoolean(request.getParameter("status"));

                // Admin không thể tự đổi role hoặc ban chính mình
                if (id == admin.getManagerId() && (!"ADMIN".equals(role) || !status)) {
                    session.setAttribute("errorMsg", "You cannot demote or ban yourself!");
                } else if (dao.isEmailExist(email, id)) {
                    session.setAttribute("errorMsg", "Email already exists in another account!");
                } else if (dao.isNameExist(name, id)) {
                    session.setAttribute("errorMsg", "Staff name already exists in another account!");
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
                    
                    if (dao.updateManager(m)) session.setAttribute("successMsg", "Staff updated successfully!");
                    else session.setAttribute("errorMsg", "Failed to update staff.");
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("managerId"));
                if (id == admin.getManagerId()) {
                    session.setAttribute("errorMsg", "You cannot ban yourself!");
                } else {
                    if (dao.banManager(id)) session.setAttribute("successMsg", "Staff account banned successfully!");
                    else session.setAttribute("errorMsg", "Failed to ban staff.");
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
