package Controllers.customer;

import DALs.OrderDAO;
import Model.Customer;
import Model.OrderDetail; // ThÃªm import nÃ y
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
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        try {
              int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");
            if (comment == null) comment = "";

            OrderDAO dao = new OrderDAO();
            
           
            OrderDetail oldDetail = dao.getOrderDetailById(orderDetailId);
            
            if (oldDetail == null) {
                session.setAttribute("error", "KhÃ´ng tÃ¬m tháº¥y thÃ´ng tin sáº£n pháº©m cáº§n Ä‘Ã¡nh giÃ¡!");
                response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");
                return;
            }

            String finalComment = comment;

           
            if (oldDetail.getRating() > 0) {
                String oldComment = oldDetail.getReviewComment();
                
                
                if (oldComment != null && oldComment.contains("[EDITED]")) {
                    session.setAttribute("error", "Báº¡n chá»‰ Ä‘Æ°á»£c phÃ©p chá»‰nh sá»­a Ä‘Ã¡nh giÃ¡ 1 láº§n duy nháº¥t!");
                    response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");
                    return;
                }
                
                
                finalComment = comment + "[EDITED]";
            }
          
            boolean isSuccess = dao.updateReview(orderDetailId, rating, finalComment);

           
            if (isSuccess) {
                if (oldDetail.getRating() > 0) {
                    session.setAttribute("profileMessage", "Chá»‰nh sá»­a Ä‘Ã¡nh giÃ¡ thÃ nh cÃ´ng!");
                } else {
                    session.setAttribute("profileMessage", "Cáº£m Æ¡n báº¡n Ä‘Ã£ Ä‘Ã¡nh giÃ¡ sáº£n pháº©m!");
                }
            } else {
                session.setAttribute("error", "CÃ³ lá»—i xáº£y ra khi lÆ°u Ä‘Ã¡nh giÃ¡, vui lÃ²ng thá»­ láº¡i!");
            }

            response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dá»¯ liá»‡u Ä‘Ã¡nh giÃ¡ khÃ´ng há»£p lá»‡.");
        }
    }
}
