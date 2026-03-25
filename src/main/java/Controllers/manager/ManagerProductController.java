package Controllers.manager;

import DALs.ProductDAO;
import Model.Brand;
import Model.Category;
import Model.Product;
import Model.ProductVariant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ManagerProductController extends HttpServlet {

    private static final String ADMIN_LIST_URL = "/admin/manage-product";
    private static final String STAFF_LIST_URL = "/staff/manage-product";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-product-content.jsp";
    private static final String STAFF_CONTENT = "/WEB-INF/views/staff/partials/manage-product-content.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        ProductDAO dao = new ProductDAO();
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "detail":
                handleDetail(request, response, dao, session);
                break;
            case "edit":
                if (isStaffRoute(request)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                handleEdit(request, response, dao, session);
                break;
            case "list":
            default:
                loadProductPage(request, response, dao, session);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        if (isStaffRoute(request)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        ProductDAO dao = new ProductDAO();
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "add":
                handleAdd(request, response, dao, session);
                break;
            case "update":
                handleUpdate(request, response, dao, session);
                break;
            case "delete":
                handleDelete(request, response, dao, session);
                break;
            case "show":
                handleShow(request, response, dao, session);
                break;
            default:
                response.sendRedirect(buildManageProductRedirect(request));
                break;
        }
    }

    private void loadProductPage(HttpServletRequest request, HttpServletResponse response,
                                 ProductDAO dao, HttpSession session)
            throws ServletException, IOException {

        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }

        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }

        List<Product> listP;
        List<Brand> listB = dao.getAllBrands();
        List<Category> listC = dao.getAllCategories();
        int activeCount;
        int inactiveCount;

        String keyword = trimToNull(request.getParameter("keyword"));
        request.setAttribute("searchKeyword", keyword);

        if (isStaffRoute(request)) {
            Integer brandId = parsePositiveInteger(request.getParameter("brandId"));
            Integer categoryId = parsePositiveInteger(request.getParameter("categoryId"));
            String status = normalizeStatus(request.getParameter("status"));

            listP = dao.getProductsForStaff(keyword, brandId, categoryId, parseStatus(status));
            activeCount = countActiveProducts(listP);
            inactiveCount = listP.size() - activeCount;

            request.setAttribute("selectedBrandId", brandId);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("totalProductCount", dao.countAllProducts());
        } else {
            String brandId = trimToNull(request.getParameter("brandId"));
            String categoryId = trimToNull(request.getParameter("categoryId"));
            String status = trimToNull(request.getParameter("status"));

            listP = dao.getProductsForAdminWithImportPrice(keyword, brandId, categoryId, status);
            activeCount = countActiveProducts(listP);
            inactiveCount = listP.size() - activeCount;

            request.setAttribute("selectedBrandId", brandId);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("selectedStatus", status);
        }

        request.setAttribute("listP", listP);
        request.setAttribute("listB", listB);
        request.setAttribute("listC", listC);
        request.setAttribute("resultCount", listP.size());
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("inactiveCount", inactiveCount);
        request.setAttribute("currentView", "products");

        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("contentPage", staffRoute ? STAFF_CONTENT : ADMIN_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response,
                              ProductDAO dao, HttpSession session)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"));
        Product product = dao.getProductById(id);

        if (product == null) {
            session.setAttribute("errorMsg", "Product detail is unavailable.");
            response.sendRedirect(buildManageProductRedirect(request));
            return;
        }

        applyPriceRange(product);
        request.setAttribute("selectedDetailProduct", product);
        request.setAttribute("detailMode", true);
        loadProductPage(request, response, dao, session);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response,
                            ProductDAO dao, HttpSession session)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"));
        Product product = dao.getProductById(id);

        if (product == null) {
            session.setAttribute("errorMsg", "Product could not be loaded for editing.");
            response.sendRedirect(buildManageProductRedirect(request));
            return;
        }

        request.setAttribute("selectedProduct", product);
        request.setAttribute("formMode", "update");
        loadProductPage(request, response, dao, session);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
                              ProductDAO dao, HttpSession session)
            throws IOException {

        int id = parseInt(request.getParameter("productId"));

        if (dao.hideProduct(id)) {
            session.setAttribute("successMsg", "Product hidden successfully.");
        } else {
            session.setAttribute("errorMsg", "Unable to hide product.");
        }

        response.sendRedirect(buildManageProductRedirect(request));
    }

    private void handleShow(HttpServletRequest request, HttpServletResponse response,
                            ProductDAO dao, HttpSession session)
            throws IOException {

        int id = parseInt(request.getParameter("productId"));

        if (dao.showProduct(id)) {
            session.setAttribute("successMsg", "Product is visible again.");
        } else {
            session.setAttribute("errorMsg", "Unable to show product again.");
        }

        response.sendRedirect(buildManageProductRedirect(request));
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response,
                           ProductDAO dao, HttpSession session)
            throws IOException, ServletException {

        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        int bid = parseInt(request.getParameter("brandId"));
        int cid = parseInt(request.getParameter("categoryId"));
        boolean status = request.getParameter("status") != null;
        String image = uploadImage(request);
        double price = parseDouble(request.getParameter("price"));
        boolean inserted = dao.insertProduct(name, desc, bid, cid, status, image, price, 0);

        if (inserted) {
            session.setAttribute("successMsg", "Product added successfully.");
        } else {
            session.setAttribute("errorMsg", "Unable to add product. Please check brand, category, and price.");
        }

        response.sendRedirect(buildManageProductRedirect(request));
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response,
                              ProductDAO dao, HttpSession session)
            throws IOException, ServletException {

        int id = parseInt(request.getParameter("productId"));
        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        int bid = parseInt(request.getParameter("brandId"));
        int cid = parseInt(request.getParameter("categoryId"));
        boolean status = request.getParameter("status") != null;
        String image = uploadImage(request);

        boolean updated = dao.updateProduct(id, name, desc, bid, cid, status, image);

        if (updated) {
            session.setAttribute("successMsg", "Product updated successfully.");
        } else {
            session.setAttribute("errorMsg", "Unable to update product.");
        }

        response.sendRedirect(buildManageProductRedirect(request));
    }

    private String uploadImage(HttpServletRequest request)
            throws IOException, ServletException {

        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            return null;
        }

        Part filePart = request.getPart("image");
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String fileName = getFileName(filePart);
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }

        String extension = fileName.substring(fileName.lastIndexOf("."));
        String finalName = UUID.randomUUID().toString() + extension;

        String path = request.getServletContext().getRealPath("/assets/img/");
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        filePart.write(path + File.separator + finalName);
        return finalName;
    }

    private int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String content : contentDisp.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
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

    private void applyPriceRange(Product product) {
        List<ProductVariant> variants = product.getVariants();
        if (variants == null || variants.isEmpty()) {
            product.setPrice(0);
            product.setMaxPrice(0);
            return;
        }

        double minPrice = variants.stream()
                .map(ProductVariant::getPrice)
                .filter(price -> price != null)
                .min(Comparator.naturalOrder())
                .map(price -> price.doubleValue())
                .orElse(0.0);

        double maxPrice = variants.stream()
                .map(ProductVariant::getPrice)
                .filter(price -> price != null)
                .max(Comparator.naturalOrder())
                .map(price -> price.doubleValue())
                .orElse(minPrice);

        product.setPrice(minPrice);
        product.setMaxPrice(maxPrice);
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getServletPath() != null && request.getServletPath().startsWith("/staff/");
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

        String redirectUrl = request.getContextPath() + (isStaffRoute(request) ? STAFF_LIST_URL : ADMIN_LIST_URL);
        StringBuilder query = new StringBuilder();

        if (keyword != null) {
            appendQueryParam(query, "keyword", keyword);
        }
        if (brandId != null) {
            appendQueryParam(query, "brandId", brandId);
        }
        if (categoryId != null) {
            appendQueryParam(query, "categoryId", categoryId);
        }
        if (status != null) {
            appendQueryParam(query, "status", status);
        }

        return query.length() == 0 ? redirectUrl : redirectUrl + "?" + query;
    }

    private void appendQueryParam(StringBuilder query, String key, String value) throws IOException {
        if (query.length() > 0) {
            query.append("&");
        }
        query.append(key)
                .append("=")
                .append(URLEncoder.encode(value, "UTF-8"));
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


