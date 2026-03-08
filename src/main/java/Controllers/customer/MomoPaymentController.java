package Controllers.customer;

import DALs.OrderDAO;
import Model.Orders;
import Services.MomoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

public class MomoPaymentController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MomoPaymentController.class.getName());
    
    private OrderDAO orderDAO;
    private MomoService momoService;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        momoService = new MomoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Model.Customer customer = (Model.Customer) session.getAttribute("customer");
        
        // Check if user is logged in
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdStr = request.getParameter("orderId");

        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int orderId;

        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Orders order = orderDAO.getOrderById(orderId);

        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Security: Verify order belongs to current customer
        if (order.getCustomerId() != customer.getCustomerId()) {
            LOGGER.warning("User " + customer.getCustomerId() + " attempted to pay for order " + orderId + " belonging to customer " + order.getCustomerId());
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Verify order is in PENDING status
        if (!"PENDING".equals(order.getPaymentStatus())) {
            LOGGER.warning("Order " + orderId + " is not in PENDING status: " + order.getPaymentStatus());
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        long amount = order.getTotalAmount().longValue();

        String payUrl = momoService.createPayment(orderId, amount);

        if (payUrl != null && !payUrl.isEmpty()) {

            response.sendRedirect(payUrl);

        } else {

            response.sendRedirect(request.getContextPath()
                    + "/checkout?error=Khong the tao thanh toan MoMo");

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // MoMo IPN callback - verify signature
        Properties params = new Properties();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.setProperty(paramName, request.getParameter(paramName));
        }
        
        // Verify callback signature
        if (!momoService.verifyCallback(params)) {
            LOGGER.warning("Invalid MoMo callback signature");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Process callback in doGet logic
        doGet(request, response);
    }
}
