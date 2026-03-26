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
        <div class="popup-actions" id="popupActions">
            <button class="popup-btn" id="popupBtn" type="button">Close</button>
            <button class="popup-btn popup-btn-secondary" id="popupCancelBtn" type="button" style="display: none;">Cancel</button>
        </div>
    </div>
</div>

<script>
    window.hidePopup = function () {
        const popup = document.getElementById('resultPopup');
        if (!popup) {
            return;
        }
        popup.classList.remove('show');
    };

    window.showPopup = function (success, message, titleText, buttonLabel, redirectUrl) {
        const popup = document.getElementById('resultPopup');
        const icon = document.getElementById('popupIcon');
        const title = document.getElementById('popupTitle');
        const msg = document.getElementById('popupMessage');
        const btn = document.getElementById('popupBtn');
        const cancelBtn = document.getElementById('popupCancelBtn');

        if (!popup) {
            return;
        }

        cancelBtn.style.display = 'none';
        cancelBtn.onclick = null;

        if (success) {
            icon.classList.remove('error', 'warning');
            icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" /></svg>';
            title.textContent = titleText || 'Success';
            btn.textContent = buttonLabel || 'Close';
        } else {
            icon.classList.remove('warning');
            icon.classList.add('error');
            icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>';
            title.textContent = titleText || 'Error';
            btn.textContent = buttonLabel || 'Close';
        }

        msg.innerHTML = message || '';
        popup.classList.add('show');

        btn.onclick = function () {
            hidePopup();
            if (redirectUrl) {
                window.location.href = redirectUrl;
            }
        };
    };

    window.showConfirmPopup = function (message, titleText, confirmLabel, cancelLabel, onConfirm) {
        const popup = document.getElementById('resultPopup');
        const icon = document.getElementById('popupIcon');
        const title = document.getElementById('popupTitle');
        const msg = document.getElementById('popupMessage');
        const btn = document.getElementById('popupBtn');
        const cancelBtn = document.getElementById('popupCancelBtn');

        if (!popup) {
            return;
        }

        icon.classList.remove('error');
        icon.classList.add('warning');
        icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v4m0 4h.01M10.29 3.86l-7.5 13A1 1 0 0 0 3.66 18h16.68a1 1 0 0 0 .87-1.5l-7.5-13a1 1 0 0 0-1.74 0Z" /></svg>';
        title.textContent = titleText || 'Confirm';
        msg.innerHTML = message || '';
        cancelBtn.style.display = 'inline-block';
        cancelBtn.textContent = cancelLabel || 'No';
        btn.textContent = confirmLabel || 'Yes';
        popup.classList.add('show');

        cancelBtn.onclick = function () {
            hidePopup();
        };

        btn.onclick = function () {
            hidePopup();
            if (typeof onConfirm === 'function') {
                onConfirm();
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

