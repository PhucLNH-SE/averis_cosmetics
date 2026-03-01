<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Forgot password</title>
</head>
<body>

<h2>Forget Password</h2>

<form action="${pageContext.request.contextPath}/ForgetPasswordController" method="post">
    <label>Email:</label>
    <input type="email" name="email" required />

    <button type="submit">Send reset link</button>
</form>

<p style="color:red">${error}</p>
<p style="color:green">${msg}</p>

</body>
</html>