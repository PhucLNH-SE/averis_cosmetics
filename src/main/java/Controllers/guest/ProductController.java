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

@WebServlet(name = "ProductController", urlPatterns = {"/products"})
public class ProductController extends HttpServlet {

    private String escapeJson(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length()).toLowerCase();
        ProductDAO dao = new ProductDAO();
        
        // Determine the action based on path info or parameters
        String action;
        if (pathInfo.contains("/product/detail") || request.getParameter("id") != null) {
            action = "detail";
        } else if (request.getParameter("keyword") != null) {
            action = "search";
        } else {
            action = "list";
        }
        
        // Check if this is an AJAX request for auto-suggest
        String ajaxRequest = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajaxRequest)) {
            // Handle AJAX auto-suggest request
            String keyword = request.getParameter("keyword");
            if (keyword != null && !keyword.trim().isEmpty()) {
                List<Product> products = dao.searchProductsForAutoSuggest(keyword.trim());
                
                // Convert to JSON manually to avoid needing Gson
                StringBuilder json = new StringBuilder();
                json.append("[");
                for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    json.append("{");
                    json.append("\"productId\":\"").append(p.getProductId()).append("\",");
                    json.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
                    json.append("\"mainImage\":\"").append(escapeJson(p.getMainImage())).append("\",");
                    json.append("\"images\":[],");
                    json.append("\"brand\":{");
                    json.append("\"name\":\"").append(escapeJson(p.getBrand() != null ? p.getBrand().getName() : "Unknown Brand")).append("\"");
                    json.append("}");
                    json.append("}");
                    if (i < products.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json.toString());
                return;
            }
        }
        
        // Handle regular requests
        switch (action) {
            case "detail":
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
                break;
            case "search":
                // Handle product search
                String keyword = request.getParameter("keyword");
                List<Product> searchResults;
                
                if (keyword != null && !keyword.trim().isEmpty()) {
                    searchResults = dao.searchProducts(keyword.trim());
                    request.setAttribute("searchKeyword", keyword.trim());
                } else {
                    searchResults = dao.getAllProducts();
                }
                
                request.setAttribute("products", searchResults);
                request.getRequestDispatcher("/views/guest/products.jsp").forward(request, response);
                break;
            case "list":
            default:
                // Handle product list request (original GuestController functionality)
                List<Product> list = dao.getAllProducts();
                request.setAttribute("products", list);
                request.getRequestDispatcher("/views/guest/products.jsp").forward(request, response);
                break;
        }
    }
}