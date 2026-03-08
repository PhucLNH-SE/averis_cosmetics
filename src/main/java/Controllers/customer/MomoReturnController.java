package Controllers.customer;

import DALs.OrderDAO;
import DALs.CartDetailDAO;
import Model.Customer;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class MomoReturnController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MomoReturnController.class.getName());
    
    private OrderDAO orderDAO;
    private CartDetailDAO cartDetailDAO;
    private MomoService momoService;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        cartDetailDAO = new CartDetailDAO();
        momoService = new MomoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verify callback signature for security
        Properties params = new Properties();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.setProperty(paramName, request.getParameter(paramName));
        }
        
        // Validate MoMo callback signature
        if (!momoService.verifyCallback(params)) {
            LOGGER.warning("Invalid MoMo callback signature from IPN");
            response.sendRedirect(request.getContextPath() + "/checkout?error=Xac thuc that bai");
            return;
        }
        
        LOGGER.info("MoMo callback signature verified successfully");

        String momoOrderId = request.getParameter("orderId");
        String resultCode = request.getParameter("resultCode");

        if (momoOrderId == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // orderId MoMo dạng ORDER_40_123456
        int orderId = 0;

        try {

            String[] parts = momoOrderId.split("_");

            orderId = Integer.parseInt(parts[1]);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing orderId from MoMo", e);
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Get customer from session to verify ownership
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        
        // Verify order exists and belongs to customer if logged in
        Orders order = orderDAO.getOrderById(orderId);
        if (order == null) {
            LOGGER.warning("Order not found: " + orderId);
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        if (customer != null && order.getCustomerId() != customer.getCustomerId()) {
            LOGGER.warning("Order " + orderId + " does not belong to customer " + customer.getCustomerId());
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if ("0".equals(resultCode)) {

            orderDAO.updatePaymentSuccess(orderId);

            if (customer != null) {
                session.removeAttribute("cart");
                cartDetailDAO.deleteAll(customer.getCustomerId());
            }

            response.sendRedirect(request.getContextPath()
                    + "/checkout?success=true&orderId=" + orderId);

        } else {

            orderDAO.updatePaymentFailed(orderId);

            response.sendRedirect(request.getContextPath()
                    + "/checkout?error=Thanh toan that bai");

        }
    }
}
