package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import java.io.IOException;
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

        // Validate dữ liệu đầu vào
        String errorMessage = validateRegistrationData(username, fullName, email, password, confirmPassword, dateOfBirthStr);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
            return;
        }

        // Kiểm tra xem username hoặc email đã tồn tại chưa
        if (customerDAO.checkUsernameExists(username)) {
            request.setAttribute("errorMessage", "Username already exists. Please choose another one.");
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
            return;
        }

        if (email != null && !email.trim().isEmpty() && customerDAO.checkEmailExists(email.trim())) {
            request.setAttribute("errorMessage", "Email already registered. Please use another email.");
            request.getRequestDispatcher("/views/customer/auth/register.jsp").forward(request, response);
            return;
        }

        // Chuyển đổi ngày sinh
        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);

        // Tạo đối tượng Customer mới
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setFullName(fullName);
        customer.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
        customer.setPassword(hashPassword(password)); // Mã hóa mật khẩu
        customer.setGender(gender);
        customer.setDateOfBirth(dateOfBirth);
        customer.setStatus(true); // Mặc định kích hoạt tài khoản
        customer.setEmailVerified(email != null && !email.trim().isEmpty()); // Chỉ verified nếu có email

        // Thêm khách hàng vào cơ sở dữ liệu
        boolean isRegistered = customerDAO.insertCustomer(customer);

        if (isRegistered) {
            // Đăng ký thành công, chuyển hướng đến trang đăng nhập
            request.setAttribute("successMessage", "Registration successful! Please log in.");
            request.getRequestDispatcher("/views/customer/auth/login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
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
            // Đăng nhập thành công
            if (customer.getStatus()) {
                // Lưu thông tin người dùng vào session
                HttpSession session = request.getSession();
                session.setAttribute("customer", customer);
                
                // Chuyển hướng về trang chủ hoặc trang trước đó
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

    private String validateRegistrationData(String username, String fullName, String email, 
            String password, String confirmPassword, String dateOfBirthStr) {
        
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        
        if (username.length() < 3) {
            return "Username must be at least 3 characters long.";
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required.";
        }
        
        if (fullName.length() < 2) {
            return "Full name must be at least 2 characters long.";
        }
        
        // Email không bắt buộc - có thể để trống
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            return "Please enter a valid email address.";
        }
        
        // Bỏ kiểm tra isValidEmail khi email là null hoặc trống
        
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        
        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
            return "Date of birth is required.";
        }
        
        try {
            LocalDate dob = LocalDate.parse(dateOfBirthStr);
            LocalDate now = LocalDate.now();
            if (dob.isAfter(now)) {
                return "Date of birth cannot be in the future.";
            }
        } catch (Exception e) {
            return "Please enter a valid date of birth.";
        }
        
        return null;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
//hello test 2
    private boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}