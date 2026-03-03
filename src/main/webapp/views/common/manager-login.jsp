<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manager Login - Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="manager-auth-page">
<main class="manager-auth-main">
    <section class="manager-auth-card">
        <h1 class="manager-auth-title">Admin / Staff Login</h1>
        <p class="manager-auth-subtitle">Dang nhap de vao trang quan tri he thong.</p>

        <c:if test="${not empty errorMessage}">
            <div class="manager-auth-error">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/manager-auth" method="post" class="manager-auth-form">
            <label for="email" class="manager-auth-label">Email</label>
            <input id="email" name="email" type="email" class="manager-auth-input"
                   value="${param.email}" required>

            <label for="password" class="manager-auth-label">Password</label>
            <input id="password" name="password" type="password" class="manager-auth-input" required>

            <button type="submit" class="manager-auth-btn">Sign In</button>
        </form>
    </section>
</main>
</body>
</html>
