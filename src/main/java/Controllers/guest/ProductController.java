package Controllers.guest;

import DALs.FeedbackDAO;
import DALs.ProductDAO;
import Model.OrderDetail;
import Model.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class ProductController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();

        String action = trimToNull(request.getParameter("action"));
        if (action == null) {
            action = request.getParameter("id") != null ? "detail" : "list";
        }

        switch (action) {
            case "detail":
                handleDetail(request, response, dao);
                break;
            case "list":
            default:
                handleListOrSearch(request, response, dao);
                break;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void setProductListAttributes(HttpServletRequest request,
                                          List<Product> products,
                                          List<String> availableBrands,
                                          List<String> availableCategories,
                                          String brandFilter,
                                          String categoryFilter,
                                          String sortBy,
                                          boolean topSalesLanding) {
        request.setAttribute("products", products);
        request.setAttribute("availableBrands", availableBrands);
        request.setAttribute("availableCategories", availableCategories);
        request.setAttribute("filterBrand", brandFilter);
        request.setAttribute("filterCategory", categoryFilter);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("isTopSalesLanding", topSalesLanding);
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            Product product = dao.getActiveProductById(productId);
            if (product != null) {
                request.setAttribute("product", product);
                FeedbackDAO feedbackDAO = new FeedbackDAO();
                List<OrderDetail> reviews = feedbackDAO.getFeedbacksByProductId(productId);
                request.setAttribute("reviews", reviews);
                forwardProductDetailView(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            // Ignore and redirect to product listing.
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }

    private void handleListOrSearch(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
            throws ServletException, IOException {
        String keyword = trimToNull(request.getParameter("keyword"));
        String brandFilter = trimToNull(request.getParameter("brand"));
        String categoryFilter = trimToNull(request.getParameter("category"));
        String sortBy = trimToNull(request.getParameter("sort"));

        if (keyword != null) {
            request.setAttribute("searchKeyword", keyword);
        }

        List<String> availableBrands = dao.getAllBrandNames();
        List<String> availableCategories = dao.getAllCategoryNames();

        boolean isDefaultLanding = keyword == null
                && brandFilter == null
                && categoryFilter == null
                && sortBy == null;
        boolean isTopSalesLanding = keyword == null
                && brandFilter == null
                && categoryFilter == null
                && "top_sales".equalsIgnoreCase(sortBy);

        List<Product> products;
        if (isDefaultLanding) {
            products = dao.getFeaturedProductsForGuest(30, 20);
        } else if (isTopSalesLanding) {
            products = dao.getFeaturedProductsForGuest(30, 20);
        } else {
            products = dao.getActiveProductsForGuest(keyword, brandFilter, categoryFilter, sortBy);
        }

        setProductListAttributes(
                request,
                products,
                availableBrands,
                availableCategories,
                brandFilter,
                categoryFilter,
                sortBy,
                isTopSalesLanding
        );
        forwardProductListView(request, response);
    }

    private void forwardProductListView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/guest/products.jsp").forward(request, response);
    }

    private void forwardProductDetailView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/guest/product-detail.jsp").forward(request, response);
    }
}
