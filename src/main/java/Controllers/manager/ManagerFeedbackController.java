package Controllers.manager;

import DALs.FeedbackDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.ProductFeedbackSummary;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ManagerFeedbackController extends HttpServlet {

    private static final String ADMIN_URL = "/admin/manage-feedback";
    private static final String STAFF_URL = "/staff/manage-feedback";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-feedback-content.jsp";
    private static final String STAFF_CONTENT = "/WEB-INF/views/staff/partials/manage-feedback-content.jsp";
    private static final String ADMIN_COMMENTS = "/WEB-INF/views/admin/partials/manage-feedback-comments.jsp";
    private static final String STAFF_COMMENTS = "/WEB-INF/views/staff/partials/manage-feedback-comments.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        FeedbackDAO dao = new FeedbackDAO();
        HttpSession session = request.getSession();

        switch (action) {
            case "getComments":
                handleGetComments(request, response, dao);
                break;
            case "delete":
                handleDelete(request, response, session, dao);
                break;
            case "list":
            default:
                loadFeedbackPage(request, response, session, dao);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (!"reply".equals(action)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        HttpSession session = request.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        handleReply(request, response, session, new FeedbackDAO(), manager);
    }

    private void loadFeedbackPage(HttpServletRequest request, HttpServletResponse response,
                                  HttpSession session, FeedbackDAO dao)
            throws ServletException, IOException {
        List<ProductFeedbackSummary> productSummaries = dao.getFeedbackSummaryList();
        request.setAttribute("productSummaries", productSummaries);

        moveFlashMessage(session, request, "successMsg");
        moveFlashMessage(session, request, "errorMsg");

        request.setAttribute("currentView", "feedback");

        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("contentPage", staffRoute ? STAFF_CONTENT : ADMIN_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private void handleGetComments(HttpServletRequest request, HttpServletResponse response,
                                   FeedbackDAO dao) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            List<OrderDetail> comments = dao.getFeedbacksByProductId(productId);
            request.setAttribute("comments", comments);
            request.getRequestDispatcher(isStaffRoute(request) ? STAFF_COMMENTS : ADMIN_COMMENTS)
                    .forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, FeedbackDAO dao) throws IOException {
        try {
            int orderDetailId = Integer.parseInt(request.getParameter("id"));
            boolean deleted = dao.deleteFeedback(orderDetailId);
            if (deleted) {
                session.setAttribute("successMsg", "Review deleted successfully!");
            } else {
                session.setAttribute("errorMsg", "Deletion failed!");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "System error during deletion.");
        }

        response.sendRedirect(buildFeedbackRedirect(request));
    }

    private void handleReply(HttpServletRequest request, HttpServletResponse response,
                             HttpSession session, FeedbackDAO dao, Manager manager) throws IOException {
        int currentManagerId = manager.getManagerId();

        try {
            int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));
            String responseContent = request.getParameter("responseContent");
            OrderDetail existingFb = dao.getFeedbackDetail(orderDetailId);

            if (existingFb != null
                    && existingFb.getManagerResponse() != null
                    && !existingFb.getManagerResponse().equals(currentManagerId)) {
                session.setAttribute("errorMsg",
                        "This review has already been responded to by " + existingFb.getManagerName() + ".");
            } else if (responseContent == null || responseContent.trim().isEmpty()) {
                session.setAttribute("errorMsg", "Response cannot be empty!");
            } else {
                boolean replied = dao.replyFeedback(orderDetailId, currentManagerId, responseContent.trim());
                if (replied) {
                    session.setAttribute("successMsg", "Response saved successfully!");
                } else {
                    session.setAttribute("errorMsg", "Failed to save response!");
                }
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "System error occurred.");
        }

        response.sendRedirect(buildFeedbackRedirect(request));
    }


    private void moveFlashMessage(HttpSession session, HttpServletRequest request, String key) {
        Object value = session.getAttribute(key);
        if (value != null) {
            request.setAttribute(key, value);
            session.removeAttribute(key);
        }
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath != null && servletPath.startsWith("/staff/");
    }

    private String buildFeedbackRedirect(HttpServletRequest request) {
        return request.getContextPath() + (isStaffRoute(request) ? STAFF_URL : ADMIN_URL) + "?action=list";
    }
}



