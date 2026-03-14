package Controllers.staff;

import DALs.OrderDAO;
import Model.Manager;
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
            case "list":
            default:
                listOrders(request, response);
                break;
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OrderDAO dao = new OrderDAO();
        List<Orders> orderList = dao.getAllOrders();

        request.setAttribute("orderList", orderList);
        request.setAttribute("currentView", "orders");
        request.setAttribute("contentPage", "/views/staff/partials/manage-orders-content.jsp");
        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("update".equals(action)) {
            HttpSession session = request.getSession(false);
            Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
            Integer changedBy = manager == null ? null : manager.getManagerId();

            String[] orderIds = request.getParameterValues("orderId");
            String[] paymentStatuses = request.getParameterValues("paymentStatus");
            String[] orderStatuses = request.getParameterValues("orderStatus");

            OrderDAO dao = new OrderDAO();

            for (int i = 0; i < orderIds.length; i++) {
                int orderId = Integer.parseInt(orderIds[i]);
                dao.updateOrder(orderId, paymentStatuses[i], orderStatuses[i], changedBy);
            }

            response.sendRedirect(request.getContextPath() + "/staff/manage-orders?success=update");
            return;
        }

        doGet(request, response);
    }
}
