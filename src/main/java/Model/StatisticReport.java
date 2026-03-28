package Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticReport {

    private int reportId;
    private String reportName;
    private String periodType;
    private Integer reportMonth;
    private int reportYear;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal totalProfit = BigDecimal.ZERO;
    private int totalOrders;
    private int completedOrders;
    private int cancelledOrders;
    private String note;
    private Date periodStartAt;
    private Date periodEndAt;
    private int createdBy;
    private String createdByName;
    private boolean status;
    private Date createdAt;
    private Date updatedAt;
    private List<StatisticReportItem> items = new ArrayList<>();

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public Integer getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(Integer reportMonth) {
        this.reportMonth = reportMonth;
    }

    public int getReportYear() {
        return reportYear;
    }

    public void setReportYear(int reportYear) {
        this.reportYear = reportYear;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit == null ? BigDecimal.ZERO : totalProfit;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(int completedOrders) {
        this.completedOrders = completedOrders;
    }

    public int getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(int cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getPeriodStartAt() {
        return periodStartAt;
    }

    public void setPeriodStartAt(Date periodStartAt) {
        this.periodStartAt = periodStartAt;
    }

    public Date getPeriodEndAt() {
        return periodEndAt;
    }

    public void setPeriodEndAt(Date periodEndAt) {
        this.periodEndAt = periodEndAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<StatisticReportItem> getItems() {
        return items;
    }

    public void setItems(List<StatisticReportItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }
}
