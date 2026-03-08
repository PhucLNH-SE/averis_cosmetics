<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Orders</title>
</head>
<body>

<form action="${pageContext.request.contextPath}/ManageOrderController" method="post">

<input type="hidden" name="action" value="update">

<table border="1">

<tr>
    <th>Username</th>
    <th>Voucher</th>
    <th>Discount</th>
    <th>Payment Method</th>
    <th>Payment Status</th>
    <th>Order Status</th>
    <th>Total</th>
    <th>Action</th>
</tr>

<c:forEach var="o" items="${orderList}">

<tr>

<td>${o.username}</td>

<td>${o.voucherCode}</td>


<td>${o.discountAmount}</td>

<td>${o.paymentMethod}</td>

<td>

<input type="hidden" name="orderId" value="${o.orderId}">

<select name="paymentStatus">

<option value="PENDING" ${o.paymentStatus=='PENDING'?'selected':''}>PENDING</option>

<option value="SUCCESS" ${o.paymentStatus=='SUCCESS'?'selected':''}>SUCCESS</option>

<option value="FAILED" ${o.paymentStatus=='FAILED'?'selected':''}>FAILED</option>

</select>

</td>

<td>

<select name="orderStatus">

<option value="CREATED" ${o.orderStatus=='CREATED'?'selected':''}>CREATED</option>

<option value="PROCESSING" ${o.orderStatus=='PROCESSING'?'selected':''}>PROCESSING</option>

<option value="SHIPPING" ${o.orderStatus=='SHIPPING'?'selected':''}>SHIPPING</option>

<option value="COMPLETED" ${o.orderStatus=='COMPLETED'?'selected':''}>COMPLETED</option>

<option value="CANCELLED" ${o.orderStatus=='CANCELLED'?'selected':''}>CANCELLED</option>

</select>

</td>

<td>${o.totalAmount}</td>

<td>

<a href="${pageContext.request.contextPath}/ManageOrderController?action=detail&orderId=${o.orderId}">
View Detail
</a>

</td>

</tr>

</c:forEach>

</table>

<br>

<button type="submit">Update</button>

</form>

</body>
</html>