package Controllers.guest;

import DALs.BrandDAO;
import DALs.CategoryDAO;
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
        BrandDAO brandDAO = new BrandDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        String action = trimToNull(request.getParameter("action"));

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "detail":
                handleProductDetail(request, response, dao);
                break;
            case "topSales":
                handleTopSalesProductList(request, response, dao, brandDAO, categoryDAO);
                break;
            case "filter":
                handleFilteredProductList(request, response, dao, brandDAO, categoryDAO);
                break;
            case "list":
            default:
                handleDefaultProductList(request, response, dao, brandDAO, categoryDAO);
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

    
    //NganNK - use to ser product list to display for customer
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

    //NganNK - use to handle product detail function
    private void handleProductDetail(HttpServletRequest request, HttpServletResponse response, ProductDAO dao)
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
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }

    //NganNK - use to handle product list function
    private void handleDefaultProductList(HttpServletRequest request, HttpServletResponse response,
            ProductDAO dao, BrandDAO brandDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {
        List<String> availableBrands = brandDAO.getActiveBrandNames();
        List<String> availableCategories = categoryDAO.getActiveCategoryNames();
        setProductListAttributes(request,
                dao.getFeaturedProductsForGuest(30, 20),
                availableBrands,
                availableCategories,
                null,
                null,
                null,
                false);
        forwardProductListView(request, response);
    }

    private void handleTopSalesProductList(HttpServletRequest request, HttpServletResponse response,
            ProductDAO dao, BrandDAO brandDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {
        List<String> availableBrands = brandDAO.getActiveBrandNames();
        List<String> availableCategories = categoryDAO.getActiveCategoryNames();
        setProductListAttributes(request,
                dao.getFeaturedProductsForGuest(30, 20),
                availableBrands,
                availableCategories,
                null,
                null,
                "top_sales",
                true);
        forwardProductListView(request, response);
    }

    private void handleFilteredProductList(HttpServletRequest request, HttpServletResponse response,
            ProductDAO dao, BrandDAO brandDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {
        String keyword = trimToNull(request.getParameter("keyword"));
        String brandFilter = trimToNull(request.getParameter("brand"));
        String categoryFilter = trimToNull(request.getParameter("category"));
        String sortBy = trimToNull(request.getParameter("sort"));

        if (keyword != null) {
            request.setAttribute("searchKeyword", keyword);
        }

        List<String> availableBrands = brandDAO.getActiveBrandNames();
        List<String> availableCategories = categoryDAO.getActiveCategoryNames();
        setProductListAttributes(
                request,
                dao.getActiveProductsForGuest(keyword, brandFilter, categoryFilter, sortBy),
                availableBrands,
                availableCategories,
                brandFilter,
                categoryFilter,
                sortBy,
                false
        );
        forwardProductListView(request, response);
    }

    private void forwardProductListView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/guest/products.jsp").forward(request, response);
    }

    private void forwardProductDetailView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/guest/product-detail.jsp").forward(request, response);
    }
}

