package DALs;

import Utils.DBContext;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import java.util.Map;

import Model.Brand;

import Model.Category;

import Model.Product;

import Model.ProductImage;

import Model.ProductVariant;

public class ProductDAO extends DBContext {

    public List<String> getAllBrandNames() {

        List<String> brands = new ArrayList<>();

        String sql = "SELECT DISTINCT name FROM Brand WHERE status = 1 ORDER BY name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String brandName = rs.getString("name");

                if (brandName != null && !brandName.trim().isEmpty()) {

                    brands.add(brandName);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return brands;

    }

    public List<String> getAllCategoryNames() {

        List<String> categories = new ArrayList<>();

        String sql = "SELECT DISTINCT name FROM Category WHERE status = 1 ORDER BY name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String categoryName = rs.getString("name");

                if (categoryName != null && !categoryName.trim().isEmpty()) {

                    categories.add(categoryName);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return categories;

    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        // 1. Cập nhật câu SQL: Thêm MIN(price), MAX(price) và GROUP BY
        String sql = "SELECT "
                + "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main, "
                + "  MIN(pv.price) AS min_price, MAX(pv.price) AS max_price "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "LEFT JOIN Product_Variant pv ON p.product_id = pv.product_id AND pv.status = 1 "
                + "GROUP BY p.product_id, p.name, p.description, p.status, "
                + "         b.brand_id, b.name, b.status, "
                + "         c.category_id, c.name, c.status, "
                + "         pi.image_id, pi.image_url, pi.is_main "
                + "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            Map<Integer, Product> productMap = new HashMap<>();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                Product p = productMap.get(productId);

                if (p == null) {
                    p = new Product();
                    p.setProductId(productId);
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setStatus(rs.getBoolean("status"));
                    
                    // 2. Set giá trị minPrice và maxPrice từ SQL vào Object
                    p.setPrice(rs.getDouble("min_price")); 
                    p.setMaxPrice(rs.getDouble("max_price"));

                    // ... (Đoạn set Brand, Category, Variants, MainImage giữ nguyên như cũ) ...
                    p.setMainImage(rs.getString("image_url"));
                    
                    Brand b = new Brand();
                    b.setBrandId(rs.getInt("brand_id"));
                    b.setName(rs.getString("brand_name"));
                    b.setStatus(rs.getBoolean("brand_status"));
                    p.setBrand(b);

                    Category c = new Category();
                    c.setCategoryId(rs.getInt("category_id"));
                    c.setName(rs.getString("category_name"));
                    c.setStatus(rs.getBoolean("category_status"));
                    p.setCategory(c);

                    p.setVariants(getProductVariants(productId));
                    p.setImages(new ArrayList<>());
                    
                    productMap.put(productId, p);
                }

                // ... (Đoạn xử lý Image List giữ nguyên như cũ) ...
                int imageId = rs.getInt("image_id");
                if (!rs.wasNull()) {
                    ProductImage img = new ProductImage();
                    img.setImageId(imageId);
                    img.setProductId(productId);
                    img.setImage(rs.getString("image_url"));
                    img.setMain(rs.getBoolean("is_main"));
                    p.getImages().add(img);

                    if (rs.getBoolean("is_main") && p.getMainImage() == null) {
                        p.setMainImage(rs.getString("image_url"));
                    }
                }
            }

            for (Product p : productMap.values()) {
                if (p.getMainImage() == null && !p.getImages().isEmpty()) {
                    p.setMainImage(p.getImages().get(0).getImage());
                }
            }
            list.addAll(productMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Product getProductById(int productId) {

        String sql
                = "SELECT "
                + "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "WHERE p.product_id = ? "
                + "ORDER BY pi.is_main DESC, pi.image_id ASC";

        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                Product p = null;

                Map<Integer, ProductImage> imageMap = new HashMap<>();

                while (rs.next()) {

                    if (p == null) {

                        p = new Product();

                        p.setProductId(rs.getInt("product_id"));

                        p.setName(rs.getString("name"));

                        p.setDescription(rs.getString("description"));

                        p.setStatus(rs.getBoolean("status"));

                        Brand b = new Brand();

                        b.setBrandId(rs.getInt("brand_id"));

                        b.setName(rs.getString("brand_name"));

                        b.setStatus(rs.getBoolean("brand_status"));

                        p.setBrand(b);

                        Category c = new Category();

                        c.setCategoryId(rs.getInt("category_id"));

                        c.setName(rs.getString("category_name"));

                        c.setStatus(rs.getBoolean("category_status"));

                        p.setCategory(c);

                        // Lấy các biến thể của sản phẩm
                        p.setVariants(getProductVariants(productId));

                    }

                    // Process images
                    int imageId = rs.getInt("image_id");

                    if (!rs.wasNull() && !imageMap.containsKey(imageId)) {

                        ProductImage img = new ProductImage();

                        img.setImageId(rs.getInt("image_id"));

                        img.setProductId(rs.getInt("product_id"));

                        img.setImage(rs.getString("image_url"));

                        img.setMain(rs.getBoolean("is_main"));

                        imageMap.put(imageId, img);

                    }

                }

                if (p != null) {

                    // Set images
                    List<ProductImage> images = new ArrayList<>(imageMap.values());

                    p.setImages(images);

                    // Set main image
                    String mainImage = null;

                    for (ProductImage img : images) {

                        if (img.isMain()) {

                            mainImage = img.getImage();

                            break;

                        }

                    }

                    if (mainImage == null && !images.isEmpty()) {

                        mainImage = images.get(0).getImage();

                    }

                    p.setMainImage(mainImage);

                }

                return p;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    private List<ProductVariant> getProductVariants(int productId) {

        List<ProductVariant> variants = new ArrayList<>();

        String sql = "SELECT variant_id, product_id, variant_name, price, stock, status FROM Product_Variant WHERE product_id = ? AND status = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ProductVariant variant = new ProductVariant();

                    variant.setVariantId(rs.getInt("variant_id"));

                    variant.setProductId(rs.getInt("product_id"));

                    variant.setVariantName(rs.getString("variant_name"));

                    variant.setPrice(rs.getBigDecimal("price"));

                    variant.setStock(rs.getInt("stock"));

                    variant.setStatus(rs.getBoolean("status"));

                    variants.add(variant);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return variants;

    }

    // Lấy dữ liệu variant (giá, tên, ảnh...) theo variantId để hiển thị trong Cart
    public ProductVariant getVariantById(int variantId) {

        // Cập nhật SQL: JOIN bảng Product để lấy tên, SELECT lồng để lấy 1 ảnh chính (TOP 1)
        String sql = "SELECT v.variant_id, v.product_id, v.variant_name, v.price, v.stock, v.status, "
                + "       p.name AS product_name, "
                + "       (SELECT TOP 1 image_url FROM Product_Image pi WHERE pi.product_id = p.product_id AND pi.is_main = 1) AS image_url "
                + "FROM Product_Variant v "
                + "JOIN Product p ON v.product_id = p.product_id "
                + "WHERE v.variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    ProductVariant v = new ProductVariant();

                    // 1. Các trường cũ
                    v.setVariantId(rs.getInt("variant_id"));

                    v.setProductId(rs.getInt("product_id"));

                    v.setVariantName(rs.getString("variant_name"));

                    v.setPrice(rs.getBigDecimal("price"));

                    v.setStock(rs.getInt("stock"));

                    v.setStatus(rs.getBoolean("status"));

                    // 2. Các trường MỚI (quan trọng để hiện thị Cart)
                    v.setProductName(rs.getString("product_name"));

                    v.setImageUrl(rs.getString("image_url"));

                    return v;

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    public List<Product> searchProducts(String keyword) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT "
                + "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "WHERE p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ? "
                + "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            String searchParam = "%" + keyword + "%";

            ps.setString(1, searchParam);

            ps.setString(2, searchParam);

            ps.setString(3, searchParam);

            try (ResultSet rs = ps.executeQuery()) {

                Map<Integer, Product> productMap = new HashMap<>();

                while (rs.next()) {

                    int productId = rs.getInt("product_id");

                    Product p = productMap.get(productId);

                    if (p == null) {

                        p = new Product();

                        p.setProductId(productId);

                        p.setName(rs.getString("name"));

                        p.setDescription(rs.getString("description"));

                        p.setStatus(rs.getBoolean("status"));

                        Brand b = new Brand();

                        b.setBrandId(rs.getInt("brand_id"));

                        b.setName(rs.getString("brand_name"));

                        b.setStatus(rs.getBoolean("brand_status"));

                        p.setBrand(b);

                        Category c = new Category();

                        c.setCategoryId(rs.getInt("category_id"));

                        c.setName(rs.getString("category_name"));

                        c.setStatus(rs.getBoolean("category_status"));

                        p.setCategory(c);

                        // Fetch variants for this product
                        p.setVariants(getProductVariants(productId));

                        // Initialize images list
                        p.setImages(new ArrayList<>());

                        productMap.put(productId, p);

                    }

                    // Process image if exists
                    int imageId = rs.getInt("image_id");

                    if (!rs.wasNull()) {

                        ProductImage img = new ProductImage();

                        img.setImageId(imageId);

                        img.setProductId(productId);

                        img.setImage(rs.getString("image_url"));

                        img.setMain(rs.getBoolean("is_main"));

                        p.getImages().add(img);

                        // Set main image if this is the main image
                        if (rs.getBoolean("is_main") && p.getMainImage() == null) {

                            p.setMainImage(rs.getString("image_url"));

                        }

                    }

                }

                // Finalize main images for products that don't have one set
                for (Product p : productMap.values()) {

                    if (p.getMainImage() == null && !p.getImages().isEmpty()) {

                        p.setMainImage(p.getImages().get(0).getImage());

                    }

                }

                list.addAll(productMap.values());

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    public List<Product> searchProductsForAutoSuggest(String keyword) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT TOP 10 "
                + // Limit to top 10 results for auto-suggest
                "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "WHERE p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ? "
                + "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            String searchParam = "%" + keyword + "%";

            ps.setString(1, searchParam);

            ps.setString(2, searchParam);

            ps.setString(3, searchParam);

            try (ResultSet rs = ps.executeQuery()) {

                Map<Integer, Product> productMap = new HashMap<>();

                while (rs.next()) {

                    int productId = rs.getInt("product_id");

                    Product p = productMap.get(productId);

                    if (p == null) {

                        p = new Product();

                        p.setProductId(productId);

                        p.setName(rs.getString("name"));

                        p.setDescription(rs.getString("description"));

                        p.setStatus(rs.getBoolean("status"));

                        Brand b = new Brand();

                        b.setBrandId(rs.getInt("brand_id"));

                        b.setName(rs.getString("brand_name"));

                        b.setStatus(rs.getBoolean("brand_status"));

                        p.setBrand(b);

                        Category c = new Category();

                        c.setCategoryId(rs.getInt("category_id"));

                        c.setName(rs.getString("category_name"));

                        c.setStatus(rs.getBoolean("category_status"));

                        p.setCategory(c);

                        // Initialize images list
                        p.setImages(new ArrayList<>());

                        productMap.put(productId, p);

                    }

                    // Process image if exists
                    int imageId = rs.getInt("image_id");

                    if (!rs.wasNull()) {

                        ProductImage img = new ProductImage();

                        img.setImageId(imageId);

                        img.setProductId(productId);

                        img.setImage(rs.getString("image_url"));

                        img.setMain(rs.getBoolean("is_main"));

                        p.getImages().add(img);

                        // Set main image if this is the main image
                        if (rs.getBoolean("is_main") && p.getMainImage() == null) {

                            p.setMainImage(rs.getString("image_url"));

                        }

                    }

                }

                // Finalize main images for products that don't have one set
                for (Product p : productMap.values()) {

                    if (p.getMainImage() == null && !p.getImages().isEmpty()) {

                        p.setMainImage(p.getImages().get(0).getImage());

                    }

                }

                list.addAll(productMap.values());

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    public List<Brand> getAllBrands() {

        List<Brand> list = new ArrayList<>();

        String sql = "SELECT * FROM Brand";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Brand b = new Brand();

                b.setBrandId(rs.getInt("brand_id"));

                b.setName(rs.getString("name"));

                b.setStatus(rs.getBoolean("status"));

                list.add(b);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    public List<Category> getAllCategories() {

        List<Category> list = new ArrayList<>();

        String sql = "SELECT * FROM Category";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Category c = new Category();

                c.setCategoryId(rs.getInt("category_id"));

                c.setName(rs.getString("name"));

                c.setStatus(rs.getBoolean("status"));

                list.add(c);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    // Đã khôi phục tham số 'double price' và thêm logic tạo Variant mặc định
    public void insertProduct(String name, String description, int brandId, int categoryId, boolean status, String imageName, double price) {
        String insertProductSql = "INSERT INTO Product (name, description, brand_id, category_id, status) VALUES (?, ?, ?, ?, ?)";
        String insertImageSql = "INSERT INTO Product_Image (product_id, image_url, is_main) VALUES (?, ?, 1)";
        // SQL tạo Variant mặc định với tên 'Standard' và số lượng tồn kho mặc định là 100
        String insertVariantSql = "INSERT INTO Product_Variant (product_id, variant_name, price, stock, status) VALUES (?, 'Standard', ?, 100, 1)";

        try {
            connection.setAutoCommit(false);
            
            // 1. Thêm Product
            PreparedStatement psP = connection.prepareStatement(insertProductSql, java.sql.Statement.RETURN_GENERATED_KEYS);
            psP.setString(1, name);
            psP.setString(2, description);
            psP.setInt(3, brandId);
            psP.setInt(4, categoryId);
            psP.setBoolean(5, status);
            psP.executeUpdate();

            ResultSet rs = psP.getGeneratedKeys();
            int newId = 0;

            if (rs.next()) {
                newId = rs.getInt(1);
            }

            if (newId > 0) {
                // 2. Thêm ảnh chính (nếu có)
                if (imageName != null && !imageName.isEmpty()) {
                    PreparedStatement psI = connection.prepareStatement(insertImageSql);
                    psI.setInt(1, newId);
                    psI.setString(2, imageName);
                    psI.executeUpdate();
                }
                
                // 3. Tự động thêm Variant mặc định
                PreparedStatement psV = connection.prepareStatement(insertVariantSql);
                psV.setInt(1, newId);
                psV.setDouble(2, price);
                psV.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
    }

// Bỏ tham số 'double price'
    public void updateProduct(int id, String name, String description, int brandId, int categoryId, boolean status, String imageName) {
        // Bỏ set price trong SQL
        String updateProductSql = "UPDATE Product SET name = ?, description = ?, brand_id = ?, category_id = ?, status = ? WHERE product_id = ?";
        String updateImgSql = "UPDATE Product_Image SET image_url = ? WHERE product_id = ? AND is_main = 1";

        try {
            connection.setAutoCommit(false);
            PreparedStatement psP = connection.prepareStatement(updateProductSql);
            psP.setString(1, name);
            psP.setString(2, description);
            psP.setInt(3, brandId);
            psP.setInt(4, categoryId);
            psP.setBoolean(5, status);
            psP.setInt(6, id);
            psP.executeUpdate();

            // 2. Cập nhật ảnh (chỉ khi người dùng có chọn ảnh mới)
            if (imageName != null && !imageName.isEmpty()) {

                PreparedStatement psI = connection.prepareStatement(updateImgSql);

                psI.setString(1, imageName);

                psI.setInt(2, id);

                psI.executeUpdate();

            }

            connection.commit();

            connection.setAutoCommit(true);

        } catch (Exception e) {

            try {
                connection.rollback();
            } catch (Exception ex) {
            }

            e.printStackTrace();

        }

    }

    public void deleteProduct(int id) {
        // Phải xóa ngược từ các bảng có chứa khóa ngoại trỏ đến Product và Variant
        String deleteReviewSql = "DELETE FROM Review WHERE product_id = ?";
        String deleteCartDetailSql = "DELETE FROM Cart_Detail WHERE variant_id IN (SELECT variant_id FROM Product_Variant WHERE product_id = ?)";
        String deleteOrderDetailSql = "DELETE FROM Order_Detail WHERE variant_id IN (SELECT variant_id FROM Product_Variant WHERE product_id = ?)";
        String deleteImageSql = "DELETE FROM Product_Image WHERE product_id = ?";
        String deleteVariantSql = "DELETE FROM Product_Variant WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM Product WHERE product_id = ?";

        try {
            // Tắt auto-commit để gom chung thành 1 Transaction an toàn
            connection.setAutoCommit(false);

            // 1. Xóa trong bảng Review
            try (PreparedStatement ps = connection.prepareStatement(deleteReviewSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 2. Xóa trong bảng Cart_Detail (Giỏ hàng của khách)
            try (PreparedStatement ps = connection.prepareStatement(deleteCartDetailSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 3. Xóa trong bảng Order_Detail (Chi tiết đơn hàng)
            try (PreparedStatement ps = connection.prepareStatement(deleteOrderDetailSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 4. Xóa ảnh sản phẩm
            try (PreparedStatement ps = connection.prepareStatement(deleteImageSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 5. Xóa các biến thể (variants) của sản phẩm
            try (PreparedStatement ps = connection.prepareStatement(deleteVariantSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 6. Cuối cùng mới xóa sản phẩm chính
            try (PreparedStatement ps = connection.prepareStatement(deleteProductSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Nếu qua được cả 6 bước không lỗi lầm gì thì lưu thay đổi
            connection.commit();

        } catch (Exception e) {
            // Nếu có lỗi, hoàn tác lại toàn bộ để bảo toàn dữ liệu
            try {
                if (connection != null) connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.out.println("Lỗi khi xóa sản phẩm ID " + id + ": " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    


public List<ProductVariant> getAllProductQuantity() {

        List<ProductVariant> list = new ArrayList<>();

        String sql =
            "SELECT  " +
            "    pv.variant_id, " +
                "    pv.variant_name, " +
            "    p.product_id, " +
            "    p.name AS product_name, " +
            "    c.name AS category_name, " +
            "    pi.image_url, " +
            "    pv.price, " +
            "    p.status, " +
            "    pv.stock " +
            "FROM Product p " +
            "JOIN Category c  " +
            "    ON p.category_id = c.category_id " +
            "LEFT JOIN Product_Image pi  " +
            "    ON p.product_id = pi.product_id  " +
            "   AND pi.is_main = 1 " +
            "JOIN Product_Variant pv  " +
            "    ON p.product_id = pv.product_id";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ProductVariant v = new ProductVariant();

                v.setVariantId(rs.getInt("variant_id"));
                v.setVariantName(rs.getString("variant_name"));
                v.setProductId(rs.getInt("product_id"));

                // 3 field bạn vừa thêm trong ProductVariant
                v.setProductName(rs.getString("product_name"));
                v.setCategoryName(rs.getString("category_name"));
               String img = rs.getString("image_url");

if (img != null && !img.startsWith("assets/")) {
    img = "assets/img/" + img;
}

v.setImageUrl(img);

                v.setPrice(rs.getBigDecimal("price"));
                v.setStock(rs.getInt("stock"));
                v.setStatus(rs.getBoolean("status"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public void updateStock(int variantId, int stock) {

    String sql = "UPDATE Product_Variant SET stock = ? WHERE variant_id = ?";

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, stock);
        ps.setInt(2, variantId);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}