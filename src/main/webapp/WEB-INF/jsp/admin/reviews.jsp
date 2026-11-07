<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reviews"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="reviews"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <h3 class="mb-3">Review moderation <span class="text-muted-de fs-6">(${fn:length(reviews)})</span></h3>

        <form class="card-de p-3 mb-3" method="get">
            <div class="row g-2">
                <div class="col-md-5">
                    <select class="form-select" name="rating">
                        <option value="">Any rating</option>
                        <c:forEach begin="1" end="5" var="i">
                            <option value="${i}" ${rating == i ? 'selected' : ''}>${i} star${i > 1 ? 's' : ''}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-5">
                    <select class="form-select" name="type">
                        <option value="">All reviews</option>
                        <option value="VERIFIED" ${type == 'VERIFIED' ? 'selected' : ''}>Verified renters</option>
                        <option value="GENERAL" ${type == 'GENERAL' ? 'selected' : ''}>General</option>
                    </select>
                </div>
                <div class="col-md-2 d-grid"><button class="btn btn-outline-primary" type="submit">Filter</button></div>
            </div>
        </form>

        <div class="card-de overflow-hidden">
            <div class="table-responsive">
                <table class="table table-de table-hover">
                    <thead><tr><th>ID</th><th>Vehicle</th><th>Customer</th><th>Rating</th><th>Comment</th><th>Type</th><th>Date</th><th></th></tr></thead>
                    <tbody>
                    <c:if test="${empty reviews}"><tr><td colspan="8" class="text-center text-muted-de p-4">No reviews found.</td></tr></c:if>
                    <c:forEach items="${reviews}" var="r">
                        <tr>
                            <td class="fw-bold">${r.id}</td>
                            <td><a href="${ctx}/vehicles/${r.vehicleId}" class="text-reset">
                                <c:out value="${empty vehicleMap[r.vehicleId] ? r.vehicleId : vehicleMap[r.vehicleId].displayName}"/></a></td>
                            <td><c:out value="${r.customerName}"/></td>
                            <td class="text-nowrap"><t:stars rating="${r.rating}"/></td>
                            <td class="small" style="max-width:320px"><c:out value="${r.comment}"/></td>
                            <td><span class="badge ${r.reviewType == 'VERIFIED' ? 'text-bg-success' : 'text-bg-light border'}">${r.badgeLabel}</span></td>
                            <td class="small">${r.createdOn}</td>
                            <td class="text-end">
                                <form action="${ctx}/reviews/${r.id}/delete" method="post" data-confirm="Delete this review?">
                                    <input type="hidden" name="returnTo" value="/admin/reviews">
                                    <button class="btn btn-sm btn-outline-danger" title="Delete"><i class="bi bi-trash"></i></button>
                                </form>
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
