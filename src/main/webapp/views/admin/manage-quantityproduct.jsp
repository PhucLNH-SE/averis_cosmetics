<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
    <title>Product Quantity</title>
    <style>
        table{
            border-collapse: collapse;
            width:100%;
        }
        th,td{
            border:1px solid #ccc;
            padding:8px;
            text-align:center;
        }
        img{
            width:60px;
            height:60px;
            object-fit:cover;
        }
    </style>
</head>
<body>
  <%@include file="/assets/header.jsp" %>
<h2>Manage Product Quantity</h2>

<table>
    <thead>
        <tr>
            
            <th>Product</th>
            <th>Image</th>
            <th>Variant Name</th>
            <th>Category</th>
            <th>Status</th>
            <th>Price</th>
            <th>Stock</th>
            
            <th>Update Quantity Product</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${list}" var="v">
            <tr>
  <td>${v.productName}</td>  
  <td>
    <c:if test="${not empty v.imageUrl}">
        <img src="${pageContext.request.contextPath}/${v.imageUrl}">
    </c:if>
</td>
                <td>${v.variantName}</td>

              

                <td>${v.categoryName}</td>

     <td>
                    <c:choose>
                        <c:when test="${v.status}">
                            Active
                        </c:when>
                        <c:otherwise>
                            Inactive
                        </c:otherwise>
                    </c:choose>
                </td>

                <td>${v.price}</td>

                <!-- update stock -->
                <td>
                    <form action="${pageContext.request.contextPath}/PQuantityManagerController"
                          method="post">

                        <input type="hidden" name="action" value="updateStock">
                        <input type="hidden" name="variantId" value="${v.variantId}">

                        <input type="number"
                               name="stock"
                               value="${v.stock}"
                               min="0"
                               style="width:70px">

                </td>

              

                <td>
                        <button type="submit">Save</button>
                    </form>
                </td>

            </tr>
        </c:forEach>
    </tbody>
</table>
  <%@include file="/assets/footer.jsp" %>

</body>
</html>