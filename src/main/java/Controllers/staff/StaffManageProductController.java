package Controllers.staff;

import DALs.ProductDAO;
import Model.Product;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffManageProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();

        // Lấy danh sách sản phẩm
        List<Product> listP = dao.getAllProducts();

        // Đẩy data sang JSP
        request.setAttribute("listP", listP);

        // dùng để active menu sidebar
        request.setAttribute("currentView", "products");

        // trang content trong staff-panel
        request.setAttribute("contentPage",
                "/views/staff/partials/manage-product-content.jsp");

        // render layout staff
        request.getRequestDispatcher("/views/staff/staff-panel.jsp")
                .forward(request, response);
    }

}