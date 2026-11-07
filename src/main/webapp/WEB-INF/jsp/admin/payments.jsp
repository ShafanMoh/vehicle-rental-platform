<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Payments"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="payments"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <h3 class="mb-3">Payments</h3>

        <div class="row g-3 mb-3">
            <div class="col-md-6">
                <div class="card-de kpi d-flex gap-3 align-items-center">
                    <div class="kpi-icon bg-success-subtle text-success"><i class="bi bi-cash-coin"></i></div>
                    <div><div class="text-muted-de small">Total received</div><div class="kpi-value"><t:money value="${revenue}"/></div></div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card-de kpi d-flex gap-3 align-items-center">
                    <div class="kpi-icon bg-warning-subtle text-warning"><i class="bi bi-hourglass-split"></i></div>
                    <div><div class="text-muted-de small">Waiting for confirmation</div><div class="kpi-value">${pendingCount}</div></div>
                </div>
            </div>
        </div>

        <form class="card-de p-3 mb-3" method="get">
            <div class="row g-2">
                <div class="col-md-5">
                    <select class="form-select" name="status">
                        <option value="">All statuses</option>
                        <c:forEach items="${statuses}" var="s">
                            <option value="${s}" ${status == s.name() ? 'selected' : ''}>${s.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-5">
                    <select class="form-select" name="method">
                        <option value="">All methods</option>
                        <option value="CARD" ${method == 'CARD' ? 'selected' : ''}>Card</option>
                        <option value="BANK" ${method == 'BANK' ? 'selected' : ''}>Bank transfer</option>
                        <option value="CASH" ${method == 'CASH' ? 'selected' : ''}>Cash</option>
                    </select>
                </div>
                <div class="col-md-2 d-grid"><button class="btn btn-outline-primary" type="submit">Filter</button></div>
            </div>
        </form>

        <div class="card-de overflow-hidden">
            <div class="table-responsive">
                <table class="table table-de table-hover">
                    <thead><tr><th>ID</th><th>Date</th><th>Customer</th><th>Booking</th><th>Method</th><th class="text-end">Amount</th><th class="text-end">Fee</th><th>Status</th><th class="text-end">Actions</th></tr></thead>
                    <tbody>
                    <c:if test="${empty payments}"><tr><td colspan="9" class="text-center text-muted-de p-4">No payments found.</td></tr></c:if>
                    <c:forEach items="${payments}" var="p">
                        <tr>
                            <td class="fw-bold">${p.id}</td>
                            <td class="small">${p.createdAtDisplay}</td>
                            <td><c:out value="${empty userMap[p.customerId] ? 'Deleted user' : userMap[p.customerId].fullName}"/></td>
                            <td>${p.rentalId}</td>
                            <td class="small">${p.methodDisplayName}<br><span class="text-muted-de"><c:out value="${p.details}"/></span></td>
                            <td class="text-end"><t:money value="${p.amount}"/></td>
                            <td class="text-end small"><t:money value="${p.processingFee}"/></td>
                            <td><span class="badge text-bg-${p.status.badgeColor}">${p.status.displayName}</span></td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/payments/${p.id}" class="btn btn-sm btn-outline-primary" title="Receipt"><i class="bi bi-receipt"></i></a>
                                <c:if test="${p.status == 'PENDING'}">
                                    <form action="${ctx}/admin/payments/${p.id}/mark-paid" method="post" class="d-inline">
                                        <button class="btn btn-sm btn-success" title="Mark as received"><i class="bi bi-check2"></i> Paid</button>
                                    </form>
                                </c:if>
                                <c:if test="${p.status != 'REFUNDED'}">
                                    <form action="${ctx}/admin/payments/${p.id}/refund" method="post" class="d-inline"
                                          data-confirm="Refund payment ${p.id}? The booking will be cancelled.">
                                        <button class="btn btn-sm btn-outline-danger" title="Refund"><i class="bi bi-arrow-counterclockwise"></i></button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
