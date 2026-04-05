package Controllers.customer;

import DALs.OrderDAO;
import DALs.CartDetailDAO;
import Model.Customer;
import Model.Orders;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

public class MomoReturnController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MomoReturnController.class.getName());

    private OrderDAO orderDAO = new OrderDAO();
    private CartDetailDAO cartDetailDAO = new CartDetailDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String resultCode = request.getParameter("resultCode");
        String momoOrderId = request.getParameter("orderId");

        if (resultCode != null && momoOrderId != null) {

            int orderId = extractOrderId(momoOrderId);
            if (orderId <= 0) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            LOGGER.info("MoMo RETURN - raw: " + momoOrderId + " | parsed: " + orderId);

            Orders order = orderDAO.getOrderById(orderId);
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            if ("0".equals(resultCode)) {

                if (!"SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {

                    String targetStatus = resolvePaidOrderStatus(order);
                    boolean success = orderDAO.markPaymentSuccess(orderId, targetStatus);

                    if (success) {
                        HttpSession session = request.getSession(false);
                        if (session != null) {
                            session.removeAttribute("cart");

                            Customer customer = (Customer) session.getAttribute("customer");
                            if (customer != null) {
                                cartDetailDAO.deleteAll(customer.getCustomerId());
                            }
                        }
                        LOGGER.info("Payment SUCCESS (GET) for order " + orderId);
                    } else {
                        LOGGER.warning("Payment update FAILED for order " + orderId);
                        response.sendRedirect(request.getContextPath()
                                + "/checkout?error=Cannot update payment result&paymentMethod=MOMO");
                        return;
                    }
                }

                response.sendRedirect(request.getContextPath()
                        + "/order-success?orderId=" + orderId);
                return;

            } else {
                if (!"FAILED".equalsIgnoreCase(order.getPaymentStatus())) {
                    boolean updated = orderDAO.updatePaymentFailed(orderId);
                    if (!updated) {
                        LOGGER.warning("Payment failed update FAILED for order " + orderId);
                        response.sendRedirect(request.getContextPath()
                                + "/checkout?error=Cannot update payment result&paymentMethod=MOMO");
                        return;
                    }
                }
                response.sendRedirect(request.getContextPath()
                        + "/checkout?error=Payment failed&paymentMethod=MOMO");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private int extractOrderId(String momoOrderId) {
        try {
            String[] parts = momoOrderId.split("_");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            LOGGER.warning("Cannot parse orderId from: " + momoOrderId);
            return 0;
        }
    }

    private String resolvePaidOrderStatus(Orders order) {
        if (order == null || order.getOrderStatus() == null) {
            return "PROCESSING";
        }
        if ("CREATED".equalsIgnoreCase(order.getOrderStatus())) {
            return "PROCESSING";
        }
        return order.getOrderStatus();
    }
}
