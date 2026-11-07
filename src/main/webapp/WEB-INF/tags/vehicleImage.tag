<%@ tag description="Shows a vehicle's picture (uploaded link or a picture for its type)" pageEncoding="UTF-8" %>
<%@ attribute name="vehicle" required="true" type="com.driveeasy.rental.model.vehicle.Vehicle" %>
<%@ attribute name="cssClass" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="src" value="${vehicle.displayImage}"/>
<c:if test="${fn:startsWith(src, '/')}">
    <c:set var="src" value="${pageContext.request.contextPath}${src}"/>
</c:if>
<img src="${fn:escapeXml(src)}" alt="${fn:escapeXml(vehicle.displayName)}" class="${cssClass}" loading="lazy"
     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/${fn:toLowerCase(vehicle.type)}.svg'">
