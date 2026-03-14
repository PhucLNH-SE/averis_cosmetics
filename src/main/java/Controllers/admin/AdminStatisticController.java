package Controllers.admin;

import DALs.StatisticDAO;
import Model.MonthlyStatisticSummary;
import Model.ProductVariant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public class AdminStatisticController extends HttpServlet {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 10;
    private static final int DEFAULT_TOP_SELLING_LIMIT = 5;
    private static final int DEFAULT_LOW_STOCK_LIMIT = 5;

    private StatisticDAO statisticDAO;

    @Override
    public void init() throws ServletException {
        statisticDAO = new StatisticDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LocalDate now = LocalDate.now();
        int year = parseIntOrDefault(request.getParameter("year"), now.getYear());
        int month = parseIntOrDefault(request.getParameter("month"), now.getMonthValue());

        if (month < 1 || month > 12) {
            month = now.getMonthValue();
        }

        MonthlyStatisticSummary summary = statisticDAO.getMonthlySummary(year, month);
        List<Map<String, Object>> topSellingProducts = statisticDAO.getTopSellingProducts(year, month, DEFAULT_TOP_SELLING_LIMIT);
        List<ProductVariant> lowStockProducts = statisticDAO.getLowStockProducts(DEFAULT_LOW_STOCK_THRESHOLD, DEFAULT_LOW_STOCK_LIMIT);
        List<Map<String, Object>> revenueProfitChart = statisticDAO.getRevenueProfitChartData(year);
        List<Map<String, Object>> orderChart = statisticDAO.getOrderStatusChartData(year);

        request.setAttribute("summary", summary);
        request.setAttribute("topSellingProducts", topSellingProducts);
        request.setAttribute("lowStockProducts", lowStockProducts);
        request.setAttribute("selectedYear", year);
        request.setAttribute("selectedMonth", month);
        request.setAttribute("currentView", "statistic");
        request.setAttribute("contentPage", "/views/admin/partials/manage-statistic-content.jsp");

        request.setAttribute("revenueProfitLabelsJson", toJsonLabels(revenueProfitChart));
        request.setAttribute("revenueChartDataJson", toJsonBigDecimalValues(revenueProfitChart, "revenue"));
        request.setAttribute("profitChartDataJson", toJsonBigDecimalValues(revenueProfitChart, "profit"));
        request.setAttribute("orderChartLabelsJson", toJsonLabels(orderChart));
        request.setAttribute("totalOrdersChartDataJson", toJsonIntValues(orderChart, "totalOrders"));
        request.setAttribute("completedOrdersChartDataJson", toJsonIntValues(orderChart, "completedOrders"));
        request.setAttribute("cancelledOrdersChartDataJson", toJsonIntValues(orderChart, "cancelledOrders"));

        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
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

    private String toJsonLabels(List<Map<String, Object>> rows) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> row : rows) {
            array.put(String.valueOf(row.get("label")));
        }
        return array.toString();
    }

    private String toJsonBigDecimalValues(List<Map<String, Object>> rows, String key) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> row : rows) {
            Object value = row.get(key);
            if (value instanceof BigDecimal) {
                array.put(((BigDecimal) value).doubleValue());
            } else {
                array.put(0);
            }
        }
        return array.toString();
    }

    private String toJsonIntValues(List<Map<String, Object>> rows, String key) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> row : rows) {
            Object value = row.get(key);
            if (value instanceof Number) {
                array.put(((Number) value).intValue());
            } else {
                array.put(0);
            }
        }
        return array.toString();
    }
}
