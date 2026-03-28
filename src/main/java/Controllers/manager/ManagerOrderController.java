package Controllers.manager;

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

public class ManagerOrderController extends HttpServlet {

    private static final String ADMIN_URL = "/admin/manage-orders";
    private static final String STAFF_URL = "/staff/manage-orders";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-orders-content.jsp";
    private static final String STAFF_CONTENT = "/WEB-INF/views/staff/partials/manage-orders-content.jsp";
    private static final String ADMIN_DETAIL = "/WEB-INF/views/admin/partials/order-detail-content.jsp";
    private static final String STAFF_DETAIL = "/WEB-INF/views/staff/partials/order-detail-content.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
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
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "update":
                updateOrderStatus(request, response);
                break;
            default:
                doGet(request, response);
                break;
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        OrderDAO dao = new OrderDAO();
        String keyword = trimToNull(request.getParameter("keyword"));
        List<Orders> orderList = dao.searchOrders(keyword);

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        if (manager != null) {
            request.setAttribute("currentManagerId", manager.getManagerId());
        }

        request.setAttribute("orderList", orderList);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("currentView", "orders");

        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("contentPage", staffRoute ? STAFF_CONTENT : ADMIN_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL)
                .forward(request, response);
    }

    private void viewOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer orderId = parseInteger(request.getParameter("orderId"));
        if (orderId == null) {
            response.sendRedirect(buildManageOrdersRedirect(request, "updateFailed", null, null));
            return;
        }

        OrderDAO dao = new OrderDAO();
        Orders order = dao.getOrderById(orderId);
        if (order == null) {
            response.sendRedirect(buildManageOrdersRedirect(request, "updateFailed", null, null));
            return;
        }
        List<OrderDetail> details = dao.getOrderDetailsByOrderId(orderId);

        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        Integer currentManagerId = manager == null ? null : manager.getManagerId();
        boolean canEditOrder = currentManagerId != null
                && (order.getHandledBy() == null || order.getHandledBy().equals(currentManagerId));

        request.setAttribute("order", order);
        request.setAttribute("details", details);
        request.setAttribute("searchKeyword", trimToNull(request.getParameter("keyword")));
        request.setAttribute("currentManagerId", currentManagerId);
        request.setAttribute("canEditOrder", canEditOrder);

        if (order != null && order.getHandledBy() != null) {
            ManagerDAO managerDAO = new ManagerDAO();
            Manager handledStaff = managerDAO.getById(order.getHandledBy());
            request.setAttribute("handledStaff", handledStaff);
        }

        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("currentView", "orders");
        request.setAttribute("contentPage", staffRoute ? STAFF_DETAIL : ADMIN_DETAIL);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        if (isStaffRoute(request) && !"STAFF".equalsIgnoreCase(manager.getManagerRole())) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        if (!isStaffRoute(request) && !"ADMIN".equalsIgnoreCase(manager.getManagerRole())) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        Integer orderId = parseInteger(request.getParameter("orderId"));
        String paymentStatus = trimToNull(request.getParameter("paymentStatus"));
        String orderStatus = trimToNull(request.getParameter("orderStatus"));

        if (orderId == null || paymentStatus == null || orderStatus == null) {
            response.sendRedirect(buildOrderDetailRedirect(request, orderId, "updateFailed", null, null));
            return;
        }

        OrderDAO dao = new OrderDAO();
        Integer handledBy = manager.getManagerId();
        Orders existingOrder = dao.getOrderUpdateInfo(orderId);
        if (existingOrder == null) {
            response.sendRedirect(buildOrderDetailRedirect(request, orderId, "updateFailed", null, null));
            return;
        }

        boolean isChanged = !equalsIgnoreCase(existingOrder.getPaymentStatus(), paymentStatus)
                || !equalsIgnoreCase(existingOrder.getOrderStatus(), orderStatus);
        if (!isChanged) {
            response.sendRedirect(buildOrderDetailRedirect(request, orderId, null, null, "update"));
            return;
        }

        Integer existingHandledBy = existingOrder.getHandledBy();
        boolean canUpdate = existingHandledBy == null || existingHandledBy.equals(handledBy);
        if (!canUpdate) {
            response.sendRedirect(buildOrderDetailRedirect(request, orderId, "notAllowed", null, null));
            return;
        }

        try {
            ValidationUtil.validateOrderStatusTransition(existingOrder.getOrderStatus(), orderStatus);
            if (equalsIgnoreCase(existingOrder.getPaymentMethod(), "COD")) {
                ValidationUtil.validateCodStatus(existingOrder.getPaymentMethod(), paymentStatus, orderStatus);
            }
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(buildOrderDetailRedirect(request, orderId, "validationError", ex.getMessage(), null));
            return;
        }

        if (!dao.updateOrder(orderId, paymentStatus, orderStatus, handledBy)) {
            response.sendRedirect(buildOrderDetailRedirect(request, orderId, "updateFailed", null, null));
            return;
        }

        response.sendRedirect(buildOrderDetailRedirect(request, orderId, null, null, "update"));
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath != null && servletPath.startsWith("/staff/");
    }

    private String buildManageOrdersRedirect(HttpServletRequest request, String error, String message, String success)
            throws IOException {
        StringBuilder redirect = new StringBuilder(request.getContextPath())
                .append(isStaffRoute(request) ? STAFF_URL : ADMIN_URL);

        String keyword = trimToNull(firstNonBlank(
                request.getParameter("returnKeyword"),
                request.getParameter("keyword")
        ));

        StringBuilder query = new StringBuilder();
        if (keyword != null) {
            appendQueryParam(query, "keyword", keyword);
        }
        if (error != null) {
            appendQueryParam(query, "error", error);
        }
        if (message != null) {
            appendQueryParam(query, "message", message);
        }
        if (success != null) {
            appendQueryParam(query, "success", success);
        }

        if (query.length() > 0) {
            redirect.append("?").append(query);
        }
        return redirect.toString();
    }

    private String buildOrderDetailRedirect(HttpServletRequest request, Integer orderId,
            String error, String message, String success) throws IOException {
        if (orderId == null) {
            return buildManageOrdersRedirect(request, error, message, success);
        }

        StringBuilder redirect = new StringBuilder(request.getContextPath())
                .append(isStaffRoute(request) ? STAFF_URL : ADMIN_URL);

        String keyword = trimToNull(firstNonBlank(
                request.getParameter("returnKeyword"),
                request.getParameter("keyword")
        ));

        StringBuilder query = new StringBuilder();
        appendQueryParam(query, "action", "detail");
        appendQueryParam(query, "orderId", String.valueOf(orderId));
        if (keyword != null) {
            appendQueryParam(query, "keyword", keyword);
        }
        if (error != null) {
            appendQueryParam(query, "error", error);
        }
        if (message != null) {
            appendQueryParam(query, "message", message);
        }
        if (success != null) {
            appendQueryParam(query, "success", success);
        }

        return redirect.append("?").append(query).toString();
    }

    private void appendQueryParam(StringBuilder query, String key, String value) throws IOException {
        if (query.length() > 0) {
            query.append("&");
        }
        query.append(key)
                .append("=")
                .append(URLEncoder.encode(value, "UTF-8"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseInteger(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }

        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private boolean equalsIgnoreCase(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equalsIgnoreCase(right);
    }
}


