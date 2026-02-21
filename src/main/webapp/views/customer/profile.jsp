<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profile - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
 </head>

    <body>
        <%@include file="/assets/header.jsp" %>

        <div class="profile-page">
            <div class="profile-container">

                <div class="profile-header">
                    <h2>Your Profile</h2>
                    <p>Manage your account information</p>
                </div>

                <div class="profile-info">
                    <div class="info-row">
                        <div class="info-label">Username:</div>
                        <div class="info-value">${c.username}</div>
                    </div>

                    <div class="info-row">
                        <div class="info-label">Full Name:</div>
                        <div class="info-value">${c.fullName}</div>
                    </div>

                    <div class="info-row">
                        <div class="info-label">Email:</div>
                        <div class="info-value">${empty c.email ? "not updated yet" : c.email}</div>
                    </div>

                    <div class="info-row">
                        <div class="info-label">Gender:</div>
                        <div class="info-value">${empty c.gender ? "not updated yet" : c.gender}</div>
                    </div>

                    <div class="info-row" style="border-bottom:none;">
                        <div class="info-label">Date of birth:</div>
                        <div class="info-value">${empty c.dateOfBirth ? "not updated yet" : c.dateOfBirth}</div>
                    </div>
                </div>

                <div class="profile-actions">
                    <a href="<%=request.getContextPath()%>/profile?action=edit" class="btn-primary">Edit Profile</a>
                    <a href="<%=request.getContextPath()%>/products" class="btn-secondary">Back To Home</a>
                </div>

            </div>
        </div>

        <%@include file="/assets/footer.jsp" %>
    </body>
</html>
