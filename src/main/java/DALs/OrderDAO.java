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
            
psDetail = conn.prepareStatement(sqlDetail);

for (CartItem item : items) {

    if (item.getVariant() == null || item.getVariant().getPrice() == null) {
        throw new SQLException("Invalid cart item");
    }

    psDetail.setInt(1, orderId);
    psDetail.setInt(2, item.getVariant().getVariantId());
    psDetail.setInt(3, item.getQuantity());
    psDetail.setBigDecimal(4, item.getVariant().getPrice());

    psDetail.addBatch();
}

psDetail.executeBatch();

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

            closeResources(rs, psOrder, psDetail,psStock);
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

        String sql = "SELECT o.*, a.receiver_name, a.phone, a.street_address, a.ward, a.district, a.province " +
                     "FROM Orders o " +
                     "LEFT JOIN Address a ON o.address_id = a.address_id " +
                     "WHERE o.order_id = ?";

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
String sql = "SELECT o.order_id, c.username, v.code AS voucher_code, o.discount_amount, " +
             "o.payment_method, o.payment_status, o.order_status, o.total_amount " +
             "FROM Orders o " +
             "JOIN Customers c ON o.customer_id = c.customer_id " +
             "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

          while (rs.next()) {

    Orders order = new Orders();
    order.setOrderId(rs.getInt("order_id"));
    order.setUsername(rs.getString("username"));
    order.setVoucherCode(rs.getString("voucher_code"));
    order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
    order.setPaymentMethod(rs.getString("payment_method"));
    order.setPaymentStatus(rs.getString("payment_status"));
    order.setOrderStatus(rs.getString("order_status"));
    order.setTotalAmount(rs.getBigDecimal("total_amount"));

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

            list.add(od);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
  public void updateOrder(int orderId, String paymentStatus, String orderStatus) {

    String sql = "UPDATE Orders SET payment_status = ?, order_status = ? WHERE order_id = ?";

    try {

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, paymentStatus);
        ps.setString(2, orderStatus);
        ps.setInt(3, orderId);

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
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

        String sql = "UPDATE pv "
        + "SET pv.stock = pv.stock - od.quantity "
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
   public List<Orders> getOrdersByCustomerId(int customerId) {

    List<Orders> list = new ArrayList<>();

    String sql = "SELECT " +
                 "o.order_id, " +              // thêm dòng này
                 "a.receiver_name, " +
                 "v.code AS voucher_code, " +
                 "o.discount_amount, " +
                 "o.order_status, " +
                 "o.total_amount, " +
                 "o.created_at " +
                 "FROM Orders o " +
                 "JOIN Address a ON o.address_id = a.address_id " +
                 "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id " +
                 "WHERE o.customer_id = ? " +
                 "ORDER BY o.created_at DESC";

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

    String sql = "UPDATE Orders "
               + "SET order_status = 'CANCELLED' "
               + "WHERE order_id = ? "
               + "AND order_status IN ('CREATED','PROCESSING')";

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, orderId);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}
