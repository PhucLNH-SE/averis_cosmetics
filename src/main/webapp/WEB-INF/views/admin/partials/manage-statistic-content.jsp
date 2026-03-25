<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<section class="admin-content__section statistic-page">
    <div class="statistic-page__header">
        <div>
            <h2 class="admin-content__title">Monthly Statistic</h2>
            <p class="admin-content__subtitle">Summary of revenue, profit, orders, and products by selected month.</p>
        </div>

        <form class="statistic-filter" method="get" action="${pageContext.request.contextPath}/admin/manage-statistic">
            <div class="statistic-filter__group">
                <label for="statMonth">Month</label>
                <select id="statMonth" name="month" class="form-select">
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
                <strong class="stat-card__value"><fmt:formatNumber value="${summary.totalRevenue}" pattern="#,##0" /> VND</strong>
            </div>
        </div>
        <div class="col-12 col-sm-6 col-xl-3">
            <div class="stat-card">
                <span class="stat-card__label">Total Profit</span>
                <strong class="stat-card__value"><fmt:formatNumber value="${summary.totalProfit}" pattern="#,##0" /> VND</strong>
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
                    <span>Comparison by selected day in month ${selectedMonth}/${selectedYear}</span>
                </div>
                <canvas id="revenueProfitChart"></canvas>
            </div>
        </div>
        <div class="col-12 col-xl-6">
            <div class="chart-card">
                <div class="chart-card__head">
                    <h3>Orders Overview</h3>
                    <span>Total, completed and cancelled orders by selected day in ${selectedMonth}/${selectedYear}</span>
                </div>
                <canvas id="orderStatusChart"></canvas>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-12 col-xl-6">
            <div class="table-card statistic-table-card">
                <div class="table-card__head">
                    <h3>Top Selling Products in Month ${selectedMonth}</h3>
                </div>
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead>
                            <tr>
                                <th class="px-4">Product</th>
                                <th>Total Sold</th>
                                <th class="text-end px-4">Revenue</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty topSellingProducts}">
                                    <tr>
                                        <td colspan="3" class="text-center py-4">No sales data in selected month.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${topSellingProducts}">
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
                                            <td>${item.totalSold}</td>
                                            <td class="text-end px-4"><fmt:formatNumber value="${item.revenue}" pattern="#,##0" /> VND</td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-12 col-xl-6">
            <div class="table-card statistic-table-card">
                <div class="table-card__head">
                    <h3>Low Stock Products</h3>
                    <span>Threshold <= 10</span>
                </div>
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead>
                            <tr>
                                <th class="px-4">Variant</th>
                                <th class="text-end px-4">Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty lowStockProducts}">
                                    <tr>
                                        <td colspan="2" class="text-center py-4">No low stock products.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="variant" items="${lowStockProducts}">
                                        <tr>
                                            <td class="px-4">
                                                <div class="product-cell">
                                                    <div class="product-cell__thumb">
                                                        <c:choose>
                                                            <c:when test="${not empty variant.imageUrl}">
                                                                <c:choose>
                                                                    <c:when test="${variant.imageUrl.startsWith('http')}">
                                                                        <img src="${variant.imageUrl}" alt="${variant.productName}"
                                                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                                                    </c:when>
                                                                    <c:when test="${variant.imageUrl.startsWith('assets/')}">
                                                                        <img src="${pageContext.request.contextPath}/${variant.imageUrl}" alt="${variant.productName}"
                                                                             onerror="this.src='${pageContext.request.contextPath}/assets/img/Logo.png';">
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <img src="${pageContext.request.contextPath}/assets/img/${variant.imageUrl}" alt="${variant.productName}"
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
                                                        <strong>${variant.productName}</strong>
                                                        <div class="text-muted small">${variant.variantName}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="text-end px-4"><span class="stock-badge">${variant.stock}</span></td>
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
                        borderColor: '#2563eb',
                        backgroundColor: 'rgba(37, 99, 235, 0.12)',
                        fill: false,
                        tension: 0,
                        borderWidth: 3,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    },
                    {
                        label: 'Profit',
                        data: ${profitChartDataJson},
                        borderColor: '#059669',
                        backgroundColor: 'rgba(5, 150, 105, 0.12)',
                        fill: false,
                        tension: 0,
                        borderWidth: 3,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        new Chart(orderStatusCanvas, {
            type: 'line',
            data: {
                labels: ${orderChartLabelsJson},
                datasets: [
                    {
                        label: 'Total Orders',
                        data: ${totalOrdersChartDataJson},
                        borderColor: '#1d4ed8',
                        backgroundColor: 'rgba(29, 78, 216, 0.12)',
                        fill: false,
                        tension: 0,
                        borderWidth: 3,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    },
                    {
                        label: 'Completed',
                        data: ${completedOrdersChartDataJson},
                        borderColor: '#059669',
                        backgroundColor: 'rgba(5, 150, 105, 0.08)',
                        fill: false,
                        tension: 0,
                        borderWidth: 3,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    },
                    {
                        label: 'Cancelled',
                        data: ${cancelledOrdersChartDataJson},
                        borderColor: '#dc2626',
                        backgroundColor: 'rgba(220, 38, 38, 0.08)',
                        fill: false,
                        tension: 0,
                        borderWidth: 3,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    })();
</script>
