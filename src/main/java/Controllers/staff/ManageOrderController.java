package Controllers.staff;

import DALs.OrderDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.Orders;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class ManageOrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {

            case "detail":
                viewOrderDetail(request, response);
                break;

            case "list":
            default:
                listOrders(request, response);
                break;
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        OrderDAO dao = new OrderDAO();

        String status = request.getParameter("status");
        List<Orders> orderList;

        if (status != null && !status.isEmpty()) {
            orderList = dao.getOrdersByStatus(status);
        } else {
            orderList = dao.getAllOrders();
        }

        request.setAttribute("orderList", orderList);
        request.setAttribute("selectedStatus", status);

        request.setAttribute("currentView", "orders");
        request.setAttribute("contentPage", "/views/staff/partials/manage-orders-content.jsp");

        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }

    // ================= VIEW ORDER DETAIL =================
  private void viewOrderDetail(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    int orderId = Integer.parseInt(request.getParameter("orderId"));

    OrderDAO dao = new OrderDAO();

    // lấy thông tin order
    Orders order = dao.getOrderById(orderId);

    // lấy danh sách sản phẩm
    List<OrderDetail> details = dao.getOrderDetailsByOrderId(orderId);

    request.setAttribute("order", order);
    request.setAttribute("details", details);

    request.setAttribute("currentView", "orders");
    request.setAttribute("contentPage", "/views/staff/partials/order-detail-content.jsp");

    request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("update".equals(action)) {

            HttpSession session = request.getSession(false);
            Manager manager = session == null ? null : (Manager) session.getAttribute("manager");

            Integer handledBy = manager == null ? null : manager.getManagerId();

            String[] orderIds = request.getParameterValues("orderId");
            String[] paymentStatuses = request.getParameterValues("paymentStatus");
            String[] orderStatuses = request.getParameterValues("orderStatus");

            OrderDAO dao = new OrderDAO();

            for (int i = 0; i < orderIds.length; i++) {

                int orderId = Integer.parseInt(orderIds[i]);

                dao.updateOrder(orderId, paymentStatuses[i], orderStatuses[i], handledBy);
            }

            response.sendRedirect(request.getContextPath() + "/staff/manage-orders?success=update");
            return;
        }

        doGet(request, response);
    }
}
