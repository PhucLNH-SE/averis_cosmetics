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
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 15    // 15MB
)
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

        List<Product> listP = dao.getAllProducts();
        List<Brand> listB = dao.getAllBrands();
        List<Category> listC = dao.getAllCategories();

        request.setAttribute("listP", listP);
        request.setAttribute("listB", listB);
        request.setAttribute("listC", listC);

        request.getRequestDispatcher("/views/admin/manage-product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        ProductDAO dao = new ProductDAO();

        // 1. Lấy các thông tin chung
        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        
        int bid = 0;
        int cid = 0;
        boolean status = false;
        
        if (!"delete".equals(action)) {
            try {
                bid = Integer.parseInt(request.getParameter("brandId"));
                cid = Integer.parseInt(request.getParameter("categoryId"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            status = request.getParameter("status") != null;
        }

        // 2. Xử lý file ảnh (Đã nâng cấp: Lưu kép chống mất ảnh cực mạnh)
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

                        // --- BẮT ĐẦU ĐOẠN FIX ĐƯỜNG DẪN ---
                        
                        // Lấy đường dẫn thực tế của Server
                        String realPath = request.getServletContext().getRealPath("/");

                        // Đồng bộ toàn bộ dấu gạch chéo về dạng chuẩn "/"
                        realPath = realPath.replace("\\", "/");

                        // Tạo đường dẫn ảo của Tomcat
                        String tomcatPath = realPath + "assets/img/";

                        // Xử lý đường dẫn Source code vĩnh viễn
                        String sourcePath = "";
                        if (realPath.contains("build/web")) {
                            // Dành cho project chuẩn Ant (NetBeans mặc định)
                            sourcePath = realPath.replace("build/web", "web") + "assets/img/";
                        } else if (realPath.contains("target/")) {
                            // Dành cho project xài Maven
                            sourcePath = realPath.substring(0, realPath.indexOf("target/")) + "src/main/webapp/assets/img/";
                        } else {
                            // Backup fallback
                            sourcePath = realPath + "assets/img/";
                        }

                        // Trả lại dấu gạch chéo chuẩn của hệ điều hành hiện tại
                        tomcatPath = tomcatPath.replace("/", File.separator);
                        sourcePath = sourcePath.replace("/", File.separator);

                        // In ra Console để theo dõi
                        System.out.println("=== DEBUG LOG ĐƯỜNG DẪN ẢNH ===");
                        System.out.println("Tomcat Path: " + tomcatPath);
                        System.out.println("Source Path: " + sourcePath);

                        // --- KẾT THÚC ĐOẠN FIX ĐƯỜNG DẪN ---

                        // Tạo thư mục nếu chưa tồn tại
                        File tomcatDir = new File(tomcatPath);
                        if (!tomcatDir.exists()) tomcatDir.mkdirs();
                        
                        File sourceDir = new File(sourcePath);
                        if (!sourceDir.exists()) sourceDir.mkdirs();

                        // Bước A: Lưu ảnh vào máy chủ Tomcat ảo
                        String fullTomcatFilePath = tomcatPath + finalImageName;
                        filePart.write(fullTomcatFilePath);

                        // Bước B: Copy ngược ảnh về thư mục gốc của project
                        try {
                            String fullSourceFilePath = sourcePath + finalImageName;
                            java.nio.file.Files.copy(
                                new java.io.File(fullTomcatFilePath).toPath(),
                                new java.io.File(fullSourceFilePath).toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                            );
                            System.out.println("-> OK: Đã copy ảnh vĩnh viễn vào source!");
                        } catch (Exception copyEx) {
                            System.out.println("-> LỖI: Không thể copy về source.");
                            copyEx.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Xử lý action
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

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("productId"));
            dao.updateProduct(id, name, desc, bid, cid, status, finalImageName);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("productId"));
            dao.deleteProduct(id);
        }

        // Redirect về trang quản lý (Chuẩn PRG)
        response.sendRedirect("manage-product");
    }
}