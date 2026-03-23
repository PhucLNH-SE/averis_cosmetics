package Controllers.staff;

import DALs.ManagerDAO;
import DALs.OrderDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.Orders;
import Utils.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
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
        if (status != null) {
            status = status.trim();
            if (status.isEmpty()) {
                status = null;
            }
        }
        List<Orders> orderList;

        if (status != null) {
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
        request.setAttribute("contentPage", "/views/staff/partials/order-detail-content.jsp");

        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (!"update".equals(action)) {
            doGet(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        if (manager == null || !"STAFF".equalsIgnoreCase(manager.getManagerRole())) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        String[] orderIds = request.getParameterValues("orderId");
        String[] paymentStatuses = request.getParameterValues("paymentStatus");
        String[] orderStatuses = request.getParameterValues("orderStatus");
        if (orderIds == null || paymentStatuses == null || orderStatuses == null) {
            response.sendRedirect(request.getContextPath() + "/staff/manage-orders?error=updateFailed");
            return;
        }

        OrderDAO dao = new OrderDAO();
        Integer handledBy = manager.getManagerId();
        boolean hadForbidden = false;
        boolean hadUpdateFailed = false;
        String validationErrorMessage = null;

        for (int i = 0; i < orderIds.length; i++) {
            int orderId = Integer.parseInt(orderIds[i]);
            Orders existingOrder = dao.getOrderUpdateInfo(orderId);

            if (existingOrder == null) {
                hadUpdateFailed = true;
                continue;
            }

            boolean isChanged = !equalsIgnoreCase(existingOrder.getPaymentStatus(), paymentStatuses[i])
                    || !equalsIgnoreCase(existingOrder.getOrderStatus(), orderStatuses[i]);
            if (!isChanged) {
                continue;
            }

            Integer existingHandledBy = existingOrder.getHandledBy();
            boolean canUpdate = existingHandledBy == null || existingHandledBy.equals(handledBy);
            if (!canUpdate) {
                hadForbidden = true;
                continue;
            }

            try {
                ValidationUtil.validateOrderStatusTransition(existingOrder.getOrderStatus(), orderStatuses[i]);
                if (equalsIgnoreCase(existingOrder.getPaymentMethod(), "COD")) {
                    ValidationUtil.validateCodStatus(existingOrder.getPaymentMethod(),
                            paymentStatuses[i], orderStatuses[i]);
                }
            } catch (IllegalArgumentException ex) {
                if (validationErrorMessage == null) {
                    validationErrorMessage = ex.getMessage();
                }
                continue;
            }

            if (!dao.updateOrder(orderId, paymentStatuses[i], orderStatuses[i], handledBy)) {
                hadUpdateFailed = true;
            }
        }

        String redirectUrl = request.getContextPath() + "/staff/manage-orders";
        if (validationErrorMessage != null) {
            response.sendRedirect(redirectUrl + "?error=validationError&message="
                    + URLEncoder.encode(validationErrorMessage, "UTF-8"));
        } else if (hadUpdateFailed) {
            response.sendRedirect(redirectUrl + "?error=updateFailed");
        } else if (hadForbidden) {
            response.sendRedirect(redirectUrl + "?error=notAllowed");
        } else {
            response.sendRedirect(redirectUrl + "?success=update");
        }
    }

    private boolean equalsIgnoreCase(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equalsIgnoreCase(right);
    }
}
