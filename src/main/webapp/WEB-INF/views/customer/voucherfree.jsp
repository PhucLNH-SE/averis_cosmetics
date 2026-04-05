<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Free Vouchers - Averis Cosmetics</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    </head>
    <body>
        <div class="home-layout free-voucher-page">
            <jsp:include page="/assets/header.jsp" />

            <c:if test="${param.success == 'claimed'}">
                <c:set var="popupMessage" scope="request" value="Voucher claimed successfully. It has been added to My Voucher." />
                <c:set var="popupType" scope="request" value="success" />
            </c:if>
            <c:if test="${not empty param.error}">
                <c:set var="popupType" scope="request" value="error" />
                <c:choose>
                    <c:when test="${param.error == 'emptyCode'}">
                        <c:set var="popupMessage" scope="request" value="Voucher code is missing." />
                    </c:when>
                    <c:when test="${param.error == 'codeNotFound'}">
                        <c:set var="popupMessage" scope="request" value="This voucher is not available for claiming." />
                    </c:when>
                    <c:when test="${param.error == 'outOfStock'}">
                        <c:set var="popupMessage" scope="request" value="This voucher is out of stock." />
                    </c:when>
                    <c:when test="${param.error == 'alreadyClaimed'}">
                        <c:set var="popupMessage" scope="request" value="You have already claimed this voucher." />
                    </c:when>
                    <c:when test="${param.error == 'voucherExpired'}">
                        <c:set var="popupMessage" scope="request" value="This voucher can no longer be claimed." />
                    </c:when>
                    <c:otherwise>
                        <c:set var="popupMessage" scope="request" value="Failed to claim voucher." />
                    </c:otherwise>
                </c:choose>
            </c:if>

            <main>
                <section class="free-voucher-hero">
                    <div class="home-container hero-grid free-voucher-hero-grid">
                        <div class="free-voucher-hero-copy">
                            <span class="hero-badge">Member perks</span>
                            <h1 class="hero-title">
                                Claim your <span>free vouchers</span> and keep them ready in your account.
                            </h1>
                            <p class="hero-copy">
                                This page gathers every voucher marked for free claim. If the voucher uses
                                <strong>RELATIVE_DAYS</strong>, it becomes active right after you claim it and
                                appears in My Voucher automatically.
                            </p>
                            <div class="hero-actions">
                                <a class="hero-btn primary" href="${pageContext.request.contextPath}/products">Shop now</a>
                                <c:choose>
                                    <c:when test="${not empty sessionScope.customer}">
                                        <a class="hero-btn voucher" href="${pageContext.request.contextPath}/profile?action=view&tab=voucher">My Voucher</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="hero-btn voucher" href="${pageContext.request.contextPath}/login">Login to Claim</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="free-voucher-hero-card">
                            <div class="free-voucher-hero-card__head">
                                <span class="section-pill">Free Voucher Hub</span>
                                <h3>Everything claimed here is saved for later checkout.</h3>
                            </div>
                            <div class="free-voucher-summary">
                                <div class="free-voucher-summary-item">
                                    <span class="free-voucher-summary-label">Available now</span>
                                    <strong>${fn:length(freeVouchers)}</strong>
                                </div>
                                <div class="free-voucher-summary-item">
                                    <span class="free-voucher-summary-label">Claim flow</span>
                                    <strong>1 click</strong>
                                </div>
                                <div class="free-voucher-summary-item">
                                    <span class="free-voucher-summary-label">Activation</span>
                                    <strong>Instant</strong>
                                </div>
                            </div>
                            <ul class="free-voucher-hero-list">
                                <li>Claimed vouchers are saved to your account wallet.</li>
                                <li>Relative-day vouchers start running from the moment you claim.</li>
                                <li>Fixed-end vouchers stay valid until their configured deadline.</li>
                            </ul>
                        </div>
                    </div>
                </section>

                <section class="home-featured free-voucher-section">
                    <div class="home-container">
                        <div class="section-header">
                            <span class="section-pill">Free voucher list</span>
                            <h2 class="section-title">Pick a voucher and <span>claim it now</span></h2>
                            <p class="section-desc">
                                Only vouchers enabled for free claim appear here. Once claimed, they can be used from your
                                Profile page when the voucher is active.
                            </p>
                        </div>

                        <c:choose>
                            <c:when test="${empty freeVouchers}">
                                <section class="free-voucher-empty">
                                    <h3>No free vouchers available right now</h3>
                                    <p>Please come back later. New campaigns will appear here as soon as they are opened.</p>
                                    <a class="hero-btn voucher" href="${pageContext.request.contextPath}/products">Explore Products</a>
                                </section>
                            </c:when>
                            <c:otherwise>
                                <section class="free-voucher-grid">
                                    <c:forEach items="${freeVouchers}" var="v">
                                        <fmt:formatNumber value="${v.discountValue}" pattern="#,##0.##" var="discountRaw"/>
                                        <c:set var="remainingQuantity" value="${v.quantity - v.claimedQuantity}" />
                                        <c:set var="fixedEndAtStr" value="${fn:replace(v.fixedEndAt, 'T', ' ')}"/>
                                        <article class="free-voucher-card">
                                            <div class="free-voucher-card-top">
                                                <span class="free-voucher-tag">
                                                    <c:choose>
                                                        <c:when test="${v.voucherType eq 'RELATIVE_DAYS'}">RELATIVE_DAYS</c:when>
                                                        <c:otherwise>FIXED_END_DATE</c:otherwise>
                                                    </c:choose>
                                                </span>
                                                <c:if test="${not empty claimedVoucherIds and claimedVoucherIds[v.voucherId]}">
                                                    <span class="free-voucher-state">Claimed</span>
                                                </c:if>
                                            </div>

                                            <div class="free-voucher-discount">
                                                <c:choose>
                                                    <c:when test="${v.discountType eq 'PERCENT'}">${discountRaw}% OFF</c:when>
                                                    <c:otherwise>${discountRaw} VND OFF</c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="free-voucher-code">${v.code}</div>

                                            <p class="free-voucher-note">
                                                <c:choose>
                                                    <c:when test="${v.voucherType eq 'RELATIVE_DAYS'}">
                                                        Active for ${v.relativeDays} day(s) right after claiming.
                                                    </c:when>
                                                    <c:otherwise>
                                                        Use it before
                                                        <c:choose>
                                                            <c:when test="${not empty fixedEndAtStr}">
                                                                ${fn:length(fixedEndAtStr) > 16 ? fn:substring(fixedEndAtStr, 0, 16) : fixedEndAtStr}.
                                                            </c:when>
                                                            <c:otherwise>
                                                                the configured end date.
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>

                                            <div class="free-voucher-details">
                                                <div class="free-voucher-detail">
                                                    <span class="free-voucher-detail__label">Remaining</span>
                                                    <span class="free-voucher-detail__value">${remainingQuantity}</span>
                                                </div>
                                                <div class="free-voucher-detail">
                                                    <span class="free-voucher-detail__label">Claim status</span>
                                                    <span class="free-voucher-detail__value">
                                                        <c:choose>
                                                            <c:when test="${not empty claimedVoucherIds and claimedVoucherIds[v.voucherId]}">Saved</c:when>
                                                            <c:otherwise>Ready</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </div>
                                            </div>

                                            <div class="free-voucher-claim">
                                                <c:choose>
                                                    <c:when test="${not empty claimedVoucherIds and claimedVoucherIds[v.voucherId]}">
                                                        <span class="free-voucher-pill">Already in My Voucher</span>
                                                    </c:when>
	                                                    <c:when test="${not empty sessionScope.customer}">
	                                                        <form method="post" action="${pageContext.request.contextPath}/voucher-free" class="free-voucher-claim-form">
	                                                            <input type="hidden" name="action" value="claim">
	                                                            <input type="hidden" name="source" value="voucher-free">
	                                                            <input type="hidden" name="voucherCode" value="${v.code}">
	                                                            <button type="submit" class="free-voucher-btn">Claim Voucher</button>
	                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a class="free-voucher-login" href="${pageContext.request.contextPath}/login">Login to Claim</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </section>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>
            </main>

            <jsp:include page="/WEB-INF/views/common/popup.jsp" />
            <jsp:include page="/assets/footer.jsp" />
        </div>
    </body>
</html>
