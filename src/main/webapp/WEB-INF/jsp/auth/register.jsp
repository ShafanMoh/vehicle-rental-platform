<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create account"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container">
    <div class="auth-wrap wide">
        <div class="card-de p-4 p-md-5">
            <div class="text-center mb-4">
                <h3 class="mb-1">Create your account</h3>
                <p class="text-muted-de mb-0">It takes less than a minute</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
            </c:if>

            <form action="${ctx}/register" method="post">
                <h6 class="text-uppercase text-muted small fw-bold mb-3">Personal details</h6>
                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label" for="fullName">Full name</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" value="${fn:escapeXml(form.fullName)}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="email">Email</label>
                        <input type="email" class="form-control" id="email" name="email" value="${fn:escapeXml(form.email)}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="phone">Phone number</label>
                        <input type="tel" class="form-control" id="phone" name="phone" value="${fn:escapeXml(form.phone)}"
                               placeholder="0771234567" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="drivingLicenseNumber">Driving licence number</label>
                        <input type="text" class="form-control" id="drivingLicenseNumber" name="drivingLicenseNumber"
                               value="${fn:escapeXml(form.drivingLicenseNumber)}" required>
                    </div>
                    <div class="col-12">
                        <label class="form-label" for="address">Address</label>
                        <input type="text" class="form-control" id="address" name="address" value="${fn:escapeXml(form.address)}" required>
                    </div>
                </div>

                <h6 class="text-uppercase text-muted small fw-bold mb-3">Login details</h6>
                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <label class="form-label" for="username">Username</label>
                        <input type="text" class="form-control" id="username" name="username" value="${fn:escapeXml(form.username)}"
                               pattern="[A-Za-z0-9_]{4,20}" title="4-20 letters, numbers or underscores" required autocomplete="username">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="password">Password</label>
                        <input type="password" class="form-control" id="password" name="password" minlength="6" required
                               autocomplete="new-password">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="confirmPassword">Confirm password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="6"
                               required autocomplete="new-password">
                    </div>
                </div>
                <button type="submit" class="btn btn-primary w-100 py-2">Create account</button>
            </form>
            <p class="text-center mt-4 mb-0 small">Already have an account? <a href="${ctx}/login">Log in</a></p>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
