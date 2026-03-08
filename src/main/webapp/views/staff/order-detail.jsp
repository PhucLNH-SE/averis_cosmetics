<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Order Detail</title>

    <style>
        table{
            width: 100%;
            border-collapse: collapse;
        }
        th, td{
            border: 1px solid #ddd;
            padding: 10px;
            text-align: center;
        }
        img{
            width: 80px;
        }
    </style>

</head>
<body>

<h2>Order Detail</h2>

<table>
    <thead>
        <tr>
            <th>Image</th>
            <th>Product Name</th>
            <th>Brand</th>
            <th>Category</th>
            <th>Quantity</th>
            <th>Price</th>
        </tr>
    </thead>

    <tbody>

        <c:forEach var="od" items="${orderDetails}">
            <tr>

                <td>
<img src="${pageContext.request.contextPath}/assets/img/${od.imageUrl}">
                </td>

                <td>${od.productName}</td>

                <td>${od.brandName}</td>

                <td>${od.categoryName}</td>

                <td>${od.quantity}</td>

                <td>${od.priceAtOrder}</td>

            </tr>
        </c:forEach>

    </tbody>
</table>

<br>

<a href="${pageContext.request.contextPath}/ManageOrderController?action=list">
    Back to Orders
</a>

</body>
</html>