package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import Utils.ValidationUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

public class AuthController extends HttpServlet {

    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "login";
        }

        switch (action) {
            case "register":
                request.getRequestDispatcher("/WEB-INF/views/customer/auth/register.jsp")
                        .forward(request, response);
                break;
            case "login":
            default:
                consumeLoginFlashMessage(request.getSession(false), request);
                request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                        .forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "login";
        }

        switch (action) {
            case "register":
                registerCustomer(request, response);
                break;
            case "login":
                loginCustomer(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth");
                break;
        }
    }

    private void registerCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String fullName = request.getParameter("fullname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String gender = request.getParameter("gender");
        String dateOfBirthStr = request.getParameter("dateOfBirth");

        Map<String, String> errors = ValidationUtil.validateRegistration(
                username, fullName, email, gender, password, confirmPassword, dateOfBirthStr
        );

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/register.jsp")
                    .forward(request, response);
            return;
        }

        username = username.trim();
        fullName = fullName.trim();
        email = (email != null && !email.trim().isEmpty()) ? email.trim() : null;

        if (customerDAO.checkUsernameExists(username)) {
            errors.put("errorUsername", "Username already exists.");
        }

        if (email != null && customerDAO.checkEmailExists(email)) {
            errors.put("errorEmail", "Email already registered.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/register.jsp")
                    .forward(request, response);
            return;
        }

        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);

        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        customer.setGender(gender);
        customer.setDateOfBirth(dateOfBirth);
        customer.setStatus(true);
        customer.setEmailVerified(false);

        boolean inserted = customerDAO.insertCustomer(customer);

        if (inserted) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            request.setAttribute("popupMessage", "Registration failed. Please try again.");
            request.setAttribute("popupType", "error");
            request.setAttribute("errorMessage", "Registration failed.");
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/register.jsp")
                    .forward(request, response);
        }
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
                request.setAttribute("popupMessage",
                        "Your account has been deactivated.");
                request.setAttribute("popupType", "error");
                request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                        .forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("customer", customer);

            response.sendRedirect(request.getContextPath() + "/home");

        } else {
            request.setAttribute("popupMessage",
                    "Invalid username or password.");
            request.setAttribute("popupType", "error");
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/login.jsp")
                    .forward(request, response);
        }
    }

    private void consumeLoginFlashMessage(HttpSession session, HttpServletRequest request) {
        if (session == null) {
            return;
        }

        Object popupMessage = session.getAttribute("loginPopupMessage");
        if (popupMessage == null) {
            return;
        }

        request.setAttribute("popupMessage", popupMessage);
        request.setAttribute(
                "popupType",
                session.getAttribute("loginPopupType") != null
                        ? session.getAttribute("loginPopupType")
                        : "success"
        );

        session.removeAttribute("loginPopupMessage");
        session.removeAttribute("loginPopupType");
    }
}

