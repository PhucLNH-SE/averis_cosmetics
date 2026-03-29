package Controllers.customer;

import DALs.OrderDAO;
import DALs.CartDetailDAO;
import Model.Customer;
import Model.Orders;
import Services.MomoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

public class MomoReturnController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MomoReturnController.class.getName());

    private OrderDAO orderDAO;
    private CartDetailDAO cartDetailDAO;
    private MomoService momoService;

    @Override
    public void init() {
        orderDAO = new OrderDAO();
        cartDetailDAO = new CartDetailDAO();
        momoService = new MomoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String resultCode = request.getParameter("resultCode");
        String momoOrderId = request.getParameter("orderId");

        if (resultCode != null && momoOrderId != null) {

            int orderId = extractOrderId(momoOrderId);
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
                    System.out.println("Update result = " + success);

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
                    }
                }

                response.sendRedirect(request.getContextPath()
                        + "/order-success?orderId=" + orderId);
                return;

            } else {
                if (!"FAILED".equalsIgnoreCase(order.getPaymentStatus())) {
                    orderDAO.updatePaymentFailed(orderId);
                }
                response.sendRedirect(request.getContextPath()
                        + "/checkout?error=Payment failed&paymentMethod=MOMO");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Properties params = new Properties();
        Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.setProperty(name, request.getParameter(name));
        }

        if (!momoService.verifyCallback(params)) {
            LOGGER.warning("Invalid MoMo signature");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String momoOrderId = params.getProperty("orderId");
        String resultCode = params.getProperty("resultCode");

        if (momoOrderId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int orderId = extractOrderId(momoOrderId);
        Orders order = orderDAO.getOrderById(orderId);

        if (order == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if ("0".equals(resultCode)) {
            String targetStatus = resolvePaidOrderStatus(order);
            orderDAO.markPaymentSuccess(orderId, targetStatus);
            LOGGER.info("Payment SUCCESS (IPN) for order " + orderId);
        } else {
            orderDAO.updatePaymentFailed(orderId);
            LOGGER.warning("Payment FAILED for order " + orderId);
        }

        response.setStatus(HttpServletResponse.SC_OK);
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
