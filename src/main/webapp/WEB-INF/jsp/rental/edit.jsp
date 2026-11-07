<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Change booking"/>
<c:set var="activeNav" value="rentals"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="container">
    <div class="auth-wrap wide">
        <div class="card-de p-4 p-md-5">
            <h3 class="mb-1">Change booking ${rental.id}</h3>
            <p class="text-muted-de"><c:out value="${vehicle.displayName}"/> &middot; currently ${rental.startDate} to ${rental.endDate}
                (<t:money value="${rental.totalCost}"/>)</p>

            <c:if test="${not empty payment and payment.status == 'PAID'}">
                <div class="alert alert-info small"><i class="bi bi-info-circle me-1"></i>This booking is already paid, so
                    the new dates must cost the same (keep the same number of days).</div>
            </c:if>
            <c:if test="${not empty payment and payment.status == 'PENDING'}">
                <div class="alert alert-warning small"><i class="bi bi-info-circle me-1"></i>If the price changes, your
                    unpaid ${payment.methodDisplayName} payment is removed and you will be asked to pay the new amount.</div>
            </c:if>

            <form action="${ctx}/rentals/${rental.id}/edit" method="post" id="bookingForm"
                  data-quote-url="${ctx}/vehicles/${vehicle.id}/quote">
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label" for="startDate">Pickup date</label>
                        <input type="date" class="form-control" id="startDate" name="startDate" value="${rental.startDate}"
                               min="${tomorrow}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="endDate">Return date</label>
                        <input type="date" class="form-control" id="endDate" name="endDate" value="${rental.endDate}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="pickupLocation">Pickup branch</label>
                        <select class="form-select" id="pickupLocation" name="pickupLocation">
                            <c:forEach items="${['Colombo','Kandy','Galle','Negombo','Ella']}" var="branch">
                                <option value="${branch}" ${branch == rental.pickupLocation ? 'selected' : ''}>${branch}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div id="quoteBox" class="quote-box my-3 d-none"></div>
                <div class="d-flex gap-2 mt-3">
                    <button type="submit" class="btn btn-primary px-4">Save changes</button>
                    <a href="${ctx}/rentals" class="btn btn-light">Back</a>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
