package DALs;

import Utils.DBContext;
import Model.Orders;
import Model.CartItem;
import Model.OrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    /**
     * Place order với transaction
     * - Insert Orders
     * - Insert Order_Detail cho từng item
     * - Update Product_Variant stock (trừ tồn kho)
     * - Commit nếu thành công, Rollback nếu có lỗi
     *
     * @param customerId ID khách hàng
     * @param addressId ID địa chỉ giao hàng
     * @param paymentMethod Phương thức thanh toán (COD/QR)
     * @param totalAmount Tổng tiền
     * @param items Danh sách sản phẩm trong giỏ
     * @return orderId nếu thành công, -1 nếu thất bại
     */
    public int placeOrder(int customerId, int addressId, String paymentMethod, 
                          java.math.BigDecimal totalAmount, List<CartItem> items) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psStock = null;
        ResultSet rs = null;

        try {
            // Lấy connection từ DBContext (đã có sẵn)
            conn = this.connection;
            if (conn == null) {
                System.out.println("Connection is null!");
                return -1;
            }

            // Bắt đầu transaction
            conn.setAutoCommit(false);

            // Bước 1: Insert vào bảng Orders
            String sqlOrder = "INSERT INTO Orders (customer_id, address_id, payment_method, payment_status, order_status, total_amount) "
                    + "VALUES (?, ?, ?, 'PENDING', 'CREATED', ?)";
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, customerId);
            psOrder.setInt(2, addressId);
            psOrder.setString(3, paymentMethod);
            psOrder.setBigDecimal(4, totalAmount);
            psOrder.executeUpdate();

            // Lấy order_id vừa tạo
            int orderId = -1;
            rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            if (orderId == -1) {
                throw new SQLException("Không thể tạo đơn hàng!");
            }

            // Bước 2: Insert Order_Detail và trừ stock cho từng item
            String sqlDetail = "INSERT INTO Order_Detail (order_id, variant_id, quantity, price_at_order) "
                    + "VALUES (?, ?, ?, ?)";
            String sqlStock = "UPDATE Product_Variant SET stock = stock - ? "
                    + "WHERE variant_id = ? AND stock >= ?";

            for (CartItem item : items) {
                // Kiểm tra null cho variant và price
                if (item.getVariant() == null || item.getVariant().getPrice() == null) {
                    throw new SQLException("Thông tin sản phẩm không hợp lệ!");
                }
                
                // 2a: Insert Order_Detail
                psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getVariant().getVariantId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setBigDecimal(4, item.getVariant().getPrice());
                psDetail.executeUpdate();

                // 2b: Trừ stock (kiểm tra đủ hàng trước)
                psStock = conn.prepareStatement(sqlStock);
                psStock.setInt(1, item.getQuantity());
                psStock.setInt(2, item.getVariant().getVariantId());
                psStock.setInt(3, item.getQuantity());
                int updated = psStock.executeUpdate();

                if (updated == 0) {
                    // Không đủ hàng hoặc variant không tồn tại
                    throw new SQLException("Sản phẩm không đủ hàng: " + item.getVariant().getVariantName());
                }
            }

            // Commit transaction
            conn.commit();
            System.out.println("Order created successfully! Order ID: " + orderId);
            return orderId;

        } catch (SQLException e) {
            // Rollback nếu có lỗi
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
            // Khôi phục autoCommit và đóng resources
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.out.println("AutoCommit reset error: " + ex.getMessage());
                }
            }
            closeResources(rs, psOrder, psDetail, psStock);
        }
    }

    /**
     * Helper method để đóng tất cả resources
     */
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

    /**
     * Lấy đơn hàng theo ID
     */
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
                    
                    // Xử lý null cho created_at
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
}

