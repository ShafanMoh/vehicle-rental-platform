<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Browse vehicles"/>
<c:set var="activeNav" value="vehicles"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="container">
        <h1 class="mb-1">Find your ride</h1>
        <p>${fn:length(vehicles)} vehicle(s) match your search</p>
    </div>
</div>

<div class="container">
    <div class="row g-4">
        <div class="col-lg-3">
            <form action="${ctx}/vehicles" method="get" class="card-de p-3 booking-panel">
                <h6 class="fw-bold mb-3"><i class="bi bi-funnel me-1"></i>Filters</h6>
                <div class="mb-3">
                    <label class="form-label" for="q">Search</label>
                    <input type="text" class="form-control" id="q" name="q" value="${fn:escapeXml(q)}" placeholder="Brand, model, city">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="type">Type</label>
                    <select class="form-select" id="type" name="type">
                        <option value="">All types</option>
                        <option value="CAR" ${type == 'CAR' ? 'selected' : ''}>Car</option>
                        <option value="SUV" ${type == 'SUV' ? 'selected' : ''}>SUV</option>
                        <option value="VAN" ${type == 'VAN' ? 'selected' : ''}>Van</option>
                        <option value="MOTORBIKE" ${type == 'MOTORBIKE' ? 'selected' : ''}>Motorbike</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="maxRate">Max daily rate (LKR)</label>
                    <input type="number" class="form-control" id="maxRate" name="maxRate" min="0" step="500"
                           value="${maxRate == null ? '' : maxRate.intValue()}" placeholder="Any">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="sort">Sort by</label>
                    <select class="form-select" id="sort" name="sort">
                        <option value="">Recommended</option>
                        <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Price: low to high</option>
                        <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Price: high to low</option>
                        <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Newest first</option>
                    </select>
                </div>
                <div class="form-check mb-3">
                    <input class="form-check-input" type="checkbox" id="availableOnly" name="availableOnly" value="true" ${availableOnly ? 'checked' : ''}>
                    <label class="form-check-label" for="availableOnly">Hide vehicles under maintenance</label>
                </div>
                <button type="submit" class="btn btn-primary w-100">Apply filters</button>
                <a href="${ctx}/vehicles" class="btn btn-link w-100 btn-sm mt-1">Clear all</a>
            </form>
        </div>

        <div class="col-lg-9">
            <c:choose>
                <c:when test="${empty vehicles}">
                    <div class="card-de p-5 text-center">
                        <i class="bi bi-search display-5 text-muted"></i>
                        <h5 class="mt-3">No vehicles found</h5>
                        <p class="text-muted-de">Try removing some filters.</p>
                        <a href="${ctx}/vehicles" class="btn btn-outline-primary">Show all vehicles</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach items="${vehicles}" var="v">
                            <div class="col-md-6 col-xl-4">
                                <%@ include file="/WEB-INF/jsp/common/vehicle-card.jsp" %>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
