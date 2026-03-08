package DALs;

import Utils.DBContext;
import Model.Orders;
import Model.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class OrderDAO extends DBContext {

    public int placeOrder(int customerId, int addressId, String paymentMethod,
            java.math.BigDecimal totalAmount, List<CartItem> items) {

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        ResultSet rs = null;

        try {

            conn = this.connection;

            if (conn == null) {
                System.out.println("Connection is null!");
                return -1;
            }

            conn.setAutoCommit(false);

            String sqlOrder = "INSERT INTO Orders (customer_id, address_id, payment_method, payment_status, order_status, total_amount) "
                    + "VALUES (?, ?, ?, 'PENDING', 'CREATED', ?)";

            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);

            psOrder.setInt(1, customerId);
            psOrder.setInt(2, addressId);
            psOrder.setString(3, paymentMethod);
            psOrder.setBigDecimal(4, totalAmount);

            psOrder.executeUpdate();

            int orderId = -1;

            rs = psOrder.getGeneratedKeys();

            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            if (orderId == -1) {
                throw new SQLException("Không thể tạo đơn hàng!");
            }

            String sqlDetail = "INSERT INTO Order_Detail (order_id, variant_id, quantity, price_at_order) "
                    + "VALUES (?, ?, ?, ?)";

            psDetail = conn.prepareStatement(sqlDetail);

            for (CartItem item : items) {

                if (item.getVariant() == null || item.getVariant().getPrice() == null) {
                    throw new SQLException("Thông tin sản phẩm không hợp lệ!");
                }

                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getVariant().getVariantId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setBigDecimal(4, item.getVariant().getPrice());

                psDetail.executeUpdate();
            }

            conn.commit();

            System.out.println("Order created successfully! Order ID: " + orderId);

            // COD → trừ stock ngay
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                deductStockAfterPayment(orderId);
            }

            return orderId;

        } catch (SQLException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back: " + e.getMessage());
                } catch (SQLException ex) {
                    System.out.println("Rollback error: " + ex.getMessage());
                }
            }

            e.printStackTrace();
            return -1;

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.out.println("AutoCommit reset error: " + ex.getMessage());
                }
            }

            closeResources(rs, psOrder, psDetail);
        }
    }

    private void closeResources(ResultSet rs, PreparedStatement... stmts) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Close rs error: " + e.getMessage());
            }
        }

        for (PreparedStatement ps : stmts) {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Close ps error: " + e.getMessage());
                }
            }
        }
    }

    public Orders getOrderById(int orderId) {

        String sql = "SELECT * FROM Orders WHERE order_id = ?";

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

                    return order;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updatePaymentSuccess(int orderId) {

        Connection conn = null;

        try {

            conn = this.connection;

            conn.setAutoCommit(false);

            String sql = "UPDATE Orders SET payment_status = 'SUCCESS', paid_at = GETDATE() WHERE order_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            deductStockAfterPayment(orderId);

            conn.commit();

            return true;

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
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

        String sql = "UPDATE Product_Variant "
                + "SET stock = stock - od.quantity "
                + "FROM Product_Variant pv "
                + "JOIN Order_Detail od ON pv.variant_id = od.variant_id "
                + "WHERE od.order_id = ? AND pv.stock >= od.quantity";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}