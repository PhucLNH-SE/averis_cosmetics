package Controllers.customer;

import DALs.CartDetailDAO;
import DALs.CustomerDAO;
import DALs.ProductDAO;
import Model.CartDetail;
import Model.CartItem;
import Model.Customer;
import Model.ProductVariant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyEmailServlet extends HttpServlet {

    private CustomerDAO customerDAO = new CustomerDAO();

    private static final String TYPE_EMAIL_VERIFY = "EMAIL_VERIFY";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("message", "Link không hợp lệ.");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }
        Customer customer = customerDAO.getCustomerByAuthToken(token.trim(), TYPE_EMAIL_VERIFY);
        if (customer == null) {
            request.setAttribute("message", "Link không hợp lệ hoặc đã được sử dụng.");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }
        if (Boolean.TRUE.equals(customer.getAuthTokenUsed())) {
            request.setAttribute("message", "Link đã được sử dụng. Email của bạn đã được xác thực trước đó.");
            request.setAttribute("success", true);
            request.getRequestDispatcher("/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }
        LocalDateTime expiredAt = customer.getAuthTokenExpiredAt();
        if (expiredAt == null || LocalDateTime.now().isAfter(expiredAt)) {
            request.setAttribute("message", "Link đã hết hạn. Vui lòng yêu cầu gửi lại email xác thực.");
            request.setAttribute("success", false);
            request.getRequestDispatcher("/views/customer/auth/verify-result.jsp").forward(request, response);
            return;
        }
        customerDAO.setEmailVerified(customer.getCustomerId());
        Customer freshCustomer = customerDAO.getCustomerById(customer.getCustomerId());
        if (freshCustomer != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("customer", freshCustomer);
            CartDetailDAO cartDetailDAO = new CartDetailDAO();
            ProductDAO productDAO = new ProductDAO();
            Map<Integer, CartItem> cart = new HashMap<>();
            List<CartDetail> details = cartDetailDAO.getByCustomerId(freshCustomer.getCustomerId());
            for (CartDetail d : details) {
                ProductVariant v = productDAO.getVariantById(d.getVariantId());
                if (v != null) {
                    cart.put(d.getVariantId(), new CartItem(v, d.getQuantity()));
                }
            }
            session.setAttribute("cart", cart);
            session.setAttribute("profileMessage", "Email của bạn đã được xác thực thành công.");
        }
        response.sendRedirect(request.getContextPath() + "/CustomerController");
    }
}
