<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="vi_VN"/>

<form action="${detailFormAction}" method="post">
    <c:if test="${canReceive}">
        <input type="hidden" name="action" value="receive">
        <input type="hidden" name="orderId" value="${orderId}">
    </c:if>

    <table class="table table-sm mb-0">
        <thead>
            <tr>
                <th>Product</th>
                <th>Variant</th>
                <th class="text-end">Ordered</th>
                <th class="text-end">Import Price</th>
                <th class="text-end">Received</th>
                <th class="text-end">Subtotal</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${details}" var="d">
                <tr>
                    <td><strong>${d.productName}</strong></td>
                    <td>${d.variantName}</td>
                    <td class="text-end">${d.quantity}</td>
                    <td class="text-end">
                        <fmt:formatNumber value="${d.importPrice}" pattern="#,##0"/> VND
                    </td>
                    <td class="text-end">
                        <c:choose>
                            <c:when test="${orderStatus == 'PENDING'}">
                                ${d.quantity}
                            </c:when>
                            <c:otherwise>
                                ${d.receivedQuantity != null ? d.receivedQuantity : d.quantity}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-end amount">
                        <c:choose>
                            <c:when test="${orderStatus == 'PENDING'}">
                                <fmt:formatNumber value="${d.quantity * d.importPrice}" pattern="#,##0"/> VND
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${(d.receivedQuantity != null ? d.receivedQuantity : d.quantity) * d.importPrice}" pattern="#,##0"/> VND
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="p-3 border-top d-flex justify-content-end">
        <c:choose>
            <c:when test="${orderStatus != 'PENDING'}">
                <span class="badge bg-success">Order already received</span>
            </c:when>
            <c:when test="${canReceive}">
                <button type="submit" class="btn btn-add text-white">
                    <i class="bi bi-check-circle me-1"></i> Approve Import Order
                </button>
            </c:when>
            <c:otherwise>
                <span class="badge bg-warning text-dark">Waiting for admin approval</span>
            </c:otherwise>
        </c:choose>
    </div>
</form>
