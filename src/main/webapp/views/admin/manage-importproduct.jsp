<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Import History</title>

</head>

<body>

<a href="${pageContext.request.contextPath}/ImportProductController?action=importproduct">
    Import Product
</a>

<br><br>

<table border="1">

<thead>

<tr>
<th></th>
<th>Order ID</th>
<th>Brand</th>
<th>Manager</th>
<th>Total Amount</th>
<th>Created At</th>
</tr>

</thead>

<tbody>

<c:forEach var="h" items="${history}">

<tr>

<td>

<a href="javascript:void(0)" onclick="toggleDetail(${h.purchaseOrderId})">
▼
</a>

</td>

<td>${h.purchaseOrderId}</td>

<td>${h.brandName}</td>

<td>
${h.managerName} (${h.managerRole})
</td>

<td>${h.totalAmount}</td>

<td>${h.createdAt}</td>

</tr>

<!-- row hiển thị detail -->

<tr id="detail-${h.purchaseOrderId}" style="display:none">

<td colspan="6">

<div id="detail-content-${h.purchaseOrderId}">
Loading...
</div>

</td>

</tr>

</c:forEach>

</tbody>

</table>

<div style="margin-bottom:10px;">

<a href="${pageContext.request.contextPath}/admin/dashboard">
    ← Back to Dashboard
</a>

</div>
<script>

function toggleDetail(orderId){

let row = document.getElementById("detail-" + orderId)

if(row.style.display === "none"){

row.style.display = "table-row"

fetch("ImportProductController?action=viewdetail&orderId=" + orderId)
.then(res => res.text())
.then(data => {

document.getElementById("detail-content-" + orderId).innerHTML = data

})

}else{

row.style.display = "none"

}

}

</script>

</body>
</html>