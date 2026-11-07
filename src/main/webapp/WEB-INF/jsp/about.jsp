<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="About us"/>
<c:set var="activeNav" value="about"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="page-header">
    <div class="container">
        <h1 class="mb-1">About DriveEasy</h1>
        <p>Simple, honest vehicle rentals since 2026.</p>
    </div>
</div>

<div class="container">
    <div class="row g-4">
        <div class="col-lg-7">
            <div class="card-de p-4 h-100">
                <h4>Who we are</h4>
                <p>DriveEasy is a vehicle rental service with branches in Colombo, Kandy, Galle, Negombo and Ella. We rent
                    well-maintained cars, SUVs, vans and motorbikes to locals and tourists, for a single day or up to a month.</p>
                <h5 class="mt-4">How our pricing works</h5>
                <ul class="mb-0">
                    <li>Every vehicle has a daily rate plus a daily insurance fee that depends on its type
                        (Car LKR 750, SUV LKR 1,200, Van LKR 1,000, Motorbike LKR 250).</li>
                    <li>Vans include a one-time cleaning fee of LKR 2,500.</li>
                    <li>Motorbikes rented for 7 days or more get 15% off.</li>
                    <li>Premium members get a further 10% off every booking.</li>
                    <li>Card payments carry a 2% processing fee; bank transfers a flat LKR 100. Cash at pickup is free.</li>
                </ul>
            </div>
        </div>
        <div class="col-lg-5">
            <div class="card-de p-4 mb-4">
                <h5>Membership types</h5>
                <table class="table table-sm mb-0">
                    <thead><tr><th></th><th>Regular</th><th>Premium</th></tr></thead>
                    <tbody>
                    <tr><td>Discount</td><td>-</td><td>10%</td></tr>
                    <tr><td>Open bookings</td><td>2</td><td>5</td></tr>
                    <tr><td>Loyalty points</td><td>-</td><td>1 per LKR 1,000</td></tr>
                    </tbody>
                </table>
            </div>
            <div class="card-de p-4">
                <h5>Booking rules</h5>
                <ul class="small mb-0">
                    <li>Bookings can be made up to 180 days ahead, for up to 30 days.</li>
                    <li>You can change dates or cancel any time before pickup.</li>
                    <li>A valid driving licence is required when collecting the vehicle.</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
