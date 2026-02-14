<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EditProfile - Averis Cosmetics</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">

        <style>
            /* ===== Layout center ===== */
            .page-wrap{
                min-height: calc(100vh - 140px);
                display: flex;
                justify-content: center;
                align-items: flex-start;
                padding: 40px 16px 60px;
                background: #ffffff;
            }

            /* ===== Card ===== */
            .profile-card{
                width: 100%;
                max-width: 920px;
                background: #fff;
                border: 1px solid #e9e2d8;
                border-radius: 14px;
                box-shadow: 0 10px 28px rgba(31,41,55,.08);
                overflow: hidden;
            }

            .profile-card__header{
                padding: 18px 22px;
                border-bottom: 1px solid #efe7dc;
                background: linear-gradient(180deg, #fff, #fcfaf7);
                text-align: center;
            }

            .profile-card__header h2{
                margin: 0;
                font-size: 30px;
                color: #111827;
                letter-spacing: .2px;
            }

            .profile-card__header p{
                margin: 8px 0 0;
                color:#6b7280;
            }

            .profile-card__body{
                padding: 22px;
            }

            /* ===== Form ===== */
            .form-grid{
                display: grid;
                grid-template-columns: 160px 1fr;
                gap: 14px 16px;
                align-items: center;
            }

            .form-grid label{
                font-weight: 600;
                color: #111827;
            }

            .form-grid input,
            .form-grid select{
                width: 100%;
                padding: 10px 12px;
                border: 1px solid #e5e7eb;
                border-radius: 10px;
                outline: none;
                font-size: 14px;
                color: #111827;
                background: #fff;
                transition: border-color .15s ease, box-shadow .15s ease;
            }

            .form-grid input:focus,
            .form-grid select:focus{
                border-color: #b45309;
                box-shadow: 0 0 0 3px rgba(180,83,9,.15);
            }
            .form-grid input,
            .form-grid select{
                width: 100%;
                max-width: 520px;   /* <-- chỉnh độ dài của ô */
            }
            .form-grid select.gender-wide{
                max-width: 545px;   /* tùy chỉnh */
            }
            .form-grid .hint{
                grid-column: 2 / 3;
                margin-top: -6px;
                font-size: 12px;
                color: #6b7280;
            }

            /* ===== Buttons ===== */
            .btns{
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 18px;
                padding-top: 16px;
                border-top: 1px solid #f1f5f9;
            }

            .btns button{
                padding: 10px 16px;
                border: none;
                border-radius: 10px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 700;
                background: #b45309;
                color: #fff;
                transition: transform .08s ease, background .2s ease;
            }
            .btns button:hover{
                background:#92400e;
            }
            .btns button:active{
                transform: translateY(1px);
            }

            .btns .linkbtn{
                display: inline-flex;
                align-items: center;
                padding: 10px 16px;
                border-radius: 10px;
                text-decoration: none;
                font-weight: 700;
                color: #111827;
                background: #f3f4f6;
                border: 1px solid #e5e7eb;
            }
            .btns .linkbtn:hover{
                background:#e5e7eb;
                text-decoration:none;
            }


        </style>
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
