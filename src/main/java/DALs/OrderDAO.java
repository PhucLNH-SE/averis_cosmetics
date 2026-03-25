package DALs;

import Model.CartItem;
import Model.Orders;
import Model.OrderDetail;
import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    public int placeOrder(int customerId, int addressId, String paymentMethod,
            java.math.BigDecimal totalAmount, List<CartItem> items) {
        return placeOrder(customerId, addressId, paymentMethod, totalAmount, items, null, null, java.math.BigDecimal.ZERO);
    }

    public int placeOrder(int customerId, int addressId, String paymentMethod,
            java.math.BigDecimal totalAmount, List<CartItem> items,
            Integer voucherId, java.math.BigDecimal discountAmount) {
        return placeOrder(customerId, addressId, paymentMethod, totalAmount, items, voucherId, null, discountAmount);
    }

    public int placeOrder(int customerId, int addressId, String paymentMethod,
            java.math.BigDecimal totalAmount, List<CartItem> items,
            Integer voucherId, Integer customerVoucherId, java.math.BigDecimal discountAmount) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psStock = null;
        PreparedStatement psVoucher = null;
        ResultSet rs = null;

        try {
            conn = this.connection;

            if (conn == null) {
                return -1;
            }

            conn.setAutoCommit(false);

            String sqlOrder = "INSERT INTO Orders (customer_id, address_id, voucher_id, discount_amount, payment_method, payment_status, order_status, total_amount) "
                    + "VALUES (?, ?, ?, ?, ?, 'PENDING', 'CREATED', ?)";
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);

            psOrder.setInt(1, customerId);
            psOrder.setInt(2, addressId);
            if (voucherId == null) {
                psOrder.setNull(3, java.sql.Types.INTEGER);
            } else {
                psOrder.setInt(3, voucherId);
            }
            psOrder.setBigDecimal(4, discountAmount == null ? java.math.BigDecimal.ZERO : discountAmount);
            psOrder.setString(5, paymentMethod);
            psOrder.setBigDecimal(6, totalAmount);
            psOrder.executeUpdate();

            int orderId = -1;

            rs = psOrder.getGeneratedKeys();

            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            if (orderId == -1) {
                throw new SQLException("Cannot create order");
            }

            String sqlDetail = "INSERT INTO Order_Detail (order_id, variant_id, quantity, price_at_order, cost_price_at_order) VALUES (?, ?, ?, ?, ?)";

            psDetail = conn.prepareStatement(sqlDetail);

            for (CartItem item : items) {

                if (item.getVariant() == null || item.getVariant().getPrice() == null) {
                    throw new SQLException("Invalid cart item");
                }

                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getVariant().getVariantId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setBigDecimal(4, item.getVariant().getPrice());
                psDetail.setBigDecimal(5, item.getVariant().getImportPrice() == null ? java.math.BigDecimal.ZERO : item.getVariant().getImportPrice());

                psDetail.addBatch();
            }

            psDetail.executeBatch();

            if (customerVoucherId != null) {
                String sqlVoucher = "UPDATE Customer_Voucher "
                        + "SET status = 'USED', used_at = GETDATE() "
                        + "WHERE customer_voucher_id = ? "
                        + "AND status = 'ACTIVE' "
                        + "AND GETDATE() BETWEEN effective_from AND effective_to";
                psVoucher = conn.prepareStatement(sqlVoucher);
                psVoucher.setInt(1, customerVoucherId);

                if (psVoucher.executeUpdate() == 0) {
                    throw new SQLException("Voucher is no longer valid for checkout");
                }
            }

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }

            e.printStackTrace();
            return -1;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }

            closeResources(rs, psOrder, psDetail, psStock, psVoucher);
        }
    }

    private void closeResources(ResultSet rs, PreparedStatement... stmts) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }

        for (PreparedStatement ps : stmts) {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public Orders getOrderById(int orderId) {

        String sql = "SELECT o.*, a.receiver_name, a.phone, a.street_address, a.ward, a.district, a.province "
                + "FROM Orders o "
                + "LEFT JOIN Address a ON o.address_id = a.address_id "
                + "WHERE o.order_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Orders order = new Orders();

                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setAddressId(rs.getInt("address_id"));
                    order.setHandledBy(rs.getObject("handled_by", Integer.class));
                    order.setVoucherId(rs.getObject("voucher_id", Integer.class));
                    order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    if (rs.getTimestamp("created_at") != null) {
                        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("paid_at") != null) {
                        order.setPaidAt(rs.getTimestamp("paid_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("completed_at") != null) {
                        order.setCompletedAt(rs.getTimestamp("completed_at").toLocalDateTime());
                    }

                    // Address info
                    order.setReceiverName(rs.getString("receiver_name"));
                    order.setReceiverPhone(rs.getString("phone"));
                    order.setStreetAddress(rs.getString("street_address"));
                    order.setWard(rs.getString("ward"));
                    order.setDistrict(rs.getString("district"));
                    order.setProvince(rs.getString("province"));

                    return order;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Orders> getAllOrders() {

        List<Orders> list = new ArrayList<>();

        String sql = "SELECT o.order_id, "
                + "a.receiver_name, "
                + "v.code AS voucher_code, "
                + "o.discount_amount, "
                + "o.payment_method, "
                + "o.payment_status, "
                + "o.order_status, "
                + "o.total_amount, "
                + "o.handled_by, "
                + "m.full_name AS handled_by_name "
                + "FROM Orders o "
                + "JOIN Address a ON o.address_id = a.address_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "LEFT JOIN Manager m ON o.handled_by = m.manager_id "
                + "ORDER BY o.order_id ASC";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Orders order = new Orders();

                order.setOrderId(rs.getInt("order_id"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setVoucherCode(rs.getString("voucher_code"));
                order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setHandledBy(rs.getObject("handled_by", Integer.class));
                order.setHandledByName(rs.getString("handled_by_name"));

                list.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {

        List<OrderDetail> list = new ArrayList<>();

        String sql = "SELECT "
                + "p.name AS product_name, "
                + "b.name AS brand_name, "
                + "c.name AS category_name, "
                + "od.quantity, "
                + "od.price_at_order, "
                + "od.rating, "
                + "od.review_comment, "
                + "od.reviewed_at, "
                + "od.response_content, "
                + "od.responded_at, "
                + "(SELECT TOP 1 image_url "
                + " FROM Product_Image "
                + " WHERE product_id = p.product_id) AS image_url "
                + "FROM Order_Detail od "
                + "JOIN Product_Variant v ON od.variant_id = v.variant_id "
                + "JOIN Product p ON v.product_id = p.product_id "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "WHERE od.order_id = ?";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                OrderDetail od = new OrderDetail();

                od.setProductName(rs.getString("product_name"));
                od.setImageUrl(rs.getString("image_url"));
                od.setBrandName(rs.getString("brand_name"));
                od.setCategoryName(rs.getString("category_name"));
                od.setQuantity(rs.getInt("quantity"));
                od.setPriceAtOrder(rs.getBigDecimal("price_at_order"));
                od.setRating((Integer) rs.getObject("rating"));
                od.setReviewComment(rs.getString("review_comment"));
                if (rs.getTimestamp("reviewed_at") != null) {
                    od.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
                }
                od.setResponseContent(rs.getString("response_content"));
                if (rs.getTimestamp("responded_at") != null) {
                    od.setRespondedAt(rs.getTimestamp("responded_at").toLocalDateTime());
                }

                list.add(od);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Orders> getOrdersHandledByManager(int managerId) {
        List<Orders> list = new ArrayList<>();

        String sql = "SELECT o.order_id, a.receiver_name, o.order_status, "
                + "o.payment_status, o.total_amount, o.created_at "
                + "FROM Orders o "
                + "JOIN Address a ON o.address_id = a.address_id "
                + "WHERE o.handled_by = ? "
                + "ORDER BY o.order_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setReceiverName(rs.getString("receiver_name"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        order.setCreatedAt(ts.toLocalDateTime());
                    }

                    list.add(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateOrder(int orderId, String paymentStatus, String orderStatus) {
        updateOrder(orderId, paymentStatus, orderStatus, null);
    }

  public boolean updateOrder(int orderId, String paymentStatus, String orderStatus, Integer handledBy) {
        Connection conn = null;

        try {
            conn = this.connection;
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            Orders order = getOrder(conn, orderId);
            if (order == null) {
                conn.rollback();
                return false;
            }

            boolean shouldDeductStock
                    = !"PROCESSING".equalsIgnoreCase(order.getOrderStatus())
                    && "PROCESSING".equalsIgnoreCase(orderStatus);

            boolean shouldRestoreStock = !"CANCELLED".equalsIgnoreCase(order.getOrderStatus())
                    && "CANCELLED".equalsIgnoreCase(orderStatus)
                    && ("COD".equalsIgnoreCase(order.getPaymentMethod())
                    || "SUCCESS".equalsIgnoreCase(order.getPaymentStatus())
                    || "SUCCESS".equalsIgnoreCase(paymentStatus));

            if (shouldDeductStock && !deductStockForOrder(conn, orderId)) {
                throw new SQLException("Cannot deduct stock for order " + orderId);
            }

            if (shouldRestoreStock && !restoreStockForOrder(conn, orderId)) {
                throw new SQLException("Cannot restore stock for order " + orderId);
            }

            String sql = "UPDATE Orders "
                    + "SET payment_status = ?, "
                    + "    order_status = ?, "
                    + "    handled_by = COALESCE(?, handled_by), "
                    + "    paid_at = CASE WHEN ? = 'SUCCESS' AND paid_at IS NULL THEN GETDATE() ELSE paid_at END, "
                    + "    completed_at = CASE WHEN ? = 'COMPLETED' THEN ISNULL(completed_at, GETDATE()) ELSE NULL END "
                    + "WHERE order_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, paymentStatus);
                ps.setString(2, orderStatus);
              if (handledBy == null) {
                  ps.setNull(3, java.sql.Types.INTEGER);
              } else {
                  ps.setInt(3, handledBy);
              }
                ps.setString(4, paymentStatus);
                ps.setString(5, orderStatus);
                ps.setInt(6, orderId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }

        return false;
    }

    public boolean updatePaymentSuccess(int orderId) {
        Connection conn = null;
        try {
            conn = new DBContext().connection; // ✅ FIX QUAN TRỌNG
            conn.setAutoCommit(false);

            // 2. update payment
            String sql = "UPDATE Orders SET payment_status = 'SUCCESS', paid_at = GETDATE() WHERE order_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                int rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new SQLException("Update failed");
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ignored) {
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public boolean updatePaymentFailed(int orderId) {

        String sql = "UPDATE Orders SET payment_status = 'FAILED' WHERE order_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deductStockAfterPayment(int orderId) {
        try {
            return deductStockForOrder(connection, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Orders> getOrdersByCustomerId(int customerId) {

        List<Orders> list = new ArrayList<>();

        String sql = "SELECT "
                + "o.order_id, "
                + // thêm dòng này
                "a.receiver_name, "
                + "v.code AS voucher_code, "
                + "o.discount_amount, "
                + "o.order_status, "
                + "o.total_amount, "
                + "o.created_at "
                + "FROM Orders o "
                + "JOIN Address a ON o.address_id = a.address_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "WHERE o.customer_id = ? "
                + "ORDER BY o.created_at DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Orders order = new Orders();

                order.setOrderId(rs.getInt("order_id"));   // thêm dòng này
                order.setReceiverName(rs.getString("receiver_name"));
                order.setVoucherCode(rs.getString("voucher_code"));
                order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    order.setCreatedAt(ts.toLocalDateTime());
                }

                list.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean cancelOrder(int orderId) {
        Connection conn = null;

        try {
            conn = this.connection;
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            Orders order = getOrder(conn, orderId);
            if (order == null
                    || (!"CREATED".equalsIgnoreCase(order.getOrderStatus())
                    && !"PROCESSING".equalsIgnoreCase(order.getOrderStatus()))) {
                conn.rollback();
                return false;
            }

          //  boolean shouldRestoreStock = "COD".equalsIgnoreCase(order.getPaymentMethod())
       //             || "SUCCESS".equalsIgnoreCase(order.getPaymentStatus());

       //     if (shouldRestoreStock && !restoreStockForOrder(conn, orderId)) {
       //         throw new SQLException("Cannot restore stock for order " + orderId);
       //     }

            String sql = "UPDATE Orders "
                    + "SET order_status = 'CANCELLED', completed_at = NULL "
                    + "WHERE order_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                boolean updated = ps.executeUpdate() > 0;
                if (!updated) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }

        return false;
    }

    public List<Orders> getOrdersByStatus(String status) {
        List<Orders> list = new ArrayList<>();
        String sql = "SELECT o.order_id, "
                + "a.receiver_name, "
                + "v.code AS voucher_code, "
                + "o.discount_amount, "
                + "o.payment_method, "
                + "o.payment_status, "
                + "o.order_status, "
                + "o.total_amount, "
                + "o.handled_by, "
                + "m.full_name AS handled_by_name "
                + "FROM Orders o "
                + "JOIN Address a ON o.address_id = a.address_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "LEFT JOIN Manager m ON o.handled_by = m.manager_id "
                + "WHERE o.order_status = ? "
                + "ORDER BY o.order_id ASC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Orders order = new Orders();
                order.setOrderId(rs.getInt("order_id"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setVoucherCode(rs.getString("voucher_code"));
                order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setHandledBy(rs.getObject("handled_by", Integer.class));
                order.setHandledByName(rs.getString("handled_by_name"));
                list.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Orders> getOrdersByHandledBy(Integer handledBy) {
        List<Orders> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.order_id, "
                + "a.receiver_name, "
                + "v.code AS voucher_code, "
                + "o.discount_amount, "
                + "o.payment_method, "
                + "o.payment_status, "
                + "o.order_status, "
                + "o.total_amount, "
                + "o.handled_by, "
                + "m.full_name AS handled_by_name "
                + "FROM Orders o "
                + "JOIN Address a ON o.address_id = a.address_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "LEFT JOIN Manager m ON o.handled_by = m.manager_id ");

        if (handledBy == null) {
            sql.append("ORDER BY o.order_id ASC");
        } else if (handledBy == 0) {
            sql.append("WHERE o.handled_by IS NULL ORDER BY o.order_id ASC");
        } else {
            sql.append("WHERE o.handled_by = ? ORDER BY o.order_id ASC");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (handledBy != null && handledBy != 0) {
                ps.setInt(1, handledBy);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setReceiverName(rs.getString("receiver_name"));
                    order.setVoucherCode(rs.getString("voucher_code"));
                    order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setHandledBy(rs.getObject("handled_by", Integer.class));
                    order.setHandledByName(rs.getString("handled_by_name"));
                    list.add(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Integer getHandledBy(int orderId) {
        String sql = "SELECT handled_by FROM Orders WHERE order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("handled_by", Integer.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Orders getOrderUpdateInfo(int orderId) {
        String sql = "SELECT order_id, handled_by, payment_method, payment_status, order_status "
                + "FROM Orders WHERE order_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setHandledBy(rs.getObject("handled_by", Integer.class));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setOrderStatus(rs.getString("order_status"));
                    return order;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean deductStockForOrder(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE pv "
                + "SET pv.stock = pv.stock - od.quantity "
                + "FROM Product_Variant pv "
                + "JOIN Order_Detail od ON pv.variant_id = od.variant_id "
                + "WHERE od.order_id = ? AND pv.stock >= od.quantity";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Stock not enough");
            }

            return true;
        }
    }

    private boolean restoreStockForOrder(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE pv "
                + "SET pv.stock = pv.stock + od.quantity "
                + "FROM Product_Variant pv "
                + "JOIN Order_Detail od ON pv.variant_id = od.variant_id "
                + "WHERE od.order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    private Orders getOrder(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT payment_method, payment_status, order_status "
                + "FROM Orders WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(orderId);
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setOrderStatus(rs.getString("order_status"));
                    return order;
                }
            }
        }

        return null;
    }

    public boolean updateReview(int orderDetailId, int rating, String comment) {
        String sql = "UPDATE Order_Detail "
                + "SET rating = ?, review_comment = ?, reviewed_at = GETDATE() "
                + "WHERE order_detail_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, orderDetailId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<OrderDetail> getOrderDetailsWithReview(int orderId) {
        List<OrderDetail> list = new ArrayList<>();

        String sql = "SELECT "
                + "od.order_detail_id, "
                + "od.rating, "
                + "od.review_comment, "
                + "od.reviewed_at, "
                + "od.response_content, "
                + "od.responded_at, "
                + "p.name AS product_name, "
                + "b.name AS brand_name, "
                + "c.name AS category_name, "
                + "od.quantity, "
                + "od.price_at_order, "
                + "(SELECT TOP 1 image_url "
                + " FROM Product_Image "
                + " WHERE product_id = p.product_id) AS image_url "
                + "FROM Order_Detail od "
                + "JOIN Product_Variant v ON od.variant_id = v.variant_id "
                + "JOIN Product p ON v.product_id = p.product_id "
                + "JOIN Brand b ON p.brand_id = b.brand_id "
                + "JOIN Category c ON p.category_id = c.category_id "
                + "WHERE od.order_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderDetail od = new OrderDetail();

                // Các trường dữ liệu phục vụ cho Review
                od.setOrderDetailId(rs.getInt("order_detail_id"));
                od.setRating(rs.getInt("rating"));
                od.setReviewComment(rs.getString("review_comment"));
                if (rs.getTimestamp("reviewed_at") != null) {
                    od.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
                }
                od.setResponseContent(rs.getString("response_content"));
                if (rs.getTimestamp("responded_at") != null) {
                    od.setRespondedAt(rs.getTimestamp("responded_at").toLocalDateTime());
                }

                // Các trường thông tin sản phẩm
                od.setProductName(rs.getString("product_name"));
                od.setImageUrl(rs.getString("image_url"));
                od.setBrandName(rs.getString("brand_name"));
                od.setCategoryName(rs.getString("category_name"));
                od.setQuantity(rs.getInt("quantity"));
                od.setPriceAtOrder(rs.getBigDecimal("price_at_order"));

                list.add(od);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public OrderDetail getOrderDetailById(int orderDetailId) {
        String sql = "SELECT order_detail_id, order_id, variant_id, quantity, price_at_order, rating, review_comment, reviewed_at "
                + "FROM Order_Detail WHERE order_detail_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderDetail od = new OrderDetail();
                    od.setOrderDetailId(rs.getInt("order_detail_id"));
                    od.setOrderId(rs.getInt("order_id"));
                    // Lấy các trường liên quan đến review để kiểm tra
                    od.setRating(rs.getInt("rating"));
                    od.setReviewComment(rs.getString("review_comment"));
                    if (rs.getTimestamp("reviewed_at") != null) {
                        od.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
                    }
                    return od;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
