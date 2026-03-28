package Controllers.manager;

import DALs.StatisticDAO;
import Model.MonthlyStatisticSummary;
import Utils.StatisticUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminStatisticController extends HttpServlet {

    private StatisticDAO statisticDAO;

    @Override
    public void init() throws ServletException {
        statisticDAO = new StatisticDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int currentYear = StatisticUtils.getDefaultPeriod().getYear();
        int currentMonth = StatisticUtils.getDefaultPeriod().getMonthValue();

        String periodType = StatisticUtils.normalizePeriodType(request.getParameter("periodType"));
        int year = StatisticUtils.parseIntOrDefault(request.getParameter("year"), currentYear);
        Integer month = StatisticUtils.isYearPeriod(periodType)
                ? null
                : StatisticUtils.parseIntOrDefault(request.getParameter("month"), currentMonth);

        if (!StatisticUtils.isValidPeriod(year, month, periodType)) {
            year = currentYear;
            month = StatisticUtils.isYearPeriod(periodType) ? null : currentMonth;
        }

        MonthlyStatisticSummary summary = statisticDAO.getSummary(year, month, periodType);
        List<Map<String, Object>> soldProductDetails = statisticDAO.getSoldProductDetails(year, month, periodType);
        List<Map<String, Object>> revenueProfitChart = statisticDAO.getRevenueProfitChartData(year, month, periodType);
        List<Map<String, Object>> orderChart = statisticDAO.getOrderStatusChartData(year, month, periodType);

        request.setAttribute("summary", summary);
        request.setAttribute("soldProductDetails", soldProductDetails);
        request.setAttribute("selectedPeriodType", periodType);
        request.setAttribute("selectedYear", year);
        request.setAttribute("selectedMonth", month == null ? currentMonth : month);
        request.setAttribute("isYearPeriod", StatisticUtils.isYearPeriod(periodType));
        request.setAttribute("currentView", "statistic");
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-statistic-content.jsp");

        request.setAttribute("revenueProfitLabelsJson", StatisticUtils.toJsonLabels(revenueProfitChart));
        request.setAttribute("revenueChartDataJson", StatisticUtils.toJsonBigDecimalValues(revenueProfitChart, "revenue"));
        request.setAttribute("profitChartDataJson", StatisticUtils.toJsonBigDecimalValues(revenueProfitChart, "profit"));
        request.setAttribute("orderChartLabelsJson", StatisticUtils.toJsonLabels(orderChart));
        request.setAttribute("totalOrdersChartDataJson", StatisticUtils.toJsonIntValues(orderChart, "totalOrders"));
        request.setAttribute("completedOrdersChartDataJson", StatisticUtils.toJsonIntValues(orderChart, "completedOrders"));
        request.setAttribute("cancelledOrdersChartDataJson", StatisticUtils.toJsonIntValues(orderChart, "cancelledOrders"));

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }
}
