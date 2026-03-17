package Controllers.admin;

import DALs.OrderDAO;
import Model.Orders;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class AdminManageOrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
            default:
                listOrders(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("update".equals(action)) {

            String[] orderIds = request.getParameterValues("orderId");
            String[] paymentStatuses = request.getParameterValues("paymentStatus");
            String[] orderStatuses = request.getParameterValues("orderStatus");

            OrderDAO dao = new OrderDAO();

            try {

                for (int i = 0; i < orderIds.length; i++) {

                    int orderId = Integer.parseInt(orderIds[i]);
                    String paymentStatus = paymentStatuses[i];
                    String orderStatus = orderStatuses[i];

                    dao.updateOrder(orderId, paymentStatus, orderStatus);
                }

                response.sendRedirect(request.getContextPath()
                        + "/admin/manage-orders?success=update");

            } catch (Exception e) {

                e.printStackTrace();

                response.sendRedirect(request.getContextPath()
                        + "/admin/manage-orders?error=updateFailed");
            }
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        OrderDAO dao = new OrderDAO();
        List<Orders> orderList = dao.getAllOrders();

        request.setAttribute("orderList", orderList);
        request.setAttribute("currentView", "orders");

        request.setAttribute("contentPage",
                "/views/admin/partials/manage-orders-content.jsp");

        request.getRequestDispatcher("/views/admin/admin-panel.jsp")
                .forward(request, response);
    }
}
