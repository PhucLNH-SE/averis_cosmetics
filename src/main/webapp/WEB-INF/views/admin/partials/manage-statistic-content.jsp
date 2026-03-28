<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<section class="admin-content__section statistic-page">
    <div class="statistic-page__header">
        <div>
            <h2 class="admin-content__title">Statistic Overview</h2>
            <p class="admin-content__subtitle">
                <c:choose>
                    <c:when test="${isYearPeriod}">Summary of revenue, profit, orders, and sold products in year ${selectedYear}.</c:when>
                    <c:otherwise>Summary of revenue, profit, orders, and sold products in month ${selectedMonth}/${selectedYear}.</c:otherwise>
                </c:choose>
            </p>
        </div>

        <form class="statistic-filter" method="get" action="${pageContext.request.contextPath}/admin/manage-statistic">
            <div class="statistic-filter__group">
                <label for="statPeriodType">Period</label>
                <select id="statPeriodType" name="periodType" class="form-select">
                    <option value="MONTH" ${selectedPeriodType == 'MONTH' ? 'selected' : ''}>Month</option>
                    <option value="YEAR" ${selectedPeriodType == 'YEAR' ? 'selected' : ''}>Year</option>
                </select>
            </div>
            <div class="statistic-filter__group" id="statMonthGroup">
                <label for="statMonth">Month</label>
                <select id="statMonth" name="month" class="form-select" ${isYearPeriod ? 'disabled' : ''}>
                    <c:forEach begin="1" end="12" var="monthValue">
                        <option value="${monthValue}" ${selectedMonth == monthValue ? 'selected' : ''}>Month ${monthValue}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="statistic-filter__group">
                <label for="statYear">Year</label>
                <input id="statYear" type="number" name="year" class="form-control" value="${selectedYear}" min="2000" max="2100">
            </div>
            <button type="submit" class="btn btn-primary">Apply</button>
        </form>
    </div>

    <div class="row g-3">
        <div class="col-12 col-sm-6 col-xl-3">
            <div class="stat-card">
                <span class="stat-card__label">Total Revenue</span>
                <strong class="stat-card__value"><fmt:formatNumber value="${summary.totalRevenue}" pattern="#,##0" /></strong>
            </div>
        </div>
        <div class="col-12 col-sm-6 col-xl-3">
            <div class="stat-card">
                <span class="stat-card__label">Total Profit</span>
                <strong class="stat-card__value"><fmt:formatNumber value="${summary.totalProfit}" pattern="#,##0" /></strong>
            </div>
        </div>
        <div class="col-12 col-sm-4 col-xl-2">
            <div class="stat-card">
                <span class="stat-card__label">Total Orders</span>
                <strong class="stat-card__value">${summary.totalOrders}</strong>
            </div>
        </div>
        <div class="col-12 col-sm-4 col-xl-2">
            <div class="stat-card">
                <span class="stat-card__label">Completed</span>
                <strong class="stat-card__value">${summary.completedOrders}</strong>
            </div>
        </div>
        <div class="col-12 col-sm-4 col-xl-2">
            <div class="stat-card">
                <span class="stat-card__label">Cancelled</span>
                <strong class="stat-card__value">${summary.cancelledOrders}</strong>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-12 col-xl-6">
            <div class="chart-card">
                <div class="chart-card__head">
                    <h3>Revenue vs Profit</h3>
                    <span>
                        <c:choose>
                            <c:when test="${isYearPeriod}">Comparison by month in ${selectedYear}</c:when>
                            <c:otherwise>Comparison by day in ${selectedMonth}/${selectedYear}</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <canvas id="revenueProfitChart"></canvas>
            </div>
        </div>
        <div class="col-12 col-xl-6">
            <div class="chart-card chart-card--orders">
                <div class="chart-card__head">
                    <h3>Orders Overview</h3>
                    <span>
                        <c:choose>
                            <c:when test="${isYearPeriod}">Orders distribution in ${selectedYear}</c:when>
                            <c:otherwise>Orders distribution in ${selectedMonth}/${selectedYear}</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <canvas id="orderStatusChart"></canvas>
                <div class="orders-overview-stats orders-overview-stats--four">
                    <div class="orders-overview-stat">
                        <strong class="orders-overview-stat__value">${summary.totalOrders}</strong>
                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot" style="background:#3b82f6"></i>Total Orders</span>
                    </div>
                    <div class="orders-overview-stat">
                        <strong class="orders-overview-stat__value">${summary.completedOrders}</strong>
                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot orders-overview-stat__dot--completed"></i>Completed</span>
                    </div>
                    <div class="orders-overview-stat">
                        <strong class="orders-overview-stat__value">${summary.totalOrders - summary.completedOrders - summary.cancelledOrders > 0 ? summary.totalOrders - summary.completedOrders - summary.cancelledOrders : 0}</strong>
                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot orders-overview-stat__dot--pending"></i>Pending</span>
                    </div>
                    <div class="orders-overview-stat">
                        <strong class="orders-overview-stat__value">${summary.cancelledOrders}</strong>
                        <span class="orders-overview-stat__label"><i class="orders-overview-stat__dot orders-overview-stat__dot--cancelled"></i>Cancelled</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-12">
            <div class="table-card statistic-table-card">
                <div class="table-card__head">
                    <h3>
                        <c:choose>
                            <c:when test="${isYearPeriod}">Sold Products in ${selectedYear}</c:when>
                            <c:otherwise>Sold Products in ${selectedMonth}/${selectedYear}</c:otherwise>
                        </c:choose>
                    </h3>
                    <span>Sorted by total sold in descending order</span>
                </div>
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead>
                            <tr>
                                <th class="px-4">Product</th>
                                <th>Variant</th>
                                <th>Total Sold</th>
                                <th class="text-end">Revenue</th>
                                <th class="text-end px-4">Profit</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty soldProductDetails}">
                                    <tr>
                                        <td colspan="5" class="text-center py-4">No sales data in selected period.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${soldProductDetails}">
                                        <tr>
                                            <td class="px-4">
                                                <div class="product-cell">
                                                    <div class="product-cell__thumb">
                                                        <c:choose>
                                                            <c:when test="${not empty item.imageUrl}">
                                                                <c:choose>
                                                                    <c:when test="${item.imageUrl.startsWith('http')}">
                                                                        <img src="${item.imageUrl}" alt="${item.productName}"
                                                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                                                    </c:when>
                                                                    <c:when test="${item.imageUrl.startsWith('assets/')}">
                                                                        <img src="${pageContext.request.contextPath}/${item.imageUrl}" alt="${item.productName}"
                                                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <img src="${pageContext.request.contextPath}/assets/img/${item.imageUrl}" alt="${item.productName}"
                                                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="No image">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div>
                                                        <strong>${item.productName}</strong>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${item.variantName}</td>
                                            <td>${item.totalSold}</td>
                                            <td class="text-end"><fmt:formatNumber value="${item.revenue}" pattern="#,##0" /> VND</td>
                                            <td class="text-end px-4"><fmt:formatNumber value="${item.profit}" pattern="#,##0" /> VND</td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script>
    (function () {
        const revenueProfitCanvas = document.getElementById('revenueProfitChart');
        const orderStatusCanvas = document.getElementById('orderStatusChart');
        const periodTypeSelect = document.getElementById('statPeriodType');
        const monthGroup = document.getElementById('statMonthGroup');
        const monthSelect = document.getElementById('statMonth');

        function syncMonthFilter() {
            if (!periodTypeSelect || !monthGroup || !monthSelect) {
                return;
            }
            const isYear = periodTypeSelect.value === 'YEAR';
            monthGroup.style.display = isYear ? 'none' : '';
            monthSelect.disabled = isYear;
        }

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

        syncMonthFilter();
        if (periodTypeSelect) {
            periodTypeSelect.addEventListener('change', syncMonthFilter);
        }

        if (!revenueProfitCanvas || !orderStatusCanvas) {
            return;
        }

        new Chart(revenueProfitCanvas, {
            type: 'line',
            data: {
                labels: ${revenueProfitLabelsJson},
                datasets: [
                    {
                        label: 'Revenue',
                        data: ${revenueChartDataJson},
                        borderColor: '#3b82f6',
                        backgroundColor: '#3b82f6',
                        fill: false
                    },
                    {
                        label: 'Profit',
                        data: ${profitChartDataJson},
                        borderColor: '#10b981',
                        backgroundColor: '#10b981',
                        fill: false
                    }
                ]
            },
            options: buildBaseOptions(true)
        });

        const completedOrders = ${summary.completedOrders};
        const cancelledOrders = ${summary.cancelledOrders};
        const pendingOrders = Math.max(0, ${summary.totalOrders} - completedOrders - cancelledOrders);

        new Chart(orderStatusCanvas, {
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
                id: 'ordersPieLabels',
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
    })();
</script>




