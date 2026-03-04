<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Invalid Link - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2 style="color: #e74c3c;">Invalid link</h2>
            <p>The password reset link is expired or does not exist.</p>
        </div>

        <div class="error-message" style="text-align: center; padding: 15px;">
            Please try the forgot password feature again.
        </div>

        <div class="auth-links">
            <p>
                <a href="${pageContext.request.contextPath}/ForgetPasswordController">
                    Resend password reset link
                </a>
            </p>
            <p>
                or
                <a href="${pageContext.request.contextPath}/auth">
                    Sign in
                </a>
            </p>
        </div>

    </div>

    <%@include file="/assets/footer.jsp" %>

</body>
</html>