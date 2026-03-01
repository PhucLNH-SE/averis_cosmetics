<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2>Quên mật khẩu</h2>
            <p>Nhập email để lấy lại mật khẩu</p>
        </div>

        <!-- SUCCESS MESSAGE -->
        <c:if test="${not empty msg}">
            <div class="success-message">
                ${msg}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/ForgetPasswordController" 
              method="post">

            <div class="form-group">
                <label>Email</label>
                <input type="email" 
                       name="email" 
                       value="${param.email}"
                       placeholder="Nhập email của bạn"
                       required>
            </div>

            <button type="submit" class="btn-login">
                Gửi link đặt lại mật khẩu
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
