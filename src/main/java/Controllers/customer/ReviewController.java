package Controllers.customer;

import DALs.OrderDAO;
import Model.Customer;
import Model.OrderDetail; // Thêm import này
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
        
        // 1. Kiểm tra xem khách hàng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        try {
            // 2. Lấy dữ liệu từ form Pop-up gửi lên
            int orderDetailId = Integer.parseInt(request.getParameter("orderDetailId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");
            if (comment == null) comment = "";

            OrderDAO dao = new OrderDAO();
            
            // --- LOGIC MỚI: KIỂM TRA ĐÁNH GIÁ LẦN ĐẦU HAY LÀ SỬA ---
            OrderDetail oldDetail = dao.getOrderDetailById(orderDetailId);
            
            if (oldDetail == null) {
                session.setAttribute("error", "Không tìm thấy thông tin sản phẩm cần đánh giá!");
                response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");
                return;
            }

            String finalComment = comment;

            // Kiểm tra xem đã từng đánh giá chưa (rating cũ > 0)
            if (oldDetail.getRating() > 0) {
                String oldComment = oldDetail.getReviewComment();
                
                // Nếu comment cũ đã chứa tag [EDITED] -> Đã sửa 1 lần rồi -> Chặn
                if (oldComment != null && oldComment.contains("[EDITED]")) {
                    session.setAttribute("error", "Bạn chỉ được phép chỉnh sửa đánh giá 1 lần duy nhất!");
                    response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");
                    return;
                }
                
                // Nếu chưa có tag -> Đây là lần sửa đầu tiên -> Gắn thêm tag ẩn [EDITED]
                finalComment = comment + "[EDITED]";
            }
            // ---------------------------------------------------------

            // 3. Gọi DAO để cập nhật đánh giá vào CSDL
            boolean isSuccess = dao.updateReview(orderDetailId, rating, finalComment);

            // 4. Trả về thông báo thành công hoặc thất bại qua session
            if (isSuccess) {
                if (oldDetail.getRating() > 0) {
                    session.setAttribute("profileMessage", "Chỉnh sửa đánh giá thành công!");
                } else {
                    session.setAttribute("profileMessage", "Cảm ơn bạn đã đánh giá sản phẩm!");
                }
            } else {
                session.setAttribute("error", "Có lỗi xảy ra khi lưu đánh giá, vui lòng thử lại!");
            }

            // 5. Chuyển hướng người dùng quay lại đúng trang chi tiết của đơn hàng đó
            response.sendRedirect(request.getContextPath() + "/profile?action=orderDetail&orderId=" + orderId + "&tab=orderDetail");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu đánh giá không hợp lệ.");
        }
    }
}
