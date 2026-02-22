package Controllers.customer;

import DALs.CartDetailDAO;
import DALs.CustomerDAO;
import DALs.ProductDAO;
import Model.CartItem;
import Model.CartDetail;
import Model.Customer;
import Model.ProductVariant;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "AuthController", urlPatterns = {"/auth"})
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
            action = "login"; // Mặc định là trang đăng nhập
        }

        switch (action) {
            case "register":
                showRegistrationForm(request, response);
                break;
            case "login":
            default:
                showLoginForm(request, response);
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
                showLoginForm(request, response);
                break;
        }
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/customer/auth/login.jsp").forward(request, response);
    }

    private void showRegistrationForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
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

        if (!validateRegistrationData(request, username, fullName, email, password, confirmPassword, dateOfBirthStr)) {
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
            return;
        }

        if (customerDAO.checkUsernameExists(username.trim())) {
            request.setAttribute("errorUsername", "Username already exists. Please choose another one.");
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
            return;
        }

        if (email != null && !email.trim().isEmpty() && customerDAO.checkEmailExists(email.trim())) {
            request.setAttribute("errorEmail", "Email already registered. Please use another email.");
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
            return;
        }

        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);

        Customer customer = new Customer();
        customer.setUsername(username.trim());
        customer.setFullName(fullName.trim());
        customer.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
        customer.setPassword(hashPassword(password));
        customer.setGender(gender != null && !gender.trim().isEmpty() ? gender.trim() : null);
        customer.setDateOfBirth(dateOfBirth);
        customer.setStatus(true);
        customer.setEmailVerified(false);

        boolean isRegistered = customerDAO.insertCustomer(customer);

        if (isRegistered) {
            String successMessage = "Registration successful! Please log in.";
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                successMessage = "Registration successful! Please log in. You can verify your email from Profile after logging in.";
            }
            request.setAttribute("successMessage", successMessage);
            request.getRequestDispatcher("/views/customer/auth/login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorUsername", "Registration failed. Please try again.");
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
        }
    }

    private void loginCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Tìm kiếm người dùng theo username
        Customer customer = customerDAO.getCustomerByUsername(username);

        if (customer != null && verifyPassword(password, customer.getPassword())) {
            if (customer.getStatus()) {
                HttpSession session = request.getSession();
                session.setAttribute("customer", customer);

                CartDetailDAO cartDetailDAO = new CartDetailDAO();
                ProductDAO productDAO = new ProductDAO();
                Map<Integer, CartItem> cart = new HashMap<>();
                List<CartDetail> details = cartDetailDAO.getByCustomerId(customer.getCustomerId());
                for (CartDetail d : details) {
                    ProductVariant v = productDAO.getVariantById(d.getVariantId());
                    if (v != null) {
                        cart.put(d.getVariantId(), new CartItem(v, d.getQuantity()));
                    }
                }
                session.setAttribute("cart", cart);

                String redirectUrl = (String) request.getSession().getAttribute("redirectAfterLogin");
                if (redirectUrl != null) {
                    request.getSession().removeAttribute("redirectAfterLogin");
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/");
                }
            } else {
                request.setAttribute("errorMessage", "Your account has been deactivated. Please contact administrator.");
                request.getRequestDispatcher("/views/customer/auth/login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/views/customer/auth/login.jsp").forward(request, response);
        }
    }

    private boolean validateRegistrationData(HttpServletRequest request, String username, String fullName, String email,
            String password, String confirmPassword, String dateOfBirthStr) {
        boolean valid = true;
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("errorUsername", "Username is required.");
            valid = false;
        } else if (username.trim().length() < 3) {
            request.setAttribute("errorUsername", "Username must be at least 3 characters long.");
            valid = false;
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorFullName", "Full name is required.");
            valid = false;
        } else if (fullName.trim().length() < 2) {
            request.setAttribute("errorFullName", "Full name must be at least 2 characters long.");
            valid = false;
        }
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            request.setAttribute("errorEmail", "Please enter a valid email address.");
            valid = false;
        }
        if (password == null || password.length() < 6) {
            request.setAttribute("errorPassword", "Password must be at least 6 characters long.");
            valid = false;
        }
        if (password != null && confirmPassword != null && !password.equals(confirmPassword)) {
            request.setAttribute("errorConfirmPassword", "Passwords do not match.");
            valid = false;
        }
        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
            request.setAttribute("errorDateOfBirth", "Date of birth is required.");
            valid = false;
        } else {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirthStr);
                if (dob.isAfter(LocalDate.now())) {
                    request.setAttribute("errorDateOfBirth", "Date of birth cannot be in the future.");
                    valid = false;
                }
            } catch (Exception e) {
                request.setAttribute("errorDateOfBirth", "Please enter a valid date of birth.");
                valid = false;
            }
        }
        return valid;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}