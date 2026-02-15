<%-- 
    Document   : add-address
    Created on : Feb 10, 2026, 8:22:37 PM
    Author     : HAPPY
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Address</title>
</head>
<body>

<h2>Add Address</h2>

<form action="<%=request.getContextPath()%>/add-address" method="post">

    <input type="text" name="receiverName" placeholder="Receiver name" required><br>
    <input type="text" name="phone" placeholder="Phone" required><br>

    <input type="text" name="province" placeholder="Province" required><br>
    <input type="text" name="district" placeholder="District" required><br>
    <input type="text" name="ward" placeholder="Ward" required><br>

    <input type="text" name="streetAddress" placeholder="Street address" required><br>

    <label>
        <input type="checkbox" name="isDefault"> Set as default
    </label><br><br>

    <button type="submit">Save</button>

</form>

</body>
</html>
