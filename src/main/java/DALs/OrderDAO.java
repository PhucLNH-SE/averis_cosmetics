package DALs;

import Model.CartItem;
import Model.Orders;
import Model.OrderDetail;
import Utils.DBContext;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    //PhucLNH - Payment
    public int placeOrder(int customerId, int addressId, String paymentMethod,
            BigDecimal totalAmount, List<CartItem> items,
            Integer voucherId, Integer customerVoucherId, BigDecimal discountAmount) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new RuntimeException("Payment method is required");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Invalid total amount");
        }
        Connection conn = this.connection;
        if (conn == null) {
            throw new RuntimeException("Database connection is null");
        }
        try {
            conn.setAutoCommit(false);
            int orderId = insertOrder(conn, customerId, addressId, paymentMethod, totalAmount, voucherId, discountAmount);
            insertOrderDetails(conn, orderId, items);
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                markVoucherUsed(conn, customerVoucherId);
            }
            conn.commit();
            return orderId;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            e.printStackTrace();
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

   

    public Orders getOrderById(int orderId) {
        String sql = "SELECT o.*, v.code AS voucher_code, a.receiver_name, a.phone, a.street_address, a.ward, a.district, a.province "
                + "FROM Orders o "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "LEFT JOIN Address a ON o.address_id = a.address_id "
                + "WHERE o.order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrderWithAddress(rs);
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
                + "ORDER BY o.order_id DESC";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Orders order = new Orders();

                order.setOrderId(rs.getInt("order_id"));
                order.setReceiverName(rs.getString("receiver_name"));
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

    public List<Orders> searchOrders(String keyword) {
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        if (normalizedKeyword == null || normalizedKeyword.isEmpty()) {
            return getAllOrders();
        }

        List<Orders> list = new ArrayList<>();
        String sql = "SELECT o.order_id, "
                + "a.receiver_name, "
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
                + "WHERE CAST(o.order_id AS NVARCHAR(20)) LIKE ? "
                + "   OR a.receiver_name LIKE ? "
                + "   OR ISNULL(v.code, '') LIKE ? "
                + "   OR o.payment_method LIKE ? "
                + "   OR o.payment_status LIKE ? "
                + "   OR o.order_status LIKE ? "
                + "   OR ISNULL(m.full_name, '') LIKE ? "
                + "ORDER BY o.order_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchValue = "%" + normalizedKeyword + "%";
            for (int i = 1; i <= 7; i++) {
                ps.setString(i, searchValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setReceiverName(rs.getString("receiver_name"));
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

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {

        List<OrderDetail> list = new ArrayList<>();

        String sql = "SELECT "
                + "p.name AS product_name, "
                + "v.variant_name, "
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
                od.setVariantName(rs.getString("variant_name"));
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

    public boolean updateOrderState(int orderId, String paymentStatus, String orderStatus, Integer handledBy) {
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

            if (!applyOrderStateUpdate(conn, order, paymentStatus, orderStatus, handledBy)) {
                conn.rollback();
                return false;
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

    //PhucLNH - Payment
    public boolean markPaymentSuccess(int orderId, String orderStatus) {
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

            boolean shouldDeductStock = !"PROCESSING".equalsIgnoreCase(order.getOrderStatus())
                    && "PROCESSING".equalsIgnoreCase(orderStatus);
            if (shouldDeductStock && !deductStockForOrder(conn, orderId)) {
                throw new SQLException("Cannot deduct stock for order " + orderId);
            }

            if (!applyOrderStateUpdate(conn, order, "SUCCESS", orderStatus, null)) {
                conn.rollback();
                return false;
            }

            if ("MOMO".equalsIgnoreCase(order.getPaymentMethod())
                    && order.getVoucherId() != null
                    && !markVoucherUsedForOrder(conn, orderId)) {
                throw new SQLException("Cannot mark voucher used for order " + orderId);
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

    //PhucLNH - Payment
    public boolean updatePaymentFailed(int orderId) {
        Connection conn = null;
        try {
            conn = this.connection;
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            if (!applyPaymentFailedUpdate(conn, orderId)) {
                conn.rollback();
                return false;
            }

            reactivateVoucherForFailedPayment(conn, orderId);

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

    //PhucLNH - Payment
    private int insertOrder(Connection conn, int customerId, int addressId, String paymentMethod,
            BigDecimal totalAmount, Integer voucherId, BigDecimal discountAmount) throws SQLException {
        String sql = "INSERT INTO Orders (customer_id, address_id, voucher_id, discount_amount, payment_method, payment_status, order_status, total_amount) "
                + "VALUES (?, ?, ?, ?, ?, 'PENDING', 'CREATED', ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setInt(2, addressId);
            if (voucherId == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, voucherId);
            }
            if (discountAmount == null) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, discountAmount);
            }
            ps.setString(5, paymentMethod);
            ps.setBigDecimal(6, totalAmount);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Cannot create order");
    }

    //PhucLNH - Payment
    private void insertOrderDetails(Connection conn, int orderId, List<CartItem> items) throws SQLException {
        String sql = "INSERT INTO Order_Detail (order_id, variant_id, quantity, price_at_order, cost_price_at_order) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartItem item : items) {
                validateCartItem(item);

                BigDecimal cost = item.getVariant().getImportPrice();
                if (cost == null) {
                    cost = BigDecimal.ZERO;
                }

                ps.setInt(1, orderId);
                ps.setInt(2, item.getVariant().getVariantId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getVariant().getPrice());
                ps.setBigDecimal(5, cost);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    //PhucLNH - Payment
    private void markVoucherUsed(Connection conn, Integer customerVoucherId) throws SQLException {
        if (customerVoucherId == null) {
            return;
        }

        String sql = "UPDATE Customer_Voucher "
                + "SET status = 'USED', used_at = GETDATE() "
                + "WHERE customer_voucher_id = ? "
                + "AND status = 'ACTIVE' "
                + "AND GETDATE() BETWEEN effective_from AND effective_to";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerVoucherId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Voucher is no longer valid");
            }
        }
    }

    //PhucLNH - Payment
    private boolean applyPaymentFailedUpdate(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE Orders "
                + "SET payment_status = 'FAILED', "
                + "    order_status = CASE "
                + "        WHEN order_status = 'CREATED' THEN 'CANCELLED' "
                + "        ELSE order_status "
                + "    END "
                + "WHERE order_id = ? "
                + "AND payment_status <> 'SUCCESS'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasProcessingOrderForVariant(int variantId) {
        String sql = "SELECT TOP 1 1 "
                + "FROM Order_Detail od "
                + "JOIN Orders o ON od.order_id = o.order_id "
                + "WHERE od.variant_id = ? AND o.order_status = 'PROCESSING'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void validateCartItem(CartItem item) {
        if (item == null
                || item.getVariant() == null
                || item.getVariant().getPrice() == null
                || item.getQuantity() <= 0) {
            throw new RuntimeException("Invalid cart item");
        }
    }

    private Orders mapOrderWithAddress(ResultSet rs) throws SQLException {
        Orders order = new Orders();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setAddressId(rs.getInt("address_id"));
        order.setHandledBy(rs.getObject("handled_by", Integer.class));
        order.setVoucherId(rs.getObject("voucher_id", Integer.class));
        order.setVoucherCode(rs.getString("voucher_code"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) {
            order.setPaidAt(paidAt.toLocalDateTime());
        }

        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            order.setCompletedAt(completedAt.toLocalDateTime());
        }

        order.setReceiverName(rs.getString("receiver_name"));
        order.setReceiverPhone(rs.getString("phone"));
        order.setStreetAddress(rs.getString("street_address"));
        order.setWard(rs.getString("ward"));
        order.setDistrict(rs.getString("district"));
        order.setProvince(rs.getString("province"));
        return order;
    }

    public List<Orders> getOrdersByCustomerId(int customerId) {

        List<Orders> list = new ArrayList<>();

        String sql = "SELECT "
                + "o.order_id, "
                + "a.receiver_name, "
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

                order.setOrderId(rs.getInt("order_id"));
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

            boolean shouldRestoreStock = "PROCESSING".equalsIgnoreCase(order.getOrderStatus())
                    && ("COD".equalsIgnoreCase(order.getPaymentMethod())
                    || "SUCCESS".equalsIgnoreCase(order.getPaymentStatus()));

            if (shouldRestoreStock && !restoreStockForOrder(conn, orderId)) {
                throw new SQLException("Cannot restore stock for cancelled order " + orderId);
            }

            if (!applyOrderStateUpdate(conn, order, order.getPaymentStatus(), "CANCELLED", null)) {
                conn.rollback();
                return false;
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

    //PhucLNH - Payment
    private boolean applyOrderStateUpdate(Connection conn, Orders order,
            String paymentStatus, String orderStatus, Integer handledBy) throws SQLException {
        Integer resolvedHandledBy = handledBy != null ? handledBy : order.getHandledBy();

        LocalDateTime resolvedPaidAt = order.getPaidAt();
        if ("SUCCESS".equalsIgnoreCase(paymentStatus) && resolvedPaidAt == null) {
            resolvedPaidAt = LocalDateTime.now();
        }

        LocalDateTime resolvedCompletedAt = order.getCompletedAt();
        if ("COMPLETED".equalsIgnoreCase(orderStatus)) {
            if (resolvedCompletedAt == null) {
                resolvedCompletedAt = LocalDateTime.now();
            }
        } else {
            resolvedCompletedAt = null;
        }

        String sql = "UPDATE Orders "
                + "SET payment_status = ?, "
                + "    order_status = ?, "
                + "    handled_by = ?, "
                + "    paid_at = ?, "
                + "    completed_at = ? "
                + "WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus);
            ps.setString(2, orderStatus);
            if (resolvedHandledBy == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, resolvedHandledBy);
            }
            if (resolvedPaidAt == null) {
                ps.setNull(4, Types.TIMESTAMP);
            } else {
                ps.setObject(4, resolvedPaidAt);
            }
            if (resolvedCompletedAt == null) {
                ps.setNull(5, Types.TIMESTAMP);
            } else {
                ps.setObject(5, resolvedCompletedAt);
            }
            ps.setInt(6, order.getOrderId());
            return ps.executeUpdate() > 0;
        }
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

    //PhucLNH - Payment
    private boolean deductStockForOrder(Connection conn, int orderId) throws SQLException {
        int expectedRows = countOrderDetailRows(conn, orderId);
        if (expectedRows <= 0) {
            throw new SQLException("Order has no details");
        }

        String sql = "UPDATE pv "
                + "SET pv.stock = pv.stock - od.quantity "
                + "FROM Product_Variant pv "
                + "JOIN Order_Detail od ON pv.variant_id = od.variant_id "
                + "WHERE od.order_id = ? AND pv.stock >= od.quantity";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();

            if (rows != expectedRows) {
                throw new SQLException("Stock not enough");
            }

            return true;
        }
    }

    //PhucLNH - Payment
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
        String sql = "SELECT handled_by, voucher_id, payment_method, payment_status, order_status, paid_at, completed_at "
                + "FROM Orders WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(orderId);
                    order.setHandledBy(rs.getObject("handled_by", Integer.class));
                    order.setVoucherId(rs.getObject("voucher_id", Integer.class));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setOrderStatus(rs.getString("order_status"));
                    Timestamp paidAt = rs.getTimestamp("paid_at");
                    if (paidAt != null) {
                        order.setPaidAt(paidAt.toLocalDateTime());
                    }
                    Timestamp completedAt = rs.getTimestamp("completed_at");
                    if (completedAt != null) {
                        order.setCompletedAt(completedAt.toLocalDateTime());
                    }
                    return order;
                }
            }
        }

        return null;
    }

    private int countOrderDetailRows(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_rows FROM Order_Detail WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_rows");
                }
            }
        }

        return 0;
    }

    //PhucLNH - Payment
    private void reactivateVoucherForFailedPayment(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE cv "
                + "SET cv.status = 'ACTIVE', cv.used_at = NULL "
                + "FROM Customer_Voucher cv "
                + "JOIN Orders o ON o.customer_id = cv.customer_id AND o.voucher_id = cv.voucher_id "
                + "WHERE o.order_id = ? "
                + "AND cv.status = 'USED'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    //PhucLNH - Payment
    private boolean markVoucherUsedForOrder(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE cv "
                + "SET cv.status = 'USED', cv.used_at = GETDATE() "
                + "FROM Customer_Voucher cv "
                + "JOIN Orders o ON o.customer_id = cv.customer_id AND o.voucher_id = cv.voucher_id "
                + "WHERE o.order_id = ? "
                + "AND cv.status = 'ACTIVE' "
                + "AND GETDATE() BETWEEN cv.effective_from AND cv.effective_to";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        }
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
                + "v.variant_name, "
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

                od.setProductName(rs.getString("product_name"));
                od.setVariantName(rs.getString("variant_name"));
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
