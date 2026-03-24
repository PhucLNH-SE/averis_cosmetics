<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification | Averis Cosmetics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="verify-result-page">
    <div class="verify-result-box">
        <h2 class="verify-result-title ${success ? 'success' : 'error'}">${success ? 'Success' : 'Verification failed'}</h2>
        <p class="verify-result-message">${message}</p>
        <a class="verify-result-link" href="${pageContext.request.contextPath}/auth?action=login">Sign in</a>
    </div>
</body>
</html>
