<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="admin-content__section admin-page staff-page staff-page--category">
    <div class="container py-4">
        <div class="page-header d-flex justify-content-between align-items-center mb-3">
            <div>
                <h4>View Category List</h4>
                <p class="text-muted mb-0">Staff can review category information here.</p>
            </div>
        </div>

        <div class="card table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Category Name</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="category" items="${categories}">
                                <tr>
                                    <td class="px-4">${category.categoryId}</td>
                                    <td><strong>${category.name}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${category.status}">
                                                <span class="status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty categories}">
                                <tr>
                                    <td colspan="3" class="text-center empty-state">
                                        <i class="bi bi-inbox d-block"></i>
                                        No categories found
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
