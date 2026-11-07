<%-- Shared page bottom: footer and scripts. --%>
</main>
<footer class="footer-de">
    <div class="container">
        <div class="row g-4">
            <div class="col-lg-4">
                <h5 class="text-white fw-bold"><i class="bi bi-car-front-fill me-1"></i>Drive<span style="color:var(--de-accent)">Easy</span></h5>
                <p class="small mb-0">Cars, SUVs, vans and bikes for every trip across Sri Lanka. Book online in minutes,
                    pick up from our branches and drive away.</p>
            </div>
            <div class="col-6 col-lg-2">
                <h6>Explore</h6>
                <ul class="list-unstyled small">
                    <li><a href="${ctx}/vehicles">All vehicles</a></li>
                    <li><a href="${ctx}/vehicles?type=CAR">Cars</a></li>
                    <li><a href="${ctx}/vehicles?type=SUV">SUVs</a></li>
                    <li><a href="${ctx}/vehicles?type=VAN">Vans</a></li>
                    <li><a href="${ctx}/vehicles?type=MOTORBIKE">Motorbikes</a></li>
                </ul>
            </div>
            <div class="col-6 col-lg-2">
                <h6>Account</h6>
                <ul class="list-unstyled small">
                    <li><a href="${ctx}/register">Create account</a></li>
                    <li><a href="${ctx}/login">Log in</a></li>
                    <li><a href="${ctx}/rentals">My bookings</a></li>
                    <li><a href="${ctx}/about">About us</a></li>
                </ul>
            </div>
            <div class="col-lg-4">
                <h6>Contact</h6>
                <ul class="list-unstyled small">
                    <li><i class="bi bi-geo-alt me-2"></i>No. 25, Galle Road, Colombo 03</li>
                    <li><i class="bi bi-telephone me-2"></i>+94 11 234 5678</li>
                    <li><i class="bi bi-envelope me-2"></i>hello@driveeasy.lk</li>
                    <li><i class="bi bi-clock me-2"></i>Open daily, 7:00 AM - 9:00 PM</li>
                </ul>
            </div>
        </div>
        <hr class="border-secondary">
        <p class="small text-center mb-0">&copy; 2026 DriveEasy Vehicle Rental Service Platform &middot; SE1020 Object Oriented Programming Project</p>
    </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/js/app.js"></script>
</body>
</html>
