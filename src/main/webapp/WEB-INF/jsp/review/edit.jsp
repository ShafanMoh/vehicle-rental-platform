<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Edit review"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container">
    <div class="auth-wrap">
        <div class="card-de p-4 p-md-5">
            <h4 class="mb-1">Edit your review</h4>
            <p class="text-muted-de"><c:out value="${vehicle.displayName}"/></p>
            <form action="${ctx}/reviews/${review.id}/edit" method="post">
                <label class="form-label d-block">Rating</label>
                <div class="rating-input mb-3">
                    <c:forEach begin="1" end="5" var="i">
                        <c:set var="star" value="${6 - i}"/>
                        <input type="radio" name="rating" id="star${star}" value="${star}" ${review.rating == star ? 'checked' : ''}>
                        <label for="star${star}" title="${star} stars"><i class="bi bi-star-fill"></i></label>
                    </c:forEach>
                </div>
                <label class="form-label" for="comment">Comment</label>
                <textarea class="form-control mb-3" id="comment" name="comment" rows="4" maxlength="500" required><c:out value="${review.comment}"/></textarea>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary px-4">Save</button>
                    <a href="${ctx}/vehicles/${vehicle.id}#reviews" class="btn btn-light">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
