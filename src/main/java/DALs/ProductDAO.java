package DALs;
import Utils.DBContext;
import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Brand;
import model.Category;
import model.Product;

public class ProductDAO extends DBContext {

public List<Product> getAllProducts() {
    List<Product> list = new ArrayList<>();

    String sql =
        "SELECT " +
        "  p.product_id, p.name, p.description, p.status, " +
        "  b.brand_id, b.name AS brand_name, b.status AS brand_status, " +
        "  c.category_id, c.name AS category_name, c.status AS category_status, " +
        "  img.image AS main_image " +
        "FROM Product p " +
        "JOIN Brand b ON p.brand_id = b.brand_id " +
        "JOIN Category c ON p.category_id = c.category_id " +
        "OUTER APPLY ( " +
        "   SELECT TOP 1 pi.image " +
        "   FROM ProductImage pi " +
        "   WHERE pi.product_id = p.product_id " +
        "   ORDER BY pi.is_main DESC, pi.image_id ASC " +
        ") img " +
        "ORDER BY p.product_id DESC";

    try (
            PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()){
        while (rs.next()) {
            Product p = new Product();
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

            // ✅ set main image cho list
            p.setMainImage(rs.getString("main_image")); // có thể null nếu chưa có ảnh

            list.add(p);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
}
