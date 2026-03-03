package Controllers.customer;

import DALs.AddressDAO;
import DALs.OrderDAO;
import Model.Address;
import Model.Orders;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "OrderSuccessController", urlPatterns = {"/order-success"})
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

            // Lấy thông tin địa chỉ
            Address address = addressDAO.getAddressById(order.getAddressId());

            request.setAttribute("order", order);
            request.setAttribute("address", address);

            request.getRequestDispatcher("/views/customer/order-success.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
