package Controllers.manager;

import DALs.StatisticDAO;
import DALs.StatisticReportDAO;
import Model.Manager;
import Model.MonthlyStatisticSummary;
import Model.StatisticReport;
import Model.StatisticReportItem;
import Utils.StatisticUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminStatisticReportController extends HttpServlet {

    private StatisticDAO statisticDAO;
    private StatisticReportDAO statisticReportDAO;

    @Override
    public void init() throws ServletException {
        statisticDAO = new StatisticDAO();
        statisticReportDAO = new StatisticReportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = StatisticUtils.trimToNull(request.getParameter("action"));
        String actionKey = action == null ? "" : action.toLowerCase();

        switch (actionKey) {
            case "detail":
                showReportDetail(request, response);
                return;
            default:
                showReportList(request, response, null);
                return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = StatisticUtils.trimToNull(request.getParameter("action"));
        String actionKey = action == null ? "" : action.toLowerCase();

        switch (actionKey) {
            case "create":
                createReport(request, response);
                return;
            case "delete":
                deleteReport(request, response);
                return;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=invalidAction");
                return;
        }
    }

    private void showReportList(HttpServletRequest request, HttpServletResponse response, StatisticReport selectedReport)
            throws ServletException, IOException {
        int defaultYear = StatisticUtils.getDefaultPeriod().getYear();
        int defaultMonth = StatisticUtils.getDefaultPeriod().getMonthValue();
        request.setAttribute("reportList", statisticReportDAO.getAllReports());
        request.setAttribute("selectedReport", selectedReport);
        request.setAttribute("selectedPeriodType", StatisticUtils.PERIOD_MONTH);
        request.setAttribute("selectedMonth", defaultMonth);
        request.setAttribute("selectedYear", defaultYear);
        request.setAttribute("currentView", "statistic-report");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-statistic-report-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void showReportDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String reportIdRaw = StatisticUtils.trimToNull(request.getParameter("id"));
        if (reportIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=notFound");
            return;
        }

        try {
            int reportId = Integer.parseInt(reportIdRaw);
            StatisticReport selectedReport = statisticReportDAO.getReportById(reportId);
            if (selectedReport == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=notFound");
                return;
            }

            prepareDetailView(request, selectedReport);
            showReportList(request, response, selectedReport);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=notFound");
        }
    }

    private void createReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/manager-auth");
            return;
        }

        String reportName = StatisticUtils.trimToNull(request.getParameter("reportName"));
        String note = StatisticUtils.trimToNull(request.getParameter("note"));
        String periodType = StatisticUtils.normalizePeriodType(request.getParameter("periodType"));

        int defaultYear = StatisticUtils.getDefaultPeriod().getYear();
        int defaultMonth = StatisticUtils.getDefaultPeriod().getMonthValue();
        int year = StatisticUtils.parseIntOrDefault(request.getParameter("year"), defaultYear);
        Integer month = StatisticUtils.isYearPeriod(periodType)
                ? null
                : StatisticUtils.parseIntOrDefault(request.getParameter("month"), defaultMonth);

        if (!StatisticUtils.isValidPeriod(year, month, periodType)) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=invalidPeriod");
            return;
        }

        LocalDateTime periodStart = StatisticUtils.getPeriodStart(year, month, periodType);
        LocalDateTime periodEnd = StatisticUtils.getPeriodEnd(year, month, periodType);

        if (reportName == null) {
            reportName = StatisticUtils.isYearPeriod(periodType)
                    ? "Statistic Report " + year
                    : "Statistic Report " + month + "/" + year;
        }

        MonthlyStatisticSummary summary = statisticDAO.getSummary(year, month, periodType);
        List<Map<String, Object>> soldProductDetails = statisticDAO.getSoldProductDetails(year, month, periodType);
        List<Map<String, Object>> revenueProfitChart = statisticDAO.getRevenueProfitChartData(year, month, periodType);
        List<Map<String, Object>> orderChart = statisticDAO.getOrderStatusChartData(year, month, periodType);

        StatisticReport report = new StatisticReport();
        report.setReportName(reportName);
        report.setPeriodType(periodType);
        report.setReportMonth(month);
        report.setReportYear(year);
        report.setTotalRevenue(summary.getTotalRevenue());
        report.setTotalProfit(summary.getTotalProfit());
        report.setTotalOrders(summary.getTotalOrders());
        report.setCompletedOrders(summary.getCompletedOrders());
        report.setCancelledOrders(summary.getCancelledOrders());
        report.setNote(note);
        report.setPeriodStartAt(StatisticUtils.toDate(periodStart));
        report.setPeriodEndAt(StatisticUtils.toDate(periodEnd));
        report.setCreatedBy(manager.getManagerId());
        report.setStatus(true);

        List<StatisticReportItem> items = buildReportItems(summary, soldProductDetails, revenueProfitChart, orderChart);

        boolean created = statisticReportDAO.createReport(report, items);
        response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?success="
                + (created ? "created" : "createFailed"));
    }

    private void deleteReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String reportIdRaw = StatisticUtils.trimToNull(request.getParameter("id"));
        if (reportIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=notFound");
            return;
        }

        try {
            int reportId = Integer.parseInt(reportIdRaw);
            boolean deleted = statisticReportDAO.deleteReport(reportId);
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?success="
                    + (deleted ? "deleted" : "deleteFailed"));
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=notFound");
        }
    }

    private void prepareDetailView(HttpServletRequest request, StatisticReport selectedReport) {
        List<StatisticReportItem> items = selectedReport.getItems();
        List<StatisticReportItem> soldProductItems = filterItemsByType(items, "SOLD_PRODUCT_DETAIL");
        List<StatisticReportItem> revenueChartItems = filterItemsByType(items, "REVENUE_CHART");
        List<StatisticReportItem> profitChartItems = filterItemsByType(items, "PROFIT_CHART");
        List<StatisticReportItem> orderChartItems = filterItemsByType(items, "ORDER_CHART");

        request.setAttribute("reportSoldProductItems", soldProductItems);
        request.setAttribute("reportRevenueChartLabelsJson", toChartLabelsJson(revenueChartItems));
        request.setAttribute("reportRevenueChartDataJson", toChartValuesJson(revenueChartItems));
        request.setAttribute("reportProfitChartDataJson", toChartValuesJson(profitChartItems));
        request.setAttribute("reportOrderChartLabelsJson", toChartLabelsJson(orderChartItems));
        request.setAttribute("reportOrderTotalChartDataJson", toChartValuesJson(orderChartItems));
        request.setAttribute("reportOrderCompletedChartDataJson", toOrderMetricJson(orderChartItems, "completed"));
        request.setAttribute("reportOrderCancelledChartDataJson", toOrderMetricJson(orderChartItems, "cancelled"));
    }

    private List<StatisticReportItem> buildReportItems(MonthlyStatisticSummary summary,
            List<Map<String, Object>> soldProductDetails,
            List<Map<String, Object>> revenueProfitChart,
            List<Map<String, Object>> orderChart) {
        List<StatisticReportItem> items = new ArrayList<>();
        int displayOrder = 1;

        items.add(createItem("SUMMARY", "Total Revenue", summary.getTotalRevenue(), "Period revenue snapshot", displayOrder++));
        items.add(createItem("SUMMARY", "Total Profit", summary.getTotalProfit(), "Period profit snapshot", displayOrder++));
        items.add(createItem("SUMMARY", "Total Orders", BigDecimal.valueOf(summary.getTotalOrders()), "Orders created in selected period", displayOrder++));
        items.add(createItem("SUMMARY", "Completed Orders", BigDecimal.valueOf(summary.getCompletedOrders()), "Completed orders in selected period", displayOrder++));
        items.add(createItem("SUMMARY", "Cancelled Orders", BigDecimal.valueOf(summary.getCancelledOrders()), "Cancelled orders in selected period", displayOrder++));

        for (Map<String, Object> product : soldProductDetails) {
            StatisticReportItem item = new StatisticReportItem();
            item.setItemType("SOLD_PRODUCT_DETAIL");
            item.setItemLabel(String.valueOf(product.get("productName")) + " - " + String.valueOf(product.get("variantName")));
            item.setItemValue(StatisticUtils.toBigDecimal(product.get("totalSold")));
            item.setItemText("Revenue=" + StatisticUtils.toBigDecimal(product.get("revenue")).toPlainString()
                    + ";Profit=" + StatisticUtils.toBigDecimal(product.get("profit")).toPlainString());
            item.setRefId(product.get("variantId") instanceof Number ? ((Number) product.get("variantId")).intValue() : null);
            item.setDisplayOrder(displayOrder++);
            items.add(item);
        }

        for (Map<String, Object> point : revenueProfitChart) {
            items.add(createItem("REVENUE_CHART", String.valueOf(point.get("label")),
                    StatisticUtils.toBigDecimal(point.get("revenue")), null, displayOrder++));
            items.add(createItem("PROFIT_CHART", String.valueOf(point.get("label")),
                    StatisticUtils.toBigDecimal(point.get("profit")), null, displayOrder++));
        }

        for (Map<String, Object> point : orderChart) {
            JSONObject payload = new JSONObject();
            payload.put("completed", point.get("completedOrders") instanceof Number ? ((Number) point.get("completedOrders")).intValue() : 0);
            payload.put("cancelled", point.get("cancelledOrders") instanceof Number ? ((Number) point.get("cancelledOrders")).intValue() : 0);

            StatisticReportItem item = new StatisticReportItem();
            item.setItemType("ORDER_CHART");
            item.setItemLabel(String.valueOf(point.get("label")));
            item.setItemValue(StatisticUtils.toBigDecimal(point.get("totalOrders")));
            item.setItemText(payload.toString());
            item.setDisplayOrder(displayOrder++);
            items.add(item);
        }

        return items;
    }

    private StatisticReportItem createItem(String type, String label, BigDecimal value, String text, int displayOrder) {
        StatisticReportItem item = new StatisticReportItem();
        item.setItemType(type);
        item.setItemLabel(label);
        item.setItemValue(value);
        item.setItemText(text);
        item.setDisplayOrder(displayOrder);
        return item;
    }

    private List<StatisticReportItem> filterItemsByType(List<StatisticReportItem> items, String type) {
        List<StatisticReportItem> filtered = new ArrayList<>();
        for (StatisticReportItem item : items) {
            if (type.equals(item.getItemType())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private String toChartLabelsJson(List<StatisticReportItem> items) {
        JSONArray array = new JSONArray();
        for (StatisticReportItem item : items) {
            array.put(item.getItemLabel());
        }
        return array.toString();
    }

    private String toChartValuesJson(List<StatisticReportItem> items) {
        JSONArray array = new JSONArray();
        for (StatisticReportItem item : items) {
            array.put(item.getItemValue() == null ? 0 : item.getItemValue().doubleValue());
        }
        return array.toString();
    }

    private String toOrderMetricJson(List<StatisticReportItem> items, String key) {
        JSONArray array = new JSONArray();
        for (StatisticReportItem item : items) {
            if (item.getItemText() == null || item.getItemText().trim().isEmpty()) {
                array.put(0);
                continue;
            }
            JSONObject json = new JSONObject(item.getItemText());
            array.put(json.optInt(key, 0));
        }
        return array.toString();
    }
}

