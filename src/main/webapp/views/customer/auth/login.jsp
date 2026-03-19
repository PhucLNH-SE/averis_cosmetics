<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2>Welcome Back</h2>
            <p>Sign in to continue your shopping experience</p>
        </div>

        <!-- SUCCESS MESSAGE -->
        <c:if test="${not empty successMessage}">
            <div class="success-message">
                ${successMessage}
            </div>
        </c:if>
        <c:if test="${not empty errors}">
            <c:set var="popupMessage" scope="request" value="Please fix the highlighted fields and try again." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

        <form action="${pageContext.request.contextPath}/auth?action=login"
              method="post">

            <div class="form-group">
                <label>Username</label>
                <input type="text"
                       name="username"
                       value="${param.username}"
                       required>
                <c:if test="${not empty errors.errorUsername}">
                    <span class="field-error">${errors.errorUsername}</span>
                </c:if>
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password"
                       name="password"
                       required>
                <c:if test="${not empty errors.errorPassword}">
                    <span class="field-error">${errors.errorPassword}</span>
                </c:if>
            </div>

            <button type="submit" class="btn-login">
                Sign In
            </button>

        </form>

        <!-- ERROR MESSAGE (for login failure, account deactivated) -->
        <c:if test="${not empty errorMessage}">
            <c:set var="popupMessage" scope="request" value="${errorMessage}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

        <!-- SUCCESS MESSAGE -->
        <c:if test="${not empty successMessage}">
            <div class="success-message">
                ${successMessage}
            </div>
        </c:if>

        <div class="auth-links">
            <p>
                Don't have an account?
                <a href="${pageContext.request.contextPath}/auth?action=register">
                    Register here
                </a>
            </p>
        </div>
 <div class="auth-links">
            <p>
                
                <a href="${pageContext.request.contextPath}/ForgetPasswordController">
                    Forget Password
                </a>
            </p>
        </div>
    </div>

    <%@include file="/assets/footer.jsp" %>

</body>
</html>
