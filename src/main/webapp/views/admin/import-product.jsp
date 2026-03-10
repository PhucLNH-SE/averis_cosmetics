<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Import Product</title>
</head>
<body>

<h2>Import Product</h2>

<!-- ===================== -->
<!-- SELECT BRAND -->
<!-- ===================== -->
<c:if test="${error != null}">
    <div style="color:red; font-weight:bold;">
        ${error}
    </div>
</c:if>
<form action="ImportProductController" method="get">

    <label>Choose Brand:</label>

    <select name="brandId">

        <c:forEach items="${brands}" var="b">

            <option value="${b.brandId}"
                <c:if test="${selectedBrand == b.brandId}">
                    selected
                </c:if>
            >
                ${b.name}
            </option>

        </c:forEach>

    </select>

    <button type="submit">Load Product</button>


</form>

<hr>


<!-- ===================== -->
<!-- IMPORT FORM -->
<!-- ===================== -->

<c:if test="${not empty variants}">

<form action="ImportProductController" method="post">

<input type="hidden" name="brandId" value="${selectedBrand}">

<table border="1">

<tr>
<th>Product</th>
<th>Variant</th>
<th>Current Stock</th>
<th>Import Price</th>
<th>Quantity</th>
</tr>

<c:forEach items="${variants}" var="v">

<tr>

<td>${v.productName}</td>

<td>${v.variantName}</td>

<td>${v.stock}</td>

<td>
<input type="number" name="price" step="0.01">
</td>

<td>
<input type="number" name="quantity">
</td>

<td style="display:none;">
<input type="hidden" name="variantId" value="${v.variantId}">
</td>

</tr>

</c:forEach>

</table>

<br>

<button type="submit">Import Product</button>

</form>

</c:if>
<a href="${pageContext.request.contextPath}/ImportProductController?action=history">
← Back to Import History
</a>
</body>
</html>