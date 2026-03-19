package Controllers.staff;

import DALs.ProductDAO;
import Model.Product;
import Model.Brand;
import Model.Category;
import Model.Manager; // Thêm model Manager 
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class StaffProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        
        // 1. Phân quyền: Chỉ Staff (hoặc Admin) mới được xem
        Manager manager = (Manager) session.getAttribute("manager"); // Lấy account đăng nhập
        if (manager == null || (!"STAFF".equals(manager.getManagerRole()) && !"ADMIN".equals(manager.getManagerRole()))) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        ProductDAO dao = new ProductDAO();

        // 2. Lấy dữ liệu (Chỉ cần list Product là đủ để view)
        List<Product> listP = dao.getAllProducts();
        
        // Truyền thêm Brand và Category nếu bro có làm chức năng Filter tìm kiếm
        List<Brand> listB = dao.getAllBrands();
        List<Category> listC = dao.getAllCategories();

        request.setAttribute("listP", listP);
        request.setAttribute("listB", listB);
        request.setAttribute("listC", listC);
        
        // 3. Đẩy sang view của Staff
        request.setAttribute("currentView", "products");
        request.setAttribute("contentPage", "/views/staff/partials/manage-product-content.jsp"); // TRỎ ĐÚNG FILE JSP CỦA STAFF

        // Render ra layout chung của panel (dùng chung layout với Admin)
        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Staff không có quyền Post (Add/Update/Delete) nên chặn luôn ở đây
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Staff only has read access.");
    }
}
