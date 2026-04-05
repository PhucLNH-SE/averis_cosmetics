package Controllers.manager;

import DALs.ManagerDAO;
import Model.Manager;
import Utils.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class ManagerAuthController extends HttpServlet {

    private ManagerDAO managerDAO;
// contrutor
    @Override
    public void init() throws ServletException {
        managerDAO = new ManagerDAO();
    }
// get manager if login
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Manager manager = (Manager) session.getAttribute("manager");
            if (manager != null) {
                redirectByRole(manager.getManagerRole(), request, response);
                return;
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/common/manager-login.jsp").forward(request, response);
    }
// check validation 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Email and password must not be left blank..");
            request.getRequestDispatcher("/WEB-INF/views/common/manager-login.jsp").forward(request, response);
            return;
        }

        ValidationUtil util = new ValidationUtil();
        Manager manager = managerDAO.getByEmail(email.trim());
        if (manager == null || !util.checkLogin(password, manager.getPassword())) {
            request.setAttribute("errorMessage", "Incorrect email or password.");
            request.getRequestDispatcher("/WEB-INF/views/common/manager-login.jsp").forward(request, response);
            return;
        }

        if (manager.getStatus() == null || !manager.getStatus()) {
            request.setAttribute("errorMessage", "The account has been locked..");
            request.getRequestDispatcher("/WEB-INF/views/common/manager-login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("manager", manager);
        session.setAttribute("managerRole", manager.getManagerRole());
        redirectByRole(manager.getManagerRole(), request, response);
    }
// redicrect role
    private void redirectByRole(String role, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String normalizedRole = role == null ? "" : role.toUpperCase();
        switch (normalizedRole) {
            case "ADMIN":
                response.sendRedirect(request.getContextPath() + "/admin/manage-statistic");
                break;
            case "STAFF":
                response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                break;
            default:
                request.setAttribute("errorMessage", "Invalid role.");
                request.getRequestDispatcher("/WEB-INF/views/common/manager-login.jsp").forward(request, response);
                break;
        }
    }
}

