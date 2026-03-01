<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Invalid link</title>
</head>
<body>

<h2>Link không hợp lệ hoặc đã hết hạn</h2>

<p>
    Vui lòng thực hiện lại chức năng quên mật khẩu.
</p>

<a href="<%=request.getContextPath()%>/views/common/home.jsp">
    Quay lại quên mật khẩu
</a>

</body>
</html>