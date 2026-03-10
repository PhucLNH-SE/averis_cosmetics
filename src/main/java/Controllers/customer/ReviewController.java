package Controllers.customer;

import DALs.OrderDAO;
import Model.Customer;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ReviewController", urlPatterns = {"/review"})
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

            // 3. Gọi DAO để cập nhật đánh giá vào CSDL
            OrderDAO dao = new OrderDAO();
            boolean isSuccess = dao.updateReview(orderDetailId, rating, comment);

            // 4. Trả về thông báo thành công hoặc thất bại qua session
            if (isSuccess) {
                session.setAttribute("profileMessage", "Cảm ơn bạn đã đánh giá sản phẩm!");
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