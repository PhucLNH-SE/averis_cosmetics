package Controllers.staff;

import DALs.OrderDAO;
import DALs.ManagerDAO;
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

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        if (manager != null) {
            request.setAttribute("currentManagerId", manager.getManagerId());
        }

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
    if (order != null && order.getHandledBy() != null) {
        ManagerDAO managerDAO = new ManagerDAO();
        Manager handledStaff = managerDAO.getById(order.getHandledBy());
        request.setAttribute("handledStaff", handledStaff);
    }

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
            if (manager == null || !"STAFF".equalsIgnoreCase(manager.getManagerRole())) {
                response.sendRedirect(request.getContextPath() + "/manager-auth");
                return;
            }
            Integer handledBy = manager.getManagerId();

            String[] orderIds = request.getParameterValues("orderId");
            String[] paymentStatuses = request.getParameterValues("paymentStatus");
            String[] orderStatuses = request.getParameterValues("orderStatus");

            OrderDAO dao = new OrderDAO();
            boolean hadForbidden = false;

            for (int i = 0; i < orderIds.length; i++) {

                int orderId = Integer.parseInt(orderIds[i]);
                Integer existingHandledBy = dao.getHandledBy(orderId);
                boolean canUpdate = existingHandledBy == null || existingHandledBy.equals(handledBy);
                if (!canUpdate) {
                    hadForbidden = true;
                    continue;
                }

                dao.updateOrder(orderId, paymentStatuses[i], orderStatuses[i], handledBy);
            }

            if (hadForbidden) {
                response.sendRedirect(request.getContextPath() + "/staff/manage-orders?error=notAllowed");
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/manage-orders?success=update");
            }
            return;
        }

        doGet(request, response);
    }
}
