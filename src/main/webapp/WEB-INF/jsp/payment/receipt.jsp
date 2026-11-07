<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Receipt ${payment.id}"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container">
    <div class="auth-wrap wide">
        <div class="card-de p-4 p-md-5">
            <div class="d-flex justify-content-between align-items-start mb-4">
                <div>
                    <h4 class="fw-bold mb-0"><i class="bi bi-car-front-fill me-1"></i>Drive<span style="color:var(--de-accent)">Easy</span></h4>
                    <div class="small text-muted-de">No. 25, Galle Road, Colombo 03 &middot; +94 11 234 5678</div>
                </div>
                <div class="text-end">
                    <div class="h5 mb-0">Receipt</div>
                    <div class="small text-muted-de">${payment.id} &middot; ${payment.createdAtDisplay}</div>
                    <span class="badge text-bg-${payment.status.badgeColor} mt-1">${payment.status.displayName}</span>
                </div>
            </div>

            <div class="row g-3 mb-4 small">
                <div class="col-md-6">
                    <div class="text-muted-de">Billed to</div>
                    <div class="fw-bold"><c:out value="${customer.fullName}"/></div>
                    <div><c:out value="${customer.email}"/></div>
                    <div><c:out value="${customer.phone}"/></div>
                </div>
                <div class="col-md-6 text-md-end">
                    <div class="text-muted-de">Booking</div>
                    <div class="fw-bold">${rental.id} &middot; ${rental.status.displayName}</div>
                    <div><c:out value="${empty vehicle ? 'Removed vehicle' : vehicle.displayName}"/></div>
                    <div>${rental.startDate} to ${rental.endDate} (${rental.days} days)</div>
                </div>
            </div>

            <table class="table">
                <tbody>
                <tr><td>Rental (${rental.days} days, insurance included)</td><td class="text-end"><t:money value="${rental.baseCost}"/></td></tr>
                <c:if test="${rental.discountAmount > 0}">
                    <tr class="text-success"><td>Premium member discount</td><td class="text-end">- <t:money value="${rental.discountAmount}"/></td></tr>
                </c:if>
                <tr><td>${payment.methodDisplayName} fee</td><td class="text-end"><t:money value="${payment.processingFee}"/></td></tr>
                <tr class="fw-bold fs-5"><td>Total</td><td class="text-end"><t:money value="${payment.totalCharged}"/></td></tr>
                </tbody>
            </table>
            <p class="small text-muted-de mb-4"><i class="bi bi-info-circle me-1"></i><c:out value="${payment.details}"/></p>

            <div class="d-flex gap-2 no-print">
                <button type="button" class="btn btn-outline-primary" onclick="window.print()"><i class="bi bi-printer me-1"></i>Print</button>
                <c:choose>
                    <c:when test="${currentUser.admin}"><a href="${ctx}/admin/payments" class="btn btn-light">Back to payments</a></c:when>
                    <c:otherwise><a href="${ctx}/rentals" class="btn btn-light">My bookings</a></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
