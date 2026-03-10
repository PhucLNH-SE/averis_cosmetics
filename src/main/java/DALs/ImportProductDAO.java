package DALs;

import Utils.DBContext;
import Model.Brand;
import Model.ProductVariant;
import Model.PurchaseDetail;
import Model.PurchaseOrder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImportProductDAO extends DBContext {

    // =============================
    // GET ALL BRAND
    // =============================
    public List<Brand> getAllBrands() {

        List<Brand> list = new ArrayList<>();

        String sql = "SELECT brand_id, name FROM Brand";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Brand b = new Brand();

                b.setBrandId(rs.getInt("brand_id"));
                b.setName(rs.getString("name"));

                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =============================
    // GET PRODUCT VARIANT BY BRAND
    // =============================
    public List<ProductVariant> getVariantByBrand(int brandId) {

        List<ProductVariant> list = new ArrayList<>();

        String sql =
                "SELECT pv.variant_id, " +
                "p.name AS product_name, " +
                "pv.variant_name, " +
                "pv.stock " +
                "FROM Product_Variant pv " +
                "JOIN Product p ON pv.product_id = p.product_id " +
                "WHERE p.brand_id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, brandId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ProductVariant v = new ProductVariant();

                v.setVariantId(rs.getInt("variant_id"));
                v.setProductName(rs.getString("product_name"));
                v.setVariantName(rs.getString("variant_name"));
                v.setStock(rs.getInt("stock"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =============================
    // CREATE PURCHASE ORDER
    // =============================
    public int createPurchaseOrder(int brandId, int adminId) {

        int orderId = -1;

        String sql =
                "INSERT INTO Purchase_Order (brand_id, created_by, created_at) " +
                "VALUES (?, ?, GETDATE())";

        try {

            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, brandId);
            ps.setInt(2, adminId);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                orderId = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderId;
    }

    // =============================
    // INSERT PURCHASE DETAIL
    // =============================
    public void insertPurchaseDetail(int orderId, int variantId, int quantity, double price) {

        String sql =
                "INSERT INTO Purchase_Order_Detail " +
                "(purchase_order_id, variant_id, quantity, import_price) " +
                "VALUES (?, ?, ?, ?)";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, orderId);
            ps.setInt(2, variantId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================
    // UPDATE PRODUCT STOCK
    // =============================
    public void updateStock(int variantId, int quantity) {

        String sql =
                "UPDATE Product_Variant " +
                "SET stock = stock + ? " +
                "WHERE variant_id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, quantity);
            ps.setInt(2, variantId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================
    // UPDATE TOTAL AMOUNT
    // =============================
    public void updateTotalAmount(int orderId, double total) {

        String sql =
                "UPDATE Purchase_Order " +
                "SET total_amount = ? " +
                "WHERE purchase_order_id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setDouble(1, total);
            ps.setInt(2, orderId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   public List<PurchaseOrder> getImportHistory() {

    List<PurchaseOrder> list = new ArrayList<>();

    String sql = "SELECT "
            + "po.purchase_order_id, "
            + "b.name, "
            + "m.full_name, "
            + "m.manager_role, "
            + "po.total_amount, "
            + "po.created_at "
            + "FROM Purchase_Order po "
            + "JOIN Brand b ON po.brand_id = b.brand_id "
            + "JOIN Manager m ON po.created_by = m.manager_id "
            + "ORDER BY po.created_at DESC";

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            PurchaseOrder po = new PurchaseOrder();

            po.setPurchaseOrderId(rs.getInt("purchase_order_id"));
            po.setBrandName(rs.getString("name"));
            po.setManagerName(rs.getString("full_name"));
            po.setManagerRole(rs.getString("manager_role"));

            po.setTotalAmount(rs.getBigDecimal("total_amount"));

            po.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
            );

            list.add(po);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
   public List<PurchaseDetail> getImportOrderDetail(int orderId) {

    List<PurchaseDetail> list = new ArrayList<>();

    String sql = "SELECT "
            + "p.name AS product_name, "
            + "pv.variant_name, "
            + "pod.quantity, "
            + "pod.import_price "
            + "FROM Purchase_Order_Detail pod "
            + "JOIN Product_Variant pv ON pod.variant_id = pv.variant_id "
            + "JOIN Product p ON pv.product_id = p.product_id "
            + "WHERE pod.purchase_order_id = ?";

    try {

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, orderId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

       PurchaseDetail d = new PurchaseDetail();

d.setProductName(rs.getString("product_name"));
d.setVariantName(rs.getString("variant_name"));
d.setQuantity(rs.getInt("quantity"));
d.setImportPrice(rs.getBigDecimal("import_price"));

list.add(d);
           
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    }