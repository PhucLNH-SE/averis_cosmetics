<%-- 
    Document   : address-list
    Created on : Feb 10, 2026, 8:22:37 PM
    Author     : HAPPY
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Address List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .address-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin: 10px 0;
            background-color: #f9f9f9;
        }
        .default-badge {
            background-color: #007bff;
            color: white;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 12px;
            margin-left: 10px;
        }
        .add-btn {
            background-color: #28a745;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin-bottom: 20px;
        }
        .address-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .receiver-info {
            font-weight: bold;
            font-size: 16px;
        }
        .phone {
            color: #666;
            margin-left: 15px;
        }
        .address-details {
            margin: 10px 0;
            line-height: 1.5;
        }
    </style>
</head>
<body>

<h2>My Addresses</h2>

<a href="<%=request.getContextPath()%>/add-address" class="add-btn">+ Add New Address</a>

<c:if test="${empty addressList}">
    <p>No addresses found. <a href="<%=request.getContextPath()%>/add-address">Add your first address</a></p>
</c:if>

<c:forEach var="address" items="${addressList}">
    <div class="address-card">
        <div class="address-header">
            <div>
                <span class="receiver-info">${address.receiverName}</span>
                <span class="phone">${address.phone}</span>
                <c:if test="${address.isDefault}">
                    <span class="default-badge">DEFAULT</span>
                </c:if>
            </div>
        </div>
        
        <div class="address-details">
            ${address.streetAddress}<br>
            ${address.ward}, ${address.district}<br>
            ${address.province}
        </div>
        
        <div>
            <!-- Add action buttons here if needed -->
            <!-- <button>Edit</button> <button>Delete</button> -->
        </div>
    </div>
</c:forEach>

</body>
</html>