package Controllers.staff;

import DALs.OrderDAO;

import Model.Orders;
import Model.OrderDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ManageOrderController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {

            case "list":
                listOrders(request, response);
                break;

            case "detail":
                showOrderDetail(request, response);
                break;

            default:
                listOrders(request, response);
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        OrderDAO dao = new OrderDAO();
        List<Orders> orderList = dao.getAllOrders();

        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("views/staff/manage-orders.jsp").forward(request, response);
    }

    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));

        OrderDAO dao = new OrderDAO();
        List<OrderDetail> orderDetails = dao.getOrderDetailsByOrderId(orderId);

        request.setAttribute("orderDetails", orderDetails);
        request.setAttribute("orderId", orderId);

        request.getRequestDispatcher("views/staff/order-detail.jsp").forward(request, response);
    }

 @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String action = request.getParameter("action");

    if ("update".equals(action)) {

        String[] orderIds = request.getParameterValues("orderId");
        String[] paymentStatus = request.getParameterValues("paymentStatus");
        String[] orderStatus = request.getParameterValues("orderStatus");

        OrderDAO dao = new OrderDAO();

        for (int i = 0; i < orderIds.length; i++) {

            int orderId = Integer.parseInt(orderIds[i]);

            dao.updateOrder(orderId, paymentStatus[i], orderStatus[i]);
        }

        response.sendRedirect(request.getContextPath() + "/ManageOrderController?action=list");

    } else {
        doGet(request, response);
    }
}
}