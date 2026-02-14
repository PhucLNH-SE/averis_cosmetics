<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profile - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">

        <style>
            .profile-page{
                background:#fff;
                padding: 40px 16px 60px;
                min-height: calc(100vh - 140px);
            }

            .profile-container {
                max-width: 920px;
                margin: 0 auto;
                padding: 26px 28px;
                border: 1px solid #e9e2d8;
                border-radius: 14px;
                background: #fff;
                box-shadow: 0 10px 28px rgba(31,41,55,.08);
            }

            .profile-header {
                margin: -26px -28px 18px;  
                border-radius: 14px 14px 0 0;
                padding: 18px 22px;
                border-bottom: 1px solid #efe7dc;
                background: linear-gradient(180deg, #fff, #fcfaf7) !important;
                text-align: center;
            }

            .profile-header h2 {
                color: #111827;
                margin: 0;
                font-size: 30px;
            }

            .profile-header p{
                margin: 8px 0 0;
                color:#6b7280;
            }

            .profile-info{
                margin-top: 10px;
            }

            .info-row{
                display:flex;
                gap: 16px;
                padding: 14px 0;
                border-bottom: 1px solid #f3f4f6;
            }

            .info-label{
                width: 160px;
                flex-shrink: 0;
                font-weight: 700;
                color:#111827;
            }

            .info-value{
                flex: 1;
                color:#374151;
            }

            .profile-actions{
                display:flex;
                justify-content:flex-end;
                gap: 10px;
                margin-top: 18px;
                padding-top: 16px;
                border-top: 1px solid #f1f5f9;
            }

            .btn-primary{
                display:inline-flex;
                align-items:center;
                justify-content:center;
                padding: 10px 18px;
                border-radius: 10px;
                font-weight: 700;
                text-decoration:none;
                background:#b45309;
                color:#fff;
                border: none;
            }
            .btn-primary:hover{
                background:#92400e;
                text-decoration:none;
                color:#fff;
            }

            .btn-secondary{
                display:inline-flex;
                align-items:center;
                justify-content:center;
                padding: 10px 18px;
                border-radius: 10px;
                font-weight: 700;
                text-decoration:none;
                background:#f3f4f6;
                color:#111827;
                border: 1px solid #e5e7eb;
            }
            .btn-secondary:hover{
                background:#e5e7eb;
                text-decoration:none;
                color:#111827;
            }


        </style>
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
