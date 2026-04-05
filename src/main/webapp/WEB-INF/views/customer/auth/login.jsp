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

<body class="auth-page customer-login-page">

    <jsp:include page="/assets/header.jsp" />

    <div class="auth-container customer-login-card">

        <div class="auth-header">
            <h2>Welcome Back</h2>
            <p>Sign in to continue your shopping experience</p>
        </div>

        <c:if test="${not empty successMessage && empty popupMessage}">
            <c:set var="popupMessage" scope="request" value="${successMessage}" />
            <c:set var="popupType" scope="request" value="success" />
        </c:if>

        <c:if test="${empty popupMessage and not empty sessionScope.loginPopupMessage}">
            <c:set var="popupMessage" scope="request" value="${sessionScope.loginPopupMessage}" />
            <c:set var="popupType" scope="request" value="${not empty sessionScope.loginPopupType ? sessionScope.loginPopupType : 'success'}" />
            <c:remove var="loginPopupMessage" scope="session" />
            <c:remove var="loginPopupType" scope="session" />
        </c:if>

        <c:if test="${not empty popupMessage}">
            <div class="${popupType == 'error' ? 'error-message' : 'success-message'}">
                ${popupMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login"
              method="post" novalidate>

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

        <div class="auth-links">
            <p>
                Don't have an account?
                <a href="${pageContext.request.contextPath}/register">
                    Register here
                </a>
            </p>
        </div>
 <div class="auth-links">
            <p>
                
                <a href="${pageContext.request.contextPath}/ForgetPasswordController?action=forget">
                    Forget Password
                </a>
            </p>
        </div>
    </div>

    <jsp:include page="/assets/footer.jsp" />

</body>
</html>

