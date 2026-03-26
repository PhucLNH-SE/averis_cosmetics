<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<section class="admin-content__section admin-page admin-page--users">
    <div class="page-header">
        <div>
            <h4>Manage Statistic Reports</h4>
            <p class="text-muted mb-0">Create and review monthly statistic report snapshots</p>
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
        <c:set var="popupMessage" scope="request" value="Future months are not allowed. Current month can be saved as a snapshot up to the creation date." />
        <c:set var="popupType" scope="request" value="error" />
    </c:if>

    <div class="card table-card mb-4">
        <div class="card-body">
            <h5 class="mb-3">Create New Report</h5>
            <form method="post" action="${pageContext.request.contextPath}/admin/manage-statistic-report" class="row g-3">
                <input type="hidden" name="action" value="create">

                <div class="col-md-4">
                    <label class="form-label">Report Name</label>
                    <input type="text" name="reportName" class="form-control" placeholder="Monthly report ${selectedMonth}/${selectedYear}">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Month</label>
                    <select name="month" class="form-select">
                        <c:forEach begin="1" end="12" var="monthValue">
                            <option value="${monthValue}" ${selectedMonth == monthValue ? 'selected' : ''}>Month ${monthValue}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Year</label>
                    <input type="number" name="year" class="form-control" min="2000" max="2100" value="${selectedYear}">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Note</label>
                    <input type="text" name="note" class="form-control" placeholder="Optional note for this snapshot">
                </div>
                <div class="col-12">
                    <div class="text-muted small mb-2">You can create reports for past months and the current month. Current month reports are snapshots from day 1 to the moment you create them.</div>
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
                                <td>${report.reportMonth}/${report.reportYear}</td>
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
                                <td colspan="7" class="text-center empty-state">
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
                            <label class="form-label fw-bold">Month</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.reportMonth}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Year</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.reportYear}">
                        </div>
                        <div class="col-md-4">
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
                                01/${selectedReport.reportMonth}/${selectedReport.reportYear}
                                to
                                <fmt:formatDate value="${selectedReport.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Total Revenue</label>
                            <div class="form-control"><fmt:formatNumber value="${selectedReport.totalRevenue}" pattern="#,##0" /> VND</div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Total Profit</label>
                            <div class="form-control"><fmt:formatNumber value="${selectedReport.totalProfit}" pattern="#,##0" /> VND</div>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Orders</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.totalOrders}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label fw-bold">Completed</label>
                            <input type="text" class="form-control" readonly value="${selectedReport.completedOrders}">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-bold">Note</label>
                            <textarea class="form-control" rows="2" readonly>${empty selectedReport.note ? 'No note' : selectedReport.note}</textarea>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Type</th>
                                    <th>Label</th>
                                    <th>Value</th>
                                    <th>Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${selectedReport.items}">
                                    <tr>
                                        <td>${item.itemType}</td>
                                        <td>${item.itemLabel}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty item.itemValue}">
                                                    <fmt:formatNumber value="${item.itemValue}" pattern="#,##0.##" />
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${empty item.itemText ? '-' : item.itemText}</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty selectedReport.items}">
                                    <tr>
                                        <td colspan="4" class="text-center">No report items found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="${pageContext.request.contextPath}/admin/manage-statistic-report" class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Close
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

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const modalElement = document.getElementById('deleteReportModal');
        if (!modalElement || typeof bootstrap === 'undefined') {
            return;
        }

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
    });
</script>


