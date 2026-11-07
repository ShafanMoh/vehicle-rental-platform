<%--
  Shared page top: <head>, navigation bar and flash messages.
  Each page sets ${pageTitle} (and optionally ${activeNav}) before including this file.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'DriveEasy' : pageTitle}"/> | DriveEasy Vehicle Rentals</title>
    <link rel="icon" href="${ctx}/images/car.svg" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark navbar-de">
    <div class="container">
        <a class="navbar-brand" href="${ctx}/"><i class="bi bi-car-front-fill me-1"></i>Drive<span>Easy</span></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto ms-lg-4">
                <li class="nav-item"><a class="nav-link ${activeNav == 'home' ? 'active' : ''}" href="${ctx}/">Home</a></li>
                <li class="nav-item"><a class="nav-link ${activeNav == 'vehicles' ? 'active' : ''}" href="${ctx}/vehicles">Browse Vehicles</a></li>
                <c:if test="${not empty currentUser and not currentUser.admin}">
                    <li class="nav-item"><a class="nav-link ${activeNav == 'rentals' ? 'active' : ''}" href="${ctx}/rentals">My Bookings</a></li>
                    <li class="nav-item"><a class="nav-link ${activeNav == 'payments' ? 'active' : ''}" href="${ctx}/payments">Payments</a></li>
                </c:if>
                <c:if test="${not empty currentUser and currentUser.admin}">
                    <li class="nav-item"><a class="nav-link ${activeNav == 'admin' ? 'active' : ''}" href="${ctx}/admin"><i class="bi bi-speedometer2 me-1"></i>Admin Panel</a></li>
                </c:if>
                <li class="nav-item"><a class="nav-link ${activeNav == 'about' ? 'active' : ''}" href="${ctx}/about">About</a></li>
            </ul>
            <c:choose>
                <c:when test="${not empty currentUser}">
                    <div class="dropdown">
                        <a class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" href="#"
                           data-bs-toggle="dropdown" aria-expanded="false">
                            <span class="avatar me-2"><c:out value="${currentUser.initials}"/></span>
                            <span class="d-none d-md-inline"><c:out value="${currentUser.fullName}"/></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                            <li><span class="dropdown-item-text small text-muted"><c:out value="${currentUser.roleDisplayName}"/></span></li>
                            <li><a class="dropdown-item" href="${ctx}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                            <c:if test="${not currentUser.admin}">
                                <li><a class="dropdown-item" href="${ctx}/rentals"><i class="bi bi-calendar-check me-2"></i>My Bookings</a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${ctx}/logout"><i class="bi bi-box-arrow-right me-2"></i>Log out</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="d-flex gap-2">
                        <a href="${ctx}/login" class="btn btn-outline-light btn-sm px-3">Log in</a>
                        <a href="${ctx}/register" class="btn btn-accent btn-sm px-3">Sign up</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<c:if test="${not empty flashSuccess or not empty flashError}">
    <div class="container mt-3">
        <c:if test="${not empty flashSuccess}">
            <div class="alert alert-success alert-dismissible fade show alert-auto mb-0" role="alert">
                <i class="bi bi-check-circle me-2"></i><c:out value="${flashSuccess}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty flashError}">
            <div class="alert alert-danger alert-dismissible fade show mb-0" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i><c:out value="${flashError}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
    </div>
</c:if>
<main>
