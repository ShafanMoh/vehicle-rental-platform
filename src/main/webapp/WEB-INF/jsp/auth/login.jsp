<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Log in"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container">
    <div class="auth-wrap">
        <div class="card-de p-4 p-md-5">
            <div class="text-center mb-4">
                <div class="feature-icon mb-3"><i class="bi bi-person-circle"></i></div>
                <h3 class="mb-1">Welcome back</h3>
                <p class="text-muted-de mb-0">Log in to book and manage your rentals</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
            </c:if>
            <c:if test="${not empty param.next and empty error}">
                <div class="alert alert-info small"><i class="bi bi-info-circle me-2"></i>Please log in to continue.</div>
            </c:if>

            <form action="${ctx}/login" method="post">
                <input type="hidden" name="next" value="${fn:escapeXml(next)}">
                <div class="mb-3">
                    <label class="form-label" for="username">Username</label>
                    <input type="text" class="form-control" id="username" name="username" value="${fn:escapeXml(username)}"
                           required autofocus autocomplete="username">
                </div>
                <div class="mb-4">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password" required
                           autocomplete="current-password">
                </div>
                <button type="submit" class="btn btn-primary w-100 py-2">Log in</button>
            </form>
            <p class="text-center mt-4 mb-0 small">New to DriveEasy? <a href="${ctx}/register">Create an account</a></p>
        </div>

        <div class="card-de p-3 mt-3 small">
            <div class="fw-bold mb-1"><i class="bi bi-key me-1"></i>Demo accounts</div>
            <div class="text-muted-de">Admin: <code>admin</code> / <code>admin123</code></div>
            <div class="text-muted-de">Customer: <code>nimal</code> / <code>nimal123</code></div>
            <div class="text-muted-de">Premium customer: <code>kasuni</code> / <code>kasuni123</code></div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
