<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="popup-overlay" id="resultPopup">
    <div class="popup-content">
        <div class="popup-icon" id="popupIcon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
            </svg>
        </div>
        <h2 class="popup-title" id="popupTitle">Success</h2>
        <p class="popup-message" id="popupMessage"></p>
        <button class="popup-btn" id="popupBtn" type="button">Close</button>
    </div>
</div>

<script>
    window.showPopup = function (success, message, titleText, buttonLabel, redirectUrl) {
        const popup = document.getElementById('resultPopup');
        const icon = document.getElementById('popupIcon');
        const title = document.getElementById('popupTitle');
        const msg = document.getElementById('popupMessage');
        const btn = document.getElementById('popupBtn');

        if (!popup) {
            return;
        }

        if (success) {
            icon.classList.remove('error');
            icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" /></svg>';
            title.textContent = titleText || 'Success';
            btn.textContent = buttonLabel || 'Close';
        } else {
            icon.classList.add('error');
            icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>';
            title.textContent = titleText || 'Error';
            btn.textContent = buttonLabel || 'Close';
        }

        msg.innerHTML = message || '';
        popup.classList.add('show');

        btn.onclick = function () {
            popup.classList.remove('show');
            if (redirectUrl) {
                window.location.href = redirectUrl;
            }
        };
    };
</script>

<c:if test="${not empty popupMessage}">
    <c:set var="popupSafeMessage" value="${fn:escapeXml(popupMessage)}" />
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            showPopup(
                ${popupType == 'success' ? 'true' : 'false'},
                "<c:out value='${popupSafeMessage}'/>"
                    .replace(/\\n/g, '<br>')
                    .replace(/\r?\n/g, '<br>')
            );
        });
    </script>
</c:if>

