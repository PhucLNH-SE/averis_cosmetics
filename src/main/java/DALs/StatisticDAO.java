package DALs;

import Model.MonthlyStatisticSummary;
import Model.ProductVariant;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticDAO extends DBContext {

    public MonthlyStatisticSummary getMonthlySummary(int year, int month) {
        MonthlyStatisticSummary summary = new MonthlyStatisticSummary();
        summary.setYear(year);
        summary.setMonth(month);

        String orderSql = "SELECT "
                + "SUM(CASE WHEN YEAR(created_at) = ? AND MONTH(created_at) = ? THEN 1 ELSE 0 END) AS total_orders, "
                + "SUM(CASE WHEN order_status = 'COMPLETED' "
                + "          AND YEAR(ISNULL(completed_at, created_at)) = ? "
                + "          AND MONTH(ISNULL(completed_at, created_at)) = ? THEN 1 ELSE 0 END) AS completed_orders, "
                + "SUM(CASE WHEN order_status = 'CANCELLED' "
                + "          AND YEAR(created_at) = ? "
                + "          AND MONTH(created_at) = ? THEN 1 ELSE 0 END) AS cancelled_orders, "
                + "SUM(CASE WHEN order_status = 'COMPLETED' "
                + "          AND YEAR(ISNULL(completed_at, created_at)) = ? "
                + "          AND MONTH(ISNULL(completed_at, created_at)) = ? THEN total_amount ELSE 0 END) AS total_revenue "
                + "FROM Orders";

        String profitSql = "SELECT "
                + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS total_profit "
                + "FROM Orders o "
                + "JOIN Order_Detail od ON o.order_id = od.order_id "
                + "WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? "
                + "AND MONTH(ISNULL(o.completed_at, o.created_at)) = ? "
                + "AND o.order_status = 'COMPLETED'";

        try (PreparedStatement orderPs = connection.prepareStatement(orderSql);
             PreparedStatement profitPs = connection.prepareStatement(profitSql)) {

            orderPs.setInt(1, year);
            orderPs.setInt(2, month);
            orderPs.setInt(3, year);
            orderPs.setInt(4, month);
            orderPs.setInt(5, year);
            orderPs.setInt(6, month);
            orderPs.setInt(7, year);
            orderPs.setInt(8, month);

            try (ResultSet rs = orderPs.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalOrders(rs.getInt("total_orders"));
                    summary.setCompletedOrders(rs.getInt("completed_orders"));
                    summary.setCancelledOrders(rs.getInt("cancelled_orders"));
                    summary.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                }
            }

            profitPs.setInt(1, year);
            profitPs.setInt(2, month);

            try (ResultSet rs = profitPs.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalProfit(rs.getBigDecimal("total_profit"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return summary;
    }

    public List<Map<String, Object>> getRevenueProfitChartData(int year) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT m.month_no, "
                + "ISNULL(r.revenue, 0) AS revenue, "
                + "ISNULL(p.profit, 0) AS profit "
                + "FROM (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12)) AS m(month_no) "
                + "LEFT JOIN ( "
                + "    SELECT MONTH(ISNULL(completed_at, created_at)) AS month_no, SUM(total_amount) AS revenue "
                + "    FROM Orders "
                + "    WHERE YEAR(ISNULL(completed_at, created_at)) = ? AND order_status = 'COMPLETED' "
                + "    GROUP BY MONTH(ISNULL(completed_at, created_at)) "
                + ") r ON m.month_no = r.month_no "
                + "LEFT JOIN ( "
                + "    SELECT MONTH(ISNULL(o.completed_at, o.created_at)) AS month_no, "
                + "           SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS profit "
                + "    FROM Orders o "
                + "    JOIN Order_Detail od ON o.order_id = od.order_id "
                + "    WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? AND o.order_status = 'COMPLETED' "
                + "    GROUP BY MONTH(ISNULL(o.completed_at, o.created_at)) "
                + ") p ON m.month_no = p.month_no "
                + "ORDER BY m.month_no";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("label", "Thg " + rs.getInt("month_no"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    row.put("profit", rs.getBigDecimal("profit"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public List<Map<String, Object>> getOrderStatusChartData(int year) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT m.month_no, "
                + "ISNULL(t.total_orders, 0) AS total_orders, "
                + "ISNULL(c.completed_orders, 0) AS completed_orders, "
                + "ISNULL(x.cancelled_orders, 0) AS cancelled_orders "
                + "FROM (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12)) AS m(month_no) "
                + "LEFT JOIN ( "
                + "    SELECT MONTH(created_at) AS month_no, "
                + "           COUNT(*) AS total_orders "
                + "    FROM Orders "
                + "    WHERE YEAR(created_at) = ? "
                + "    GROUP BY MONTH(created_at) "
                + ") t ON m.month_no = t.month_no "
                + "LEFT JOIN ( "
                + "    SELECT MONTH(ISNULL(completed_at, created_at)) AS month_no, "
                + "           COUNT(*) AS completed_orders "
                + "    FROM Orders "
                + "    WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                + "      AND order_status = 'COMPLETED' "
                + "    GROUP BY MONTH(ISNULL(completed_at, created_at)) "
                + ") c ON m.month_no = c.month_no "
                + "LEFT JOIN ( "
                + "    SELECT MONTH(created_at) AS month_no, "
                + "           COUNT(*) AS cancelled_orders "
                + "    FROM Orders "
                + "    WHERE YEAR(created_at) = ? "
                + "      AND order_status = 'CANCELLED' "
                + "    GROUP BY MONTH(created_at) "
                + ") x ON m.month_no = x.month_no "
                + "ORDER BY m.month_no";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, year);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("label", "Thg " + rs.getInt("month_no"));
                    row.put("totalOrders", rs.getInt("total_orders"));
                    row.put("completedOrders", rs.getInt("completed_orders"));
                    row.put("cancelledOrders", rs.getInt("cancelled_orders"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public List<Map<String, Object>> getTopSellingProducts(int year, int month, int limit) {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT TOP (?) "
                + "p.product_id, "
                + "p.name AS product_name, "
                + "SUM(od.quantity) AS total_sold, "
                + "SUM(od.price_at_order * od.quantity) AS revenue, "
                + "(SELECT TOP 1 pi.image_url "
                + " FROM Product_Image pi "
                + " WHERE pi.product_id = p.product_id "
                + " ORDER BY pi.is_main DESC, pi.image_id ASC) AS image_url "
                + "FROM Orders o "
                + "JOIN Order_Detail od ON o.order_id = od.order_id "
                + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                + "JOIN Product p ON pv.product_id = p.product_id "
                + "WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? "
                + "AND MONTH(ISNULL(o.completed_at, o.created_at)) = ? "
                + "AND o.order_status = 'COMPLETED' "
                + "GROUP BY p.product_id, p.name "
                + "ORDER BY total_sold DESC, revenue DESC, p.product_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", rs.getInt("product_id"));
                    row.put("productName", rs.getString("product_name"));
                    row.put("totalSold", rs.getInt("total_sold"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    row.put("imageUrl", rs.getString("image_url"));
                    products.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<ProductVariant> getLowStockProducts(int threshold, int limit) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT TOP (?) "
                + "pv.variant_id, pv.product_id, pv.variant_name, pv.price, pv.stock, pv.status, pv.avg_cost, "
                + "p.name AS product_name, "
                + "(SELECT TOP 1 pi.image_url "
                + " FROM Product_Image pi "
                + " WHERE pi.product_id = p.product_id "
                + " ORDER BY pi.is_main DESC, pi.image_id ASC) AS image_url "
                + "FROM Product_Variant pv "
                + "JOIN Product p ON pv.product_id = p.product_id "
                + "WHERE pv.status = 1 AND p.status = 1 AND pv.stock <= ? "
                + "ORDER BY pv.stock ASC, pv.variant_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant variant = new ProductVariant();
                    variant.setVariantId(rs.getInt("variant_id"));
                    variant.setProductId(rs.getInt("product_id"));
                    variant.setVariantName(rs.getString("variant_name"));
                    variant.setPrice(rs.getBigDecimal("price"));
                    variant.setStock(rs.getInt("stock"));
                    variant.setStatus(rs.getBoolean("status"));
                    variant.setImportPrice(rs.getBigDecimal("avg_cost"));
                    variant.setProductName(rs.getString("product_name"));
                    variant.setImageUrl(rs.getString("image_url"));
                    variants.add(variant);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return variants;
    }
}
