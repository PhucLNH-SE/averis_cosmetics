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
import java.io.BufferedReader;
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
        
        // For GET requests (user redirect from MoMo), check resultCode first
        String resultCode = request.getParameter("resultCode");
        
        // If resultCode exists in query params, this is a redirect
        if (resultCode != null) {
            LOGGER.info("MoMo redirect GET - resultCode: " + resultCode);
            
            String momoOrderId = request.getParameter("orderId");
            
            if ("0".equals(resultCode) && momoOrderId != null) {
                // Payment successful - process the order
                
                // Extract internal order ID from MoMo order ID (format: ORDER_40_123456)
                int orderId = extractOrderId(momoOrderId);
                LOGGER.info("Processing successful payment for order: " + orderId);
                
                // Update payment status in database
                orderDAO.updatePaymentSuccess(orderId);
                LOGGER.info("Payment status updated to SUCCESS for order: " + orderId);
                
                // Clear cart from session
                HttpSession session = request.getSession();
                session.removeAttribute("cart");
                LOGGER.info("Cart removed from session");
                
                // Also clear from database if logged in
                Customer customer = (Customer) session.getAttribute("customer");
                if (customer != null) {
                    cartDetailDAO.deleteAll(customer.getCustomerId());
                    LOGGER.info("Cart deleted from database for customer: " + customer.getCustomerId());
                }
                
                // Redirect to success page
                response.sendRedirect(request.getContextPath() 
                        + "/checkout?success=true&orderId=" + orderId);
                return;
                
            } else {
                // Payment failed
                response.sendRedirect(request.getContextPath() 
                        + "/checkout?error=Thanh+toan+that+bai");
                return;
            }
        }
        
        // If no resultCode, treat as verification needed (IPN callback)
        processCallback(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("MoMo IPN POST callback received");
        
        // MoMo IPN callback - process it
        processCallback(request, response);
    }
    
    private void processCallback(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get all parameters from request (works for both GET query string and POST body)
        Properties params = new Properties();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.setProperty(paramName, request.getParameter(paramName));
        }
        
        // Also try to read from POST body if empty
        if (params.isEmpty()) {
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String postData = sb.toString();
                LOGGER.info("POST body data: " + postData);
                
                // Parse POST body parameters
                if (postData != null && !postData.isEmpty()) {
                    String[] pairs = postData.split("&");
                    for (String pair : pairs) {
                        String[] kv = pair.split("=");
                        if (kv.length == 2) {
                            params.setProperty(kv[0], java.net.URLDecoder.decode(kv[1], "UTF-8"));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error reading POST body", e);
            }
        }

        // Validate MoMo callback signature
        if (!momoService.verifyCallback(params)) {
            LOGGER.warning("Invalid MoMo callback signature from IPN");
            response.sendRedirect(request.getContextPath() + "/checkout?error=Xac thuc that bai");
            return;
        }
        
        LOGGER.info("MoMo callback signature verified successfully");

        String momoOrderId = params.getProperty("orderId");
        String resultCode = params.getProperty("resultCode");

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

            // Clear cart - always remove from session
            session.removeAttribute("cart");
            LOGGER.info("Cart removed from session for order " + orderId);
            
            // Also delete from database if customer is logged in
            if (customer != null) {
                cartDetailDAO.deleteAll(customer.getCustomerId());
                LOGGER.info("Cart deleted from database for customer " + customer.getCustomerId());
            }

            response.sendRedirect(request.getContextPath()
                    + "/checkout?success=true&orderId=" + orderId);

        } else {

            orderDAO.updatePaymentFailed(orderId);

            response.sendRedirect(request.getContextPath()
                    + "/checkout?error=Thanh toan that bai");

        }
    }
    
    /**
     * Extract order ID from MoMo order ID format: ORDER_40_123456
     */
    private int extractOrderId(String momoOrderId) {
        try {
            String[] parts = momoOrderId.split("_");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }
}
