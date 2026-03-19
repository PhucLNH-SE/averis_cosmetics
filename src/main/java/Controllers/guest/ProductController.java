package Controllers.guest;

import DALs.FeedbackDAO;
import Model.OrderDetail;
import DALs.ProductDAO;
import Model.Product;
import Model.ProductVariant;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private List<Product> applySort(List<Product> products, String sortBy, ProductDAO dao) {
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
            case "top_sales":
                List<Integer> topIds = dao.getTopSellingProductIds();
                if (topIds == null || topIds.isEmpty()) {
                    break;
                }
                List<Product> topOnly = new ArrayList<>();
                for (Integer productId : topIds) {
                    for (Product product : sorted) {
                        if (product.getProductId() == productId) {
                            topOnly.add(product);
                            break;
                        }
                    }
                }
                return topOnly;
            default:
                break;
        }

        return sorted;
    }

    private List<Product> buildFeaturedProducts(List<Product> allProducts, List<Integer> topIds,
                                                int topLimit, int randomLimit) {
        if (allProducts == null || allProducts.isEmpty()) {
            return allProducts;
        }

        Map<Integer, Product> productMap = new HashMap<>();
        for (Product product : allProducts) {
            productMap.put(product.getProductId(), product);
        }

        List<Product> result = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        if (topIds != null && !topIds.isEmpty()) {
            for (Integer id : topIds) {
                if (id == null || usedIds.contains(id)) {
                    continue;
                }
                Product product = productMap.get(id);
                if (product != null) {
                    result.add(product);
                    usedIds.add(id);
                    if (result.size() >= topLimit) {
                        break;
                    }
                }
            }
        }

        List<Product> remaining = new ArrayList<>();
        for (Product product : allProducts) {
            if (!usedIds.contains(product.getProductId())) {
                remaining.add(product);
            }
        }

        Collections.shuffle(remaining);
        int addRandom = Math.min(randomLimit, remaining.size());
        for (int i = 0; i < addRandom; i++) {
            result.add(remaining.get(i));
        }

        return result;
    }

    private void setProductListAttributes(HttpServletRequest request,
                                          List<Product> products,
                                          List<String> availableBrands,
                                          List<String> availableCategories,
                                          String brandFilter,
                                          String categoryFilter,
                                          String sortBy) {
        request.setAttribute("products", products);
        request.setAttribute("availableBrands", availableBrands);
        request.setAttribute("availableCategories", availableCategories);
        request.setAttribute("filterBrand", brandFilter);
        request.setAttribute("filterCategory", categoryFilter);
        request.setAttribute("sortBy", sortBy);
    }

    private String resolveAction(HttpServletRequest request) {
        if (request.getParameter("id") != null) {
            return "detail";
        }
        if (request.getParameter("keyword") != null) {
            return "search";
        }
        return "list";
    }

    private boolean handleAutoSuggestAjax(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws IOException {
        String ajaxRequest = request.getHeader("X-Requested-With");
        if (!"XMLHttpRequest".equals(ajaxRequest)) {
            return false;
        }

        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }

        List<Product> products = dao.searchProductsForAutoSuggest(keyword.trim());
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
        return true;
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            Product product = dao.getActiveProductById(productId); // <-- Đã sửa

            if (product != null) {
                request.setAttribute("product", product);
                
                // --- THÊM PHẦN LẤY DỮ LIỆU FEEDBACK Ở ĐÂY ---
                FeedbackDAO feedbackDAO = new FeedbackDAO();
                List<OrderDetail> reviews = feedbackDAO.getFeedbacksByProductId(productId);
                request.setAttribute("reviews", reviews);
                // --------------------------------------------

                request.getRequestDispatcher("/views/guest/product-detail.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            // Ignore and redirect to product listing.
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws ServletException, IOException {
        String keyword = trimToNull(request.getParameter("keyword"));
        List<Product> products = (keyword != null) ? dao.searchProducts(keyword) : dao.getAllActiveProducts(); // <-- Đã sửa
        if (keyword != null) {
            request.setAttribute("searchKeyword", keyword);
        }

        List<String> availableBrands = dao.getAllBrandNames();
        List<String> availableCategories = dao.getAllCategoryNames();

        String brandFilter = trimToNull(request.getParameter("brand"));
        String categoryFilter = trimToNull(request.getParameter("category"));
        String sortBy = trimToNull(request.getParameter("sort"));

        products = applyFilters(products, brandFilter, categoryFilter);
        products = applySort(products, sortBy, dao);

        setProductListAttributes(
                request,
                products,
                availableBrands,
                availableCategories,
                brandFilter,
                categoryFilter,
                sortBy
        );
        request.getRequestDispatcher("/views/guest/products.jsp").forward(request, response);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws ServletException, IOException {
        List<Product> products = dao.getAllActiveProducts();
        List<String> availableBrands = dao.getAllBrandNames();
        List<String> availableCategories = dao.getAllCategoryNames();
        String brandFilter = trimToNull(request.getParameter("brand"));
        String categoryFilter = trimToNull(request.getParameter("category"));
        String sortBy = trimToNull(request.getParameter("sort"));

        boolean isDefaultLanding = brandFilter == null
                && categoryFilter == null
                && sortBy == null;

        if (isDefaultLanding) {
            products = buildFeaturedProducts(products, dao.getTopSellingProductIds(), 30, 20);
        } else {
            products = applyFilters(products, brandFilter, categoryFilter);
            products = applySort(products, sortBy, dao);
        }

        setProductListAttributes(
                request,
                products,
                availableBrands,
                availableCategories,
                brandFilter,
                categoryFilter,
                sortBy
        );
        request.getRequestDispatcher("/views/guest/products.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();
        String action = resolveAction(request);

        if (handleAutoSuggestAjax(request, response, dao)) {
            return;
        }

        switch (action) {
            case "detail":
                handleDetail(request, response, dao);
                break;
            case "search":
                handleSearch(request, response, dao);
                break;
            case "list":
            default:
                handleList(request, response, dao);
                break;
        }
    }
}
