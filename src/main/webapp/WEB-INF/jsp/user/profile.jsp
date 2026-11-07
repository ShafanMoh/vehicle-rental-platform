<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My profile"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container my-4">
    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card-de p-4 text-center mb-4">
                <span class="avatar avatar-lg mb-3"><c:out value="${user.initials}"/></span>
                <h4 class="mb-0"><c:out value="${user.fullName}"/></h4>
                <div class="text-muted-de small mb-2">@<c:out value="${user.username}"/> &middot; ${user.id}</div>
                <span class="badge ${user.admin ? 'text-bg-dark' : (user.premium ? 'text-bg-warning' : 'text-bg-primary')} px-3 py-2">
                    <c:if test="${not user.admin and user.premium}"><i class="bi bi-gem me-1"></i></c:if>${user.roleDisplayName}
                </span>
                <hr>
                <ul class="list-unstyled small text-start mb-0">
                    <li class="d-flex justify-content-between py-1"><span class="text-muted-de">Member since</span><span>${user.registeredDate}</span></li>
                    <c:choose>
                        <c:when test="${user.admin}">
                            <li class="d-flex justify-content-between py-1"><span class="text-muted-de">Job title</span><span><c:out value="${user.jobTitle}"/></span></li>
                        </c:when>
                        <c:otherwise>
                            <li class="d-flex justify-content-between py-1"><span class="text-muted-de">Bookings made</span><span>${rentalCount}</span></li>
                            <li class="d-flex justify-content-between py-1"><span class="text-muted-de">Discount</span><span><fmt:formatNumber value="${user.discountRate}" type="percent"/></span></li>
                            <li class="d-flex justify-content-between py-1"><span class="text-muted-de">Open bookings allowed</span><span>${user.maxActiveRentals}</span></li>
                            <c:if test="${user.premium}">
                                <li class="d-flex justify-content-between py-1"><span class="text-muted-de">Loyalty points</span><span class="fw-bold">${user.loyaltyPoints}</span></li>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>

            <div class="card-de p-4 border-danger-subtle">
                <h6 class="text-danger"><i class="bi bi-exclamation-octagon me-1"></i>Delete account</h6>
                <p class="small text-muted-de">This removes your account permanently. Accounts with open bookings cannot be deleted.</p>
                <form action="${ctx}/profile/delete" method="post" data-confirm="Delete your account permanently? This cannot be undone.">
                    <input type="password" class="form-control form-control-sm mb-2" name="password" placeholder="Enter your password" required>
                    <button type="submit" class="btn btn-outline-danger btn-sm w-100">Delete my account</button>
                </form>
            </div>
        </div>

        <div class="col-lg-8">
            <div class="card-de p-4 mb-4">
                <h5 class="mb-3">Personal details</h5>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
                </c:if>
                <form action="${ctx}/profile" method="post">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label" for="fullName">Full name</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="${fn:escapeXml(form.fullName)}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="email">Email</label>
                            <input type="email" class="form-control" id="email" name="email" value="${fn:escapeXml(form.email)}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="phone">Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone" value="${fn:escapeXml(form.phone)}" required>
                        </div>
                        <c:choose>
                            <c:when test="${user.admin}">
                                <div class="col-md-6">
                                    <label class="form-label" for="jobTitle">Job title</label>
                                    <input type="text" class="form-control" id="jobTitle" name="jobTitle" value="${fn:escapeXml(form.jobTitle)}" required>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="col-md-6">
                                    <label class="form-label" for="drivingLicenseNumber">Driving licence number</label>
                                    <input type="text" class="form-control" id="drivingLicenseNumber" name="drivingLicenseNumber"
                                           value="${fn:escapeXml(form.drivingLicenseNumber)}" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label" for="address">Address</label>
                                    <input type="text" class="form-control" id="address" name="address" value="${fn:escapeXml(form.address)}" required>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <button type="submit" class="btn btn-primary mt-3 px-4">Save changes</button>
                </form>
            </div>

            <div class="card-de p-4 mb-4">
                <h5 class="mb-3">Change password</h5>
                <form action="${ctx}/profile/password" method="post">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label" for="currentPassword">Current password</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required autocomplete="current-password">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="newPassword">New password</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6" required autocomplete="new-password">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="confirmPassword">Confirm new password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="6" required autocomplete="new-password">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-outline-primary mt-3 px-4">Update password</button>
                </form>
            </div>

            <c:if test="${not user.admin}">
                <div class="card-de p-4">
                    <h5 class="mb-3">My reviews</h5>
                    <c:if test="${empty myReviews}"><p class="text-muted-de mb-0">You haven't written any reviews yet.</p></c:if>
                    <c:forEach items="${myReviews}" var="r">
                        <div class="d-flex justify-content-between align-items-start py-2 border-top">
                            <div>
                                <a href="${ctx}/vehicles/${r.vehicleId}" class="fw-bold text-decoration-none">
                                    <c:out value="${empty vehicleMap[r.vehicleId] ? 'Removed vehicle' : vehicleMap[r.vehicleId].displayName}"/></a>
                                <span class="badge text-bg-light border ms-1">${r.badgeLabel}</span><br>
                                <t:stars rating="${r.rating}"/>
                                <span class="small text-muted-de ms-1"><c:out value="${r.comment}"/></span>
                            </div>
                            <div class="d-flex gap-2">
                                <a href="${ctx}/reviews/${r.id}/edit" class="btn btn-sm btn-outline-primary">Edit</a>
                                <form action="${ctx}/reviews/${r.id}/delete" method="post" data-confirm="Delete this review?">
                                    <input type="hidden" name="returnTo" value="/profile">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
