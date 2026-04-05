package DALs;

import Model.ProductVariant;
import Model.ImportOrder;
import Model.ImportOrderDetail;
import Utils.DBContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImportProductDAO extends DBContext {

    public List<ProductVariant> getVariantByBrand(int brandId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT pv.variant_id, p.name AS product_name, pv.variant_name, pv.stock "
                + "FROM Product_Variant pv "
                + "JOIN Product p ON pv.product_id = p.product_id "
                + "WHERE p.brand_id = ?";

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
                BigDecimal currentAvgCost = rs.getBigDecimal("avg_cost");
                BigDecimal importCost = BigDecimal.valueOf(importPrice);
                int newStock = currentStock + quantity;
                BigDecimal newAvgCost;

                if (newStock <= 0) {
                    newAvgCost = currentAvgCost == null ? BigDecimal.ZERO : currentAvgCost;
                } else if (currentAvgCost == null || currentStock <= 0) {
                    newAvgCost = importCost;
                } else {
                    BigDecimal existingCost = currentAvgCost.multiply(BigDecimal.valueOf(currentStock));
                    BigDecimal incomingCost = importCost.multiply(BigDecimal.valueOf(quantity));
                    newAvgCost = existingCost.add(incomingCost)
                            .divide(BigDecimal.valueOf(newStock), 2, RoundingMode.HALF_UP);
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

    public int createPurchaseOrderWithDetails(ImportOrder importOrder) {
        if (importOrder == null || !importOrder.hasDetails()) {
            return -1;
        }

        boolean previousAutoCommit = true;

        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int orderId = insertPurchaseOrder(importOrder);
            if (orderId <= 0) {
                connection.rollback();
                return -1;
            }

            insertPurchaseDetails(orderId, importOrder.getDetails());

            connection.commit();
            return orderId;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return -1;
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int insertPurchaseOrder(ImportOrder importOrder) throws SQLException {
        String sql = "INSERT INTO Import_Order (import_code, supplier_id, created_by, invoice_no, note, total_amount, created_at, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try (PreparedStatement orderPs = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            orderPs.setString(1, importOrder.getImportCode());
            setNullableInteger(orderPs, 2, importOrder.getSupplierId());
            orderPs.setInt(3, importOrder.getCreatedBy());
            orderPs.setString(4, importOrder.getInvoiceNo());
            orderPs.setString(5, importOrder.getNote());
            orderPs.setBigDecimal(6, importOrder.getTotalAmount() == null ? BigDecimal.ZERO : importOrder.getTotalAmount());
            orderPs.setString(7, importOrder.getStatus() == null ? "PENDING" : importOrder.getStatus());
            orderPs.executeUpdate();

            try (ResultSet rs = orderPs.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    private void insertPurchaseDetails(int orderId, List<ImportOrderDetail> details) throws SQLException {
        String sql = "INSERT INTO Import_Order_Detail (import_order_id, variant_id, quantity, import_price, received_quantity) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement detailPs = connection.prepareStatement(sql)) {
            for (ImportOrderDetail detail : details) {
                detailPs.setInt(1, orderId);
                detailPs.setInt(2, detail.getVariantId());
                detailPs.setInt(3, detail.getQuantity());
                detailPs.setBigDecimal(4, detail.getImportPrice() == null ? BigDecimal.ZERO : detail.getImportPrice());
                detailPs.setNull(5, java.sql.Types.INTEGER);
                detailPs.addBatch();
            }
            detailPs.executeBatch();
        }
    }

    private void setNullableInteger(PreparedStatement statement, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, java.sql.Types.INTEGER);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

    public List<ImportOrder> getImportHistory() {
        List<ImportOrder> list = new ArrayList<>();
        String sql = "SELECT po.import_order_id, po.import_code, m.full_name, m.manager_role, "
                + "s.name AS supplier_name, s.phone AS supplier_phone, s.address AS supplier_address, "
                + "po.invoice_no, po.note, po.total_amount, po.created_at, po.status, po.received_at, po.received_by, "
                + "mr.full_name AS received_by_name "
                + "FROM Import_Order po "
                + "LEFT JOIN Supplier s ON po.supplier_id = s.supplier_id "
                + "JOIN Manager m ON po.created_by = m.manager_id "
                + "LEFT JOIN Manager mr ON po.received_by = mr.manager_id "
                + "ORDER BY CASE WHEN po.status = 'PENDING' THEN 0 ELSE 1 END, "
                + "po.created_at DESC, po.import_order_id DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ImportOrder po = new ImportOrder();
                po.setPurchaseOrderId(rs.getInt("import_order_id"));
                po.setImportCode(rs.getString("import_code"));
                po.setManagerName(rs.getString("full_name"));
                po.setManagerRole(rs.getString("manager_role"));
                po.setSupplierName(rs.getString("supplier_name"));
                po.setSupplierPhone(rs.getString("supplier_phone"));
                po.setSupplierAddress(rs.getString("supplier_address"));
                po.setInvoiceNo(rs.getString("invoice_no"));
                po.setNote(rs.getString("note"));
                po.setTotalAmount(rs.getBigDecimal("total_amount"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                po.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
                po.setStatus(rs.getString("status"));
                Timestamp receivedAt = rs.getTimestamp("received_at");
                po.setReceivedAt(receivedAt == null ? null : receivedAt.toLocalDateTime());
                po.setReceivedBy(rs.getObject("received_by", Integer.class));
                po.setReceivedByName(rs.getString("received_by_name"));
                list.add(po);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ImportOrder getImportOrderById(int orderId) {
        String sql = "SELECT po.import_order_id, po.import_code, po.supplier_id, m.full_name, m.manager_role, "
                + "s.name AS supplier_name, s.phone AS supplier_phone, s.address AS supplier_address, "
                + "po.invoice_no, po.note, po.total_amount, po.created_at, po.status, po.received_at, po.received_by, "
                + "mr.full_name AS received_by_name "
                + "FROM Import_Order po "
                + "LEFT JOIN Supplier s ON po.supplier_id = s.supplier_id "
                + "JOIN Manager m ON po.created_by = m.manager_id "
                + "LEFT JOIN Manager mr ON po.received_by = mr.manager_id "
                + "WHERE po.import_order_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ImportOrder po = new ImportOrder();
                    po.setPurchaseOrderId(rs.getInt("import_order_id"));
                    po.setImportCode(rs.getString("import_code"));
                    po.setSupplierId(rs.getObject("supplier_id", Integer.class));
                    po.setManagerName(rs.getString("full_name"));
                    po.setManagerRole(rs.getString("manager_role"));
                    po.setSupplierName(rs.getString("supplier_name"));
                    po.setSupplierPhone(rs.getString("supplier_phone"));
                    po.setSupplierAddress(rs.getString("supplier_address"));
                    po.setInvoiceNo(rs.getString("invoice_no"));
                    po.setNote(rs.getString("note"));
                    po.setTotalAmount(rs.getBigDecimal("total_amount"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    po.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
                    po.setStatus(rs.getString("status"));
                    Timestamp receivedAt = rs.getTimestamp("received_at");
                    po.setReceivedAt(receivedAt == null ? null : receivedAt.toLocalDateTime());
                    po.setReceivedBy(rs.getObject("received_by", Integer.class));
                    po.setReceivedByName(rs.getString("received_by_name"));
                    return po;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ImportOrderDetail> getImportOrderDetail(int orderId) {
        List<ImportOrderDetail> list = new ArrayList<>();
        String sql = "SELECT b.name AS brand_name, p.name AS product_name, pv.variant_name, pod.variant_id, "
                + "pod.quantity, pod.import_price, pod.received_quantity "
                + "FROM Import_Order_Detail pod "
                + "JOIN Product_Variant pv ON pod.variant_id = pv.variant_id "
                + "JOIN Product p ON pv.product_id = p.product_id "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "WHERE pod.import_order_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ImportOrderDetail d = new ImportOrderDetail();
                d.setBrandName(rs.getString("brand_name"));
                d.setProductName(rs.getString("product_name"));
                d.setVariantName(rs.getString("variant_name"));
                d.setVariantId(rs.getInt("variant_id"));
                d.setQuantity(rs.getInt("quantity"));
                d.setImportPrice(rs.getBigDecimal("import_price"));
                d.setReceivedQuantity((Integer) rs.getObject("received_quantity"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getImportOrderStatus(int orderId) {
        String sql = "SELECT status FROM Import_Order WHERE import_order_id = ?";
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

    public boolean confirmReceipt(int orderId, int staffId, int[] variantIds, int[] receivedQuantities) {
        String selectDetailsSql = "SELECT variant_id, quantity, import_price FROM Import_Order_Detail WHERE import_order_id = ?";
        String updateDetailSql = "UPDATE Import_Order_Detail SET received_quantity = ? WHERE import_order_id = ? AND variant_id = ?";
        String updateOrderSql = "UPDATE Import_Order SET status = ?, received_by = ?, received_at = GETDATE(), total_amount = ? WHERE import_order_id = ? AND status = 'PENDING'";

        boolean previousAutoCommit = true;
        BigDecimal total = BigDecimal.ZERO;
        List<ImportOrderDetail> details = new ArrayList<>();

        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement selectDetailsPs = connection.prepareStatement(selectDetailsSql)) {
                selectDetailsPs.setInt(1, orderId);
                try (ResultSet rs = selectDetailsPs.executeQuery()) {
                    while (rs.next()) {
                ImportOrderDetail detail = new ImportOrderDetail();
                        detail.setVariantId(rs.getInt("variant_id"));
                        detail.setQuantity(rs.getInt("quantity"));
                        detail.setImportPrice(rs.getBigDecimal("import_price"));
                        details.add(detail);
                    }
                }
            }

            if (details.isEmpty()) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement updateDetailPs = connection.prepareStatement(updateDetailSql)) {
        for (ImportOrderDetail detail : details) {
                    int receivedQty = resolveReceivedQuantity(detail.getVariantId(), detail.getQuantity(), variantIds, receivedQuantities);
                    BigDecimal importPrice = detail.getImportPrice() == null ? BigDecimal.ZERO : detail.getImportPrice();
                    updateDetailPs.setInt(1, receivedQty);
                    updateDetailPs.setInt(2, orderId);
                    updateDetailPs.setInt(3, detail.getVariantId());
                    updateDetailPs.addBatch();

                    total = total.add(importPrice.multiply(BigDecimal.valueOf(receivedQty)));
                    updateStock(detail.getVariantId(), receivedQty, importPrice.doubleValue());
                }
                updateDetailPs.executeBatch();
            }

            try (PreparedStatement updateOrderPs = connection.prepareStatement(updateOrderSql)) {
                updateOrderPs.setString(1, "RECEIVED");
                updateOrderPs.setInt(2, staffId);
                updateOrderPs.setBigDecimal(3, total);
                updateOrderPs.setInt(4, orderId);
                int updatedRows = updateOrderPs.executeUpdate();
                if (updatedRows == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int resolveReceivedQuantity(int variantId, int fallbackQuantity, int[] variantIds, int[] receivedQuantities) {
        if (variantIds == null || receivedQuantities == null) {
            return fallbackQuantity;
        }

        for (int i = 0; i < variantIds.length; i++) {
            if (variantIds[i] == variantId) {
                if (i < receivedQuantities.length && receivedQuantities[i] >= 0) {
                    return receivedQuantities[i];
                }
                break;
            }
        }

        return fallbackQuantity;
    }
}
