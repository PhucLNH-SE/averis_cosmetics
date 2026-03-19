package Controllers.admin;

import DALs.OrderDAO;
import DALs.ManagerDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.Orders;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
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
            case "detail":
                viewOrderDetail(request, response);
                break;
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
        String staffIdParam = request.getParameter("staffId");
        Integer staffId = null;
        if (staffIdParam != null && !staffIdParam.trim().isEmpty()) {
            try {
                staffId = Integer.parseInt(staffIdParam);
            } catch (NumberFormatException ignored) {
                staffId = null;
            }
        }

        List<Orders> orderList = staffId == null
                ? dao.getAllOrders()
                : dao.getOrdersByHandledBy(staffId);

        ManagerDAO managerDAO = new ManagerDAO();
        List<Manager> allManagers = managerDAO.getAllManagers();
        List<Manager> staffList = new ArrayList<>();
        for (Manager m : allManagers) {
            if ("STAFF".equalsIgnoreCase(m.getManagerRole())) {
                staffList.add(m);
            }
        }

        request.setAttribute("orderList", orderList);
        request.setAttribute("staffList", staffList);
        request.setAttribute("selectedStaffId", staffId);
        request.setAttribute("currentView", "orders");

        request.setAttribute("contentPage",
                "/views/admin/partials/manage-orders-content.jsp");

        request.getRequestDispatcher("/views/admin/admin-panel.jsp")
                .forward(request, response);
    }

    private void viewOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));

        OrderDAO dao = new OrderDAO();
        Orders order = dao.getOrderById(orderId);
        List<OrderDetail> details = dao.getOrderDetailsByOrderId(orderId);

        request.setAttribute("order", order);
        request.setAttribute("details", details);

        if (order != null && order.getHandledBy() != null) {
            ManagerDAO managerDAO = new ManagerDAO();
            Manager handledStaff = managerDAO.getById(order.getHandledBy());
            request.setAttribute("handledStaff", handledStaff);
        }

        request.setAttribute("currentView", "orders");
        request.setAttribute("contentPage", "/views/admin/partials/order-detail-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }
}
