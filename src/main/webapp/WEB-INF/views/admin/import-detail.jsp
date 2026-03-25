<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<fmt:setLocale value="vi_VN"/>

<form action="${pageContext.request.contextPath}/admin/import-product" method="post">
    <input type="hidden" name="action" value="receive">
    <input type="hidden" name="orderId" value="${orderId}">

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
                    <td class="text-end">${d.receivedQuantity != null ? d.receivedQuantity : d.quantity}</td>
                    <td class="text-end amount">
                        <fmt:formatNumber value="${(d.receivedQuantity != null ? d.receivedQuantity : d.quantity) * d.importPrice}" pattern="#,##0"/> VND
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="p-3 border-top d-flex justify-content-end">
        <c:if test="${orderStatus == 'PENDING'}">
            <button type="submit" class="btn btn-add text-white">
                <i class="bi bi-check-circle me-1"></i> Confirm Receipt
            </button>
        </c:if>
        <c:if test="${orderStatus != 'PENDING'}">
            <span class="badge bg-success">Order already received</span>
        </c:if>
    </div>
</form>

