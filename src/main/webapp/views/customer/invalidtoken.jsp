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

    <jsp:include page="/assets/header.jsp" />

    <div class="auth-container">

        <div class="auth-header">
            <h2 style="color: #e74c3c;">Invalid link</h2>
            <p>The password reset link is expired or does not exist.</p>
        </div>

        <c:set var="popupMessage" scope="request" value="Please try the forgot password feature again." />
        <c:set var="popupType" scope="request" value="error" />

        <div class="auth-links">
            <p>
                <a href="${pageContext.request.contextPath}/ForgetPasswordController?action=forget">
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

    <jsp:include page="/assets/footer.jsp" />

</body>
</html>

