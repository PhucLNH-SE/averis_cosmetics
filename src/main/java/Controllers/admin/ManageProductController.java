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
        
        // Tránh lỗi NullPointerException khi gọi action="delete"
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

        // 2. Xử lý file ảnh
        String finalImageName = null;
        try {
            Part filePart = request.getPart("image");
            if (filePart != null) {
                String fileName = getFileName(filePart);
                if (fileName != null && !fileName.isEmpty()) {
                    String extension = fileName.substring(fileName.lastIndexOf("."));
                    finalImageName = UUID.randomUUID().toString() + extension;

                    String savePath = request.getServletContext().getRealPath("/")
                            + "assets" + File.separator
                            + "img" + File.separator
                            + "products" + File.separator;

                    File fileSaveDir = new File(savePath);
                    if (!fileSaveDir.exists()) {
                        fileSaveDir.mkdirs();
                    }
                    filePart.write(savePath + finalImageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Xử lý action
        if ("add".equals(action)) {
            // ---> MỚI THÊM: Hứng giá trị price từ form Add Product
            double price = 0;
            try {
                String priceStr = request.getParameter("price");
                if (priceStr != null && !priceStr.isEmpty()) {
                    price = Double.parseDouble(priceStr);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            
            // Gọi hàm insertProduct có chứa tham số price (nhớ update lại ProductDAO như mình đã dặn nha)
            dao.insertProduct(name, desc, bid, cid, status, finalImageName, price);

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("productId"));
            dao.updateProduct(id, name, desc, bid, cid, status, finalImageName);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("productId"));
            dao.deleteProduct(id);
        }

        // Redirect về trang quản lý
        response.sendRedirect("manage-product");
    }
}