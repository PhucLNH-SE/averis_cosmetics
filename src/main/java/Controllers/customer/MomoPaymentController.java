package Controllers.customer;

import DALs.OrderDAO;
import Model.Customer;
import Model.Orders;
import Services.MomoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.logging.Logger;

public class MomoPaymentController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MomoPaymentController.class.getName());

    private OrderDAO orderDAO = new OrderDAO();
    private MomoService momoService = new MomoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Orders order = orderDAO.getOrderById(orderId);
        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (order.getCustomerId() != customer.getCustomerId()) {
            LOGGER.warning("Unauthorized payment attempt: order " + orderId);
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (!"MOMO".equalsIgnoreCase(order.getPaymentMethod())) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (!"PENDING".equalsIgnoreCase(order.getPaymentStatus())) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            response.sendRedirect(request.getContextPath()
                    + "/checkout?error=Invalid payment amount&paymentMethod=MOMO");
            return;
        }

        long amount = order.getTotalAmount().longValue();
        String payUrl = momoService.createPayment(orderId, amount);

        if (payUrl != null && !payUrl.isEmpty()) {
            response.sendRedirect(payUrl);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/checkout?error=Cannot create MoMo payment");
        }
    }
}
