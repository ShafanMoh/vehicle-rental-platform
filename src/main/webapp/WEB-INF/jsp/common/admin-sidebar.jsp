<%-- Admin side menu. Each admin page sets ${adminMenu} to highlight its item. --%>
<aside class="admin-sidebar">
    <div class="small text-uppercase text-muted fw-bold mb-2 px-2">Administration</div>
    <nav class="nav flex-column">
        <a class="nav-link ${adminMenu == 'dashboard' ? 'active' : ''}" href="${ctx}/admin"><i class="bi bi-speedometer2"></i>Dashboard</a>
        <a class="nav-link ${adminMenu == 'vehicles' ? 'active' : ''}" href="${ctx}/admin/vehicles"><i class="bi bi-car-front"></i>Fleet</a>
        <a class="nav-link ${adminMenu == 'rentals' ? 'active' : ''}" href="${ctx}/admin/rentals"><i class="bi bi-calendar-week"></i>Bookings</a>
        <a class="nav-link ${adminMenu == 'payments' ? 'active' : ''}" href="${ctx}/admin/payments"><i class="bi bi-credit-card"></i>Payments</a>
        <a class="nav-link ${adminMenu == 'users' ? 'active' : ''}" href="${ctx}/admin/users"><i class="bi bi-people"></i>Users</a>
        <a class="nav-link ${adminMenu == 'reviews' ? 'active' : ''}" href="${ctx}/admin/reviews"><i class="bi bi-chat-square-quote"></i>Reviews</a>
        <hr>
        <a class="nav-link" href="${ctx}/vehicles"><i class="bi bi-shop"></i>View website</a>
    </nav>
</aside>
