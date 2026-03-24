package Controllers.staff;

import DALs.ProductDAO;
import Model.Brand;
import Model.Category;
import Model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class StaffProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = trimToNull(request.getParameter("keyword"));
        Integer brandId = parsePositiveInteger(request.getParameter("brandId"));
        Integer categoryId = parsePositiveInteger(request.getParameter("categoryId"));
        String status = normalizeStatus(request.getParameter("status"));
        ProductDAO productDAO = new ProductDAO();

        List<Product> listP = productDAO.getProductsForStaff(keyword, brandId, categoryId, parseStatus(status));
        List<Brand> listB = productDAO.getAllBrands();
        List<Category> listC = productDAO.getAllCategories();
        int activeCount = countActiveProducts(listP);
        int inactiveCount = listP.size() - activeCount;

        request.setAttribute("listP", listP);
        request.setAttribute("listB", listB);
        request.setAttribute("listC", listC);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedBrandId", brandId);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("resultCount", listP.size());
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("inactiveCount", inactiveCount);
        request.setAttribute("totalProductCount", productDAO.countAllProducts());
        request.setAttribute("currentView", "products");
        request.setAttribute("contentPage", "/WEB-INF/views/staff/partials/manage-product-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/staff/staff-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parsePositiveInteger(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(normalized);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeStatus(String status) {
        String normalized = trimToNull(status);
        if (normalized == null) {
            return null;
        }

        if ("active".equalsIgnoreCase(normalized)) {
            return "active";
        }

        if ("inactive".equalsIgnoreCase(normalized)) {
            return "inactive";
        }

        return null;
    }

    private Boolean parseStatus(String status) {
        if (status == null) {
            return null;
        }

        if ("active".equalsIgnoreCase(status)) {
            return Boolean.TRUE;
        }

        if ("inactive".equalsIgnoreCase(status)) {
            return Boolean.FALSE;
        }

        return null;
    }

    private int countActiveProducts(List<Product> products) {
        int count = 0;

        for (Product product : products) {
            if (product.isStatus()) {
                count++;
            }
        }

        return count;
    }
}

