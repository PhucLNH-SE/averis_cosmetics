<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification | Averis Cosmetics</title>
    <style>
        body { font-family: Arial, sans-serif; background: #faf8f5; margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }
        .box { max-width: 420px; padding: 32px; background: #fff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); text-align: center; }
        .box h2 { margin: 0 0 16px 0; font-size: 20px; color: #1f2937; }
        .box p { margin: 0 0 24px 0; color: #6b7280; line-height: 1.5; }
        .box a { display: inline-block; padding: 12px 24px; background: #b45309; color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; }
        .box a:hover { background: #92400e; }
        .success { color: #059669; }
        .error { color: #dc2626; }
    </style>
</head>
<body>
    <div class="box">
        <h2 class="${success ? 'success' : 'error'}">${success ? 'Success' : 'Verification failed'}</h2>
        <p>${message}</p>
        <a href="${pageContext.request.contextPath}/auth?action=login">Sign in</a>
    </div>
</body>
</html>
