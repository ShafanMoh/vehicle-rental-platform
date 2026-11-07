<%-- One vehicle card. Expects ${v} (Vehicle) and ${ratings} (Map of vehicleId to average rating). --%>
<div class="card-de vehicle-card d-flex flex-column">
    <a href="${ctx}/vehicles/${v.id}" class="vehicle-img text-decoration-none">
        <span class="badge text-bg-light badge-type"><i class="bi ${v.icon} me-1"></i>${v.typeDisplayName}</span>
        <c:if test="${v.status != 'AVAILABLE'}">
            <span class="badge text-bg-${v.status.badgeColor} badge-status">${v.status.displayName}</span>
        </c:if>
        <t:vehicleImage vehicle="${v}"/>
    </a>
    <div class="p-3 d-flex flex-column flex-grow-1">
        <div class="d-flex justify-content-between align-items-start mb-1">
            <h5 class="mb-0"><a href="${ctx}/vehicles/${v.id}" class="text-reset text-decoration-none"><c:out value="${v.displayName}"/></a></h5>
            <span class="text-muted-de small">${v.year}</span>
        </div>
        <div class="small mb-2">
            <c:choose>
                <c:when test="${not empty ratings[v.id]}">
                    <t:stars rating="${ratings[v.id]}"/> <span class="text-muted-de">${ratings[v.id]}</span>
                </c:when>
                <c:otherwise><span class="text-muted-de">No reviews yet</span></c:otherwise>
            </c:choose>
        </div>
        <div class="spec-list mb-3">
            <span><i class="bi bi-people"></i>${v.seats} seats</span>
            <span><i class="bi bi-fuel-pump"></i><c:out value="${v.fuelType}"/></span>
            <span><i class="bi bi-geo-alt"></i><c:out value="${v.location}"/></span>
            <span><i class="bi bi-gear"></i><c:out value="${v.specSummary}"/></span>
        </div>
        <div class="mt-auto d-flex justify-content-between align-items-center">
            <div class="price"><t:money value="${v.dailyRate}"/> <small>/ day</small></div>
            <a href="${ctx}/vehicles/${v.id}" class="btn btn-primary btn-sm px-3">${v.bookable ? 'Book now' : 'View'}</a>
        </div>
    </div>
</div>
