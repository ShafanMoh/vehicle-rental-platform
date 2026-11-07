<%@ tag description="Shows a 0-5 star rating" pageEncoding="UTF-8" %>
<%@ attribute name="rating" required="true" type="java.lang.Double" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<span class="stars" title="${rating} out of 5"><c:forEach begin="1" end="5" var="i"><c:choose><c:when test="${rating >= i}"><i class="bi bi-star-fill"></i></c:when><c:when test="${rating >= i - 0.5}"><i class="bi bi-star-half"></i></c:when><c:otherwise><i class="bi bi-star"></i></c:otherwise></c:choose></c:forEach></span>
