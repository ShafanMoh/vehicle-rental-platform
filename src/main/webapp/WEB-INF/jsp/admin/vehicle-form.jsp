<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="editing" value="${not empty vehicleId}"/>
<c:set var="pageTitle" value="${editing ? 'Edit vehicle' : 'Add vehicle'}"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="vehicles"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <a href="${ctx}/admin/vehicles" class="small text-decoration-none"><i class="bi bi-arrow-left me-1"></i>Back to fleet</a>
        <h3 class="mt-2 mb-3">${editing ? 'Edit vehicle '.concat(vehicleId) : 'Add a new vehicle'}</h3>

        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
        </c:if>

        <form method="post" action="${ctx}/admin/vehicles/${editing ? vehicleId.concat('/edit') : 'new'}">
            <div class="row g-4">
                <div class="col-xl-8">
                    <div class="card-de p-4">
                        <div class="row g-3">
                            <div class="col-md-4">
                                <label class="form-label" for="vehicleType">Vehicle type</label>
                                <select class="form-select" id="vehicleType" name="type">
                                    <option value="CAR" ${form.type == 'CAR' ? 'selected' : ''}>Car</option>
                                    <option value="SUV" ${form.type == 'SUV' ? 'selected' : ''}>SUV</option>
                                    <option value="VAN" ${form.type == 'VAN' ? 'selected' : ''}>Van</option>
                                    <option value="MOTORBIKE" ${form.type == 'MOTORBIKE' ? 'selected' : ''}>Motorbike</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label" for="brand">Brand</label>
                                <input type="text" class="form-control" id="brand" name="brand" value="${fn:escapeXml(form.brand)}" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label" for="model">Model</label>
                                <input type="text" class="form-control" id="model" name="model" value="${fn:escapeXml(form.model)}" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label" for="year">Year</label>
                                <input type="number" class="form-control" id="year" name="year" value="${form.year}" min="1990" max="2030" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label" for="plateNumber">Plate number</label>
                                <input type="text" class="form-control text-uppercase" id="plateNumber" name="plateNumber" value="${fn:escapeXml(form.plateNumber)}" placeholder="CAB-1234" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label" for="dailyRate">Daily rate (LKR)</label>
                                <input type="number" class="form-control" id="dailyRate" name="dailyRate" value="${form.dailyRate}" min="1" step="50" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label" for="seats">Seats</label>
                                <input type="number" class="form-control" id="seats" name="seats" value="${form.seats}" min="1" max="60" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label" for="fuelType">Fuel type</label>
                                <select class="form-select" id="fuelType" name="fuelType">
                                    <c:forEach items="${['Petrol','Diesel','Hybrid','Electric']}" var="fuel">
                                        <option ${form.fuelType == fuel ? 'selected' : ''}>${fuel}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label" for="location">Branch</label>
                                <select class="form-select" id="location" name="location">
                                    <c:forEach items="${['Colombo','Kandy','Galle','Negombo','Ella']}" var="branch">
                                        <option ${form.location == branch ? 'selected' : ''}>${branch}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label" for="status">Status</label>
                                <select class="form-select" id="status" name="status">
                                    <c:forEach items="${statuses}" var="s">
                                        <option value="${s}" ${form.status == s.name() ? 'selected' : ''}>${s.displayName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Fields that depend on the vehicle type -->
                            <div class="col-md-4" data-types="CAR,SUV">
                                <label class="form-label" for="transmission">Transmission</label>
                                <select class="form-select" id="transmission" name="transmission">
                                    <option ${form.transmission == 'Automatic' ? 'selected' : ''}>Automatic</option>
                                    <option ${form.transmission == 'Manual' ? 'selected' : ''}>Manual</option>
                                </select>
                            </div>
                            <div class="col-md-4 d-flex align-items-end" data-types="SUV">
                                <div class="form-check mb-2">
                                    <input class="form-check-input" type="checkbox" id="fourWheelDrive" name="fourWheelDrive" value="true" ${form.fourWheelDrive ? 'checked' : ''}>
                                    <label class="form-check-label" for="fourWheelDrive">Four-wheel drive (4WD)</label>
                                </div>
                            </div>
                            <div class="col-md-4" data-types="VAN">
                                <label class="form-label" for="cargoCapacityKg">Cargo capacity (kg)</label>
                                <input type="number" class="form-control" id="cargoCapacityKg" name="cargoCapacityKg" value="${form.cargoCapacityKg}" min="1">
                            </div>
                            <div class="col-md-4" data-types="MOTORBIKE">
                                <label class="form-label" for="engineCc">Engine size (cc)</label>
                                <input type="number" class="form-control" id="engineCc" name="engineCc" value="${form.engineCc}" min="1">
                            </div>
                            <div class="col-md-4 d-flex align-items-end" data-types="MOTORBIKE">
                                <div class="form-check mb-2">
                                    <input class="form-check-input" type="checkbox" id="helmetIncluded" name="helmetIncluded" value="true" ${form.helmetIncluded ? 'checked' : ''}>
                                    <label class="form-check-label" for="helmetIncluded">Helmet included</label>
                                </div>
                            </div>

                            <div class="col-12">
                                <label class="form-label" for="description">Description</label>
                                <textarea class="form-control" id="description" name="description" rows="3"><c:out value="${form.description}"/></textarea>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-xl-4">
                    <div class="card-de p-4">
                        <label class="form-label" for="imageUrl">Image link (optional)</label>
                        <input type="url" class="form-control mb-3" id="imageUrl" name="imageUrl" value="${fn:escapeXml(form.imageUrl)}" placeholder="https://...">
                        <div class="vehicle-img rounded-3 mb-2">
                            <img id="imagePreview" alt="Preview" data-fallback="${ctx}/images/TYPE.svg">
                        </div>
                        <p class="small text-muted-de">Leave empty to use the standard picture for the vehicle type.</p>
                        <button type="submit" class="btn btn-primary w-100 py-2">${editing ? 'Save changes' : 'Add vehicle'}</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
