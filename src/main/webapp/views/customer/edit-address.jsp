<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="formAddress" value="${not empty requestScope.address ? requestScope.address : address}" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Edit Address</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="/assets/header.jsp" />

        <div class="container">
            <div class="address-form-container">
                <div class="address-form-header">
                    <h2><i class="fas fa-edit"></i> Edit Address</h2>
                    <p>Update your delivery address details</p>
                </div>

                <c:if test="${not empty error}">
                    <c:set var="popupMessage" scope="request" value="${error}" />
                    <c:set var="popupType" scope="request" value="error" />
                </c:if>

                <form action="${pageContext.request.contextPath}/address" method="POST"
                      data-address-form="true"
                      data-address-api-url="${pageContext.request.contextPath}/address-api">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${formAddress.addressId}">

                    <div class="address-form-group">
                        <label class="address-form-label" for="receiverName">Receiver Name *</label>
                        <input type="text" class="form-control" id="receiverName" name="receiverName"
                               value="${formAddress.receiverName}" required>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="phone">Phone Number *</label>
                        <input type="tel" class="form-control" id="phone" name="phone"
                               value="${formAddress.phone}" inputmode="numeric"
                               placeholder="e.g. 0912345678" required>
                        <small class="form-text text-muted">Enter a valid Vietnamese phone number.</small>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="province">Province/City *</label>
                        <select class="form-control" id="province" name="province" data-selected="${formAddress.province}" required>
                            <option value="">Select province/city</option>
                        </select>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="district">District *</label>
                        <select class="form-control" id="district" name="district" data-selected="${formAddress.district}" required>
                            <option value="">Select district</option>
                        </select>
                        <small id="addressApiStatus" class="form-text text-muted"></small>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="ward">Ward *</label>
                        <select class="form-control" id="ward" name="ward" data-selected="${formAddress.ward}" required>
                            <option value="">Select ward</option>
                        </select>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="streetAddress">Street Address *</label>
                        <input type="text" class="form-control" id="streetAddress" name="streetAddress"
                               value="${formAddress.streetAddress}"
                               placeholder="House number, street name, apartment..." required>
                        <small class="form-text text-muted">Enter the specific street address manually.</small>
                    </div>

                    <div class="address-form-group form-check">
                        <input type="checkbox" class="form-check-input" id="isDefault" name="isDefault"
                               ${formAddress.isDefault ? 'checked' : ''}>
                        <label class="form-check-label" for="isDefault">
                            Set as default address
                        </label>
                    </div>

                    <div class="address-form-actions">
                        <button type="submit" class="btn btn-primary address-btn-submit">
                            <i class="fas fa-save"></i> Update Address
                        </button>
                        <a href="${pageContext.request.contextPath}/address"
                           class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Addresses
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <jsp:include page="/assets/footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/address-api.js"></script>
    </body>
</html>

