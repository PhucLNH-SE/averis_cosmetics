<form action="${pageContext.request.contextPath}/ResetPasswordController"
      method="post">

    <input type="hidden" name="token" value="${token}"/>

    <label>New password</label>
    <input type="password" name="password" required/>


    <label>Confirm password</label>
    <input type="password" name="confirmPassword" required/>
    <p style="color:red">${errors.errorPassword}</p>
<p style="color:red">${errors.errorConfirmPassword}</p>
    <button type="submit">Reset password</button>
</form>


