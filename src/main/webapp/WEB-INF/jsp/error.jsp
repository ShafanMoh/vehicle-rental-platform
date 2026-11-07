<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container">
    <div class="auth-wrap text-center">
        <div class="card-de p-5">
            <div class="display-4 fw-bold text-primary mb-2"><c:out value="${empty status ? 'Oops' : status}"/></div>
            <h4 class="mb-3">
                <c:choose>
                    <c:when test="${status == 404}">We couldn't find that page</c:when>
                    <c:when test="${status == 403}">You don't have access to this page</c:when>
                    <c:otherwise>Something went wrong</c:otherwise>
                </c:choose>
            </h4>
            <c:if test="${not empty message and message != 'No message available'}">
                <p class="text-muted-de"><c:out value="${message}"/></p>
            </c:if>
            <a href="${ctx}/" class="btn btn-primary mt-2">Back to home</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
