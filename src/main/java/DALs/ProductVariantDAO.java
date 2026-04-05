package DALs;

import Utils.DBContext;
import Model.ProductVariant;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAO extends DBContext {


    public List<ProductVariant> getVariantsByProductId(int productId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT variant_id, product_id, variant_name, price, stock, status "
                   + "FROM Product_Variant WHERE product_id = ? ORDER BY price ASC";
                   
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant v = new ProductVariant();
                    v.setVariantId(rs.getInt("variant_id"));
                    v.setProductId(rs.getInt("product_id"));
                    v.setVariantName(rs.getString("variant_name"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setStock(rs.getInt("stock"));
                    v.setStatus(rs.getBoolean("status"));
                    list.add(v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertVariant(int productId, String variantName, BigDecimal price, int stock, boolean status) {
        String sql = "INSERT INTO Product_Variant (product_id, variant_name, price, stock, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, variantName);
            ps.setBigDecimal(3, price);
            ps.setInt(4, stock);
            ps.setBoolean(5, status);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateVariant(int variantId, String variantName, BigDecimal price, int stock, boolean status) {
        String sql = "UPDATE Product_Variant SET variant_name = ?, price = ?, stock = ?, status = ? WHERE variant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, variantName);
            ps.setBigDecimal(2, price);
            ps.setInt(3, stock);
            ps.setBoolean(4, status);
            ps.setInt(5, variantId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteVariant(int variantId) {
        String sql = "DELETE FROM Product_Variant WHERE variant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT pv.variant_id, pv.product_id, pv.variant_name, pv.price, pv.stock, pv.status, "
                   + "p.name AS product_name, pi.image_url "
                   + "FROM Product_Variant pv "
                   + "JOIN Product p ON pv.product_id = p.product_id "
                   + "LEFT JOIN Product_Image pi ON p.product_id = pi.product_id AND pi.is_main = 1 "
                   + "WHERE pv.variant_id = ?";
                   
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductVariant v = new ProductVariant();
                    v.setVariantId(rs.getInt("variant_id"));
                    v.setProductId(rs.getInt("product_id"));
                    v.setVariantName(rs.getString("variant_name"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setStock(rs.getInt("stock"));
                    v.setStatus(rs.getBoolean("status"));
                    
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
}
