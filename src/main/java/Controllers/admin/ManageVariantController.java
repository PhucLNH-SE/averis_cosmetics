package Controllers.admin;

import DALs.ProductVariantDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
        response.sendRedirect(buildManageProductRedirect(request));
    }

    private String buildManageProductRedirect(HttpServletRequest request) throws IOException {
        String keyword = firstNonBlank(
                request.getParameter("returnKeyword"),
                request.getParameter("keyword")
        );
        String brandId = firstNonBlank(
                request.getParameter("returnBrandId"),
                request.getParameter("brandId")
        );
        String categoryId = firstNonBlank(
                request.getParameter("returnCategoryId"),
                request.getParameter("categoryId")
        );
        String status = firstNonBlank(
                request.getParameter("returnStatus"),
                request.getParameter("status")
        );
        String redirectUrl = request.getContextPath() + "/admin/manage-product";
        StringBuilder query = new StringBuilder();

        appendQueryParam(query, "keyword", keyword);
        appendQueryParam(query, "brandId", brandId);
        appendQueryParam(query, "categoryId", categoryId);
        appendQueryParam(query, "status", status);

        return query.length() == 0 ? redirectUrl : redirectUrl + "?" + query;
    }

    private void appendQueryParam(StringBuilder query, String key, String value) throws IOException {
        if (value == null) {
            return;
        }
        if (query.length() > 0) {
            query.append("&");
        }
        query.append(key)
                .append("=")
                .append(URLEncoder.encode(value, "UTF-8"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }
}
