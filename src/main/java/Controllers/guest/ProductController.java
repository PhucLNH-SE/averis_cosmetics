package Controllers.guest;

import DALs.ProductDAO;
import model.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "ProductController", urlPatterns = {"/products", "/product/detail"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length());
        ProductDAO dao = new ProductDAO();
        
        if (pathInfo.startsWith("/product/detail")) {
            // Handle product detail request
            try {
                int productId = Integer.parseInt(request.getParameter("id"));
                Product product = dao.getProductById(productId);
                
                if (product != null) {
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/views/guest/product-detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/products");
                }
                
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } else {
            // Handle product list request (original GuestController functionality)
            List<Product> list = dao.getAllProducts();
            request.setAttribute("products", list);
            request.getRequestDispatcher("/views/guest/products.jsp").forward(request, response);
        }
    }
}