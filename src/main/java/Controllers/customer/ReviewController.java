package Controllers.customer;

import DALs.OrderDAO;
import Model.OrderDetail;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ReviewController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String redirectTab = request.getParameter("redirectTab");
            String comment = request.getParameter("comment");
            if (comment == null) {
                comment = "";
            }

            OrderDAO dao = new OrderDAO();
            OrderDetail oldDetail = dao.getOrderDetailById(orderDetailId);

            if (oldDetail == null) {
                session.setAttribute("profileMessage", "The product information for this review could not be found.");
                session.setAttribute("profileMessageType", "error");
                response.sendRedirect(resolveRedirectUrl(request, orderId, redirectTab));
                return;
            }

            String finalComment = comment;

            if (oldDetail.getRating() > 0) {
                String oldComment = oldDetail.getReviewComment();

                if (oldComment != null && oldComment.contains("[EDITED]")) {
                    session.setAttribute("profileMessage", "You can only edit your review once.");
                    session.setAttribute("profileMessageType", "error");
                    response.sendRedirect(resolveRedirectUrl(request, orderId, redirectTab));
                    return;
                }

                finalComment = comment + "[EDITED]";
            }

            boolean isSuccess = dao.updateReview(orderDetailId, rating, finalComment);

            if (isSuccess) {
                if (oldDetail.getRating() > 0) {
                    session.setAttribute("profileMessage", "Review updated successfully!");
                } else {
                    session.setAttribute("profileMessage", "Thank you for reviewing this product!");
                }
                session.setAttribute("profileMessageType", "success");
            } else {
                session.setAttribute("profileMessage", "An error occurred while saving your review. Please try again.");
                session.setAttribute("profileMessageType", "error");
            }

            response.sendRedirect(resolveRedirectUrl(request, orderId, redirectTab));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid review data.");
        }
    }

    private String resolveRedirectUrl(HttpServletRequest request, int orderId, String redirectTab) {
        if ("feedback".equalsIgnoreCase(redirectTab)) {
            return request.getContextPath() + "/profile?action=view&tab=feedback";
        }
        return request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail";
    }
}
