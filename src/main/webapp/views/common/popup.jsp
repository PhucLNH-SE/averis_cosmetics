<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style>
    .popup-overlay {
        display: none;
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.6);
        z-index: 9999;
        justify-content: center;
        align-items: center;
        backdrop-filter: blur(4px);
    }

    .popup-overlay.show {
        display: flex;
    }

    .popup-content {
        background: #fff;
        border-radius: 20px;
        padding: 40px;
        max-width: 420px;
        width: 90%;
        text-align: center;
        animation: popupSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
        box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    }

    @keyframes popupSlideIn {
        from {
            opacity: 0;
            transform: translateY(-30px) scale(0.9);
        }

        to {
            opacity: 1;
            transform: translateY(0) scale(1);
        }
    }

    .popup-icon {
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #dcfce7, #86efac);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 24px;
        animation: popupIconBounce 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
    }

    @keyframes popupIconBounce {
        0% {
            transform: scale(0);
        }

        50% {
            transform: scale(1.2);
        }

        100% {
            transform: scale(1);
        }
    }

    .popup-icon svg {
        width: 40px;
        height: 40px;
        color: #16a34a;
    }

    .popup-icon.error {
        background: linear-gradient(135deg, #fee2e2, #fecaca);
    }

    .popup-icon.error svg {
        color: #dc2626;
    }

    .popup-title {
        font-size: 26px;
        font-weight: 700;
        color: #1f2937;
        margin: 0 0 12px 0;
    }

    .popup-message {
        font-size: 15px;
        color: #6b7280;
        margin: 0 0 28px 0;
        line-height: 1.6;
    }

    .popup-btn {
        display: inline-block;
        background: linear-gradient(135deg, #b45309, #92400e);
        color: #fff;
        text-decoration: none;
        padding: 14px 40px;
        border-radius: 10px;
        font-weight: 600;
        transition: all 0.2s ease;
        border: none;
        cursor: pointer;
        font-size: 15px;
        box-shadow: 0 4px 6px rgba(180, 83, 9, 0.3);
    }

    .popup-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 15px rgba(180, 83, 9, 0.4);
    }
</style>



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
    window.showPopup = function (success, message, titleText, buttonLabel) {
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
        };
    };
</script>

<c:if test="${not empty popupMessage}">
    <c:set var="popupSafeMessage" value="${fn:escapeXml(popupMessage)}" />
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            showPopup(
                ${popupType == 'success' ? 'true' : 'false'},
                "<c:out value='${popupSafeMessage}'/>".replace(/\r?\n/g, '<br>')
            );
        });
    </script>
</c:if>

