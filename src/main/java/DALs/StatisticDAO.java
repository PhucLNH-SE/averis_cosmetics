package DALs;

import Model.MonthlyStatisticSummary;
import Utils.DBContext;
import Utils.StatisticUtils;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticDAO extends DBContext {

    public List<Map<String, Object>> getRevenueProfitChartData(int year, int month) {
        return getRevenueProfitChartData(year, Integer.valueOf(month), StatisticUtils.PERIOD_MONTH);
    }

    public List<Map<String, Object>> getOrderStatusChartData(int year, int month) {
        return getOrderStatusChartData(year, Integer.valueOf(month), StatisticUtils.PERIOD_MONTH);
    }

    public List<Map<String, Object>> getTopSellingProducts(int year, int month, int limit) {
        return getTopSellingProducts(year, Integer.valueOf(month), StatisticUtils.PERIOD_MONTH, limit);
    }

    public MonthlyStatisticSummary getSummary(int year, Integer month, String periodType) {
        MonthlyStatisticSummary summary = new MonthlyStatisticSummary();
        summary.setYear(year);
        summary.setMonth(month == null ? 0 : month);

        LocalDateTime periodStart = StatisticUtils.getPeriodStart(year, month, periodType);
        LocalDateTime periodEnd = StatisticUtils.getPeriodEnd(year, month, periodType);

        String orderSql = "SELECT "
                + "SUM(CASE WHEN o.created_at BETWEEN ? AND ? THEN 1 ELSE 0 END) AS total_orders, "
                + "SUM(CASE WHEN o.order_status = 'COMPLETED' "
                + "          AND ISNULL(o.completed_at, o.created_at) BETWEEN ? AND ? THEN 1 ELSE 0 END) AS completed_orders, "
                + "SUM(CASE WHEN o.order_status = 'CANCELLED' "
                + "          AND o.created_at BETWEEN ? AND ? THEN 1 ELSE 0 END) AS cancelled_orders, "
                + "SUM(CASE WHEN o.order_status = 'COMPLETED' "
                + "          AND ISNULL(o.completed_at, o.created_at) BETWEEN ? AND ? THEN o.total_amount ELSE 0 END) AS total_revenue "
                + "FROM Orders o";

        String profitSql = "SELECT "
                + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS total_profit "
                + "FROM Orders o "
                + "JOIN Order_Detail od ON o.order_id = od.order_id "
                + "WHERE o.order_status = 'COMPLETED' "
                + "AND ISNULL(o.completed_at, o.created_at) BETWEEN ? AND ?";

        try (PreparedStatement orderPs = connection.prepareStatement(orderSql);
             PreparedStatement profitPs = connection.prepareStatement(profitSql)) {

            applySameRange(orderPs, 1, periodStart, periodEnd, 4);

            try (ResultSet rs = orderPs.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalOrders(rs.getInt("total_orders"));
                    summary.setCompletedOrders(rs.getInt("completed_orders"));
                    summary.setCancelledOrders(rs.getInt("cancelled_orders"));
                    summary.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                }
            }

            setRange(profitPs, 1, periodStart, periodEnd);
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

    public List<Map<String, Object>> getRevenueProfitChartData(int year, Integer month, String periodType) {
        List<Map<String, Object>> rows = createTimeBucketRows(year, month, periodType);
        Map<Integer, Map<String, Object>> rowsByBucket = toBucketRowMap(rows);

        String revenueSql;
        String profitSql;

        if (StatisticUtils.isYearPeriod(periodType)) {
            revenueSql = "SELECT MONTH(ISNULL(completed_at, created_at)) AS bucket_no, SUM(total_amount) AS revenue "
                    + "FROM Orders "
                    + "WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                    + "AND order_status = 'COMPLETED' "
                    + "GROUP BY MONTH(ISNULL(completed_at, created_at))";

            profitSql = "SELECT MONTH(ISNULL(o.completed_at, o.created_at)) AS bucket_no, "
                    + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS profit "
                    + "FROM Orders o "
                    + "JOIN Order_Detail od ON o.order_id = od.order_id "
                    + "WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? "
                    + "AND o.order_status = 'COMPLETED' "
                    + "GROUP BY MONTH(ISNULL(o.completed_at, o.created_at))";
        } else {
            revenueSql = "SELECT DAY(ISNULL(completed_at, created_at)) AS bucket_no, SUM(total_amount) AS revenue "
                    + "FROM Orders "
                    + "WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                    + "AND MONTH(ISNULL(completed_at, created_at)) = ? "
                    + "AND order_status = 'COMPLETED' "
                    + "GROUP BY DAY(ISNULL(completed_at, created_at))";

            profitSql = "SELECT DAY(ISNULL(o.completed_at, o.created_at)) AS bucket_no, "
                    + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS profit "
                    + "FROM Orders o "
                    + "JOIN Order_Detail od ON o.order_id = od.order_id "
                    + "WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? "
                    + "AND MONTH(ISNULL(o.completed_at, o.created_at)) = ? "
                    + "AND o.order_status = 'COMPLETED' "
                    + "GROUP BY DAY(ISNULL(o.completed_at, o.created_at))";
        }

        try (PreparedStatement revenuePs = connection.prepareStatement(revenueSql);
             PreparedStatement profitPs = connection.prepareStatement(profitSql)) {

            bindPeriodParameters(revenuePs, year, month, periodType);
            try (ResultSet rs = revenuePs.executeQuery()) {
                while (rs.next()) {
                    updateBigDecimalBucket(rowsByBucket, rs.getInt("bucket_no"), "revenue", rs.getBigDecimal("revenue"));
                }
            }

            bindPeriodParameters(profitPs, year, month, periodType);
            try (ResultSet rs = profitPs.executeQuery()) {
                while (rs.next()) {
                    updateBigDecimalBucket(rowsByBucket, rs.getInt("bucket_no"), "profit", rs.getBigDecimal("profit"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public List<Map<String, Object>> getOrderStatusChartData(int year, Integer month, String periodType) {
        List<Map<String, Object>> rows = createTimeBucketRows(year, month, periodType);
        Map<Integer, Map<String, Object>> rowsByBucket = toBucketRowMap(rows);

        String totalSql;
        String completedSql;
        String cancelledSql;

        if (StatisticUtils.isYearPeriod(periodType)) {
            totalSql = "SELECT MONTH(created_at) AS bucket_no, COUNT(*) AS total_orders "
                    + "FROM Orders "
                    + "WHERE YEAR(created_at) = ? "
                    + "GROUP BY MONTH(created_at)";

            completedSql = "SELECT MONTH(ISNULL(completed_at, created_at)) AS bucket_no, COUNT(*) AS completed_orders "
                    + "FROM Orders "
                    + "WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                    + "AND order_status = 'COMPLETED' "
                    + "GROUP BY MONTH(ISNULL(completed_at, created_at))";

            cancelledSql = "SELECT MONTH(created_at) AS bucket_no, COUNT(*) AS cancelled_orders "
                    + "FROM Orders "
                    + "WHERE YEAR(created_at) = ? "
                    + "AND order_status = 'CANCELLED' "
                    + "GROUP BY MONTH(created_at)";
        } else {
            totalSql = "SELECT DAY(created_at) AS bucket_no, COUNT(*) AS total_orders "
                    + "FROM Orders "
                    + "WHERE YEAR(created_at) = ? AND MONTH(created_at) = ? "
                    + "GROUP BY DAY(created_at)";

            completedSql = "SELECT DAY(ISNULL(completed_at, created_at)) AS bucket_no, COUNT(*) AS completed_orders "
                    + "FROM Orders "
                    + "WHERE YEAR(ISNULL(completed_at, created_at)) = ? "
                    + "AND MONTH(ISNULL(completed_at, created_at)) = ? "
                    + "AND order_status = 'COMPLETED' "
                    + "GROUP BY DAY(ISNULL(completed_at, created_at))";

            cancelledSql = "SELECT DAY(created_at) AS bucket_no, COUNT(*) AS cancelled_orders "
                    + "FROM Orders "
                    + "WHERE YEAR(created_at) = ? "
                    + "AND MONTH(created_at) = ? "
                    + "AND order_status = 'CANCELLED' "
                    + "GROUP BY DAY(created_at)";
        }

        try (PreparedStatement totalPs = connection.prepareStatement(totalSql);
             PreparedStatement completedPs = connection.prepareStatement(completedSql);
             PreparedStatement cancelledPs = connection.prepareStatement(cancelledSql)) {

            bindPeriodParameters(totalPs, year, month, periodType);
            try (ResultSet rs = totalPs.executeQuery()) {
                while (rs.next()) {
                    updateIntBucket(rowsByBucket, rs.getInt("bucket_no"), "totalOrders", rs.getInt("total_orders"));
                }
            }

            bindPeriodParameters(completedPs, year, month, periodType);
            try (ResultSet rs = completedPs.executeQuery()) {
                while (rs.next()) {
                    updateIntBucket(rowsByBucket, rs.getInt("bucket_no"), "completedOrders", rs.getInt("completed_orders"));
                }
            }

            bindPeriodParameters(cancelledPs, year, month, periodType);
            try (ResultSet rs = cancelledPs.executeQuery()) {
                while (rs.next()) {
                    updateIntBucket(rowsByBucket, rs.getInt("bucket_no"), "cancelledOrders", rs.getInt("cancelled_orders"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public List<Map<String, Object>> getSoldProductDetails(int year, Integer month, String periodType) {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql;

        if (StatisticUtils.isYearPeriod(periodType)) {
            sql = "SELECT "
                    + "p.product_id, pv.variant_id, "
                    + "p.name AS product_name, pv.variant_name, "
                    + "SUM(od.quantity) AS total_sold, "
                    + "SUM(od.price_at_order * od.quantity) AS revenue, "
                    + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS profit, "
                    + "(SELECT TOP 1 pi.image_url "
                    + " FROM Product_Image pi "
                    + " WHERE pi.product_id = p.product_id "
                    + " ORDER BY pi.is_main DESC, pi.image_id ASC) AS image_url "
                    + "FROM Orders o "
                    + "JOIN Order_Detail od ON o.order_id = od.order_id "
                    + "JOIN Product_Variant pv ON od.variant_id = pv.variant_id "
                    + "JOIN Product p ON pv.product_id = p.product_id "
                    + "WHERE YEAR(ISNULL(o.completed_at, o.created_at)) = ? "
                    + "AND o.order_status = 'COMPLETED' "
                    + "GROUP BY p.product_id, p.name, pv.variant_id, pv.variant_name "
                    + "ORDER BY total_sold DESC, revenue DESC, p.product_id DESC, pv.variant_id DESC";
        } else {
            sql = "SELECT "
                    + "p.product_id, pv.variant_id, "
                    + "p.name AS product_name, pv.variant_name, "
                    + "SUM(od.quantity) AS total_sold, "
                    + "SUM(od.price_at_order * od.quantity) AS revenue, "
                    + "SUM((od.price_at_order - ISNULL(od.cost_price_at_order, 0)) * od.quantity) AS profit, "
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
                    + "GROUP BY p.product_id, p.name, pv.variant_id, pv.variant_name "
                    + "ORDER BY total_sold DESC, revenue DESC, p.product_id DESC, pv.variant_id DESC";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindPeriodParameters(ps, year, month, periodType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", rs.getInt("product_id"));
                    row.put("variantId", rs.getInt("variant_id"));
                    row.put("productName", rs.getString("product_name"));
                    row.put("variantName", rs.getString("variant_name"));
                    row.put("totalSold", rs.getInt("total_sold"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    row.put("profit", rs.getBigDecimal("profit"));
                    row.put("imageUrl", normalizeProductImageUrl(rs.getString("image_url")));
                    products.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<Map<String, Object>> getTopSellingProducts(int year, Integer month, String periodType, int limit) {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql;

        if (StatisticUtils.isYearPeriod(periodType)) {
            sql = "SELECT TOP (?) "
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
                    + "AND o.order_status = 'COMPLETED' "
                    + "GROUP BY p.product_id, p.name "
                    + "ORDER BY total_sold DESC, revenue DESC, p.product_id DESC";
        } else {
            sql = "SELECT TOP (?) "
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
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            if (StatisticUtils.isYearPeriod(periodType)) {
                ps.setInt(2, year);
            } else {
                ps.setInt(2, year);
                ps.setInt(3, month);
            }

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

    private void bindPeriodParameters(PreparedStatement ps, int year, Integer month, String periodType) throws Exception {
        ps.setInt(1, year);
        if (StatisticUtils.isMonthPeriod(periodType)) {
            ps.setInt(2, month);
        }
    }

    private void setRange(PreparedStatement ps, int startIndex, LocalDateTime periodStart, LocalDateTime periodEnd) throws Exception {
        ps.setTimestamp(startIndex, Timestamp.valueOf(periodStart));
        ps.setTimestamp(startIndex + 1, Timestamp.valueOf(periodEnd));
    }

    private void applySameRange(PreparedStatement ps, int startIndex, LocalDateTime periodStart,
            LocalDateTime periodEnd, int repetitions) throws Exception {
        for (int i = 0; i < repetitions; i++) {
            setRange(ps, startIndex + (i * 2), periodStart, periodEnd);
        }
    }

    private List<Map<String, Object>> createTimeBucketRows(int year, Integer month, String periodType) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Integer bucket : getStatisticBuckets(year, month, periodType)) {
            Map<String, Object> row = new HashMap<>();
            row.put("bucket", bucket);
            row.put("label", StatisticUtils.isYearPeriod(periodType) ? "Month " + bucket : "Day " + bucket);
            row.put("revenue", BigDecimal.ZERO);
            row.put("profit", BigDecimal.ZERO);
            row.put("totalOrders", 0);
            row.put("completedOrders", 0);
            row.put("cancelledOrders", 0);
            rows.add(row);
        }
        return rows;
    }

    private Map<Integer, Map<String, Object>> toBucketRowMap(List<Map<String, Object>> rows) {
        Map<Integer, Map<String, Object>> map = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            map.put((Integer) row.get("bucket"), row);
        }
        return map;
    }

    private List<Integer> getStatisticBuckets(int year, Integer month, String periodType) {
        List<Integer> buckets = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if (StatisticUtils.isYearPeriod(periodType)) {
            int endMonth = year == today.getYear() ? today.getMonthValue() : 12;
            for (int monthNo = 1; monthNo <= endMonth; monthNo++) {
                buckets.add(monthNo);
            }
            return buckets;
        }

        int endDay = (year == today.getYear() && month == today.getMonthValue())
                ? today.getDayOfMonth()
                : YearMonth.of(year, month).lengthOfMonth();

        for (int day = 1; day <= endDay; day++) {
            buckets.add(day);
        }
        return buckets;
    }

    private void updateBigDecimalBucket(Map<Integer, Map<String, Object>> rowsByBucket, int bucket,
            String key, BigDecimal value) {
        Map<String, Object> row = rowsByBucket.get(bucket);
        if (row != null) {
            row.put(key, value == null ? BigDecimal.ZERO : value);
        }
    }

    private void updateIntBucket(Map<Integer, Map<String, Object>> rowsByBucket, int bucket,
            String key, int value) {
        Map<String, Object> row = rowsByBucket.get(bucket);
        if (row != null) {
            row.put(key, value);
        }
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


