package Controllers.admin;

import DALs.ProductDAO;
import Model.Product;
import Model.Brand;
import Model.Category;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

public class ManageProductController extends HttpServlet {

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String content : contentDisp.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();
        HttpSession session = request.getSession();
        
        String action = request.getParameter("action");
        
        // --- XỬ LÝ DELETE (ẨN SẢN PHẨM) ---
        if ("delete".equals(action)) {
            String idStr = request.getParameter("productId");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    if (dao.hideProduct(id)) { 
                        session.setAttribute("successMsg", "Đã ẩn sản phẩm thành công!");
                    } else {
                        session.setAttribute("errorMsg", "Lỗi: Không thể ẩn sản phẩm.");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/manage-product");
            return; 
        }
        
        // --- XỬ LÝ SHOW (HIỆN LẠI SẢN PHẨM) ---
        if ("show".equals(action)) {
            String idStr = request.getParameter("productId");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    if (dao.showProduct(id)) { 
                        session.setAttribute("successMsg", "Đã hiển thị lại sản phẩm thành công!");
                    } else {
                        session.setAttribute("errorMsg", "Lỗi: Không thể hiển thị lại sản phẩm.");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/manage-product");
            return; 
        }
        // ------------------------------

        // Đẩy Flash Message ra View
        if (session.getAttribute("successMsg") != null) {
            request.setAttribute("successMsg", session.getAttribute("successMsg"));
            session.removeAttribute("successMsg");
        }
        if (session.getAttribute("errorMsg") != null) {
            request.setAttribute("errorMsg", session.getAttribute("errorMsg"));
            session.removeAttribute("errorMsg");
        }

        // Render giao diện
        List<Product> listP = dao.getAllProducts();
        List<Brand> listB = dao.getAllBrands();
        List<Category> listC = dao.getAllCategories();

        request.setAttribute("listP", listP);
        request.setAttribute("listB", listB);
        request.setAttribute("listC", listC);
        request.setAttribute("currentView", "products");
        request.setAttribute("contentPage", "/views/admin/partials/manage-product-content.jsp");

        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        ProductDAO dao = new ProductDAO();
        HttpSession session = request.getSession();

        // Xử lý action từ form submit trực tiếp (phòng hờ)
        if ("delete".equals(action)) {
            String idStr = request.getParameter("productId");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    if (dao.hideProduct(id)) {
                        session.setAttribute("successMsg", "Đã ẩn sản phẩm thành công!");
                    } else {
                        session.setAttribute("errorMsg", "Lỗi: Không thể ẩn sản phẩm.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/manage-product");
            return;
        }
        
        if ("show".equals(action)) {
            String idStr = request.getParameter("productId");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    if (dao.showProduct(id)) {
                        session.setAttribute("successMsg", "Đã hiển thị lại sản phẩm thành công!");
                    } else {
                        session.setAttribute("errorMsg", "Lỗi: Không thể hiển thị lại sản phẩm.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/manage-product");
            return;
        }

        // 1. Lấy các thông tin chung
        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        
        int bid = 0;
        int cid = 0;
        boolean status = false;
        
        try {
            String brandStr = request.getParameter("brandId");
            String catStr = request.getParameter("categoryId");
            if (brandStr != null) bid = Integer.parseInt(brandStr);
            if (catStr != null) cid = Integer.parseInt(catStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        status = request.getParameter("status") != null;

        // 2. Xử lý file ảnh
        String finalImageName = null;
        String contentType = request.getContentType();
        
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            try {
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = getFileName(filePart);
                    if (fileName != null && !fileName.isEmpty()) {
                        String extension = fileName.substring(fileName.lastIndexOf("."));
                        finalImageName = UUID.randomUUID().toString() + extension;

                        String realPath = request.getServletContext().getRealPath("/");
                        realPath = realPath.replace("\\", "/");

                        String tomcatPath = realPath + "assets/img/";

                        String sourcePath = "";
                        if (realPath.contains("build/web")) {
                            sourcePath = realPath.replace("build/web", "web") + "assets/img/";
                        } else if (realPath.contains("target/")) {
                            sourcePath = realPath.substring(0, realPath.indexOf("target/")) + "src/main/webapp/assets/img/";
                        } else {
                            sourcePath = realPath + "assets/img/";
                        }

                        tomcatPath = tomcatPath.replace("/", File.separator);
                        sourcePath = sourcePath.replace("/", File.separator);

                        File tomcatDir = new File(tomcatPath);
                        if (!tomcatDir.exists()) tomcatDir.mkdirs();
                        
                        File sourceDir = new File(sourcePath);
                        if (!sourceDir.exists()) sourceDir.mkdirs();

                        String fullTomcatFilePath = tomcatPath + finalImageName;
                        filePart.write(fullTomcatFilePath);

                        try {
                            String fullSourceFilePath = sourcePath + finalImageName;
                            java.nio.file.Files.copy(
                                new java.io.File(fullTomcatFilePath).toPath(),
                                new java.io.File(fullSourceFilePath).toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                            );
                        } catch (Exception copyEx) {
                            copyEx.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Xử lý action Add và Update
        if ("add".equals(action)) {
            double price = 0;
            try {
                String priceStr = request.getParameter("price");
                if (priceStr != null && !priceStr.isEmpty()) {
                    price = Double.parseDouble(priceStr);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            dao.insertProduct(name, desc, bid, cid, status, finalImageName, price);
            session.setAttribute("successMsg", "Thêm sản phẩm thành công!");

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("productId"));
            dao.updateProduct(id, name, desc, bid, cid, status, finalImageName);
            session.setAttribute("successMsg", "Cập nhật sản phẩm thành công!");
        }

        // Redirect về trang quản lý bằng đường dẫn tuyệt đối để tránh lỗi
        response.sendRedirect(request.getContextPath() + "/admin/manage-product");
    }
}
