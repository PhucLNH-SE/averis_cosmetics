<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<section class="admin-content__section admin-page admin-page--users">
    <div class="page-header">
        <div>
            <h4>Manage Statistic Reports</h4>
            <p class="text-muted mb-0">Create and review monthly or yearly statistic report snapshots</p>
        </div>
    </div>

    <c:if test="${param.success == 'created'}">
        <c:set var="popupMessage" scope="request" value="Statistic report created successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <c:set var="popupMessage" scope="request" value="Statistic report deleted successfully." />
        <c:set var="popupType" scope="request" value="success" />
    </c:if>
    <c:if test="${param.success == 'createFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to create statistic report." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.success == 'deleteFailed'}">
        <c:set var="popupMessage" scope="request" value="Failed to delete statistic report." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'notFound'}">
        <c:set var="popupMessage" scope="request" value="Statistic report not found." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'invalidAction'}">
        <c:set var="popupMessage" scope="request" value="Invalid report action." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>
    <c:if test="${param.error == 'invalidPeriod'}">
        <c:set var="popupMessage" scope="request" value="Future periods are not allowed." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card table-card mb-4">
        <div class="card-body">
            <h5 class="mb-3">Create New Report</h5>
            <form method="post" action="${pageContext.request.contextPath}/admin/manage-statistic-report" class="row g-3">
                <input type="hidden" name="action" value="create">

                <div class="col-md-3">
                    <label class="form-label">Report Name</label>
                    <input type="text" name="reportName" class="form-control" placeholder="Statistic report name">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Period</label>
                    <select name="periodType" id="reportPeriodType" class="form-select">
                        <option value="MONTH" selected>Month</option>
                        <option value="YEAR">Year</option>
                    </select>
                </div>
                <div class="col-md-2" id="reportMonthGroup">
                    <label class="form-label">Month</label>
                    <select name="month" id="reportMonth" class="form-select">
                        <c:forEach begin="1" end="12" var="monthValue">
                            <option value="${monthValue}" ${selectedMonth == monthValue ? 'selected' : ''}>Month ${monthValue}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Year</label>
                    <input type="number" name="year" class="form-control" min="2000" max="2100" value="${selectedYear}">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Note</label>
                    <input type="text" name="note" class="form-control" placeholder="Optional note for this snapshot">
                </div>
                <div class="col-12">
                    <div class="text-muted small mb-2">Reports are stored as snapshots, including summary, charts, and sold products of the selected period.</div>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-file-earmark-plus"></i> Create Report
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card table-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th class="px-4">ID</th>
                            <th>Report Name</th>
                            <th>Type</th>
                            <th>Period</th>
                            <th>Total Revenue</th>
                            <th>Total Profit</th>
                            <th>Created By</th>
                            <th class="text-end px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="report" items="${reportList}">
                            <tr>
                                <td class="px-4">${report.reportId}</td>
                                <td>
                                    <strong>${report.reportName}</strong>
                                    <div class="text-muted small">${empty report.note ? 'No note' : report.note}</div>
                                </td>
                                <td>${report.periodType}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${report.periodType == 'YEAR'}">${report.reportYear}</c:when>
                                        <c:otherwise>${report.reportMonth}/${report.reportYear}</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatNumber value="${report.totalRevenue}" pattern="#,##0" /> VND</td>
                                <td><fmt:formatNumber value="${report.totalProfit}" pattern="#,##0" /> VND</td>
                                <td>${report.createdByName}</td>
                                <td class="text-end px-4">
                                    <a href="${pageContext.request.contextPath}/admin/manage-statistic-report?action=detail&id=${report.reportId}"
                                       class="btn btn-info btn-sm text-white me-1">
                                        <i class="bi bi-eye"></i> View Detail
                                    </a>
                                    <button type="button" class="btn btn-danger btn-sm text-white js-delete-report"
                                            data-report-id="${report.reportId}"
                                            data-report-name="${report.reportName}">
                                        <i class="bi bi-trash"></i> Delete
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reportList}">
                            <tr>
                                <td colspan="8" class="text-center empty-state">
                                    <i class="bi bi-inbox d-block"></i>
                                    No statistic reports found
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<c:if test="${not empty selectedReport}">
    <div class="modal fade show d-block" tabindex="-1" aria-modal="true" role="dialog">
        <div class="modal-dialog modal-dialog-centered modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Statistic Report Detail</h5>
                    <a href="${pageContext.request.contextPath}/admin/manage-statistic-report" class="btn-close" aria-label="Close"></a>
                </div>
                <div class="modal-body">
                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Report Name</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.reportName}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Period Type</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.periodType}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Month</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.periodType == 'YEAR' ? '-' : selectedReport.reportMonth}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Year</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.reportYear}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Created By</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.createdByName}">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Created At</label>
                            <div class="form-control"><fmt:formatDate value="${selectedReport.createdAt}" pattern="dd/MM/yyyy HH:mm" /></div>
                        </div>
                        <div class="col-md-8">
                            <label class="form-label fw-bold">Snapshot Range</label>
                            <div class="form-control">
                                <fmt:formatDate value="${selectedReport.periodStartAt}" pattern="dd/MM/yyyy HH:mm" />
                                to
                                <fmt:formatDate value="${selectedReport.periodEndAt}" pattern="dd/MM/yyyy HH:mm" />
                            </div>
                        </div>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-sm-6 col-xl-3">
                            <div class="stat-card">
                                <span class="stat-card__label">Total Revenue</span>
                                <strong class="stat-card__value"><fmt:formatNumber value="${selectedReport.totalRevenue}" pattern="#,##0" /> VND</strong>
                            </div>
                        </div>
                        <div class="col-12 col-sm-6 col-xl-3">
                            <div class="stat-card">
                                <span class="stat-card__label">Total Profit</span>
                                <strong class="stat-card__value"><fmt:formatNumber value="${selectedReport.totalProfit}" pattern="#,##0" /> VND</strong>
                            </div>
                        </div>
                        <div class="col-12 col-sm-4 col-xl-2">
                            <div class="stat-card">
                                <span class="stat-card__label">Total Orders</span>
                                <strong class="stat-card__value">${selectedReport.totalOrders}</strong>
                            </div>
                        </div>
                        <div class="col-12 col-sm-4 col-xl-2">
                            <div class="stat-card">
                                <span class="stat-card__label">Completed</span>
                                <strong class="stat-card__value">${selectedReport.completedOrders}</strong>
                            </div>
                        </div>
                        <div class="col-12 col-sm-4 col-xl-2">
                            <div class="stat-card">
                                <span class="stat-card__label">Cancelled</span>
                                <strong class="stat-card__value">${selectedReport.cancelledOrders}</strong>
                            </div>
                        </div>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-12 col-xl-6">
                            <div class="chart-card">
                                <div class="chart-card__head">
                                    <h3>Revenue vs Profit Snapshot</h3>
                                    <span>Snapshot data stored in this report</span>
                                </div>
                                <canvas id="reportRevenueProfitChart"></canvas>
                            </div>
                        </div>
                        <div class="col-12 col-xl-6">
                            <div class="chart-card chart-card--orders">
                                <div class="chart-card__head">
                                    <h3>Orders Snapshot</h3>
                                    <span>Orders distribution stored in this report</span>
                                </div>
                                <canvas id="reportOrderChart"></canvas>
                                <div class="orders-overview-stats orders-overview-stats--four">
                                    <div class="orders-overview-stat">
                                        <strong class="orders-overview-stat__value">${selectedReport.totalOrders}</strong>
                                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot" style="background:#3b82f6"></i>Total Orders</span>
                                    </div>
                                    <div class="orders-overview-stat">
                                        <strong class="orders-overview-stat__value">${selectedReport.completedOrders}</strong>
                                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot orders-overview-stat__dot--completed"></i>Completed</span>
                                    </div>
                                    <div class="orders-overview-stat">
                                        <strong class="orders-overview-stat__value">${selectedReport.totalOrders - selectedReport.completedOrders - selectedReport.cancelledOrders > 0 ? selectedReport.totalOrders - selectedReport.completedOrders - selectedReport.cancelledOrders : 0}</strong>
                                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot orders-overview-stat__dot--pending"></i>Pending</span>
                                    </div>
                                    <div class="orders-overview-stat">
                                        <strong class="orders-overview-stat__value">${selectedReport.cancelledOrders}</strong>
                                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot orders-overview-stat__dot--cancelled"></i>Cancelled</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="table-card statistic-table-card mb-4">
                        <div class="table-card__head">
                            <h3>Sold Product Details</h3>
                            <span>Sorted by sold quantity at snapshot creation time</span>
                        </div>
                        <div class="table-responsive">
                            <table class="table mb-0">
                                <thead>
                                    <tr>
                                        <th>Product / Variant</th>
                                        <th class="text-end">Sold</th>
                                        <th>Description</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty reportSoldProductItems}">
                                            <tr><td colspan="3" class="text-center py-4">No sold product detail stored.</td></tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="item" items="${reportSoldProductItems}">
                                                <tr>
                                                    <td>${item.itemLabel}</td>
                                                    <td class="text-end"><fmt:formatNumber value="${item.itemValue}" pattern="#,##0" /></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${fn:contains(item.itemText, 'Revenue=') and fn:contains(item.itemText, ';Profit=')}">
                                                                <c:set var="parts" value="${fn:split(item.itemText, ';')}" />
                                                                <c:set var="revenueRaw" value="${fn:substringAfter(parts[0], 'Revenue=')}" />
                                                                <c:set var="profitRaw" value="${fn:substringAfter(parts[1], 'Profit=')}" />
                                                                <fmt:parseNumber var="revenueValue" value="${revenueRaw}" type="number" />
                                                                <fmt:parseNumber var="profitValue" value="${profitRaw}" type="number" />
                                                                <div>Revenue: <fmt:formatNumber value="${revenueValue}" pattern="#,##0" /></div>
                                                                <div>Profit: <fmt:formatNumber value="${profitValue}" pattern="#,##0" /></div>
                                                            </c:when>
                                                            <c:otherwise>${item.itemText}</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-bold">Note</label>
                        <textarea class="form-control" rows="2" readonly>${empty selectedReport.note ? 'No note' : selectedReport.note}</textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="${pageContext.request.contextPath}/admin/manage-statistic-report" class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Close
                    </a>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-backdrop fade show"></div>
</c:if>

<div class="modal fade" id="deleteReportModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete <strong id="deleteReportName"></strong>?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form method="post" action="${pageContext.request.contextPath}/admin/manage-statistic-report" class="d-inline">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" id="deleteReportId">
                    <button type="submit" class="btn btn-danger">Delete</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const modalElement = document.getElementById('deleteReportModal');
        const periodTypeSelect = document.getElementById('reportPeriodType');
        const monthGroup = document.getElementById('reportMonthGroup');
        const monthSelect = document.getElementById('reportMonth');

        function syncMonthFilter() {
            if (!periodTypeSelect || !monthGroup || !monthSelect) {
                return;
            }
            const isYear = periodTypeSelect.value === 'YEAR';
            monthGroup.style.display = isYear ? 'none' : '';
            monthSelect.disabled = isYear;
        }

        syncMonthFilter();
        if (periodTypeSelect) {
            periodTypeSelect.addEventListener('change', syncMonthFilter);
        }

        if (modalElement && typeof bootstrap !== 'undefined') {
            const modal = new bootstrap.Modal(modalElement);
            const idInput = document.getElementById('deleteReportId');
            const nameLabel = document.getElementById('deleteReportName');
            const buttons = document.querySelectorAll('.js-delete-report');

            buttons.forEach(function (button) {
                button.addEventListener('click', function () {
                    idInput.value = button.getAttribute('data-report-id');
                    nameLabel.textContent = button.getAttribute('data-report-name');
                    modal.show();
                });
            });
        }

        const revenueProfitCanvas = document.getElementById('reportRevenueProfitChart');
        const orderCanvas = document.getElementById('reportOrderChart');

        function formatCurrency(value) {
            return Number(value || 0).toLocaleString('vi-VN') + ' VND';
        }

        function formatCompactMillions(value) {
            return (Number(value || 0) / 1000000).toFixed(1) + 'M';
        }

        function buildBaseOptions(isMoneyChart) {
            return {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                elements: {
                    line: {
                        tension: 0.35,
                        borderWidth: 2
                    },
                    point: {
                        radius: 4,
                        hoverRadius: 6,
                        hitRadius: 12,
                        borderWidth: 0
                    }
                },
                plugins: {
                    legend: {
                        position: 'bottom',
                        align: 'start',
                        labels: {
                            usePointStyle: true,
                            pointStyle: 'line',
                            boxWidth: 32,
                            boxHeight: 8,
                            padding: 20,
                            color: '#374151',
                            font: {
                                size: 12,
                                weight: '500'
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: '#ffffff',
                        titleColor: '#111827',
                        bodyColor: '#374151',
                        borderColor: '#e5e7eb',
                        borderWidth: 1,
                        titleFont: {
                            size: 12,
                            weight: '600'
                        },
                        bodyFont: {
                            size: 12
                        },
                        padding: 12,
                        displayColors: true,
                        boxPadding: 4,
                        usePointStyle: true,
                        callbacks: {
                            label: function (context) {
                                const label = context.dataset.label || '';
                                const value = context.parsed.y;
                                return label + ': ' + (isMoneyChart ? formatCurrency(value) : Number(value || 0).toLocaleString('vi-VN'));
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false,
                            drawBorder: false
                        },
                        border: {
                            display: false
                        },
                        ticks: {
                            color: '#9ca3af',
                            font: {
                                size: 12
                            }
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f0f0f0',
                            borderDash: [3, 3],
                            drawBorder: false
                        },
                        border: {
                            display: false
                        },
                        ticks: {
                            color: '#9ca3af',
                            font: {
                                size: 12
                            },
                            precision: isMoneyChart ? undefined : 0,
                            callback: function (value) {
                                return isMoneyChart ? formatCompactMillions(value) : value;
                            }
                        }
                    }
                }
            };
        }

        if (revenueProfitCanvas) {
            new Chart(revenueProfitCanvas, {
                type: 'line',
                data: {
                    labels: ${empty selectedReport ? '[]' : reportRevenueChartLabelsJson},
                    datasets: [
                        {
                            label: 'Revenue',
                            data: ${empty selectedReport ? '[]' : reportRevenueChartDataJson},
                            borderColor: '#3b82f6',
                            backgroundColor: '#3b82f6',
                            fill: false
                        },
                        {
                            label: 'Profit',
                            data: ${empty selectedReport ? '[]' : reportProfitChartDataJson},
                            borderColor: '#10b981',
                            backgroundColor: '#10b981',
                            fill: false
                        }
                    ]
                },
                options: buildBaseOptions(true)
            });
        }

        if (orderCanvas) {
            const completedOrders = ${empty selectedReport ? '0' : selectedReport.completedOrders};
            const cancelledOrders = ${empty selectedReport ? '0' : selectedReport.cancelledOrders};
            const pendingOrders = Math.max(0, ${empty selectedReport ? '0' : selectedReport.totalOrders} - completedOrders - cancelledOrders);

            new Chart(orderCanvas, {
                type: 'doughnut',
                data: {
                    labels: ['Completed', 'Pending', 'Cancelled'],
                    datasets: [
                        {
                            data: [completedOrders, pendingOrders, cancelledOrders],
                            backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
                            borderWidth: 0,
                            hoverOffset: 6
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '0%',
                    radius: '88%',
                    plugins: {
                        legend: {
                            display: false
                        },
                        tooltip: {
                            backgroundColor: '#ffffff',
                            titleColor: '#111827',
                            bodyColor: '#374151',
                            borderColor: '#e5e7eb',
                            borderWidth: 1,
                            titleFont: {
                                size: 12,
                                weight: '600'
                            },
                            bodyFont: {
                                size: 12
                            },
                            padding: 12,
                            callbacks: {
                                label: function (context) {
                                    const value = Number(context.parsed || 0);
                                    const total = completedOrders + pendingOrders + cancelledOrders;
                                    const percent = total > 0 ? Math.round((value / total) * 100) : 0;
                                    return context.label + ': ' + value.toLocaleString('vi-VN') + ' (' + percent + '%)';
                                }
                            }
                        }
                    }
                },
                plugins: [{
                    id: 'reportOrdersPieLabels',
                    afterDatasetsDraw(chart) {
                        const dataset = chart.data.datasets[0];
                        const meta = chart.getDatasetMeta(0);
                        const values = dataset.data || [];
                        const total = values.reduce((sum, value) => sum + Number(value || 0), 0);
                        const ctx = chart.ctx;

                        ctx.save();
                        ctx.font = '600 13px Arial';
                        ctx.fillStyle = '#ffffff';
                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'middle';

                        meta.data.forEach((element, index) => {
                            const value = Number(values[index] || 0);
                            if (!value || !total) {
                                return;
                            }
                            const percent = Math.round((value / total) * 100);
                            const position = element.tooltipPosition();
                            ctx.fillText(percent + '%', position.x, position.y);
                        });

                        ctx.restore();
                    }
                }]
            });
        }
    });
</script>






