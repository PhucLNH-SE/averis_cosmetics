package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

public class VerifyEmailController extends HttpServlet {

    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("message", "Invalid verification link.");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }

        Customer customer = customerDAO.getCustomerByAuthToken(token.trim(), CustomerDAO.TYPE_EMAIL_VERIFY);
        if (customer == null) {
            request.setAttribute("message", "Invalid or expired verification link.");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }

        if (Boolean.TRUE.equals(customer.getAuthTokenUsed())) {
            request.setAttribute("message", "This link has already been used. Your email was verified earlier.");
            request.setAttribute("success", true);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }

        LocalDateTime expiredAt = customer.getAuthTokenExpiredAt();
        if (expiredAt == null || LocalDateTime.now().isAfter(expiredAt)) {
            request.setAttribute("message", "Verification link has expired. Please request a new one.");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/WEB-INF/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }

        customerDAO.setEmailVerified(customer.getCustomerId());
        Customer freshCustomer = customerDAO.getCustomerById(customer.getCustomerId());
        if (freshCustomer != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("customer", freshCustomer);
            session.setAttribute("profileMessage", "Your email has been verified successfully.");
            session.setAttribute("profileMessageType", "success");
        }
        response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=profile");
    }
}

