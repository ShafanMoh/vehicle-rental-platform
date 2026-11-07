<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Rent a vehicle"/>
<c:set var="activeNav" value="home"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section class="hero">
    <div class="container">
        <div class="row align-items-center g-5">
            <div class="col-lg-6">
                <span class="badge rounded-pill text-bg-warning mb-3 px-3 py-2"><i class="bi bi-lightning-charge-fill me-1"></i>Instant online booking</span>
                <h1 class="mb-3">Your journey across Sri Lanka <span class="highlight">starts here.</span></h1>
                <p class="lead mb-4">Choose from cars, SUVs, vans and motorbikes. Transparent prices with insurance
                    included, and 10% off every trip for Premium members.</p>
                <div class="d-flex flex-wrap gap-2">
                    <a href="${ctx}/vehicles" class="btn btn-accent btn-lg px-4">Browse vehicles</a>
                    <c:if test="${empty currentUser}">
                        <a href="${ctx}/register" class="btn btn-outline-light btn-lg px-4">Create free account</a>
                    </c:if>
                </div>
                <div class="row stat-strip mt-5 g-3">
                    <div class="col-4"><div class="stat">${vehicleCount}</div><div class="small">Vehicles</div></div>
                    <div class="col-4"><div class="stat">${availableCount}</div><div class="small">Ready to drive</div></div>
                    <div class="col-4"><div class="stat">${customerCount}+</div><div class="small">Happy customers</div></div>
                </div>
            </div>
            <div class="col-lg-6 text-center">
                <img src="${ctx}/images/suv.svg" alt="SUV" class="hero-img" width="560">
            </div>
        </div>
    </div>
</section>

<div class="container">
    <form action="${ctx}/vehicles" method="get" class="search-panel">
        <div class="row g-2 align-items-end">
            <div class="col-md-4">
                <label class="form-label" for="q">Brand, model or city</label>
                <input type="text" class="form-control" id="q" name="q" placeholder="e.g. Toyota, Kandy">
            </div>
            <div class="col-md-3">
                <label class="form-label" for="type">Vehicle type</label>
                <select class="form-select" id="type" name="type">
                    <option value="">Any type</option>
                    <option value="CAR">Car</option>
                    <option value="SUV">SUV</option>
                    <option value="VAN">Van</option>
                    <option value="MOTORBIKE">Motorbike</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="maxRate">Max price per day</label>
                <select class="form-select" id="maxRate" name="maxRate">
                    <option value="">Any price</option>
                    <option value="5000">Up to LKR 5,000</option>
                    <option value="10000">Up to LKR 10,000</option>
                    <option value="20000">Up to LKR 20,000</option>
                </select>
            </div>
            <div class="col-md-2 d-grid">
                <button class="btn btn-primary py-2" type="submit"><i class="bi bi-search me-1"></i>Search</button>
            </div>
        </div>
    </form>

    <section class="my-5">
        <div class="d-flex justify-content-between align-items-end mb-4">
            <div>
                <h2 class="mb-1">Popular vehicles</h2>
                <p class="text-muted-de mb-0">Hand-picked and ready for your next trip</p>
            </div>
            <a href="${ctx}/vehicles" class="btn btn-outline-primary d-none d-md-inline-block">View all</a>
        </div>
        <div class="row g-4">
            <c:forEach items="${featured}" var="v">
                <div class="col-md-6 col-lg-4">
                    <%@ include file="/WEB-INF/jsp/common/vehicle-card.jsp" %>
                </div>
            </c:forEach>
        </div>
    </section>

    <section class="my-5">
        <h2 class="text-center mb-4">Rent in three easy steps</h2>
        <div class="row g-4 text-center">
            <div class="col-md-4">
                <div class="card-de p-4 h-100">
                    <div class="feature-icon mb-3"><i class="bi bi-search"></i></div>
                    <h5>1. Choose a vehicle</h5>
                    <p class="text-muted-de mb-0">Filter by type, price or city and see real availability.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card-de p-4 h-100">
                    <div class="feature-icon mb-3"><i class="bi bi-calendar2-check"></i></div>
                    <h5>2. Pick your dates</h5>
                    <p class="text-muted-de mb-0">See the full price with insurance before you confirm.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card-de p-4 h-100">
                    <div class="feature-icon mb-3"><i class="bi bi-key"></i></div>
                    <h5>3. Pay &amp; drive</h5>
                    <p class="text-muted-de mb-0">Pay by card, bank transfer or cash at pickup.</p>
                </div>
            </div>
        </div>
    </section>

    <section class="my-5">
        <div class="card-de p-4 p-md-5" style="background:linear-gradient(120deg,#fff7e6,#ffffff)">
            <div class="row align-items-center g-4">
                <div class="col-md-8">
                    <h3 class="mb-2"><i class="bi bi-gem text-warning me-2"></i>Become a Premium member</h3>
                    <p class="text-muted-de mb-0">Premium members get 10% off every booking, can hold up to 5 bookings at
                        once and earn loyalty points on every trip. Ask our staff to upgrade your account.</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <c:forEach items="${fleetByType}" var="entry">
                        <span class="badge rounded-pill text-bg-light border me-1 mb-1 px-3 py-2">${entry.value} <c:out value="${entry.key}"/>s</span>
                    </c:forEach>
                </div>
            </div>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
