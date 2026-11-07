<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${vehicle.displayName}"/>
<c:set var="activeNav" value="vehicles"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container my-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb small">
            <li class="breadcrumb-item"><a href="${ctx}/vehicles">Vehicles</a></li>
            <li class="breadcrumb-item"><a href="${ctx}/vehicles?type=${vehicle.type}">${vehicle.typeDisplayName}</a></li>
            <li class="breadcrumb-item active" aria-current="page"><c:out value="${vehicle.displayName}"/></li>
        </ol>
    </nav>

    <div class="row g-4">
        <!-- Vehicle information -->
        <div class="col-lg-8">
            <div class="vehicle-img large mb-4">
                <span class="badge text-bg-light badge-type fs-6"><i class="bi ${vehicle.icon} me-1"></i>${vehicle.typeDisplayName}</span>
                <span class="badge text-bg-${vehicle.status.badgeColor} badge-status fs-6">${vehicle.status.displayName}</span>
                <t:vehicleImage vehicle="${vehicle}"/>
            </div>

            <div class="card-de p-4 mb-4">
                <div class="d-flex flex-wrap justify-content-between align-items-start gap-2">
                    <div>
                        <h2 class="mb-1"><c:out value="${vehicle.displayName}"/> <span class="text-muted-de fs-5">${vehicle.year}</span></h2>
                        <div>
                            <t:stars rating="${averageRating}"/>
                            <span class="text-muted-de small ms-1">${averageRating > 0 ? averageRating : 'No ratings'} &middot; ${fn:length(reviews)} review(s)</span>
                        </div>
                    </div>
                    <div class="text-end">
                        <div class="price fs-3"><t:money value="${vehicle.dailyRate}"/></div>
                        <div class="text-muted-de small">per day + <t:money value="${vehicle.insuranceFeePerDay}"/> insurance</div>
                    </div>
                </div>
                <hr>
                <div class="row g-3 text-center">
                    <div class="col-6 col-md-3"><div class="text-muted-de small">Seats</div><div class="fw-bold"><i class="bi bi-people me-1"></i>${vehicle.seats}</div></div>
                    <div class="col-6 col-md-3"><div class="text-muted-de small">Fuel</div><div class="fw-bold"><i class="bi bi-fuel-pump me-1"></i><c:out value="${vehicle.fuelType}"/></div></div>
                    <div class="col-6 col-md-3"><div class="text-muted-de small">Branch</div><div class="fw-bold"><i class="bi bi-geo-alt me-1"></i><c:out value="${vehicle.location}"/></div></div>
                    <div class="col-6 col-md-3"><div class="text-muted-de small">Plate</div><div class="fw-bold"><c:out value="${vehicle.plateNumber}"/></div></div>
                </div>
                <hr>
                <p class="mb-2"><i class="bi bi-gear me-2 text-primary"></i><c:out value="${vehicle.specSummary}"/></p>
                <c:if test="${not empty vehicle.pricingNote}">
                    <p class="mb-2"><i class="bi bi-tag me-2 text-primary"></i>${vehicle.pricingNote}</p>
                </c:if>
                <c:if test="${not empty vehicle.description}">
                    <p class="mb-0 text-muted-de"><c:out value="${vehicle.description}"/></p>
                </c:if>
            </div>

            <!-- Reviews -->
            <div class="card-de p-4" id="reviews">
                <h4 class="mb-3">Customer reviews</h4>

                <c:choose>
                    <c:when test="${empty currentUser}">
                        <div class="alert alert-light border small"><a href="${ctx}/login?next=/vehicles/${vehicle.id}">Log in</a> to write a review.</div>
                    </c:when>
                    <c:when test="${not currentUser.admin and empty myReview}">
                        <form action="${ctx}/reviews" method="post" class="border rounded-3 p-3 mb-4">
                            <input type="hidden" name="vehicleId" value="${vehicle.id}">
                            <label class="form-label d-block">Your rating</label>
                            <div class="rating-input mb-2">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:set var="star" value="${6 - i}"/>
                                    <input type="radio" name="rating" id="star${star}" value="${star}" ${star == 5 ? 'required' : ''}>
                                    <label for="star${star}" title="${star} stars"><i class="bi bi-star-fill"></i></label>
                                </c:forEach>
                            </div>
                            <textarea name="comment" class="form-control mb-2" rows="3" maxlength="500" required
                                      placeholder="How was your experience with this vehicle?"></textarea>
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="small text-muted-de">Reviews after a completed rental get a "Verified Renter" badge.</span>
                                <button type="submit" class="btn btn-primary btn-sm px-3">Post review</button>
                            </div>
                        </form>
                    </c:when>
                </c:choose>

                <c:if test="${empty reviews}">
                    <p class="text-muted-de mb-0">No reviews yet. Be the first to share your experience!</p>
                </c:if>
                <c:forEach items="${reviews}" var="r">
                    <div class="d-flex gap-3 py-3 border-top">
                        <span class="avatar flex-shrink-0">${fn:toUpperCase(fn:substring(r.customerName, 0, 1))}</span>
                        <div class="flex-grow-1">
                            <div class="d-flex flex-wrap justify-content-between">
                                <div>
                                    <strong><c:out value="${r.customerName}"/></strong>
                                    <span class="badge ${r.reviewType == 'VERIFIED' ? 'text-bg-success' : 'text-bg-light border'} ms-1">
                                        <c:if test="${r.reviewType == 'VERIFIED'}"><i class="bi bi-patch-check me-1"></i></c:if>${r.badgeLabel}
                                    </span>
                                </div>
                                <span class="small text-muted-de">${r.createdOn}</span>
                            </div>
                            <t:stars rating="${r.rating}"/>
                            <p class="mb-1 mt-1"><c:out value="${r.comment}"/></p>
                            <c:if test="${not empty currentUser and (r.customerId == currentUser.id or currentUser.admin)}">
                                <div class="d-flex gap-2">
                                    <c:if test="${r.customerId == currentUser.id}">
                                        <a href="${ctx}/reviews/${r.id}/edit" class="btn btn-link btn-sm p-0">Edit</a>
                                    </c:if>
                                    <form action="${ctx}/reviews/${r.id}/delete" method="post" data-confirm="Delete this review?">
                                        <button type="submit" class="btn btn-link btn-sm p-0 text-danger">Delete</button>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Booking panel -->
        <div class="col-lg-4">
            <div class="card-de p-4 booking-panel">
                <h5 class="mb-3"><i class="bi bi-calendar2-check me-2"></i>Book this vehicle</h5>
                <c:choose>
                    <c:when test="${not vehicle.bookable}">
                        <div class="alert alert-warning mb-0">This vehicle is under maintenance. Please choose another vehicle.</div>
                    </c:when>
                    <c:when test="${empty currentUser}">
                        <p class="text-muted-de">Log in or create a free account to book.</p>
                        <a href="${ctx}/login?next=/vehicles/${vehicle.id}" class="btn btn-primary w-100 mb-2">Log in to book</a>
                        <a href="${ctx}/register" class="btn btn-outline-primary w-100">Create account</a>
                    </c:when>
                    <c:when test="${currentUser.admin}">
                        <div class="alert alert-info small mb-2">You are logged in as an admin. Admins manage this vehicle from the admin panel.</div>
                        <a href="${ctx}/admin/vehicles/${vehicle.id}/edit" class="btn btn-outline-primary w-100">Edit in admin panel</a>
                    </c:when>
                    <c:otherwise>
                        <form action="${ctx}/rentals/book" method="post" id="bookingForm" data-quote-url="${ctx}/vehicles/${vehicle.id}/quote">
                            <input type="hidden" name="vehicleId" value="${vehicle.id}">
                            <div class="mb-3">
                                <label class="form-label" for="startDate">Pickup date</label>
                                <input type="date" class="form-control" id="startDate" name="startDate" min="${today}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" for="endDate">Return date</label>
                                <input type="date" class="form-control" id="endDate" name="endDate" min="${tomorrow}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" for="pickupLocation">Pickup branch</label>
                                <select class="form-select" id="pickupLocation" name="pickupLocation">
                                    <c:forEach items="${['Colombo','Kandy','Galle','Negombo','Ella']}" var="branch">
                                        <option value="${branch}" ${branch == vehicle.location ? 'selected' : ''}>${branch}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div id="quoteBox" class="quote-box mb-3 d-none"></div>
                            <c:if test="${currentUser.premium}">
                                <div class="small text-success mb-2"><i class="bi bi-gem me-1"></i>Your 10% Premium discount is applied automatically.</div>
                            </c:if>
                            <button type="submit" class="btn btn-accent w-100 py-2">Continue to payment</button>
                        </form>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty bookedDates}">
                    <hr>
                    <div class="small fw-bold mb-2">Already booked</div>
                    <ul class="list-unstyled small text-muted-de mb-0">
                        <c:forEach items="${bookedDates}" var="b">
                            <li><i class="bi bi-x-circle text-danger me-1"></i>${b.startDate} to ${b.endDate}</li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
