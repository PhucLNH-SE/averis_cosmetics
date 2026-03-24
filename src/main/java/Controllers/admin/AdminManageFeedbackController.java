package Controllers.admin;

import java.io.IOException;
import java.util.List;

import DALs.FeedbackDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.ProductFeedbackSummary;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminManageFeedbackController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        FeedbackDAO dao = new FeedbackDAO();

        switch (action) {

            case "list":

                List<ProductFeedbackSummary> productSummaries = dao.getFeedbackSummaryList();

                request.setAttribute("productSummaries", productSummaries);

                request.setAttribute("currentView", "feedback");

                request.setAttribute("contentPage",
                        "/WEB-INF/views/admin/partials/manage-feedback-content.jsp");

                request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp")
                        .forward(request, response);

                break;


            case "getComments":

                try {

                    int productId = Integer.parseInt(request.getParameter("productId"));

                    List<OrderDetail> comments = dao.getFeedbacksByProductId(productId);

                    request.setAttribute("comments", comments);

                    request.getRequestDispatcher(
                            "/WEB-INF/views/admin/partials/manage-feedback-comments.jsp")
                            .forward(request, response);

                } catch (Exception e) {

                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                }

                break;


            case "delete":

                try {

                    int orderDetailId = Integer.parseInt(request.getParameter("id"));

                    boolean deleted = dao.deleteFeedback(orderDetailId);

                    if (deleted) {
                        request.getSession().setAttribute("successMsg", "Review deleted successfully!");
                    } else {
                        request.getSession().setAttribute("errorMsg", "Deletion failed!");
                    }

                } catch (Exception e) {

                    request.getSession().setAttribute("errorMsg", "System error during deletion.");

                }

                response.sendRedirect(request.getContextPath() + "/admin/manage-feedback");

                break;
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Manager manager = (Manager) session.getAttribute("manager");

        int currentManagerId = manager != null ? manager.getManagerId() : 1;

        String action = request.getParameter("action");

        if (!"reply".equals(action)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {

            int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));

            String responseContent = request.getParameter("responseContent");

            FeedbackDAO dao = new FeedbackDAO();

            OrderDetail existingFb = dao.getFeedbackDetail(orderDetailId);

            if (existingFb != null
                    && existingFb.getManagerResponse() != null
                    && !existingFb.getManagerResponse().equals(currentManagerId)) {

                session.setAttribute("errorMsg",
                        "This review has already been responded by another staff.");

            } else if (responseContent == null || responseContent.trim().isEmpty()) {

                session.setAttribute("errorMsg", "Response cannot be empty!");

            } else {

                boolean replied = dao.replyFeedback(
                        orderDetailId,
                        currentManagerId,
                        responseContent.trim()
                );

                if (replied) {
                    session.setAttribute("successMsg", "Response saved successfully!");
                } else {
                    session.setAttribute("errorMsg", "Failed to save response!");
                }
            }

        } catch (Exception e) {

            session.setAttribute("errorMsg", "System error occurred.");

        }

        response.sendRedirect(request.getContextPath() + "/admin/manage-feedback");
    }
}
