<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đặt lại mật khẩu - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2>Đặt lại mật khẩu</h2>
            <p>Nhập mật khẩu mới</p>
        </div>

        <!-- ERROR MESSAGE -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/ResetPasswordController" 
              method="post">

            <input type="hidden" name="token" value="${token}"/>

            <div class="form-group">
                <label>Mật khẩu mới</label>
                <input type="password" 
                       name="password" 
                       placeholder="Nhập mật khẩu mới"
                       required>
                <c:if test="${not empty errors.errorPassword}">
                    <span class="field-error">${errors.errorPassword}</span>
                </c:if>
            </div>

            <div class="form-group">
                <label>Xác nhận mật khẩu</label>
                <input type="password" 
                       name="confirmPassword" 
                       placeholder="Nhập lại mật khẩu"
                       required>
                <c:if test="${not empty errors.errorConfirmPassword}">
                    <span class="field-error">${errors.errorConfirmPassword}</span>
                </c:if>
            </div>

            <button type="submit" class="btn-login">
                Đặt lại mật khẩu
            </button>

        </form>

        <!-- ERROR MESSAGE -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>

        <div class="auth-links">
            <p>
                Quay lại đăng nhập?
                <a href="${pageContext.request.contextPath}/auth">
                    Đăng nhập
                </a>
            </p>
        </div>
    </div>

    <%@include file="/assets/footer.jsp" %>

</body>
</html>