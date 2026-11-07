<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin dashboard"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="dashboard"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
            <div>
                <h3 class="mb-0">Dashboard</h3>
                <p class="text-muted-de mb-0">Welcome, <c:out value="${currentUser.fullName}"/>. Here is today's overview.</p>
            </div>
            <a href="${ctx}/admin/vehicles/new" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>Add vehicle</a>
        </div>

        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-3">
                <div class="card-de kpi d-flex gap-3 align-items-center">
                    <div class="kpi-icon bg-success-subtle text-success"><i class="bi bi-cash-coin"></i></div>
                    <div><div class="text-muted-de small">Revenue received</div><div class="kpi-value"><t:money value="${stats.revenue}"/></div></div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card-de kpi d-flex gap-3 align-items-center">
                    <div class="kpi-icon bg-primary-subtle text-primary"><i class="bi bi-car-front"></i></div>
                    <div><div class="text-muted-de small">Fleet (available / total)</div><div class="kpi-value">${stats.availableVehicles} / ${stats.totalVehicles}</div></div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card-de kpi d-flex gap-3 align-items-center">
                    <div class="kpi-icon bg-info-subtle text-info"><i class="bi bi-calendar-check"></i></div>
                    <div><div class="text-muted-de small">Upcoming / on rent</div><div class="kpi-value">${stats.confirmedBookings} / ${stats.ongoingBookings}</div></div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card-de kpi d-flex gap-3 align-items-center">
                    <div class="kpi-icon bg-warning-subtle text-warning"><i class="bi bi-hourglass-split"></i></div>
                    <div><div class="text-muted-de small">Pending payments</div><div class="kpi-value">${stats.pendingPayments}</div></div>
                </div>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <div class="col-xl-8">
                <div class="card-de p-4 h-100">
                    <h5 class="mb-0">Revenue - last 6 months</h5>
                    <div class="bar-chart">
                        <c:forEach items="${monthlyRevenue}" var="m">
                            <div class="bar-col" title="${m.key}">
                                <div class="small fw-bold mb-1"><fmt:formatNumber value="${m.value / 1000}" maxFractionDigits="1"/>k</div>
                                <div class="bar" style="height:${m.value / highestMonth * 80}%"></div>
                                <div class="bar-label">${m.key}</div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
            <div class="col-xl-4">
                <div class="card-de p-4 h-100">
                    <h5 class="mb-3">Fleet status</h5>
                    <ul class="list-unstyled mb-3">
                        <li class="d-flex justify-content-between py-1"><span><span class="badge text-bg-success me-2">&nbsp;</span>Available</span><strong>${stats.availableVehicles}</strong></li>
                        <li class="d-flex justify-content-between py-1"><span><span class="badge text-bg-primary me-2">&nbsp;</span>On rent</span><strong>${stats.rentedVehicles}</strong></li>
                        <li class="d-flex justify-content-between py-1"><span><span class="badge text-bg-warning me-2">&nbsp;</span>Maintenance</span><strong>${stats.maintenanceVehicles}</strong></li>
                    </ul>
                    <h6 class="mb-2">By type</h6>
                    <c:forEach items="${fleetByType}" var="entry">
                        <div class="d-flex justify-content-between small py-1"><span><c:out value="${entry.key}"/></span><strong>${entry.value}</strong></div>
                    </c:forEach>
                    <hr>
                    <div class="d-flex justify-content-between small"><span>Customers</span><strong>${stats.customers}</strong></div>
                    <div class="d-flex justify-content-between small"><span>Completed trips</span><strong>${stats.completedBookings}</strong></div>
                    <div class="d-flex justify-content-between small"><span>Reviews</span><strong>${stats.reviews}</strong></div>
                </div>
            </div>
        </div>

        <div class="card-de overflow-hidden">
            <div class="d-flex justify-content-between align-items-center p-3">
                <h5 class="mb-0">Recent bookings</h5>
                <a href="${ctx}/admin/rentals" class="btn btn-sm btn-outline-primary">All bookings</a>
            </div>
            <div class="table-responsive">
                <table class="table table-de table-hover">
                    <thead><tr><th>ID</th><th>Customer</th><th>Vehicle</th><th>Dates</th><th class="text-end">Total</th><th>Status</th></tr></thead>
                    <tbody>
                    <c:forEach items="${recentRentals}" var="r">
                        <tr>
                            <td class="fw-bold">${r.id}</td>
                            <td><c:out value="${empty userMap[r.customerId] ? 'Deleted user' : userMap[r.customerId].fullName}"/></td>
                            <td><c:out value="${empty vehicleMap[r.vehicleId] ? 'Removed vehicle' : vehicleMap[r.vehicleId].displayName}"/></td>
                            <td class="small">${r.startDate} &rarr; ${r.endDate}</td>
                            <td class="text-end"><t:money value="${r.totalCost}"/></td>
                            <td><span class="badge text-bg-${r.status.badgeColor}">${r.status.displayName}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
