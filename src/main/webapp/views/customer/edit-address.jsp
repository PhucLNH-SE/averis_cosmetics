<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String geoapifyApiKey = application.getInitParameter("GEOAPIFY_API_KEY");
if (geoapifyApiKey == null || geoapifyApiKey.trim().isEmpty()) {
    geoapifyApiKey = System.getenv("GEOAPIFY_API_KEY");
}
request.setAttribute("geoapifyApiKey", geoapifyApiKey);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Edit Address</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/leaflet@1.9.4/dist/leaflet.css">
        <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    </head>
    <body>
        <%@include file="/assets/header.jsp" %>

        <div class="container">
            <div class="address-form-container">
                <div class="address-form-header">
                    <h2><i class="fas fa-edit"></i> Edit Address</h2>
                    <p>Update your delivery address details</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/address" method="POST"
                      data-address-form="true"
                      data-address-api-url="${pageContext.request.contextPath}/address-api"
                      data-geoapify-key="${geoapifyApiKey}"
                      data-require-address-suggestion="true">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${address.addressId}">

                    <div class="address-form-group">
                        <label class="address-form-label" for="receiverName">Receiver Name *</label>
                        <input type="text" class="form-control" id="receiverName" name="receiverName"
                               value="${address.receiverName}" required>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="phone">Phone Number *</label>
                        <input type="tel" class="form-control" id="phone" name="phone"
                               value="${address.phone}" required>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="province">Province/City *</label>
                        <select class="form-control" id="province" name="province" data-selected="${address.province}" required>
                            <option value="">Select province/city</option>
                        </select>
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="ward">Ward *</label>
                        <select class="form-control" id="ward" name="ward" data-selected="${address.ward}" required>
                            <option value="">Select ward</option>
                        </select>
                        <small id="addressApiStatus" class="form-text text-muted"></small>
                    </div>

                    <input type="hidden" id="district" name="district" data-selected="${address.district}"
                           value="${address.district}">

                    <div class="address-form-group">
                        <label class="address-form-label" for="streetAddress">Street Address *</label>
                        <input type="text" class="form-control" id="streetAddress" name="streetAddress"
                               value="${address.streetAddress}" autocomplete="off"
                               placeholder="Type at least 1 character to search address" required>
                        <div id="streetSuggestions" class="street-suggestion-list list-group" hidden></div>
                        <small class="form-text text-muted">Please choose one suggested address from the list.</small>
                        <input type="hidden" id="selectedAddressId" name="selectedAddressId" value="${param.selectedAddressId}">
                        <input type="hidden" id="selectedAddressLat" name="selectedAddressLat" value="${param.selectedAddressLat}">
                        <input type="hidden" id="selectedAddressLon" name="selectedAddressLon" value="${param.selectedAddressLon}">
                        <input type="hidden" id="originalStreetAddress" value="${address.streetAddress}">
                    </div>

                    <div class="address-form-group">
                        <label class="address-form-label" for="addressMap">Map Location</label>
                        <div id="addressMap" class="address-map-box" style="min-height: 320px;"></div>
                        <small id="geoAddressStatus" class="form-text text-muted"></small>
                    </div>

                    <div class="address-form-group form-check">
                        <input type="checkbox" class="form-check-input" id="isDefault" name="isDefault"
                               ${address.isDefault ? 'checked' : ''}>
                        <label class="form-check-label" for="isDefault">
                            Set as default address
                        </label>
                    </div>

                    <div class="address-form-actions">
                        <button type="submit" class="btn btn-primary address-btn-submit">
                            <i class="fas fa-save"></i> Update Address
                        </button>
                        <a href="${pageContext.request.contextPath}/profile?action=view&tab=address"
                           class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Addresses
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <%@include file="/assets/footer.jsp" %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/leaflet@1.9.4/dist/leaflet.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/address-api.js"></script>
    </body>
</html>
