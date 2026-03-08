package DALs;

import Model.CartItem;
import Model.Orders;
import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class OrderDAO extends DBContext {

    public int placeOrder(int customerId, int addressId, String paymentMethod,
                          java.math.BigDecimal totalAmount, List<CartItem> items) {
        return placeOrder(customerId, addressId, paymentMethod, totalAmount, items, null, java.math.BigDecimal.ZERO);
    }

    public int placeOrder(int customerId, int addressId, String paymentMethod,
                          java.math.BigDecimal totalAmount, List<CartItem> items,
                          Integer voucherId, java.math.BigDecimal discountAmount) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psStock = null;
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

            String sqlDetail = "INSERT INTO Order_Detail (order_id, variant_id, quantity, price_at_order) VALUES (?, ?, ?, ?)";
            String sqlStock = "UPDATE Product_Variant SET stock = stock - ? WHERE variant_id = ? AND stock >= ?";

            for (CartItem item : items) {
                if (item.getVariant() == null || item.getVariant().getPrice() == null) {
                    throw new SQLException("Invalid cart item");
                }

                psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getVariant().getVariantId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setBigDecimal(4, item.getVariant().getPrice());
                psDetail.executeUpdate();

                psStock = conn.prepareStatement(sqlStock);
                psStock.setInt(1, item.getQuantity());
                psStock.setInt(2, item.getVariant().getVariantId());
                psStock.setInt(3, item.getQuantity());
                int updated = psStock.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Out of stock: " + item.getVariant().getVariantName());
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
            closeResources(rs, psOrder, psDetail, psStock);
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
}
