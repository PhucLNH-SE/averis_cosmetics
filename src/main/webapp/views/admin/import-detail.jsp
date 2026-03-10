<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<table border="1" width="100%">

<tr>
    <th>Product</th>
    <th>Variant</th>
    <th>Quantity</th>
    <th>Import Price</th>
    <th>Subtotal</th>
</tr>

<c:forEach items="${details}" var="d">

<tr>
    <td>${d.productName}</td>

    <td>${d.variantName}</td>

    <td>${d.quantity}</td>

    <td>${d.importPrice}</td>

    <td>
        ${d.quantity * d.importPrice}
    </td>
</tr>

</c:forEach>

</table>
