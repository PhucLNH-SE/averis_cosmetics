package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import Utils.ValidationUtil;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController extends HttpServlet {

    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loginCustomer(request, response);
    }

    private void loginCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Map<String, String> errors = ValidationUtil.validateLogin(username, password);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                    .forward(request, response);
            return;
        }

        username = username.trim();

        Customer customer = customerDAO.getCustomerByUsername(username);

        if (customer != null && BCrypt.checkpw(password, customer.getPassword())) {

            if (!customer.getStatus()) {
                request.setAttribute("popupMessage", "Your account has been deactivated.");
                request.setAttribute("popupType", "error");
                request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                        .forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("customer", customer);

            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("popupMessage", "Invalid username or password.");
        request.setAttribute("popupType", "error");
        request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                .forward(request, response);
    }
}
