package Controllers.manager;

import DALs.StatisticDAO;
import DALs.StatisticReportDAO;
import Model.Manager;
import Model.MonthlyStatisticSummary;
import Model.ProductVariant;
import Model.StatisticReport;
import Model.StatisticReportItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminStatisticReportController extends HttpServlet {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 10;
    private static final int DEFAULT_TOP_SELLING_LIMIT = 5;
    private static final int DEFAULT_LOW_STOCK_LIMIT = 5;

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
        String action = trimToNull(request.getParameter("action"));
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
        String action = trimToNull(request.getParameter("action"));
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
        YearMonth defaultPeriod = getDefaultPeriod();
        request.setAttribute("reportList", statisticReportDAO.getAllReports());
        request.setAttribute("selectedReport", selectedReport);
        request.setAttribute("selectedMonth", defaultPeriod.getMonthValue());
        request.setAttribute("selectedYear", defaultPeriod.getYear());
        request.setAttribute("currentDate", LocalDate.now());
        request.setAttribute("currentView", "statistic-report");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-statistic-report-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void showReportDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String reportIdRaw = trimToNull(request.getParameter("id"));
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

        String reportName = trimToNull(request.getParameter("reportName"));
        String note = trimToNull(request.getParameter("note"));
        YearMonth defaultPeriod = getDefaultPeriod();

        int month = parseIntOrDefault(request.getParameter("month"), defaultPeriod.getMonthValue());
        int year = parseIntOrDefault(request.getParameter("year"), defaultPeriod.getYear());

        if (month < 1 || month > 12) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=invalidPeriod");
            return;
        }

        YearMonth selectedPeriod;
        try {
            selectedPeriod = YearMonth.of(year, month);
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=invalidPeriod");
            return;
        }

        if (selectedPeriod.isAfter(YearMonth.now())) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?error=invalidPeriod");
            return;
        }

        if (reportName == null) {
            reportName = "Statistic Report " + month + "/" + year;
        }

        MonthlyStatisticSummary summary = statisticDAO.getMonthlySummary(year, month);
        List<Map<String, Object>> topSellingProducts = statisticDAO.getTopSellingProducts(year, month, DEFAULT_TOP_SELLING_LIMIT);
        List<ProductVariant> lowStockProducts = statisticDAO.getLowStockProducts(DEFAULT_LOW_STOCK_THRESHOLD, DEFAULT_LOW_STOCK_LIMIT);

        StatisticReport report = new StatisticReport();
        report.setReportName(reportName);
        report.setReportMonth(month);
        report.setReportYear(year);
        report.setTotalRevenue(summary.getTotalRevenue());
        report.setTotalProfit(summary.getTotalProfit());
        report.setTotalOrders(summary.getTotalOrders());
        report.setCompletedOrders(summary.getCompletedOrders());
        report.setCancelledOrders(summary.getCancelledOrders());
        report.setNote(note);
        report.setCreatedBy(manager.getManagerId());
        report.setStatus(true);

        List<StatisticReportItem> items = buildReportItems(summary, topSellingProducts, lowStockProducts);

        boolean created = statisticReportDAO.createReport(report, items);
        response.sendRedirect(request.getContextPath() + "/admin/manage-statistic-report?success="
                + (created ? "created" : "createFailed"));
    }

    private void deleteReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String reportIdRaw = trimToNull(request.getParameter("id"));
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

    private List<StatisticReportItem> buildReportItems(MonthlyStatisticSummary summary,
            List<Map<String, Object>> topSellingProducts, List<ProductVariant> lowStockProducts) {
        List<StatisticReportItem> items = new ArrayList<>();
        int displayOrder = 1;

        items.add(createItem("SUMMARY", "Total Revenue", summary.getTotalRevenue(), "Monthly revenue snapshot", displayOrder++));
        items.add(createItem("SUMMARY", "Total Profit", summary.getTotalProfit(), "Monthly profit snapshot", displayOrder++));
        items.add(createItem("SUMMARY", "Total Orders", BigDecimal.valueOf(summary.getTotalOrders()), "Orders created in selected month", displayOrder++));
        items.add(createItem("SUMMARY", "Completed Orders", BigDecimal.valueOf(summary.getCompletedOrders()), "Completed orders in selected month", displayOrder++));
        items.add(createItem("SUMMARY", "Cancelled Orders", BigDecimal.valueOf(summary.getCancelledOrders()), "Cancelled orders in selected month", displayOrder++));

        for (Map<String, Object> product : topSellingProducts) {
            StatisticReportItem item = new StatisticReportItem();
            item.setItemType("TOP_SELLING_PRODUCT");
            item.setItemLabel(String.valueOf(product.get("productName")));
            item.setItemValue(toBigDecimal(product.get("totalSold")));
            item.setItemText("Revenue: " + toBigDecimal(product.get("revenue")).toPlainString());
            item.setRefId(product.get("productId") instanceof Number ? ((Number) product.get("productId")).intValue() : null);
            item.setDisplayOrder(displayOrder++);
            items.add(item);
        }

        for (ProductVariant variant : lowStockProducts) {
            StatisticReportItem item = new StatisticReportItem();
            item.setItemType("LOW_STOCK_PRODUCT");
            item.setItemLabel(variant.getProductName() + " - " + variant.getVariantName());
            item.setItemValue(BigDecimal.valueOf(variant.getStock()));
            item.setItemText("Current stock is below threshold");
            item.setRefId(variant.getVariantId());
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

    private YearMonth getDefaultPeriod() {
        return YearMonth.now();
    }

    private BigDecimal toBigDecimal(Object value) {
        switch (value == null ? "NULL" : value.getClass().getSimpleName()) {
            case "BigDecimal":
                return (BigDecimal) value;
            case "Integer":
            case "Long":
            case "Double":
            case "Float":
            case "Short":
            case "Byte":
                return BigDecimal.valueOf(((Number) value).doubleValue());
            default:
                return BigDecimal.ZERO;
        }
    }

    private int parseIntOrDefault(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
