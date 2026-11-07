<%@ tag description="Formats an amount as LKR 1,234.00" pageEncoding="UTF-8" %>
<%@ attribute name="value" required="true" type="java.lang.Double" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
LKR <fmt:formatNumber value="${value}" pattern="#,##0.00"/>