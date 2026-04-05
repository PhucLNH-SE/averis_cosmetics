package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import Utils.MailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

public class SendVerifyEmailController extends HttpServlet {

    private static final int VERIFY_EMAIL_COOLDOWN_SECONDS = 60;
    private static final String COOLDOWN_KEY = "verifyEmailCooldownUntil";

    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long cooldownUntil = (Long) session.getAttribute(COOLDOWN_KEY);
        long now = System.currentTimeMillis();
        if (cooldownUntil != null && now < cooldownUntil) {
            long remainingSeconds = Math.max(1, (cooldownUntil - now + 999) / 1000);
            session.setAttribute("profileMessage",
                    "Please wait " + remainingSeconds + " seconds before requesting another verification email.");
            session.setAttribute("profileMessageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=profile");
            return;
        }

        Customer customer = (Customer) session.getAttribute("customer");
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            session.setAttribute("profileMessage", "Email is missing. Please update your email in your profile.");
            session.setAttribute("profileMessageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=profile");
            return;
        }
        if (Boolean.TRUE.equals(customer.getEmailVerified())) {
            session.setAttribute("profileMessage", "Your email has already been verified.");
            session.setAttribute("profileMessageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=profile");
            return;
        }

        String token = MailUtil.generateToken();
        LocalDateTime expiredAt = LocalDateTime.now().plusHours(1);
        customerDAO.updateAuthTokenForVerification(customer.getCustomerId(), token, expiredAt);
        String verifyLink = buildVerifyLink(request, token);
        boolean emailSent = MailUtil.sendVerificationEmail(customer.getEmail(), verifyLink);
        if (!emailSent) {
            session.setAttribute("profileMessage", "Unable to send verification email. Please try again later.");
            session.setAttribute("profileMessageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=profile");
            return;
        }

        session.setAttribute("profileMessage", "Verification email sent. Please check your inbox (and spam folder).");
        session.setAttribute("profileMessageType", "success");
        session.setAttribute(COOLDOWN_KEY, now + VERIFY_EMAIL_COOLDOWN_SECONDS * 1000L);
        response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=profile");
    }

    private String buildVerifyLink(HttpServletRequest request, String token) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();
        String portPart = (port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme)) ? "" : ":" + port;
        return scheme + "://" + serverName + portPart + contextPath + "/verify-email?token=" + token;
    }
}
