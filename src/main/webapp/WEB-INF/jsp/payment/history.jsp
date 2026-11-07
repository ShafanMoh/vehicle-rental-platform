<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My payments"/>
<c:set var="activeNav" value="payments"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="container">
        <h1 class="mb-1">My payments</h1>
        <p>All payments and refunds for your bookings</p>
    </div>
</div>

<div class="container">
    <div class="card-de overflow-hidden">
        <c:choose>
            <c:when test="${empty payments}">
                <div class="p-5 text-center text-muted-de">No payments yet.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-de table-hover">
                        <thead>
                        <tr><th>Payment</th><th>Date</th><th>Booking</th><th>Method</th><th class="text-end">Amount</th><th>Status</th><th></th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${payments}" var="p">
                            <c:set var="r" value="${rentalMap[p.rentalId]}"/>
                            <tr>
                                <td class="fw-bold">${p.id}</td>
                                <td class="small">${p.createdAtDisplay}</td>
                                <td class="small">${p.rentalId}
                                    <c:if test="${not empty r and not empty vehicleMap[r.vehicleId]}">
                                        <br><span class="text-muted-de"><c:out value="${vehicleMap[r.vehicleId].displayName}"/></span>
                                    </c:if>
                                </td>
                                <td class="small">${p.methodDisplayName}</td>
                                <td class="text-end fw-bold"><t:money value="${p.totalCharged}"/></td>
                                <td><span class="badge text-bg-${p.status.badgeColor}">${p.status.displayName}</span></td>
                                <td><a href="${ctx}/payments/${p.id}" class="btn btn-sm btn-outline-primary">Receipt</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
