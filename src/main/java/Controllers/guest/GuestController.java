package Controllers.guest;

import DALs.CategoryDAO;
import DALs.ProductDAO;
import DALs.StatisticDAO;
import Model.Category;
import Model.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {

            case "/home":
                prepareHomeData(request);
                request.getRequestDispatcher("/WEB-INF/views/common/home.jsp")
                        .forward(request, response);
                break;

            case "/introduce":
                request.getRequestDispatcher("/WEB-INF/views/guest/about-us.jsp")
                        .forward(request, response);
                break;

            case "/contact":
                request.getRequestDispatcher("/WEB-INF/views/guest/contact.jsp")
                        .forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void prepareHomeData(HttpServletRequest request) {
        LocalDate now = LocalDate.now();

        StatisticDAO statisticDAO = new StatisticDAO();
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        List<Map<String, Object>> topSelling = statisticDAO.getTopSellingProducts(
                now.getYear(),
                now.getMonthValue(),
                3
        );

        List<Product> activeProducts = productDAO.getAllActiveProducts();
        Map<Integer, Product> productMap = new HashMap<>();
        if (activeProducts != null) {
            for (Product p : activeProducts) {
                productMap.put(p.getProductId(), p);
            }
        }

        List<Map<String, Object>> featuredProducts = new ArrayList<>();
        if (topSelling != null && !topSelling.isEmpty()) {
            for (Map<String, Object> row : topSelling) {
                Integer productId = (Integer) row.get("productId");
                if (productId == null) {
                    continue;
                }
                Product product = productMap.get(productId);

                Map<String, Object> item = new HashMap<>();
                item.put("productId", productId);
                item.put("productName", row.get("productName"));
                item.put("imageUrl", row.get("imageUrl"));

                if (product != null) {
                    if (item.get("productName") == null) {
                        item.put("productName", product.getName());
                    }
                    if (item.get("imageUrl") == null) {
                        item.put("imageUrl", product.getMainImage());
                    }
                    item.put("minPrice", product.getPrice());
                    item.put("maxPrice", product.getMaxPrice());
                } else {
                    item.put("minPrice", 0);
                    item.put("maxPrice", 0);
                }

                featuredProducts.add(item);
            }
        }

        if (featuredProducts.isEmpty() && activeProducts != null) {
            int limit = Math.min(3, activeProducts.size());
            for (int i = 0; i < limit; i++) {
                Product product = activeProducts.get(i);
                Map<String, Object> item = new HashMap<>();
                item.put("productId", product.getProductId());
                item.put("productName", product.getName());
                item.put("imageUrl", product.getMainImage());
                item.put("minPrice", product.getPrice());
                item.put("maxPrice", product.getMaxPrice());
                featuredProducts.add(item);
            }
        }

        List<Category> categories = categoryDAO.getAllCategories();
        List<Category> featuredCategories = new ArrayList<>();
        if (categories != null) {
            for (Category c : categories) {
                if (c != null && c.isStatus()) {
                    featuredCategories.add(c);
                }
                if (featuredCategories.size() >= 4) {
                    break;
                }
            }
        }

        request.setAttribute("topSellingProducts", featuredProducts);
        request.setAttribute("featuredCategories", featuredCategories);
    }
}

