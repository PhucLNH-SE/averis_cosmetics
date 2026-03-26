package DALs;

import Model.OrderDetail;
import Model.ProductFeedbackSummary;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO extends DBContext {

    public List<OrderDetail> getAllFeedbacks() {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.order_detail_id, od.order_id, od.rating, od.review_comment, od.reviewed_at, "
                   + "od.manager_response, od.response_content, od.responded_at, "
                   + "p.name AS product_name, "
                   + "c.full_name AS customer_name, "
                   + "m.full_name AS manager_name "
                   + "FROM Order_Detail od "
                   + "JOIN Orders o ON od.order_id = o.order_id "
                   + "JOIN Customers c ON o.customer_id = c.customer_id "
                   + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                   + "JOIN Product p ON pv.product_id = p.product_id "
                   + "LEFT JOIN Manager m ON od.manager_response = m.manager_id "
                   + "WHERE od.rating IS NOT NULL "
                   + "ORDER BY od.reviewed_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToOrderDetail(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public OrderDetail getFeedbackDetail(int orderDetailId) {
        String sql = "SELECT od.order_detail_id, od.order_id, od.rating, od.review_comment, od.reviewed_at, "
                   + "od.manager_response, od.response_content, od.responded_at, "
                   + "p.name AS product_name, "
                   + "c.full_name AS customer_name, "
                   + "m.full_name AS manager_name "
                   + "FROM Order_Detail od "
                   + "JOIN Orders o ON od.order_id = o.order_id "
                   + "JOIN Customers c ON o.customer_id = c.customer_id "
                   + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                   + "JOIN Product p ON pv.product_id = p.product_id "
                   + "LEFT JOIN Manager m ON od.manager_response = m.manager_id "
                   + "WHERE od.order_detail_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderDetail(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean replyFeedback(int orderDetailId, int managerId, String responseContent) {
        String sql = "UPDATE Order_Detail "
                   + "SET manager_response = ?, response_content = ?, responded_at = GETDATE() "
                   + "WHERE order_detail_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            ps.setString(2, responseContent);
            ps.setInt(3, orderDetailId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFeedback(int orderDetailId) {
        String sql = "UPDATE Order_Detail "
                   + "SET rating = NULL, review_comment = NULL, reviewed_at = NULL, "
                   + "manager_response = NULL, response_content = NULL, responded_at = NULL "
                   + "WHERE order_detail_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderDetailId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private OrderDetail mapResultSetToOrderDetail(ResultSet rs) throws Exception {
        OrderDetail od = new OrderDetail();
        od.setOrderDetailId(rs.getInt("order_detail_id"));
        od.setOrderId(rs.getInt("order_id"));
        od.setRating(rs.getInt("rating"));
        od.setReviewComment(rs.getString("review_comment"));
        
        if (rs.getTimestamp("reviewed_at") != null) {
            od.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
        }

        od.setManagerResponse(rs.getObject("manager_response", Integer.class));
        od.setResponseContent(rs.getString("response_content"));
        
        if (rs.getTimestamp("responded_at") != null) {
            od.setRespondedAt(rs.getTimestamp("responded_at").toLocalDateTime());
        }

        od.setProductName(rs.getString("product_name"));
        od.setCustomerName(rs.getString("customer_name"));
        od.setManagerName(rs.getString("manager_name"));
        
        return od;
    }
    
    public List<OrderDetail> getFeedbacksByProductId(int productId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.order_detail_id, od.order_id, od.rating, od.review_comment, od.reviewed_at, "
                   + "od.manager_response, od.response_content, od.responded_at, "
                   + "c.full_name AS customer_name, "
                   + "m.full_name AS manager_name "
                   + "FROM Order_Detail od "
                   + "JOIN Orders o ON od.order_id = o.order_id "
                   + "JOIN Customers c ON o.customer_id = c.customer_id "
                   + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                   + "LEFT JOIN Manager m ON od.manager_response = m.manager_id "
                   + "WHERE pv.product_id = ? AND od.rating IS NOT NULL "
                   + "ORDER BY od.reviewed_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail od = new OrderDetail();
                    od.setOrderDetailId(rs.getInt("order_detail_id"));
                    
                    od.setOrderId(rs.getInt("order_id")); 
                    
                    od.setRating(rs.getInt("rating"));
                    od.setReviewComment(rs.getString("review_comment"));
                    
                    if (rs.getTimestamp("reviewed_at") != null) {
                        od.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
                    }

                    od.setResponseContent(rs.getString("response_content"));
                    if (rs.getTimestamp("responded_at") != null) {
                        od.setRespondedAt(rs.getTimestamp("responded_at").toLocalDateTime());
                    }

                    od.setCustomerName(rs.getString("customer_name"));
                    od.setManagerName(rs.getString("manager_name"));
                    
                    list.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderDetail> getFeedbacksByCustomerId(int customerId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.order_detail_id, od.order_id, od.rating, od.review_comment, od.reviewed_at, "
                   + "od.response_content, od.responded_at, "
                   + "p.name AS product_name, "
                   + "pv.variant_name, "
                   + "(SELECT TOP 1 image_url FROM Product_Image pi "
                   + " WHERE pi.product_id = p.product_id "
                   + " ORDER BY pi.is_main DESC, pi.image_id ASC) AS image_url "
                   + "FROM Order_Detail od "
                   + "JOIN Orders o ON od.order_id = o.order_id "
                   + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                   + "JOIN Product p ON pv.product_id = p.product_id "
                   + "WHERE o.customer_id = ? AND od.rating IS NOT NULL "
                   + "ORDER BY od.reviewed_at DESC, od.order_detail_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail od = new OrderDetail();
                    od.setOrderDetailId(rs.getInt("order_detail_id"));
                    od.setOrderId(rs.getInt("order_id"));
                    od.setRating(rs.getInt("rating"));
                    od.setReviewComment(rs.getString("review_comment"));
                    od.setProductName(rs.getString("product_name"));
                    od.setVariantName(rs.getString("variant_name"));
                    od.setImageUrl(rs.getString("image_url"));
                    od.setResponseContent(rs.getString("response_content"));

                    if (rs.getTimestamp("reviewed_at") != null) {
                        od.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("responded_at") != null) {
                        od.setRespondedAt(rs.getTimestamp("responded_at").toLocalDateTime());
                    }

                    list.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<OrderDetail> getFeedbacksByManagerResponse(int managerId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.order_detail_id, od.order_id, od.rating, od.review_comment, "
                + "od.reviewed_at, od.response_content, od.responded_at, "
                + "p.name AS product_name, c.full_name AS customer_name "
                + "FROM Order_Detail od "
                + "JOIN Orders o ON od.order_id = o.order_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                + "JOIN Product p ON pv.product_id = p.product_id "
                + "WHERE od.manager_response = ? AND od.responded_at IS NOT NULL "
                + "ORDER BY od.responded_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail od = new OrderDetail();
                    od.setOrderDetailId(rs.getInt("order_detail_id"));
                    od.setOrderId(rs.getInt("order_id"));
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
                    od.setCustomerName(rs.getString("customer_name"));
                    list.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ProductFeedbackSummary> getFeedbackSummaryList() {
        List<ProductFeedbackSummary> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name AS product_name, "
                   + "(SELECT TOP 1 image_url FROM Product_Image pi WHERE pi.product_id = p.product_id AND pi.is_main = 1) AS image_url, "
                   + "COUNT(od.order_detail_id) AS total_feedbacks, "
                   + "CAST(AVG(CAST(od.rating AS FLOAT)) AS DECIMAL(10,1)) AS average_rating "
                   + "FROM Order_Detail od "
                   + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                   + "JOIN Product p ON pv.product_id = p.product_id "
                   + "WHERE od.rating IS NOT NULL "
                   + "GROUP BY p.product_id, p.name "
                   + "ORDER BY total_feedbacks DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductFeedbackSummary summary = new ProductFeedbackSummary();
                summary.setProductId(rs.getInt("product_id"));
                summary.setProductName(rs.getString("product_name"));
                
                String imageUrl = rs.getString("image_url");
                if (imageUrl != null && !imageUrl.startsWith("assets/")) {
                    imageUrl = "assets/img/" + imageUrl;
                }
                summary.setProductImageUrl(imageUrl);
                
                summary.setTotalFeedbacks(rs.getInt("total_feedbacks"));
                summary.setAverageRating(rs.getDouble("average_rating"));
                
                list.add(summary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
}
