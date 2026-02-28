<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Averis Cosmetics</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="auth-page">

    <%@include file="/assets/header.jsp" %>

    <div class="auth-container">

        <div class="auth-header">
            <h2>Create Account</h2>
            <p>Join us today to enjoy exclusive benefits</p>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="success-message">
                ${successMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth?action=register"
              method="post">

            <!-- USERNAME -->
            <div class="form-group">
                <label>Username *</label>
                <input type="text"
                       name="username"
                       value="${param.username}"
                       class="${not empty errors.errorUsername ? 'error' : ''}"
                       required>

                <c:if test="${not empty errors.errorUsername}">
                    <span class="error-message">
                        ${errors.errorUsername}
                    </span>
                </c:if>
            </div>

            <!-- FULL NAME -->
            <div class="form-group">
                <label>Full Name *</label>
                <input type="text"
                       name="fullname"
                       value="${param.fullname}"
                       class="${not empty errors.errorFullName ? 'error' : ''}"
                       required>

                <c:if test="${not empty errors.errorFullName}">
                    <span class="error-message">
                        ${errors.errorFullName}
                    </span>
                </c:if>
            </div>

            <!-- EMAIL -->
            <div class="form-group">
                <label>Email</label>
                <input type="email"
                       name="email"
                       value="${param.email}"
                       class="${not empty errors.errorEmail ? 'error' : ''}">

                <c:if test="${not empty errors.errorEmail}">
                    <span class="error-message">
                        ${errors.errorEmail}
                    </span>
                </c:if>
            </div>

            <!-- PASSWORD -->
            <div class="form-group">
                <label>Password *</label>
                <input type="password"
                       name="password"
                       class="${not empty errors.errorPassword ? 'error' : ''}"
                       required>

                <c:if test="${not empty errors.errorPassword}">
                    <span class="error-message">
                        ${errors.errorPassword}
                    </span>
                </c:if>
            </div>

            <!-- CONFIRM PASSWORD -->
            <div class="form-group">
                <label>Confirm Password *</label>
                <input type="password"
                       name="confirmPassword"
                       class="${not empty errors.errorConfirmPassword ? 'error' : ''}"
                       required>

                <c:if test="${not empty errors.errorConfirmPassword}">
                    <span class="error-message">
                        ${errors.errorConfirmPassword}
                    </span>
                </c:if>
            </div>

            <!-- GENDER -->
            <div class="form-group">
                <label>Gender</label>
                <select name="gender">
                    <option value="">Select Gender</option>
                    <option value="Male"
                        <c:if test="${param.gender == 'Male'}">selected</c:if>>
                        Male
                    </option>
                    <option value="Female"
                        <c:if test="${param.gender == 'Female'}">selected</c:if>>
                        Female
                    </option>
                    <option value="Other"
                        <c:if test="${param.gender == 'Other'}">selected</c:if>>
                        Other
                    </option>
                </select>
            </div>

            <!-- DATE OF BIRTH -->
            <div class="form-group">
                <label>Date of Birth *</label>
                <input type="date"
                       name="dateOfBirth"
                       value="${param.dateOfBirth}"
                       class="${not empty errors.errorDateOfBirth ? 'error' : ''}"
                       required>

                <c:if test="${not empty errors.errorDateOfBirth}">
                    <span class="error-message">
                        ${errors.errorDateOfBirth}
                    </span>
                </c:if>
            </div>

            <button type="submit" class="btn-register">
                Create Account
            </button>

        </form>

        <div class="auth-links">
            <p>
                Already have an account?
                <a href="${pageContext.request.contextPath}/auth?action=login">
                    Login here
                </a>
            </p>
        </div>

    </div>

    <%@include file="/assets/footer.jsp" %>

</body>
</html>