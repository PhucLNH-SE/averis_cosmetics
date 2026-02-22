<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Register - Averis Cosmetics</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    <style>
        * {
            box-sizing: border-box;
        }
        
        body {
            background: linear-gradient(135deg, #b45309 0%, #92400e 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .auth-container {
            max-width: 500px;
            margin: 30px auto;
            padding: 40px;
            background: white;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
        }
        
        .auth-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .auth-header h2 {
            color: #333;
            margin: 0 0 10px 0;
            font-size: 32px;
            font-weight: 700;
        }
        
        .auth-header p {
            color: #666;
            margin: 0;
            font-size: 16px;
        }
        
        .form-group {
            margin-bottom: 25px;
            position: relative;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #444;
            font-size: 14px;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 14px 16px;
            border: 2px solid #e1e5e9;
            border-radius: 12px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #f8f9fa;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #b45309;
            background: white;
            box-shadow: 0 0 0 4px rgba(180, 83, 9, 0.1);
        }
        
        .form-group input.error,
        .form-group select.error {
            border-color: #e74c3c;
            background: #fdf2f2;
        }
        
        .error-message {
            color: #e74c3c;
            font-size: 13px;
            margin-top: 6px;
            display: block;
            opacity: 0;
            transform: translateY(-10px);
            transition: all 0.3s ease;
        }
        
        .error-message.show {
            opacity: 1;
            transform: translateY(0);
        }
        
        .btn-register {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #b45309 0%, #92400e 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-top: 10px;
        }
        
        .btn-register:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(180, 83, 9, 0.3);
        }
        
        .btn-register:active {
            transform: translateY(0);
        }
        
        .auth-links {
            text-align: center;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        
        .auth-links a {
            color: #b45309;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s ease;
        }
        
        .auth-links a:hover {
            color: #92400e;
            text-decoration: underline;
        }
        
        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-left: 4px solid #28a745;
            animation: slideIn 0.5s ease;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
    </style>
</head>
<body>
    <%@include file="/assets/header.jsp" %>
    
    <div class="container">
        <div class="auth-container">
            <div class="auth-header">
                <h2>Create Account</h2>
                <p>Join us today to enjoy exclusive benefits</p>
            </div>
            
            <c:if test="${not empty successMessage}">
                <div class="success-message">${successMessage}</div>
            </c:if>
            
            <form id="registerForm" action="${pageContext.request.contextPath}/auth?action=register" method="post">
                <div class="form-group">
                    <label for="username">Username *</label>
                    <input type="text" id="username" name="username" value="${param.username}" required minlength="3" maxlength="50"
                           class="${not empty errorUsername ? 'error' : ''}">
                    <span class="error-message ${not empty errorUsername ? 'show' : ''}" id="username-error">${errorUsername}</span>
                </div>
                
                <div class="form-group">
                    <label for="fullname">Full Name *</label>
                    <input type="text" id="fullname" name="fullname" value="${param.fullname}" required minlength="2" maxlength="100"
                           class="${not empty errorFullName ? 'error' : ''}">
                    <span class="error-message ${not empty errorFullName ? 'show' : ''}" id="fullname-error">${errorFullName}</span>
                </div>
                
                <div class="form-group">
                    <label for="email">Email (Optional)</label>
                    <input type="email" id="email" name="email" value="${param.email}"
                           class="${not empty errorEmail ? 'error' : ''}">
                    <span class="error-message ${not empty errorEmail ? 'show' : ''}" id="email-error">${errorEmail}</span>
                </div>
                
                <div class="form-group">
                    <label for="password">Password *</label>
                    <input type="password" id="password" name="password" required minlength="6"
                           class="${not empty errorPassword ? 'error' : ''}">
                    <span class="error-message ${not empty errorPassword ? 'show' : ''}" id="password-error">${errorPassword}</span>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password *</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required
                           class="${not empty errorConfirmPassword ? 'error' : ''}">
                    <span class="error-message ${not empty errorConfirmPassword ? 'show' : ''}" id="confirmPassword-error">${errorConfirmPassword}</span>
                </div>
                
                <div class="form-group">
                    <label for="gender">Gender</label>
                    <select id="gender" name="gender">
                        <option value="">Select Gender</option>
                        <option value="Male" ${param.gender == 'Male' ? 'selected' : ''}>Male</option>
                        <option value="Female" ${param.gender == 'Female' ? 'selected' : ''}>Female</option>
                        <option value="Other" ${param.gender == 'Other' ? 'selected' : ''}>Other</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="dateOfBirth">Date of Birth *</label>
                    <input type="date" id="dateOfBirth" name="dateOfBirth" value="${param.dateOfBirth}" required
                           class="${not empty errorDateOfBirth ? 'error' : ''}">
                    <span class="error-message ${not empty errorDateOfBirth ? 'show' : ''}" id="dateOfBirth-error">${errorDateOfBirth}</span>
                </div>
                
                <button type="submit" class="btn-register">Create Account</button>
            </form>
            
            <div class="auth-links">
                <p>Already have an account? <a href="<%=request.getContextPath()%>/auth?action=login">Login here</a></p>
            </div>
        </div>
    </div>
    
    <%@include file="/assets/footer.jsp" %>
    
</body>
</html>