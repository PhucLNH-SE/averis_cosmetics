package DALs;

import Model.Brand;
import Model.Category;
import Model.Product;
import Model.ProductImage;
import Model.ProductVariant;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO extends DBContext {

    public List<String> getAllBrandNames() {
        List<String> brands = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM Brand WHERE status = 1 ORDER BY name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Map<Integer, Product> productMap = new HashMap<>();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                Product product = productMap.get(productId);

                if (product == null) {
                    product = mapBaseProduct(rs, productId, true);
                    product.setPrice(rs.getDouble("min_price"));
                    product.setMaxPrice(rs.getDouble("max_price"));
                    product.setVariants(getProductVariants(productId));
                    productMap.put(productId, product);
                }

                addImageFromRow(rs, product, productId);
            }

            finalizeMainImages(productMap);
            list.addAll(productMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Integer> getTopSellingProductIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT p.product_id, SUM(od.quantity) AS total_sold "
        + "FROM Orders o "
        + "JOIN Order_Detail od ON o.order_id = od.order_id "
        + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
        + "JOIN Product p ON pv.product_id = p.product_id "
        + "WHERE o.order_status <> 'CANCELLED' AND p.status = 1 " // <-- ĐÃ THÊM Ở ĐÂY
        + "GROUP BY p.product_id "
        + "ORDER BY total_sold DESC, p.product_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("product_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    public Product getProductById(int productId) {
        String sql = "SELECT "
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

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                Product product = null;
                Map<Integer, ProductImage> imageMap = new HashMap<>();

                while (rs.next()) {
                    if (product == null) {
                        product = mapBaseProduct(rs, productId, true);
                        product.setVariants(getProductVariants(productId));
                    }

                    int imageId = rs.getInt("image_id");
                    if (!rs.wasNull() && !imageMap.containsKey(imageId)) {
                        ProductImage img = new ProductImage();
                        img.setImageId(imageId);
                        img.setProductId(productId);
                        img.setImage(rs.getString("image_url"));
                        img.setMain(rs.getBoolean("is_main"));
                        imageMap.put(imageId, img);
                    }
                }

                if (product != null) {
                    List<ProductImage> images = new ArrayList<>(imageMap.values());
                    product.setImages(images);

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

                    product.setMainImage(mainImage);
                }

                return product;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<ProductVariant> getProductVariants(int productId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT variant_id, product_id, variant_name, price, stock, status "
                + "FROM Product_Variant WHERE product_id = ? AND status = 1";

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

    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT v.variant_id, v.product_id, v.variant_name, v.price, v.stock, v.status, v.avg_cost, "
                + "       p.name AS product_name, "
                + "       (SELECT TOP 1 image_url FROM Product_Image pi "
                + "        WHERE pi.product_id = p.product_id AND pi.is_main = 1) AS image_url "
                + "FROM Product_Variant v "
                + "JOIN Product p ON v.product_id = p.product_id "
                + "WHERE v.variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductVariant variant = new ProductVariant();
                    variant.setVariantId(rs.getInt("variant_id"));
                    variant.setProductId(rs.getInt("product_id"));
                    variant.setVariantName(rs.getString("variant_name"));
                    variant.setPrice(rs.getBigDecimal("price"));
                    variant.setStock(rs.getInt("stock"));
                    variant.setStatus(rs.getBoolean("status"));
                    variant.setImportPrice(rs.getBigDecimal("avg_cost"));
                    variant.setProductName(rs.getString("product_name"));
                    variant.setImageUrl(rs.getString("image_url"));
                    return variant;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> searchProducts(String keyword) {
        String sql = "SELECT "
                + "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "WHERE (p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ?) AND p.status = 1 " // <-- ĐÃ SỬA
                + "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";
        return searchProductsByKeyword(keyword, sql, true);
    }

    public List<Product> searchProductsForAutoSuggest(String keyword) {
        String sql = "SELECT TOP 10 "
                + "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "WHERE (p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ?) AND p.status = 1 " // <-- ĐÃ SỬA
                + "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";
        return searchProductsByKeyword(keyword, sql, false);
    }

    public List<Brand> getAllBrands() {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM Brand";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Brand brand = new Brand();
                brand.setBrandId(rs.getInt("brand_id"));
                brand.setName(rs.getString("name"));
                brand.setStatus(rs.getBoolean("status"));
                list.add(brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                category.setStatus(rs.getBoolean("status"));
                list.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertProduct(String name, String description, int brandId, int categoryId,
                              boolean status, String imageName, double price, int stock, double importPrice) {
        String insertProductSql = "INSERT INTO Product (name, description, brand_id, category_id, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        String insertImageSql = "INSERT INTO Product_Image (product_id, image_url, is_main) VALUES (?, ?, 1)"; 
        String insertVariantSql = "INSERT INTO Product_Variant (product_id, variant_name, price, stock, avg_cost, status) "
                + "VALUES (?, 'Standard', ?, ?, ?, 1)";

        try {
            connection.setAutoCommit(false);

            int newId = 0;
            try (PreparedStatement psProduct = connection.prepareStatement(insertProductSql, Statement.RETURN_GENERATED_KEYS)) {
                psProduct.setString(1, name);
                psProduct.setString(2, description);
                psProduct.setInt(3, brandId);
                psProduct.setInt(4, categoryId);
                psProduct.setBoolean(5, status);
                psProduct.executeUpdate();

                try (ResultSet rs = psProduct.getGeneratedKeys()) {
                    if (rs.next()) {
                        newId = rs.getInt(1);
                    }
                }
            }

            if (newId > 0) {
                if (imageName != null && !imageName.isEmpty()) {
                    try (PreparedStatement psImage = connection.prepareStatement(insertImageSql)) {
                        psImage.setInt(1, newId);
                        psImage.setString(2, imageName);
                        psImage.executeUpdate();
                    }
                }

                try (PreparedStatement psVariant = connection.prepareStatement(insertVariantSql)) {
                    psVariant.setInt(1, newId);
                    psVariant.setDouble(2, price);
                    psVariant.setInt(3, stock);
                    psVariant.setDouble(4, importPrice);
                    psVariant.executeUpdate();
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void updateProduct(int id, String name, String description, int brandId, int categoryId,
                              boolean status, String imageName) {
        String updateProductSql = "UPDATE Product SET name = ?, description = ?, brand_id = ?, "
                + "category_id = ?, status = ? WHERE product_id = ?";
        String updateImgSql = "UPDATE Product_Image SET image_url = ? WHERE product_id = ? AND is_main = 1";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psProduct = connection.prepareStatement(updateProductSql)) {
                psProduct.setString(1, name);
                psProduct.setString(2, description);
                psProduct.setInt(3, brandId);
                psProduct.setInt(4, categoryId);
                psProduct.setBoolean(5, status);
                psProduct.setInt(6, id);
                psProduct.executeUpdate();
            }

            if (imageName != null && !imageName.isEmpty()) {
                try (PreparedStatement psImage = connection.prepareStatement(updateImgSql)) {
                    psImage.setString(1, imageName);
                    psImage.setInt(2, id);
                    psImage.executeUpdate();
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void deleteProduct(int id) {
        String deleteReviewSql = "DELETE FROM Review WHERE product_id = ?";
        String deleteCartDetailSql = "DELETE FROM Cart_Detail WHERE variant_id IN "
                + "(SELECT variant_id FROM Product_Variant WHERE product_id = ?)";
        String deleteOrderDetailSql = "DELETE FROM Order_Detail WHERE variant_id IN "
                + "(SELECT variant_id FROM Product_Variant WHERE product_id = ?)";
        String deleteImageSql = "DELETE FROM Product_Image WHERE product_id = ?";
        String deleteVariantSql = "DELETE FROM Product_Variant WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM Product WHERE product_id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(deleteReviewSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(deleteCartDetailSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(deleteOrderDetailSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(deleteImageSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(deleteVariantSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(deleteProductSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.out.println("Error deleting product ID " + id + ": " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

  
   

    private List<Product> searchProductsByKeyword(String keyword, String sql, boolean includeVariants) {
        List<Product> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchParam = "%" + keyword + "%";
            ps.setString(1, searchParam);
            ps.setString(2, searchParam);
            ps.setString(3, searchParam);

            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Product> productMap = new HashMap<>();

                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    Product product = productMap.get(productId);

                    if (product == null) {
                        product = mapBaseProduct(rs, productId, true);
                        if (includeVariants) {
                            product.setVariants(getProductVariants(productId));
                        }
                        productMap.put(productId, product);
                    }

                    addImageFromRow(rs, product, productId);
                }

                finalizeMainImages(productMap);
                list.addAll(productMap.values());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Product mapBaseProduct(ResultSet rs, int productId, boolean initImages) throws Exception {
        Product product = new Product();
        product.setProductId(productId);
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setStatus(rs.getBoolean("status"));
        product.setMainImage(rs.getString("image_url"));

        Brand brand = new Brand();
        brand.setBrandId(rs.getInt("brand_id"));
        brand.setName(rs.getString("brand_name"));
        brand.setStatus(rs.getBoolean("brand_status"));
        product.setBrand(brand);

        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("category_name"));
        category.setStatus(rs.getBoolean("category_status"));
        product.setCategory(category);

        if (initImages) {
            product.setImages(new ArrayList<>());
        }

        return product;
    }

    private void addImageFromRow(ResultSet rs, Product product, int productId) throws Exception {
        int imageId = rs.getInt("image_id");
        if (rs.wasNull()) {
            return;
        }

        ProductImage image = new ProductImage();
        image.setImageId(imageId);
        image.setProductId(productId);
        image.setImage(rs.getString("image_url"));
        image.setMain(rs.getBoolean("is_main"));
        product.getImages().add(image);

        if (rs.getBoolean("is_main") && product.getMainImage() == null) {
            product.setMainImage(rs.getString("image_url"));
        }
    }

    private void finalizeMainImages(Map<Integer, Product> productMap) {
        for (Product product : productMap.values()) {
            if (product.getMainImage() == null && product.getImages() != null && !product.getImages().isEmpty()) {
                product.setMainImage(product.getImages().get(0).getImage());
            }
        }
    }
    
    // Ẩn sản phẩm (Soft Delete) thay vì xóa hoàn toàn
    public boolean hideProduct(int productId) {
        String sql = "UPDATE Product SET status = 0 WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Hiện sản phẩm (Khôi phục sau khi ẩn)
    public boolean showProduct(int productId) {
        String sql = "UPDATE Product SET status = 1 WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    // MỚI: Dành riêng cho trang Khách (Shop / Home) - Chỉ hiển thị sản phẩm Active
    public List<Product> getAllActiveProducts() {
        List<Product> list = new ArrayList<>();
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
                + "WHERE p.status = 1 " // <-- CHỈ LẤY SẢN PHẨM KHÔNG BỊ ẨN
                + "GROUP BY p.product_id, p.name, p.description, p.status, "
                + "         b.brand_id, b.name, b.status, "
                + "         c.category_id, c.name, c.status, "
                + "         pi.image_id, pi.image_url, pi.is_main "
                + "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Map<Integer, Product> productMap = new HashMap<>();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                Product product = productMap.get(productId);

                if (product == null) {
                    product = mapBaseProduct(rs, productId, true);
                    product.setPrice(rs.getDouble("min_price"));
                    product.setMaxPrice(rs.getDouble("max_price"));
                    product.setVariants(getProductVariants(productId));
                    productMap.put(productId, product);
                }

                addImageFromRow(rs, product, productId);
            }

            finalizeMainImages(productMap);
            list.addAll(productMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    // MỚI: Dùng cho trang Product Detail của KHÁCH HÀNG (Tránh việc gõ URL truy cập sản phẩm đã ẩn)
    public Product getActiveProductById(int productId) {
        String sql = "SELECT "
                + "  p.product_id, p.name, p.description, p.status, "
                + "  b.brand_id, b.name AS brand_name, b.status AS brand_status, "
                + "  c.category_id, c.name AS category_name, c.status AS category_status, "
                + "  pi.image_id, pi.image_url, pi.is_main "
                + "FROM Product p "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id "
                + "WHERE p.product_id = ? AND p.status = 1 " // <-- Bắt buộc status = 1
                + "ORDER BY pi.is_main DESC, pi.image_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                Product product = null;
                Map<Integer, ProductImage> imageMap = new HashMap<>();

                while (rs.next()) {
                    if (product == null) {
                        product = mapBaseProduct(rs, productId, true);
                        product.setVariants(getProductVariants(productId));
                    }

                    int imageId = rs.getInt("image_id");
                    if (!rs.wasNull() && !imageMap.containsKey(imageId)) {
                        ProductImage img = new ProductImage();
                        img.setImageId(imageId);
                        img.setProductId(productId);
                        img.setImage(rs.getString("image_url"));
                        img.setMain(rs.getBoolean("is_main"));
                        imageMap.put(imageId, img);
                    }
                }

                if (product != null) {
                    List<ProductImage> images = new ArrayList<>(imageMap.values());
                    product.setImages(images);

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

                    product.setMainImage(mainImage);
                }

                return product;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // =========================================================================
    // CÁC HÀM MỚI THÊM ĐỂ LẤY GIÁ NHẬP (KHÔNG ẢNH HƯỞNG CODE CŨ CỦA TEAM)
    // =========================================================================

    public List<Product> getAllProductsWithImportPrice() {
        List<Product> list = new ArrayList<>();
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

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Map<Integer, Product> productMap = new HashMap<>();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                Product product = productMap.get(productId);

                if (product == null) {
                    product = mapBaseProduct(rs, productId, true);
                    product.setPrice(rs.getDouble("min_price"));
                    product.setMaxPrice(rs.getDouble("max_price"));
                    // GỌI ĐẾN HÀM LẤY VARIANT MỚI BÊN DƯỚI
                    product.setVariants(getProductVariantsWithImportPrice(productId)); 
                    productMap.put(productId, product);
                }

                addImageFromRow(rs, product, productId);
            }

            finalizeMainImages(productMap);
            list.addAll(productMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private List<ProductVariant> getProductVariantsWithImportPrice(int productId) {
        List<ProductVariant> variants = new ArrayList<>();
        // Đã thêm avg_cost vào câu SELECT
        String sql = "SELECT variant_id, product_id, variant_name, price, stock, avg_cost, status "
                + "FROM Product_Variant WHERE product_id = ? AND status = 1";

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
                    // Set giá nhập vào đây (Dùng BigDecimal theo form mẫu cũ của bro)
                    variant.setImportPrice(rs.getBigDecimal("avg_cost")); 
                    variants.add(variant);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return variants;
    }
    
}
