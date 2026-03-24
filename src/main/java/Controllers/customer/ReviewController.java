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
        
        // 1. Kiá»ƒm tra xem khÃ¡ch hÃ ng Ä‘Ã£ Ä‘Äƒng nháº­p chÆ°a
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        try {
            // 2. Láº¥y dá»¯ liá»‡u tá»« form Pop-up gá»­i lÃªn
            int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");
            if (comment == null) comment = "";

            OrderDAO dao = new OrderDAO();
            
            // --- LOGIC Má»šI: KIá»‚M TRA ÄÃNH GIÃ Láº¦N Äáº¦U HAY LÃ€ Sá»¬A ---
            OrderDetail oldDetail = dao.getOrderDetailById(orderDetailId);
            
            if (oldDetail == null) {
                session.setAttribute("error", "KhÃ´ng tÃ¬m tháº¥y thÃ´ng tin sáº£n pháº©m cáº§n Ä‘Ã¡nh giÃ¡!");
                response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");
                return;
            }

            String finalComment = comment;

            // Kiá»ƒm tra xem Ä‘Ã£ tá»«ng Ä‘Ã¡nh giÃ¡ chÆ°a (rating cÅ© > 0)
            if (oldDetail.getRating() > 0) {
                String oldComment = oldDetail.getReviewComment();
                
                // Náº¿u comment cÅ© Ä‘Ã£ chá»©a tag [EDITED] -> ÄÃ£ sá»­a 1 láº§n rá»“i -> Cháº·n
                if (oldComment != null && oldComment.contains("[EDITED]")) {
                    session.setAttribute("error", "Báº¡n chá»‰ Ä‘Æ°á»£c phÃ©p chá»‰nh sá»­a Ä‘Ã¡nh giÃ¡ 1 láº§n duy nháº¥t!");
                    response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");
                    return;
                }
                
                // Náº¿u chÆ°a cÃ³ tag -> ÄÃ¢y lÃ  láº§n sá»­a Ä‘áº§u tiÃªn -> Gáº¯n thÃªm tag áº©n [EDITED]
                finalComment = comment + "[EDITED]";
            }
            // ---------------------------------------------------------

            // 3. Gá»i DAO Ä‘á»ƒ cáº­p nháº­t Ä‘Ã¡nh giÃ¡ vÃ o CSDL
            boolean isSuccess = dao.updateReview(orderDetailId, rating, finalComment);

            // 4. Tráº£ vá» thÃ´ng bÃ¡o thÃ nh cÃ´ng hoáº·c tháº¥t báº¡i qua session
            if (isSuccess) {
                if (oldDetail.getRating() > 0) {
                    session.setAttribute("profileMessage", "Chá»‰nh sá»­a Ä‘Ã¡nh giÃ¡ thÃ nh cÃ´ng!");
                } else {
                    session.setAttribute("profileMessage", "Cáº£m Æ¡n báº¡n Ä‘Ã£ Ä‘Ã¡nh giÃ¡ sáº£n pháº©m!");
                }
            } else {
                session.setAttribute("error", "CÃ³ lá»—i xáº£y ra khi lÆ°u Ä‘Ã¡nh giÃ¡, vui lÃ²ng thá»­ láº¡i!");
            }

            // 5. Chuyá»ƒn hÆ°á»›ng ngÆ°á»i dÃ¹ng quay láº¡i Ä‘Ãºng trang chi tiáº¿t cá»§a Ä‘Æ¡n hÃ ng Ä‘Ã³
            response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dá»¯ liá»‡u Ä‘Ã¡nh giÃ¡ khÃ´ng há»£p lá»‡.");
        }
    }
}
