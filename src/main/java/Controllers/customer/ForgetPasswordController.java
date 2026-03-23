package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import Utils.MailUtil;
import Utils.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class ForgetPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "forget";
        }

        switch (action) {
            case "reset":
                showResetPasswordPage(request, response);
                break;
            case "forget":
            default:
                showForgetPasswordPage(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "forget";
        }

        switch (action) {
            case "reset":
                submitResetPassword(request, response);
                break;
            case "forget":
            default:
                submitForgetPassword(request, response);
                break;
        }
    }

    private void showForgetPasswordPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                .forward(request, response);
    }

    private void submitForgetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        email = email == null ? "" : email.trim();

        if (email.isEmpty()) {
            request.setAttribute("error", "Please enter your email address.");
            request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                    .forward(request, response);
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.findByEmailAndVerified(email);

        if (customer == null) {
            request.setAttribute("error",
                    "The email address does not exist or has not been verified.");
            request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                    .forward(request, response);
            return;
        }

        String token = MailUtil.generateToken();
        Timestamp expiredAt = Timestamp.valueOf(LocalDateTime.now().plusMinutes(15));

        boolean saved = customerDAO.saveResetPasswordToken(email, token, expiredAt);
        if (!saved) {
            request.setAttribute("error",
                    "Unable to create a reset link. Please try again.");
            request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                    .forward(request, response);
            return;
        }

        String resetLink = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort()
                + request.getContextPath()
                + "/ForgetPasswordController?action=reset&token=" + token;
        MailUtil.sendResetPasswordEmail(email, resetLink);

        request.setAttribute("msg",
                "Please check your email to reset your password.");
        request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                .forward(request, response);
    }

    private void showResetPasswordPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        token = token == null ? "" : token.trim();

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.findByResetToken(token);

        if (token.isEmpty() || customer == null) {
            request.getRequestDispatcher("/views/customer/invalidtoken.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("token", token);
        request.getRequestDispatcher("/views/customer/resetpassword.jsp")
                .forward(request, response);
    }

    private void submitResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        token = token == null ? "" : token.trim();

        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        Map<String, String> errors = ValidationUtil.validateResetPassword(
                newPassword, confirmPassword
        );

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/customer/resetpassword.jsp")
                    .forward(request, response);
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        boolean updated = customerDAO.updatePasswordByToken(token, newPassword);

        if (!updated) {
            request.setAttribute("error", "The token is invalid or has expired.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/customer/resetpassword.jsp")
                    .forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/auth?action=login");
    }
}
