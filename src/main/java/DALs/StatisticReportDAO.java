package DALs;

import Model.StatisticReport;
import Model.StatisticReportItem;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StatisticReportDAO extends DBContext {

    public List<StatisticReport> getAllReports() {
        List<StatisticReport> reports = new ArrayList<>();
        String sql = "SELECT sr.report_id, sr.report_name, sr.period_type, sr.report_month, sr.report_year, "
                + "sr.total_revenue, sr.total_profit, sr.total_orders, sr.completed_orders, sr.cancelled_orders, "
                + "sr.note, sr.period_start_at, sr.period_end_at, sr.created_by, sr.status, sr.created_at, sr.updated_at, "
                + "m.full_name AS created_by_name "
                + "FROM Statistic_Report sr "
                + "JOIN Manager m ON sr.created_by = m.manager_id "
                + "ORDER BY sr.report_year DESC, ISNULL(sr.report_month, 0) DESC, sr.report_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reports.add(mapReport(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }

    public StatisticReport getReportById(int reportId) {
        String reportSql = "SELECT sr.report_id, sr.report_name, sr.period_type, sr.report_month, sr.report_year, "
                + "sr.total_revenue, sr.total_profit, sr.total_orders, sr.completed_orders, sr.cancelled_orders, "
                + "sr.note, sr.period_start_at, sr.period_end_at, sr.created_by, sr.status, sr.created_at, sr.updated_at, "
                + "m.full_name AS created_by_name "
                + "FROM Statistic_Report sr "
                + "JOIN Manager m ON sr.created_by = m.manager_id "
                + "WHERE sr.report_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(reportSql)) {
            ps.setInt(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StatisticReport report = mapReport(rs);
                    report.setItems(getItemsByReportId(reportId));
                    return report;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean createReport(StatisticReport report, List<StatisticReportItem> items) {
        String reportSql = "INSERT INTO Statistic_Report "
                + "(report_name, period_type, report_month, report_year, total_revenue, total_profit, total_orders, "
                + "completed_orders, cancelled_orders, note, period_start_at, period_end_at, created_by, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), NULL)";

        String itemSql = "INSERT INTO Statistic_Report_Item "
                + "(report_id, item_type, item_label, item_value, item_text, ref_id, display_order) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement reportPs = connection.prepareStatement(reportSql, Statement.RETURN_GENERATED_KEYS)) {
                reportPs.setString(1, report.getReportName());
                reportPs.setString(2, report.getPeriodType());
                if (report.getReportMonth() == null) {
                    reportPs.setNull(3, java.sql.Types.INTEGER);
                } else {
                    reportPs.setInt(3, report.getReportMonth());
                }
                reportPs.setInt(4, report.getReportYear());
                reportPs.setBigDecimal(5, report.getTotalRevenue());
                reportPs.setBigDecimal(6, report.getTotalProfit());
                reportPs.setInt(7, report.getTotalOrders());
                reportPs.setInt(8, report.getCompletedOrders());
                reportPs.setInt(9, report.getCancelledOrders());
                reportPs.setString(10, report.getNote());
                reportPs.setTimestamp(11, report.getPeriodStartAt() == null ? null : new java.sql.Timestamp(report.getPeriodStartAt().getTime()));
                reportPs.setTimestamp(12, report.getPeriodEndAt() == null ? null : new java.sql.Timestamp(report.getPeriodEndAt().getTime()));
                reportPs.setInt(13, report.getCreatedBy());
                reportPs.setBoolean(14, report.isStatus());

                if (reportPs.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet keys = reportPs.getGeneratedKeys()) {
                    if (keys.next()) {
                        report.setReportId(keys.getInt(1));
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

            if (items != null && !items.isEmpty()) {
                try (PreparedStatement itemPs = connection.prepareStatement(itemSql)) {
                    for (StatisticReportItem item : items) {
                        itemPs.setInt(1, report.getReportId());
                        itemPs.setString(2, item.getItemType());
                        itemPs.setString(3, item.getItemLabel());
                        itemPs.setBigDecimal(4, item.getItemValue());
                        itemPs.setString(5, item.getItemText());
                        if (item.getRefId() == null) {
                            itemPs.setNull(6, java.sql.Types.INTEGER);
                        } else {
                            itemPs.setInt(6, item.getRefId());
                        }
                        itemPs.setInt(7, item.getDisplayOrder());
                        itemPs.addBatch();
                    }
                    itemPs.executeBatch();
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM Statistic_Report WHERE report_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<StatisticReportItem> getItemsByReportId(int reportId) {
        List<StatisticReportItem> items = new ArrayList<>();
        String sql = "SELECT item_id, report_id, item_type, item_label, item_value, item_text, ref_id, display_order "
                + "FROM Statistic_Report_Item WHERE report_id = ? "
                + "ORDER BY display_order ASC, item_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StatisticReportItem item = new StatisticReportItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setReportId(rs.getInt("report_id"));
                    item.setItemType(rs.getString("item_type"));
                    item.setItemLabel(rs.getString("item_label"));
                    item.setItemValue(rs.getBigDecimal("item_value"));
                    item.setItemText(rs.getString("item_text"));

                    int refId = rs.getInt("ref_id");
                    item.setRefId(rs.wasNull() ? null : refId);
                    item.setDisplayOrder(rs.getInt("display_order"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    private StatisticReport mapReport(ResultSet rs) throws Exception {
        StatisticReport report = new StatisticReport();
        report.setReportId(rs.getInt("report_id"));
        report.setReportName(rs.getString("report_name"));
        report.setPeriodType(rs.getString("period_type"));

        int reportMonth = rs.getInt("report_month");
        report.setReportMonth(rs.wasNull() ? null : reportMonth);
        report.setReportYear(rs.getInt("report_year"));
        report.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        report.setTotalProfit(rs.getBigDecimal("total_profit"));
        report.setTotalOrders(rs.getInt("total_orders"));
        report.setCompletedOrders(rs.getInt("completed_orders"));
        report.setCancelledOrders(rs.getInt("cancelled_orders"));
        report.setNote(rs.getString("note"));
        report.setPeriodStartAt(rs.getTimestamp("period_start_at"));
        report.setPeriodEndAt(rs.getTimestamp("period_end_at"));
        report.setCreatedBy(rs.getInt("created_by"));
        report.setCreatedByName(rs.getString("created_by_name"));
        report.setStatus(rs.getBoolean("status"));
        report.setCreatedAt(rs.getTimestamp("created_at"));
        report.setUpdatedAt(rs.getTimestamp("updated_at"));
        return report;
    }
}
