<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage bookings"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="rentals"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <h3 class="mb-3">Bookings <span class="text-muted-de fs-6">(${fn:length(rentals)})</span></h3>

        <form class="card-de p-3 mb-3" method="get">
            <div class="row g-2">
                <div class="col-md-5"><input type="text" class="form-control" name="q" value="${fn:escapeXml(q)}" placeholder="Booking ID, customer ID (U002) or vehicle ID (V001)"></div>
                <div class="col-md-5">
                    <select class="form-select" name="status">
                        <option value="">All statuses</option>
                        <c:forEach items="${statuses}" var="s">
                            <option value="${s}" ${status == s.name() ? 'selected' : ''}>${s.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2 d-grid"><button class="btn btn-outline-primary" type="submit">Filter</button></div>
            </div>
        </form>

        <div class="card-de overflow-hidden">
            <div class="table-responsive">
                <table class="table table-de table-hover">
                    <thead><tr><th>ID</th><th>Customer</th><th>Vehicle</th><th>Dates</th><th class="text-end">Total</th><th>Payment</th><th>Status</th><th class="text-end">Actions</th></tr></thead>
                    <tbody>
                    <c:if test="${empty rentals}"><tr><td colspan="8" class="text-center text-muted-de p-4">No bookings found.</td></tr></c:if>
                    <c:forEach items="${rentals}" var="r">
                        <c:set var="p" value="${paymentMap[r.id]}"/>
                        <tr>
                            <td class="fw-bold">${r.id}<br><span class="small text-muted-de fw-normal">${r.bookedOn}</span></td>
                            <td><c:out value="${empty userMap[r.customerId] ? 'Deleted user' : userMap[r.customerId].fullName}"/><br>
                                <span class="small text-muted-de">${r.customerId}</span></td>
                            <td><c:out value="${empty vehicleMap[r.vehicleId] ? 'Removed vehicle' : vehicleMap[r.vehicleId].displayName}"/><br>
                                <span class="small text-muted-de">${r.vehicleId} &middot; <c:out value="${r.pickupLocation}"/></span></td>
                            <td class="small">${r.startDate}<br>${r.endDate} (${r.days}d)</td>
                            <td class="text-end"><t:money value="${r.totalCost}"/></td>
                            <td class="small">
                                <c:choose>
                                    <c:when test="${empty p}"><span class="text-muted-de">None</span></c:when>
                                    <c:otherwise>${p.methodDisplayName}<br><span class="badge text-bg-${p.status.badgeColor}">${p.status.displayName}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><span class="badge text-bg-${r.status.badgeColor}">${r.status.displayName}</span></td>
                            <td class="text-end text-nowrap">
                                <c:if test="${r.status == 'CONFIRMED'}">
                                    <form action="${ctx}/admin/rentals/${r.id}/status" method="post" class="d-inline">
                                        <input type="hidden" name="status" value="ONGOING">
                                        <button class="btn btn-sm btn-primary" title="Customer collected the vehicle"><i class="bi bi-key me-1"></i>Hand over</button>
                                    </form>
                                </c:if>
                                <c:if test="${r.status == 'ONGOING'}">
                                    <form action="${ctx}/admin/rentals/${r.id}/status" method="post" class="d-inline">
                                        <input type="hidden" name="status" value="COMPLETED">
                                        <button class="btn btn-sm btn-success" title="Vehicle returned"><i class="bi bi-check2-circle me-1"></i>Returned</button>
                                    </form>
                                </c:if>
                                <c:if test="${r.cancellable}">
                                    <form action="${ctx}/admin/rentals/${r.id}/status" method="post" class="d-inline"
                                          data-confirm="Cancel booking ${r.id}? Any payment will be refunded.">
                                        <input type="hidden" name="status" value="CANCELLED">
                                        <button class="btn btn-sm btn-outline-danger" title="Cancel booking"><i class="bi bi-x-lg"></i></button>
                                    </form>
                                </c:if>
                                <c:if test="${not r.blockingVehicle}">
                                    <form action="${ctx}/admin/rentals/${r.id}/delete" method="post" class="d-inline"
                                          data-confirm="Delete booking record ${r.id} and its payments from the files?">
                                        <button class="btn btn-sm btn-outline-secondary" title="Delete record"><i class="bi bi-trash"></i></button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <p class="small text-muted-de mt-2"><i class="bi bi-info-circle me-1"></i>"Hand over" marks a cash payment as received.
            Bank transfers must be marked as paid on the Payments page first.</p>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
