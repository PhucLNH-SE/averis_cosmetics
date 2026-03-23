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
                "INSERT INTO Purchase_Order (brand_id, created_by, created_at, status) " +
                "VALUES (?, ?, GETDATE(), 'PENDING')";

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
                "(purchase_order_id, variant_id, quantity, import_price, received_quantity) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, orderId);
            ps.setInt(2, variantId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            ps.setNull(5, java.sql.Types.INTEGER);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================
    // UPDATE PRODUCT STOCK
    // =============================
    public void updateStock(int variantId, int quantity, double importPrice) {

        String selectSql = "SELECT stock, avg_cost FROM Product_Variant WHERE variant_id = ?";
        String updateSql = "UPDATE Product_Variant SET stock = ?, avg_cost = ? WHERE variant_id = ?";

        try (PreparedStatement selectPs = connection.prepareStatement(selectSql)) {

            selectPs.setInt(1, variantId);

            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    return;
                }

                int currentStock = rs.getInt("stock");
                java.math.BigDecimal currentAvgCost = rs.getBigDecimal("avg_cost");
                java.math.BigDecimal importCost = java.math.BigDecimal.valueOf(importPrice);

                int newStock = currentStock + quantity;
                java.math.BigDecimal newAvgCost;

                if (newStock <= 0) {
                    newAvgCost = currentAvgCost == null ? java.math.BigDecimal.ZERO : currentAvgCost;
                } else if (currentAvgCost == null || currentStock <= 0) {
                    newAvgCost = importCost;
                } else {
                    java.math.BigDecimal existingCost = currentAvgCost.multiply(java.math.BigDecimal.valueOf(currentStock));
                    java.math.BigDecimal incomingCost = importCost.multiply(java.math.BigDecimal.valueOf(quantity));
                    newAvgCost = existingCost.add(incomingCost)
                            .divide(java.math.BigDecimal.valueOf(newStock), 2, java.math.RoundingMode.HALF_UP);
                }

                try (PreparedStatement updatePs = connection.prepareStatement(updateSql)) {
                    updatePs.setInt(1, newStock);
                    updatePs.setBigDecimal(2, newAvgCost);
                    updatePs.setInt(3, variantId);
                    updatePs.executeUpdate();
                }
            }

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
            + "po.created_at, "
            + "po.status, "
            + "po.received_at, "
            + "po.received_by "
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
            po.setStatus(rs.getString("status"));
            Timestamp receivedAt = rs.getTimestamp("received_at");
            po.setReceivedAt(receivedAt == null ? null : receivedAt.toLocalDateTime());
            int receivedBy = rs.getInt("received_by");
            po.setReceivedBy(rs.wasNull() ? null : receivedBy);

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
            + "pod.variant_id, "
            + "pod.quantity, "
            + "pod.import_price, "
            + "pod.received_quantity "
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
       d.setVariantId(rs.getInt("variant_id"));
       d.setQuantity(rs.getInt("quantity"));
       d.setImportPrice(rs.getBigDecimal("import_price"));
       Integer receivedQuantity = (Integer) rs.getObject("received_quantity");
       d.setReceivedQuantity(receivedQuantity);

       list.add(d);
           
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}

   public String getImportOrderStatus(int orderId) {
        String sql = "SELECT status FROM Purchase_Order WHERE purchase_order_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   public List<PurchaseOrder> getPendingImportOrders() {

        List<PurchaseOrder> list = new ArrayList<>();

        String sql = "SELECT "
                + "po.purchase_order_id, "
                + "b.name, "
                + "m.full_name, "
                + "m.manager_role, "
                + "po.total_amount, "
                + "po.created_at, "
                + "po.status "
                + "FROM Purchase_Order po "
                + "JOIN Brand b ON po.brand_id = b.brand_id "
                + "JOIN Manager m ON po.created_by = m.manager_id "
                + "WHERE po.status = 'PENDING' "
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
                po.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                po.setStatus(rs.getString("status"));

                list.add(po);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean confirmReceipt(int orderId, int staffId, int[] variantIds, int[] receivedQuantities) {
        if (variantIds == null || receivedQuantities == null || variantIds.length != receivedQuantities.length) {
            return false;
        }

        String updateDetailSql = "UPDATE Purchase_Order_Detail "
                + "SET received_quantity = ? "
                + "WHERE purchase_order_id = ? AND variant_id = ?";
        String selectPriceSql = "SELECT import_price FROM Purchase_Order_Detail "
                + "WHERE purchase_order_id = ? AND variant_id = ?";
        String updateOrderSql = "UPDATE Purchase_Order "
                + "SET status = ?, received_by = ?, received_at = GETDATE(), total_amount = ? "
                + "WHERE purchase_order_id = ? AND status = 'PENDING'";

        boolean previousAutoCommit = true;
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement updateDetailPs = connection.prepareStatement(updateDetailSql);
                 PreparedStatement selectPricePs = connection.prepareStatement(selectPriceSql)) {

                for (int i = 0; i < variantIds.length; i++) {
                    int variantId = variantIds[i];
                    int receivedQty = Math.max(0, receivedQuantities[i]);

                    selectPricePs.setInt(1, orderId);
                    selectPricePs.setInt(2, variantId);
                    try (ResultSet rs = selectPricePs.executeQuery()) {
                        if (!rs.next()) {
                            continue;
                        }
                        java.math.BigDecimal importPrice = rs.getBigDecimal("import_price");
                        if (importPrice == null) {
                            importPrice = java.math.BigDecimal.ZERO;
                        }

                        updateDetailPs.setInt(1, receivedQty);
                        updateDetailPs.setInt(2, orderId);
                        updateDetailPs.setInt(3, variantId);
                        updateDetailPs.executeUpdate();

                        if (receivedQty > 0) {
                            updateStock(variantId, receivedQty, importPrice.doubleValue());
                            total = total.add(importPrice.multiply(java.math.BigDecimal.valueOf(receivedQty)));
                        }
                    }
                }
            }

            try (PreparedStatement updateOrderPs = connection.prepareStatement(updateOrderSql)) {
                updateOrderPs.setString(1, "RECEIVED");
                updateOrderPs.setInt(2, staffId);
                updateOrderPs.setBigDecimal(3, total);
                updateOrderPs.setInt(4, orderId);
                int updated = updateOrderPs.executeUpdate();
                if (updated == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
