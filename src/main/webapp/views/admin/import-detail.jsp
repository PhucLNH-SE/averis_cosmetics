<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<table class="table table-sm mb-0">
    <thead>
        <tr>
            <th>Product</th>
            <th>Variant</th>
            <th class="text-end">Quantity</th>
            <th class="text-end">Import Price</th>
            <th class="text-end">Subtotal</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${details}" var="d">
            <tr>
                <td><strong>${d.productName}</strong></td>
                <td>${d.variantName}</td>
                <td class="text-end">${d.quantity}</td>
                <td class="text-end">${d.importPrice} VNĐ</td>
                <td class="text-end amount">${d.quantity * d.importPrice} VNĐ</td>
            </tr>
        </c:forEach>
    </tbody>
</table>
