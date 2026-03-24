package DALs;

import Model.MonthlyStatisticSummary;
import Model.ProductVariant;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public List<Map<String, Object>> getRevenueProfitChartData(int year, int month) {
        List<Map<String, Object>> rows = createDayBucketRows(year, month);
        Map<Integer, Map<String, Object>> rowsByDay = toDayRowMap(rows);

        String revenueSql = "SELECT DAY(ISNULL(completed_at, created_at)) AS day_no, SUM(total_amount) AS revenue "
                + "FROM Orders "
                + "WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                + "AND MONTH(ISNULL(completed_at, created_at)) = ? "
                + "AND order_status = 'COMPLETED' "
                + "GROUP BY DAY(ISNULL(completed_at, created_at))";

        String profitSql = "SELECT DAY(ISNULL(o.completed_at, o.created_at)) AS day_no, "
                + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS profit "
                + "FROM Orders o "
                + "JOIN Order_Detail od ON o.order_id = od.order_id "
                + "WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? "
                + "AND MONTH(ISNULL(o.completed_at, o.created_at)) = ? "
                + "AND o.order_status = 'COMPLETED' "
                + "GROUP BY DAY(ISNULL(o.completed_at, o.created_at))";

        try (PreparedStatement revenuePs = connection.prepareStatement(revenueSql);
             PreparedStatement profitPs = connection.prepareStatement(profitSql)) {

            revenuePs.setInt(1, year);
            revenuePs.setInt(2, month);
            try (ResultSet rs = revenuePs.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day_no");
                    Map<String, Object> row = rowsByDay.get(day);
                    if (row != null) {
                        row.put("revenue", rs.getBigDecimal("revenue"));
                    }
                }
            }

            profitPs.setInt(1, year);
            profitPs.setInt(2, month);
            try (ResultSet rs = profitPs.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day_no");
                    Map<String, Object> row = rowsByDay.get(day);
                    if (row != null) {
                        row.put("profit", rs.getBigDecimal("profit"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public List<Map<String, Object>> getOrderStatusChartData(int year, int month) {
        List<Map<String, Object>> rows = createDayBucketRows(year, month);
        Map<Integer, Map<String, Object>> rowsByDay = toDayRowMap(rows);

        String totalSql = "SELECT DAY(created_at) AS day_no, COUNT(*) AS total_orders "
                + "FROM Orders "
                + "WHERE YEAR(created_at) = ? AND MONTH(created_at) = ? "
                + "GROUP BY DAY(created_at)";

        String completedSql = "SELECT DAY(ISNULL(completed_at, created_at)) AS day_no, COUNT(*) AS completed_orders "
                + "FROM Orders "
                + "WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                + "AND MONTH(ISNULL(completed_at, created_at)) = ? "
                + "AND order_status = 'COMPLETED' "
                + "GROUP BY DAY(ISNULL(completed_at, created_at))";

        String cancelledSql = "SELECT DAY(created_at) AS day_no, COUNT(*) AS cancelled_orders "
                + "FROM Orders "
                + "WHERE YEAR(created_at) = ? "
                + "AND MONTH(created_at) = ? "
                + "AND order_status = 'CANCELLED' "
                + "GROUP BY DAY(created_at)";

        try (PreparedStatement totalPs = connection.prepareStatement(totalSql);
             PreparedStatement completedPs = connection.prepareStatement(completedSql);
             PreparedStatement cancelledPs = connection.prepareStatement(cancelledSql)) {

            totalPs.setInt(1, year);
            totalPs.setInt(2, month);
            try (ResultSet rs = totalPs.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day_no");
                    Map<String, Object> row = rowsByDay.get(day);
                    if (row != null) {
                        row.put("totalOrders", rs.getInt("total_orders"));
                    }
                }
            }

            completedPs.setInt(1, year);
            completedPs.setInt(2, month);
            try (ResultSet rs = completedPs.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day_no");
                    Map<String, Object> row = rowsByDay.get(day);
                    if (row != null) {
                        row.put("completedOrders", rs.getInt("completed_orders"));
                    }
                }
            }

            cancelledPs.setInt(1, year);
            cancelledPs.setInt(2, month);
            try (ResultSet rs = cancelledPs.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day_no");
                    Map<String, Object> row = rowsByDay.get(day);
                    if (row != null) {
                        row.put("cancelledOrders", rs.getInt("cancelled_orders"));
                    }
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
                    row.put("imageUrl", normalizeProductImageUrl(rs.getString("image_url")));
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
                    variant.setImageUrl(normalizeProductImageUrl(rs.getString("image_url")));
                    variants.add(variant);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return variants;
    }

    private List<Map<String, Object>> createDayBucketRows(int year, int month) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Integer day : getStatisticDays(year, month)) {
            Map<String, Object> row = new HashMap<>();
            row.put("day", day);
            row.put("label", "Day " + day);
            row.put("revenue", java.math.BigDecimal.ZERO);
            row.put("profit", java.math.BigDecimal.ZERO);
            row.put("totalOrders", 0);
            row.put("completedOrders", 0);
            row.put("cancelledOrders", 0);
            rows.add(row);
        }
        return rows;
    }

    private Map<Integer, Map<String, Object>> toDayRowMap(List<Map<String, Object>> rows) {
        Map<Integer, Map<String, Object>> map = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            map.put((Integer) row.get("day"), row);
        }
        return map;
    }

    private List<Integer> getStatisticDays(int year, int month) {
        int lastDay = YearMonth.of(year, month).lengthOfMonth();
        List<Integer> days = new ArrayList<>(Arrays.asList(1, 5, 10, 15, 20, 25));
        if (!days.contains(lastDay)) {
            days.add(lastDay);
        }
        return days;
    }

    private String normalizeProductImageUrl(String imageUrl) {
        if (imageUrl == null) {
            return null;
        }

        String normalized = imageUrl.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        normalized = normalized.replace('\\', '/');

        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        int assetsImgIndex = normalized.indexOf("assets/img/");
        if (assetsImgIndex >= 0) {
            return normalized.substring(assetsImgIndex + "assets/img/".length());
        }

        return normalized;
    }
}
