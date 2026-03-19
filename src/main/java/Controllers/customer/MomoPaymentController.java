package Controllers.customer;

import DALs.OrderDAO;
import Model.Orders;
import Services.MomoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.logging.Logger;

public class MomoPaymentController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MomoPaymentController.class.getName());

    private OrderDAO orderDAO;
    private MomoService momoService;

    @Override
    public void init() {
        orderDAO = new OrderDAO();
        momoService = new MomoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Model.Customer customer = (Model.Customer) session.getAttribute("customer");

        // Check login
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
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

        // Security: check ownership
        if (order.getCustomerId() != customer.getCustomerId()) {
            LOGGER.warning("Unauthorized payment attempt: order " + orderId);
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Only allow PENDING
        if (!"PENDING".equalsIgnoreCase(order.getPaymentStatus())) {
            response.sendRedirect(request.getContextPath() + "/cart");
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
