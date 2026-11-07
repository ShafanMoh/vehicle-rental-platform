<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My bookings"/>
<c:set var="activeNav" value="rentals"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="container d-flex flex-wrap justify-content-between align-items-center gap-2">
        <div>
            <h1 class="mb-1">My bookings</h1>
            <p>View, change or cancel your rentals</p>
        </div>
        <a href="${ctx}/vehicles" class="btn btn-accent"><i class="bi bi-plus-lg me-1"></i>New booking</a>
    </div>
</div>

<div class="container">
    <c:if test="${empty rentals}">
        <div class="card-de p-5 text-center">
            <i class="bi bi-calendar-x display-5 text-muted"></i>
            <h5 class="mt-3">You have no bookings yet</h5>
            <a href="${ctx}/vehicles" class="btn btn-primary mt-2">Find a vehicle</a>
        </div>
    </c:if>

    <div class="row g-4">
        <c:forEach items="${rentals}" var="r">
            <c:set var="v" value="${vehicleMap[r.vehicleId]}"/>
            <c:set var="p" value="${paymentMap[r.id]}"/>
            <div class="col-lg-6">
                <div class="card-de p-3 h-100">
                    <div class="d-flex gap-3">
                        <c:choose>
                            <c:when test="${not empty v}"><t:vehicleImage vehicle="${v}" cssClass="thumb" /></c:when>
                            <c:otherwise><div class="thumb d-flex align-items-center justify-content-center"><i class="bi bi-question"></i></div></c:otherwise>
                        </c:choose>
                        <div class="flex-grow-1">
                            <div class="d-flex justify-content-between">
                                <h5 class="mb-0">
                                    <c:choose>
                                        <c:when test="${not empty v}"><a href="${ctx}/vehicles/${v.id}" class="text-reset text-decoration-none"><c:out value="${v.displayName}"/></a></c:when>
                                        <c:otherwise>Removed vehicle</c:otherwise>
                                    </c:choose>
                                </h5>
                                <span class="badge text-bg-${r.status.badgeColor} align-self-start">${r.status.displayName}</span>
                            </div>
                            <div class="small text-muted-de">Booking ${r.id} &middot; booked on ${r.bookedOn}</div>
                        </div>
                    </div>
                    <div class="row small g-2 mt-2">
                        <div class="col-6"><span class="text-muted-de">Pickup</span><br><strong>${r.startDate}</strong></div>
                        <div class="col-6"><span class="text-muted-de">Return</span><br><strong>${r.endDate}</strong></div>
                        <div class="col-6"><span class="text-muted-de">Branch</span><br><strong><c:out value="${r.pickupLocation}"/></strong></div>
                        <div class="col-6"><span class="text-muted-de">Total (${r.days} days)</span><br><strong><t:money value="${r.totalCost}"/></strong>
                            <c:if test="${r.discountAmount > 0}"><span class="text-success">(saved <t:money value="${r.discountAmount}"/>)</span></c:if>
                        </div>
                    </div>
                    <c:if test="${not empty p}">
                        <div class="small mt-2">
                            <i class="bi bi-credit-card me-1"></i>${p.methodDisplayName}
                            <span class="badge text-bg-${p.status.badgeColor}">${p.status.displayName}</span>
                            <a href="${ctx}/payments/${p.id}" class="ms-1">Receipt</a>
                        </div>
                    </c:if>
                    <div class="d-flex flex-wrap gap-2 mt-3">
                        <c:if test="${r.awaitingPayment}">
                            <a href="${ctx}/payments/checkout/${r.id}" class="btn btn-accent btn-sm">Pay now</a>
                        </c:if>
                        <c:if test="${r.editable}">
                            <a href="${ctx}/rentals/${r.id}/edit" class="btn btn-outline-primary btn-sm"><i class="bi bi-pencil me-1"></i>Change</a>
                        </c:if>
                        <c:if test="${r.cancellable}">
                            <form action="${ctx}/rentals/${r.id}/cancel" method="post"
                                  data-confirm="Cancel booking ${r.id}? Any payment will be refunded.">
                                <button type="submit" class="btn btn-outline-danger btn-sm"><i class="bi bi-x-lg me-1"></i>Cancel</button>
                            </form>
                        </c:if>
                        <c:if test="${r.status == 'COMPLETED' and not empty v}">
                            <a href="${ctx}/vehicles/${v.id}#reviews" class="btn btn-outline-secondary btn-sm"><i class="bi bi-star me-1"></i>Review</a>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
