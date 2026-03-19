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

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2>Forget Password</h2>
            <p>Enter your new password</p>
        </div>

        <!-- ERROR MESSAGE -->
        <c:if test="${not empty error}">
            <c:set var="popupMessage" scope="request" value="${error}" />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>
        <c:if test="${empty error && not empty errors}">
            <c:set var="popupMessage" scope="request" value="Please fix the highlighted fields and try again." />
            <c:set var="popupType" scope="request" value="error" />
        </c:if>

        <form action="${pageContext.request.contextPath}/ResetPasswordController" 
              method="post">

            <input type="hidden" name="token" value="${token}"/>

            <div class="form-group">
                <label>New password</label>
                <input type="password" 
                       name="password" 
                       placeholder="Enter your new password"
                       required>
                <c:if test="${not empty errors.errorPassword}">
                    <span class="field-error">${errors.errorPassword}</span>
                </c:if>
            </div>

            <div class="form-group">
                <label>Confirm password</label>
                <input type="password" 
                       name="confirmPassword" 
                       placeholder="Re-enter your password"
                       required>
                <c:if test="${not empty errors.errorConfirmPassword}">
                    <span class="field-error">${errors.errorConfirmPassword}</span>
                </c:if>
            </div>

            <button type="submit" class="btn-login">
                Reset password
            </button>

        </form>

        <div class="auth-links">
            <p>
                Back to login?
                <a href="${pageContext.request.contextPath}/auth">
                    Login
                </a>
            </p>
        </div>
    </div>

    <%@include file="/assets/footer.jsp" %>

</body>
</html>
