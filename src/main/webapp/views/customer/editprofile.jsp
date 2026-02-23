<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EditProfile - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    </head>

    <body>
        <%@include file="/assets/header.jsp" %>

        <div class="page-wrap">
            <div class="profile-card">
                <div class="profile-card__header">
                    <h2>Edit Profile</h2>
                    <p>Update your personal information</p>
                </div>

                <div class="profile-card__body">
                    <form action="${pageContext.request.contextPath}/CustomerController?action=edit" method="post">
                        <div class="form-grid">
                         

                            <label>Full name:</label>
                            <input type="text" name="fullName" value="${customer.fullName}" required />

                            <label>Email:</label>
                            <input type="email" name="email" value="${customer.email}" />

                            <label>Gender:</label>
                            <select name="gender" class="gender-wide">
                                <option value="" ${empty customer.gender ? "selected" : ""}>-- Select --</option>
                                <option value="MALE"   ${customer.gender == "MALE" ? "selected" : ""}>Male</option>
                                <option value="FEMALE" ${customer.gender == "FEMALE" ? "selected" : ""}>Female</option>
                                <option value="OTHER"  ${customer.gender == "OTHER" ? "selected" : ""}>Other</option>
                            </select>

                            <label>Date Of Birth:</label>
                            <input type="date" id="dateOfBirth" name="dateOfBirth"
                                   value="${param.dateOfBirth != null ? param.dateOfBirth : customer.dateOfBirth}"
                                   required />
                            <div class="hint">Format: yyyy-mm-dd</div>
                        </div>

                        <div class="btns">
                            <button type="submit">Save</button>
                            <a class="linkbtn" href="${pageContext.request.contextPath}/CustomerController?action=view">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>

    <%@include file="/assets/footer.jsp" %>
</html>