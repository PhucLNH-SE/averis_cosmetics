package Controllers.admin;

import DALs.ProductDAO;
import Model.Product;
import Model.Brand;
import Model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

public class ManageProductController extends HttpServlet {

    // ================== GET ==================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "delete":
                handleDelete(request, response, dao, session);
                return;

            case "show":
                handleShow(request, response, dao, session);
                return;

            default:
                loadProductPage(request, response, dao, session);
        }
    }

    // ================== POST ==================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        ProductDAO dao = new ProductDAO();
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        if (action == null) action = "";

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
                response.sendRedirect(request.getContextPath() + "/admin/manage-product");
        }
    }

    // ================== LOAD PAGE ==================
    private void loadProductPage(HttpServletRequest request, HttpServletResponse response,
                                ProductDAO dao, HttpSession session)
            throws ServletException, IOException {

        // Flash message
        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }

        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }

        String keyword = trimToNull(request.getParameter("keyword"));
        String brandId = trimToNull(request.getParameter("brandId"));
        String categoryId = trimToNull(request.getParameter("categoryId"));
        String status = trimToNull(request.getParameter("status"));

        List<Product> listP = dao.getProductsForAdminWithImportPrice(keyword, brandId, categoryId, status);
        List<Brand> listB = dao.getAllBrands();
        List<Category> listC = dao.getAllCategories();

        int activeCount = 0;
        for (Product product : listP) {
            if (product.isStatus()) {
                activeCount++;
            }
        }

        request.setAttribute("listP", listP);
        request.setAttribute("listB", listB);
        request.setAttribute("listC", listC);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedBrandId", brandId);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("resultCount", listP.size());
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("inactiveCount", listP.size() - activeCount);

        request.setAttribute("currentView", "products");
        request.setAttribute("contentPage", "/views/admin/partials/manage-product-content.jsp");

        request.getRequestDispatcher("/views/admin/admin-panel.jsp")
                .forward(request, response);
    }

    // ================== DELETE ==================
    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
                              ProductDAO dao, HttpSession session)
            throws IOException {

        int id = parseInt(request.getParameter("productId"));

        if (dao.hideProduct(id)) {
            session.setAttribute("successMsg", "Đã ẩn sản phẩm thành công!");
        } else {
            session.setAttribute("errorMsg", "Lỗi: Không thể ẩn sản phẩm.");
        }

        response.sendRedirect(buildManageProductRedirect(request));
    }

    // ================== SHOW ==================
    private void handleShow(HttpServletRequest request, HttpServletResponse response,
                            ProductDAO dao, HttpSession session)
            throws IOException {

        int id = parseInt(request.getParameter("productId"));

        if (dao.showProduct(id)) {
            session.setAttribute("successMsg", "Đã hiển thị lại sản phẩm thành công!");
        } else {
            session.setAttribute("errorMsg", "Lỗi: Không thể hiển thị lại sản phẩm.");
        }

        response.sendRedirect(buildManageProductRedirect(request));
    }

    // ================== ADD ==================
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
        double importPrice = parseDouble(request.getParameter("importPrice"));

        dao.insertProduct(name, desc, bid, cid, status, image, price, 0, importPrice);

        session.setAttribute("successMsg", "Thêm sản phẩm thành công!");
        response.sendRedirect(buildManageProductRedirect(request));
    }

    // ================== UPDATE ==================
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

        dao.updateProduct(id, name, desc, bid, cid, status, image);

        session.setAttribute("successMsg", "Cập nhật sản phẩm thành công!");
        response.sendRedirect(buildManageProductRedirect(request));
    }

    // ================== UPLOAD IMAGE ==================
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
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String finalName = UUID.randomUUID().toString() + extension;

        String path = request.getServletContext().getRealPath("/assets/img/");
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        filePart.write(path + File.separator + finalName);

        return finalName;
    }

    // ================== UTILS ==================
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String buildManageProductRedirect(HttpServletRequest request) throws IOException {
        String keyword = trimToNull(request.getParameter("keyword"));
        String brandId = trimToNull(request.getParameter("brandId"));
        String categoryId = trimToNull(request.getParameter("categoryId"));
        String status = trimToNull(request.getParameter("status"));
        String redirectUrl = request.getContextPath() + "/admin/manage-product";
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
}
