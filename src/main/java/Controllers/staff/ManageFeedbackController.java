package Controllers.staff;

import DALs.FeedbackDAO;
import Model.Manager;
import Model.OrderDetail;
import Model.ProductFeedbackSummary;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ManageFeedbackController", urlPatterns = {"/staff/manage-feedback"})
public class ManageFeedbackController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(); 
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        FeedbackDAO dao = new FeedbackDAO();

        switch (action) {
            case "list":
                // Lấy danh sách sản phẩm (Group by) thay vì từng comment đơn lẻ
                List<ProductFeedbackSummary> productSummaries = dao.getFeedbackSummaryList();
                request.setAttribute("productSummaries", productSummaries);
                
                // Handle Flash Messages
                if (session.getAttribute("successMsg") != null) {
                    request.setAttribute("successMsg", session.getAttribute("successMsg"));
                    session.removeAttribute("successMsg");
                }
                if (session.getAttribute("errorMsg") != null) {
                    request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
                    session.removeAttribute("errorMsg");
                }

                request.getRequestDispatcher("/views/staff/manage-feedback.jsp").forward(request, response);
                break;

            case "getComments":
    try {
        int productId = Integer.parseInt(request.getParameter("productId"));
        List<OrderDetail> comments = dao.getFeedbacksByProductId(productId);
        
        request.setAttribute("comments", comments);
        // Đặt biển báo: Đây là yêu cầu AJAX nhé!
        request.setAttribute("isAjax", true); 
        
        // Forward về chính nó luôn
        request.getRequestDispatcher("/views/staff/manage-feedback.jsp").forward(request, response);
    } catch (Exception e) {
        response.sendError(500);
    }
    break;

            case "delete":
                try {
                    int orderDetailId = Integer.parseInt(request.getParameter("id"));
                    
                    // DELETE CONSTRAINT
                    FeedbackDAO feedbackDAO = new FeedbackDAO();
                    OrderDetail currentFb = feedbackDAO.getFeedbackDetail(orderDetailId);
                    Manager manager = (Manager) session.getAttribute("manager");
                    int currentManagerId = (manager != null) ? manager.getManagerId() : 1;

                    if (currentFb.getManagerResponse() != null && !currentFb.getManagerResponse().equals(currentManagerId)) {
                        session.setAttribute("errorMsg", "Error: You cannot delete a review that has been assigned to another staff member (" + currentFb.getManagerName() + ").");
                    } else {
                        boolean isDeleted = feedbackDAO.deleteFeedback(orderDetailId);
                        if (isDeleted) {
                            session.setAttribute("successMsg", "Review deleted successfully!");
                        } else {
                            session.setAttribute("errorMsg", "Deletion failed.");
                        }
                    }
                } catch (Exception e) {
                    session.setAttribute("errorMsg", "System error occurred during deletion.");
                }
                response.sendRedirect(request.getContextPath() + "/staff/manage-feedback?action=list");
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        int currentManagerId = (manager != null) ? manager.getManagerId() : 1; 

        String action = request.getParameter("action");

        if ("reply".equals(action)) {
            try {
                int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));
                String responseContent = request.getParameter("responseContent");

                FeedbackDAO dao = new FeedbackDAO();
                
                // --- IMPORTANT CONSTRAINT CHECK ---
                OrderDetail existingFb = dao.getFeedbackDetail(orderDetailId);
                
                if (existingFb != null && existingFb.getManagerResponse() != null) {
                    if (!existingFb.getManagerResponse().equals(currentManagerId)) {
                        session.setAttribute("errorMsg", "Error: This review has already been responded to by another staff member (" 
                                             + existingFb.getManagerName() + "). You do not have permission to change it.");
                        response.sendRedirect(request.getContextPath() + "/staff/manage-feedback?action=list");
                        return; // Halt operation
                    }
                }
                // ------------------------------------------

                if (responseContent == null || responseContent.trim().isEmpty()) {
                    session.setAttribute("errorMsg", "Response content cannot be empty!");
                } else {
                    boolean isReplied = dao.replyFeedback(orderDetailId, currentManagerId, responseContent.trim());
                    if (isReplied) {
                        session.setAttribute("successMsg", "Response saved successfully!");
                    } else {
                        session.setAttribute("errorMsg", "Error: Could not record the response in the system.");
                    }
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMsg", "Invalid input data.");
            }
            response.sendRedirect(request.getContextPath() + "/staff/manage-feedback?action=list");
        }
    }
}