package Controllers.admin;

import DALs.ProductVariantDAO;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ManageVariantController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        ProductVariantDAO vDao = new ProductVariantDAO();

        try {
            if ("add".equals(action)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                String name = request.getParameter("variantName");
                BigDecimal price = new BigDecimal(request.getParameter("price"));
                int stock = Integer.parseInt(request.getParameter("stock"));
                
                vDao.insertVariant(productId, name, price, stock, true); // Mặc định status = true
                
            } else if ("update".equals(action)) {
                int variantId = Integer.parseInt(request.getParameter("variantId"));
                String name = request.getParameter("variantName");
                BigDecimal price = new BigDecimal(request.getParameter("price"));
                int stock = Integer.parseInt(request.getParameter("stock"));
                
                vDao.updateVariant(variantId, name, price, stock, true);
                
            } else if ("delete".equals(action)) {
                int variantId = Integer.parseInt(request.getParameter("variantId"));
                vDao.deleteVariant(variantId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cập nhật giá xong thì chuyển hướng lại về trang danh sách sản phẩm
        response.sendRedirect(request.getContextPath() + "/admin/manage-product");
    }
}
