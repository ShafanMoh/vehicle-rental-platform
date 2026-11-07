<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage fleet"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="vehicles"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <h3 class="mb-0">Fleet <span class="text-muted-de fs-6">(${fn:length(vehicles)})</span></h3>
            <a href="${ctx}/admin/vehicles/new" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>Add vehicle</a>
        </div>

        <form class="card-de p-3 mb-3" method="get">
            <div class="row g-2">
                <div class="col-md-5"><input type="text" class="form-control" name="q" value="${fn:escapeXml(q)}" placeholder="Search brand, model, plate or branch"></div>
                <div class="col-md-3">
                    <select class="form-select" name="type">
                        <option value="">All types</option>
                        <option value="CAR" ${type == 'CAR' ? 'selected' : ''}>Car</option>
                        <option value="SUV" ${type == 'SUV' ? 'selected' : ''}>SUV</option>
                        <option value="VAN" ${type == 'VAN' ? 'selected' : ''}>Van</option>
                        <option value="MOTORBIKE" ${type == 'MOTORBIKE' ? 'selected' : ''}>Motorbike</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <select class="form-select" name="status">
                        <option value="">Any status</option>
                        <c:forEach items="${statuses}" var="s">
                            <option value="${s}" ${status == s.name() ? 'selected' : ''}>${s.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2 d-grid"><button class="btn btn-outline-primary" type="submit">Search</button></div>
            </div>
        </form>

        <div class="card-de overflow-hidden">
            <div class="table-responsive">
                <table class="table table-de table-hover">
                    <thead><tr><th></th><th>ID</th><th>Vehicle</th><th>Type</th><th>Plate</th><th>Branch</th><th class="text-end">Rate / day</th><th>Status</th><th class="text-end">Actions</th></tr></thead>
                    <tbody>
                    <c:if test="${empty vehicles}"><tr><td colspan="9" class="text-center text-muted-de p-4">No vehicles found.</td></tr></c:if>
                    <c:forEach items="${vehicles}" var="v">
                        <tr>
                            <td><t:vehicleImage vehicle="${v}" cssClass="thumb"/></td>
                            <td class="fw-bold">${v.id}</td>
                            <td><a href="${ctx}/vehicles/${v.id}" class="text-reset"><c:out value="${v.displayName}"/></a><br>
                                <span class="small text-muted-de">${v.year} &middot; <c:out value="${v.specSummary}"/></span></td>
                            <td><i class="bi ${v.icon} me-1"></i>${v.typeDisplayName}</td>
                            <td><c:out value="${v.plateNumber}"/></td>
                            <td><c:out value="${v.location}"/></td>
                            <td class="text-end"><t:money value="${v.dailyRate}"/></td>
                            <td>
                                <form action="${ctx}/admin/vehicles/${v.id}/status" method="post">
                                    <select name="status" class="form-select form-select-sm" onchange="this.form.submit()" aria-label="Change status">
                                        <c:forEach items="${statuses}" var="s">
                                            <option value="${s}" ${v.status == s ? 'selected' : ''}>${s.displayName}</option>
                                        </c:forEach>
                                    </select>
                                </form>
                            </td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/admin/vehicles/${v.id}/edit" class="btn btn-sm btn-outline-primary" title="Edit"><i class="bi bi-pencil"></i></a>
                                <form action="${ctx}/admin/vehicles/${v.id}/delete" method="post" class="d-inline"
                                      data-confirm="Delete ${fn:escapeXml(v.displayName)} (${v.id})? Its reviews will also be removed.">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete"><i class="bi bi-trash"></i></button>
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
