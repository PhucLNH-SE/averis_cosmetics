<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EditProfile - Averis Cosmetics</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    </head>

    <body>
        <jsp:include page="/assets/header.jsp" />

        <div class="page-wrap">
            <div class="profile-card">
                <div class="profile-card__header">
                    <h2>Edit Profile</h2>
                    <p>Update your personal information</p>
                </div>

                <div class="profile-card__body">
                    <c:if test="${not empty error}">
                        <div class="profile-form-error">
                            ${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/profile?action=edit" method="post">
                        <div class="form-grid">
                            <label for="fullName">Full name:</label>
                            <div class="form-field">
                                <input type="text"
                                       id="fullName"
                                       name="fullName"
                                       value="${customer.fullName}"
                                       class="${not empty errorFullName ? 'error' : ''}"
                                       required />
                                <c:if test="${not empty errorFullName}">
                                    <span class="field-error">${errorFullName}</span>
                                </c:if>
                            </div>

                            <label for="gender">Gender:</label>
                            <div class="form-field">
                                <select id="gender"
                                        name="gender"
                                        class="gender-wide ${not empty errorGender ? 'error' : ''}">
                                    <option value="" ${empty customer.gender ? "selected" : ""}>-- Select --</option>
                                    <option value="MALE"   ${customer.gender == "MALE" ? "selected" : ""}>Male</option>
                                    <option value="FEMALE" ${customer.gender == "FEMALE" ? "selected" : ""}>Female</option>
                                    <option value="OTHER"  ${customer.gender == "OTHER" ? "selected" : ""}>Other</option>
                                </select>
                                <c:if test="${not empty errorGender}">
                                    <span class="field-error">${errorGender}</span>
                                </c:if>
                            </div>

                            <label for="dateOfBirth">Date Of Birth:</label>
                            <div class="form-field">
                                <input type="date" id="dateOfBirth" name="dateOfBirth"
                                       value="${not empty param.dateOfBirth ? param.dateOfBirth : (not empty dateOfBirth ? dateOfBirth : customer.dateOfBirth)}"
                                       class="${not empty errorDateOfBirth ? 'error' : ''}"
                                       required />
                                <c:if test="${not empty errorDateOfBirth}">
                                    <span class="field-error">${errorDateOfBirth}</span>
                                </c:if>
                            </div>
                        </div>

                        <div class="btns">
                            <button type="submit">Save</button>
                            <a class="linkbtn" href="${pageContext.request.contextPath}/profile?action=view">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>

    <jsp:include page="/assets/footer.jsp" />
</html>

