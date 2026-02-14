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

    public List<Product> getAllProducts() {
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

        String sql =
            "SELECT " +
            "  p.product_id, p.name, p.description, p.status, " +
            "  b.brand_id, b.name AS brand_name, b.status AS brand_status, " +
            "  c.category_id, c.name AS category_name, c.status AS category_status, " +
            "  pi.image_id, pi.image_url, pi.is_main " +
            "FROM Product p " +
            "JOIN Brand b ON p.brand_id = b.brand_id " +
            "JOIN Category c ON p.category_id = c.category_id " +
            "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id " +
            "WHERE p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ? " +
            "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";

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

        String sql =
            "SELECT TOP 10 " +  // Limit to top 10 results for auto-suggest
            "  p.product_id, p.name, p.description, p.status, " +
            "  b.brand_id, b.name AS brand_name, b.status AS brand_status, " +
            "  c.category_id, c.name AS category_name, c.status AS category_status, " +
            "  pi.image_id, pi.image_url, pi.is_main " +
            "FROM Product p " +
            "JOIN Brand b ON p.brand_id = b.brand_id " +
            "JOIN Category c ON p.category_id = c.category_id " +
            "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id " +
            "WHERE p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ? " +
            "ORDER BY p.product_id DESC, pi.is_main DESC, pi.image_id ASC";

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
}
