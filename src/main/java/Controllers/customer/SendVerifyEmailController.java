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

    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            session.setAttribute("profileMessage", "Bạn chưa có email. Vui lòng cập nhật email trong profile.");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=profile");
            return;
        }
        if (Boolean.TRUE.equals(customer.getEmailVerified())) {
            session.setAttribute("profileMessage", "Email của bạn đã được xác thực.");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=profile");
            return;
        }
        String token = MailUtil.generateToken();
        LocalDateTime expiredAt = LocalDateTime.now().plusHours(24);
        customerDAO.updateAuthTokenForVerification(customer.getCustomerId(), token, expiredAt);
        String verifyLink = buildVerifyLink(request, token);
        MailUtil.sendVerificationEmail(customer.getEmail(), verifyLink);
        session.setAttribute("profileMessage", "Đã gửi email xác thực. Vui lòng kiểm tra hộp thư (và thư mục spam).");
        response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=profile");
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
