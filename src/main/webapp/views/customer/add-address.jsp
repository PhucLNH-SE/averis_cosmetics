<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add New Address</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    </head>
    <body>
        <%@include file="/assets/header.jsp" %>
        
        <div class="container">
            <div class="address-form-container">
                <div class="address-form-header">
                    <h2><i class="fas fa-plus-circle"></i> Add New Address</h2>
                    <p>Enter your delivery address details</p>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/address" method="POST">
                    <input type="hidden" name="action" value="add">
                    
                    <div class="address-form-group">
                        <label class="address-form-label" for="receiverName">Receiver Name *</label>
                        <input type="text" class="form-control" id="receiverName" name="receiverName" 
                               value="${param.receiverName}" required>
                    </div>
                    
                    <div class="address-form-group">
                        <label class="address-form-label" for="phone">Phone Number *</label>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               value="${param.phone}" required>
                    </div>
                    
                    <div class="address-form-group">
                        <label class="address-form-label" for="province">Province/City *</label>
                        <input type="text" class="form-control" id="province" name="province" 
                               value="${param.province}" required>
                    </div>
                    
                    <div class="address-form-group">
                        <label class="address-form-label" for="district">District *</label>
                        <input type="text" class="form-control" id="district" name="district" 
                               value="${param.district}" required>
                    </div>
                    
                    <div class="address-form-group">
                        <label class="address-form-label" for="ward">Ward *</label>
                        <input type="text" class="form-control" id="ward" name="ward" 
                               value="${param.ward}" required>
                    </div>
                    
                    <div class="address-form-group">
                        <label class="address-form-label" for="streetAddress">Street Address *</label>
                        <textarea class="form-control" id="streetAddress" name="streetAddress" 
                                  rows="3" required>${param.streetAddress}</textarea>
                    </div>
                    
                    <div class="address-form-group form-check">
                        <input type="checkbox" class="form-check-input" id="isDefault" name="isDefault" 
                               ${param.isDefault == 'on' ? 'checked' : ''}>
                        <label class="form-check-label" for="isDefault">
                            Set as default address
                        </label>
                    </div>
                    
                    <div class="address-form-actions">
                        <button type="submit" class="btn btn-primary address-btn-submit">
                            <i class="fas fa-save"></i> Save Address
                        </button>
                        <a href="${pageContext.request.contextPath}/CustomerController?action=view&tab=address" 
                           class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Addresses
                        </a>
                    </div>
                </form>
            </div>
        </div>
        
        <%@include file="/assets/footer.jsp" %>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>