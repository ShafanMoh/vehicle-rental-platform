<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="editing" value="${not empty editUser}"/>
<c:set var="pageTitle" value="${editing ? 'Edit user' : 'Add user'}"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="users"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <a href="${ctx}/admin/users" class="small text-decoration-none"><i class="bi bi-arrow-left me-1"></i>Back to users</a>
        <h3 class="mt-2 mb-3">
            <c:choose>
                <c:when test="${editing}">Edit <c:out value="${editUser.fullName}"/> <span class="text-muted-de fs-6">${editUser.id}</span></c:when>
                <c:otherwise>Add a new user</c:otherwise>
            </c:choose>
        </h3>

        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
        </c:if>

        <div class="row g-4">
            <div class="col-xl-8">
                <form method="post" action="${ctx}/admin/users/${editing ? editUser.id.concat('/edit') : 'new'}" class="card-de p-4">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label" for="roleSelect">Account type</label>
                            <c:choose>
                                <c:when test="${editing and editUser.admin}">
                                    <input type="hidden" name="role" value="ADMIN">
                                    <input type="text" class="form-control" value="Administrator" disabled>
                                </c:when>
                                <c:otherwise>
                                    <select class="form-select" id="roleSelect" name="role">
                                        <option value="CUSTOMER" ${form.role == 'CUSTOMER' ? 'selected' : ''}>Regular customer</option>
                                        <option value="PREMIUM" ${form.role == 'PREMIUM' ? 'selected' : ''}>Premium member</option>
                                        <c:if test="${not editing}">
                                            <option value="ADMIN" ${form.role == 'ADMIN' ? 'selected' : ''}>Administrator</option>
                                        </c:if>
                                    </select>
                                    <c:if test="${editing}"><div class="form-text">Change this to upgrade or downgrade the membership.</div></c:if>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="username">Username</label>
                            <input type="text" class="form-control" id="username" name="username" value="${fn:escapeXml(form.username)}"
                                   ${editing ? 'disabled' : 'required'}>
                            <c:if test="${editing}"><div class="form-text">Usernames cannot be changed.</div></c:if>
                        </div>
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
                            <c:when test="${editing and editUser.admin}">
                                <div class="col-md-6">
                                    <label class="form-label" for="jobTitle">Job title</label>
                                    <input type="text" class="form-control" id="jobTitle" name="jobTitle" value="${fn:escapeXml(form.jobTitle)}" required>
                                </div>
                            </c:when>
                            <c:when test="${editing}">
                                <div class="col-md-6">
                                    <label class="form-label" for="drivingLicenseNumber">Driving licence number</label>
                                    <input type="text" class="form-control" id="drivingLicenseNumber" name="drivingLicenseNumber" value="${fn:escapeXml(form.drivingLicenseNumber)}" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label" for="address">Address</label>
                                    <input type="text" class="form-control" id="address" name="address" value="${fn:escapeXml(form.address)}" required>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="col-md-6" data-roles="ADMIN">
                                    <label class="form-label" for="jobTitle">Job title</label>
                                    <input type="text" class="form-control" id="jobTitle" name="jobTitle" value="${fn:escapeXml(form.jobTitle)}">
                                </div>
                                <div class="col-md-6" data-roles="CUSTOMER,PREMIUM">
                                    <label class="form-label" for="drivingLicenseNumber">Driving licence number</label>
                                    <input type="text" class="form-control" id="drivingLicenseNumber" name="drivingLicenseNumber" value="${fn:escapeXml(form.drivingLicenseNumber)}">
                                </div>
                                <div class="col-12" data-roles="CUSTOMER,PREMIUM">
                                    <label class="form-label" for="address">Address</label>
                                    <input type="text" class="form-control" id="address" name="address" value="${fn:escapeXml(form.address)}">
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="col-md-6">
                            <label class="form-label" for="password">${editing ? 'Reset password (optional)' : 'Password'}</label>
                            <input type="password" class="form-control" id="password" name="password" minlength="6"
                                   ${editing ? '' : 'required'} autocomplete="new-password">
                        </div>
                        <c:if test="${not editing}">
                            <div class="col-md-6">
                                <label class="form-label" for="confirmPassword">Confirm password</label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="6" required autocomplete="new-password">
                            </div>
                        </c:if>
                    </div>
                    <button type="submit" class="btn btn-primary mt-4 px-4">${editing ? 'Save changes' : 'Create user'}</button>
                </form>
            </div>

            <c:if test="${editing and not editUser.admin}">
                <div class="col-xl-4">
                    <div class="card-de p-4">
                        <h6 class="mb-3">Membership</h6>
                        <div class="small d-flex justify-content-between"><span>Discount</span><strong><fmt:formatNumber value="${editUser.discountRate}" type="percent"/></strong></div>
                        <div class="small d-flex justify-content-between"><span>Open bookings allowed</span><strong>${editUser.maxActiveRentals}</strong></div>
                        <c:if test="${editUser.premium}">
                            <div class="small d-flex justify-content-between"><span>Loyalty points</span><strong>${editUser.loyaltyPoints}</strong></div>
                        </c:if>
                        <hr>
                        <h6 class="mb-2">Bookings (${fn:length(userRentals)})</h6>
                        <c:if test="${empty userRentals}"><p class="small text-muted-de mb-0">No bookings yet.</p></c:if>
                        <c:forEach items="${userRentals}" var="r">
                            <div class="small d-flex justify-content-between py-1 border-top">
                                <span>${r.id} &middot; <c:out value="${empty vehicleMap[r.vehicleId] ? 'Removed vehicle' : vehicleMap[r.vehicleId].displayName}"/></span>
                                <span class="badge text-bg-${r.status.badgeColor}">${r.status.displayName}</span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
