package Controllers.guest;

import DALs.ProductDAO;
import Model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "ProductSuggestController", urlPatterns = {"/products/suggest"})
public class ProductSuggestController extends HttpServlet {

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        JSONArray json = new JSONArray();

        if (keyword != null && !keyword.trim().isEmpty()) {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.searchProductsForAutoSuggest(keyword.trim());

            for (Product p : products) {
                JSONObject brand = new JSONObject();
                String brandName = (p.getBrand() != null) ? p.getBrand().getName() : "Unknown Brand";
                brand.put("name", nullToEmpty(brandName));

                JSONObject item = new JSONObject();
                item.put("productId", p.getProductId());
                item.put("name", nullToEmpty(p.getName()));
                item.put("mainImage", nullToEmpty(p.getMainImage()));
                item.put("images", new JSONArray());
                item.put("brand", brand);
                json.put(item);
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
}
