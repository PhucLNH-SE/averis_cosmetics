<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="order-detail-container">
<a href="${pageContext.request.contextPath}/staff/manage-orders"
   class="btn btn-secondary">
   ← Back to Orders
</a>

<h2>Order Detail - #${order.orderId}</h2>

<div style="margin-bottom:20px;">
    <p><b>Receiver:</b> ${order.receiverName}</p>
    <p><b>Phone:</b> ${order.receiverPhone}</p>

    <p>
        <b>Address:</b>
        ${order.streetAddress},
        ${order.ward},
        ${order.district},
        ${order.province}
    </p>

    <p><b>Payment Method:</b> ${order.paymentMethod}</p>
    <p><b>Order Status:</b> ${order.orderStatus}</p>
</div>


<table class="table table-bordered">
    <thead>
        <tr>
            <th>Image</th>
            <th>Product</th>
            <th>Brand</th>
            <th>Category</th>
            <th>Quantity</th>
            <th>Price</th>
        </tr>
    </thead>

    <tbody>

        <c:forEach items="${details}" var="d">
            <tr>

                <td>
                    <img src="${pageContext.request.contextPath}/assets/img/${d.imageUrl}" width="70">
                </td>

                <td>${d.productName}</td>

                <td>${d.brandName}</td>

                <td>${d.categoryName}</td>

                <td>${d.quantity}</td>

                <td>
                    <fmt:formatNumber value="${d.priceAtOrder}" type="currency"/>
                </td>

            </tr>
        </c:forEach>

    </tbody>
</table>


<div style="margin-top:20px; font-size:18px;">
    <b>Total:</b>
    <fmt:formatNumber value="${order.totalAmount}" type="currency"/>
</div>


</div>
