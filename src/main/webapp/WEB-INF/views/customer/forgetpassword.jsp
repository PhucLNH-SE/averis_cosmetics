<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Forget Password - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <jsp:include page="/assets/header.jsp" />

    <div class="auth-container">

        <div class="auth-header">
            <h2>Forget Password</h2>
            <p>Enter your email to reset your password</p>
        </div>

        <!-- SUCCESS MESSAGE -->
        <c:if test="${not empty msg}">
            <div class="success-message">
                ${msg}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/ForgetPasswordController?action=forget"
              method="post">

            <div class="form-group">
                <label>Email</label>
                <input type="email"
                       name="email"
                       value="${param.email}"
                       placeholder="Enter your email address"
                       required>
            </div>

            <button type="submit" class="btn-login">
                Send password reset link
            </button>
        </form>

        <!-- ERROR MESSAGE -->
        <c:if test="${not empty error}">
            <c:set var="popupMessage" scope="request" value="${error}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

        <div class="auth-links">
            <p>
                Back to login?
                <a href="${pageContext.request.contextPath}/auth">
                    Sign in
                </a>
            </p>
        </div>

    </div>

    <jsp:include page="/assets/footer.jsp" />

</body>
</html>

