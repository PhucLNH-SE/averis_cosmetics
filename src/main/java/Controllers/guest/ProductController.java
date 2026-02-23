package Controllers.guest;

import DALs.ProductDAO;
import Model.Product;
import Model.ProductVariant;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private BigDecimal getProductDisplayPrice(Product product) {
        if (product == null || product.getVariants() == null || product.getVariants().isEmpty()) {
            return null;
        }

        BigDecimal minPrice = null;
        for (ProductVariant variant : product.getVariants()) {
            if (variant.getPrice() == null) {
                continue;
            }
            if (minPrice == null || variant.getPrice().compareTo(minPrice) < 0) {
                minPrice = variant.getPrice();
            }
        }
        return minPrice;
    }

    private List<Product> applyFilters(List<Product> products, String brandFilter, String categoryFilter) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            boolean matchedBrand = (brandFilter == null)
                    || (product.getBrand() != null
                    && product.getBrand().getName() != null
                    && product.getBrand().getName().equalsIgnoreCase(brandFilter));

            boolean matchedCategory = (categoryFilter == null)
                    || (product.getCategory() != null
                    && product.getCategory().getName() != null
                    && product.getCategory().getName().equalsIgnoreCase(categoryFilter));

            if (matchedBrand && matchedCategory) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    private List<Product> applySort(List<Product> products, String sortBy) {
        if (products == null || products.size() <= 1 || sortBy == null) {
            return products;
        }

        List<Product> sorted = new ArrayList<>(products);
        switch (sortBy.toLowerCase()) {
            case "price_asc":
                sorted.sort(Comparator.comparing(this::getProductDisplayPrice, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "price_desc":
                sorted.sort((a, b) -> {
                    BigDecimal pa = getProductDisplayPrice(a);
                    BigDecimal pb = getProductDisplayPrice(b);
                    if (pa == null && pb == null) {
                        return 0;
                    }
                    if (pa == null) {
                        return 1;
                    }
                    if (pb == null) {
                        return -1;
                    }
                    return pb.compareTo(pa);
                });
                break;
            case "name_asc":
                sorted.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "name_desc":
                sorted.sort((a, b) -> b.getName().compareToIgnoreCase(a.getName()));
                break;
            default:
                break;
        }

        return sorted;
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
                // Handle product search + post-search filters
                String keyword = request.getParameter("keyword");
                List<Product> searchResults;

                if (keyword != null && !keyword.trim().isEmpty()) {
                    keyword = keyword.trim();
                    searchResults = dao.searchProducts(keyword);
                    request.setAttribute("searchKeyword", keyword);
                } else {
                    searchResults = dao.getAllProducts();
                }

                List<String> availableBrands = dao.getAllBrandNames();
                List<String> availableCategories = dao.getAllCategoryNames();

                String brandFilter = trimToNull(request.getParameter("brand"));
                String categoryFilter = trimToNull(request.getParameter("category"));
                String sortBy = trimToNull(request.getParameter("sort"));

                searchResults = applyFilters(searchResults, brandFilter, categoryFilter);
                searchResults = applySort(searchResults, sortBy);

                request.setAttribute("availableBrands", availableBrands);
                request.setAttribute("availableCategories", availableCategories);
                request.setAttribute("filterBrand", brandFilter);
                request.setAttribute("filterCategory", categoryFilter);
                request.setAttribute("sortBy", sortBy);
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
