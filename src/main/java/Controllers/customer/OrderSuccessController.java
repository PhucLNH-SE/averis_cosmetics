package Controllers.customer;

import DALs.AddressDAO;
import DALs.OrderDAO;
import Model.Address;
import Model.Orders;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class OrderSuccessController extends HttpServlet {

    private OrderDAO orderDAO;
    private AddressDAO addressDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        addressDAO = new AddressDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        
        // Kiá»ƒm tra Ä‘Äƒng nháº­p
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String orderIdStr = request.getParameter("orderId");

        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            Orders order = orderDAO.getOrderById(orderId);

            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            
            // Báº¢O Máº¬T: Kiá»ƒm tra order thuá»™c vá» customer hiá»‡n táº¡i
            if (order.getCustomerId() != customer.getCustomerId()) {
                System.out.println("SECURITY WARNING: Customer " + customer.getCustomerId() 
                    + " attempted to access order " + orderId + " belonging to customer " + order.getCustomerId());
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            // Láº¥y thÃ´ng tin Ä‘á»‹a chá»‰
            Address address = null;
            if (order.getAddressId() > 0) {
                address = addressDAO.getAddressById(order.getAddressId());
            }

            request.setAttribute("order", order);
            request.setAttribute("address", address);

            request.getRequestDispatcher("/WEB-INF/views/customer/order-success.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}

