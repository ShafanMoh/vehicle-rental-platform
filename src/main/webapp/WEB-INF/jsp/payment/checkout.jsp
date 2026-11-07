<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Checkout"/>
<c:set var="activeNav" value="rentals"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container my-4">
    <h2 class="mb-4">Complete your booking</h2>
    <div class="row g-4">
        <div class="col-lg-7">
            <div class="card-de p-4">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
                </c:if>
                <form action="${ctx}/payments/checkout/${rental.id}" method="post" autocomplete="off">
                    <h5 class="mb-3">Choose how to pay</h5>
                    <div class="row g-3 mb-4">
                        <div class="col-md-4 method-option">
                            <input type="radio" name="method" id="mCard" value="CARD" ${form.method == 'CARD' ? 'checked' : ''}>
                            <label for="mCard"><i class="bi bi-credit-card fs-4 d-block mb-1"></i><strong>Card</strong>
                                <span class="d-block small text-muted-de">Instant &middot; 2% fee</span></label>
                        </div>
                        <div class="col-md-4 method-option">
                            <input type="radio" name="method" id="mBank" value="BANK" ${form.method == 'BANK' ? 'checked' : ''}>
                            <label for="mBank"><i class="bi bi-bank fs-4 d-block mb-1"></i><strong>Bank transfer</strong>
                                <span class="d-block small text-muted-de">LKR 100 fee</span></label>
                        </div>
                        <div class="col-md-4 method-option">
                            <input type="radio" name="method" id="mCash" value="CASH" ${form.method == 'CASH' ? 'checked' : ''}>
                            <label for="mCash"><i class="bi bi-cash-stack fs-4 d-block mb-1"></i><strong>Cash</strong>
                                <span class="d-block small text-muted-de">Pay at pickup &middot; free</span></label>
                        </div>
                    </div>

                    <div data-method="CARD">
                        <div class="row g-3">
                            <div class="col-12">
                                <label class="form-label" for="cardHolderName">Name on card</label>
                                <input type="text" class="form-control" id="cardHolderName" name="cardHolderName"
                                       value="${fn:escapeXml(empty form.cardHolderName ? currentUser.fullName : form.cardHolderName)}" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label" for="cardNumber">Card number</label>
                                <input type="text" class="form-control" id="cardNumber" name="cardNumber" inputmode="numeric"
                                       placeholder="1234 5678 9012 3456" required>
                            </div>
                            <div class="col-6">
                                <label class="form-label" for="expiry">Expiry (MM/YY)</label>
                                <input type="text" class="form-control" id="expiry" name="expiry" placeholder="08/28"
                                       value="${fn:escapeXml(form.expiry)}" required>
                            </div>
                            <div class="col-6">
                                <label class="form-label" for="cvv">CVV</label>
                                <input type="password" class="form-control" id="cvv" name="cvv" maxlength="4" inputmode="numeric" required>
                            </div>
                        </div>
                        <p class="small text-muted-de mt-2 mb-0"><i class="bi bi-shield-lock me-1"></i>We only keep the last 4 digits of your card.
                            This is a demo: no real payment is taken.</p>
                    </div>

                    <div data-method="BANK" class="d-none">
                        <div class="alert alert-light border small">
                            Transfer the total to <strong>DriveEasy (Pvt) Ltd</strong>, Commercial Bank, A/C 1000 2233 44,
                            then enter your reference below. We confirm transfers within one working day.
                        </div>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label" for="bankName">Your bank</label>
                                <input type="text" class="form-control" id="bankName" name="bankName" value="${fn:escapeXml(form.bankName)}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="referenceNumber">Transfer reference</label>
                                <input type="text" class="form-control" id="referenceNumber" name="referenceNumber"
                                       value="${fn:escapeXml(form.referenceNumber)}" required>
                            </div>
                        </div>
                    </div>

                    <div data-method="CASH" class="d-none">
                        <div class="alert alert-light border small mb-0">
                            Pay the total in cash at the <strong><c:out value="${rental.pickupLocation}"/></strong> branch when you collect
                            the vehicle. Your booking is confirmed now.
                        </div>
                    </div>

                    <button type="submit" class="btn btn-accent w-100 py-2 mt-4">Confirm booking</button>
                </form>
            </div>
        </div>

        <div class="col-lg-5">
            <div class="card-de p-4 booking-panel">
                <h5 class="mb-3">Booking summary</h5>
                <div class="d-flex gap-3 mb-3">
                    <t:vehicleImage vehicle="${vehicle}" cssClass="thumb"/>
                    <div>
                        <div class="fw-bold"><c:out value="${vehicle.displayName}"/></div>
                        <div class="small text-muted-de">${vehicle.typeDisplayName} &middot; <c:out value="${vehicle.plateNumber}"/></div>
                    </div>
                </div>
                <ul class="list-unstyled small mb-3">
                    <li class="d-flex justify-content-between"><span>Booking ID</span><strong>${rental.id}</strong></li>
                    <li class="d-flex justify-content-between"><span>Pickup</span><strong>${rental.startDate}</strong></li>
                    <li class="d-flex justify-content-between"><span>Return</span><strong>${rental.endDate}</strong></li>
                    <li class="d-flex justify-content-between"><span>Branch</span><strong><c:out value="${rental.pickupLocation}"/></strong></li>
                </ul>
                <div class="quote-box">
                    <div class="d-flex justify-content-between"><span>${rental.days} day(s) incl. insurance</span><span><t:money value="${rental.baseCost}"/></span></div>
                    <c:if test="${rental.discountAmount > 0}">
                        <div class="d-flex justify-content-between text-success"><span>Premium discount</span><span>- <t:money value="${rental.discountAmount}"/></span></div>
                    </c:if>
                    <c:if test="${not empty vehicle.pricingNote}">
                        <div class="small text-muted-de mt-1">${vehicle.pricingNote}</div>
                    </c:if>
                    <hr class="my-2">
                    <div class="d-flex justify-content-between total"><span>Total</span><span><t:money value="${rental.totalCost}"/></span></div>
                    <div class="small text-muted-de">Payment method fees are added at checkout.</div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
