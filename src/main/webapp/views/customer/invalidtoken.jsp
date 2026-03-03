<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Link không hợp lệ - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2 style="color: #e74c3c;">Link không hợp lệ</h2>
            <p>Link đặt lại mật khẩu đã hết hạn hoặc không tồn tại.</p>
        </div>

        <div class="error-message" style="text-align: center; padding: 15px;">
            Vui lòng thực hiện lại chức năng quên mật khẩu.
        </div>

        <div class="auth-links">
            <p>
                <a href="${pageContext.request.contextPath}/ForgetPasswordController">
                    Gửi lại link đặt lại mật khẩu
                </a>
            </p>
            <p>
                hoặc
                <a href="${pageContext.request.contextPath}/auth">
                    Đăng nhập
                </a>
            </p>
        </div>
    </div>

    <%@include file="/assets/footer.jsp" %>

</body>
</html>
