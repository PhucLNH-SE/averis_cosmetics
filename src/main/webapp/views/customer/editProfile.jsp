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
                    <form action="${pageContext.request.contextPath}/profile?action=edit" method="post">
                        <div class="form-grid">
                            <label>Username:</label>
                            <input type="text" name="username" value="${c.username}" required />

                            <label>Full name:</label>
                            <input type="text" name="fullName" value="${c.fullName}" required />

                            <label>Email:</label>
                            <input type="email" name="email" value="${c.email}" />

                            <label>Gender:</label>
                            <select name="gender" class="gender-wide">
                                <option value="" ${empty c.gender ? "selected" : ""}>-- Select --</option>
                                <option value="MALE"   ${c.gender == "MALE" ? "selected" : ""}>Male</option>
                                <option value="FEMALE" ${c.gender == "FEMALE" ? "selected" : ""}>Female</option>
                                <option value="OTHER"  ${c.gender == "OTHER" ? "selected" : ""}>Other</option>
                            </select>

                            <label>Date Of Birth:</label>
                            <input type="date" id="dateOfBirth" name="dateOfBirth"
                                   value="${param.dateOfBirth != null ? param.dateOfBirth : c.dateOfBirth}"
                                   required />
                            <div class="hint">Format: yyyy-mm-dd</div>
                        </div>

                        <div class="btns">
                            <button type="submit">Save</button>
                            <a class="linkbtn" href="${pageContext.request.contextPath}/profile?action=view">Cancel</a> 

                        </div>

                </div>
                </form>
            </div>
        </div>



    </body>
    <%@include file="/assets/footer.jsp" %> 
</html>
