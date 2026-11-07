<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage users"/>
<c:set var="activeNav" value="admin"/>
<c:set var="adminMenu" value="users"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="admin-layout">
    <%@ include file="/WEB-INF/jsp/common/admin-sidebar.jsp" %>
    <div class="admin-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <h3 class="mb-0">Users <span class="text-muted-de fs-6">(${fn:length(users)})</span></h3>
            <a href="${ctx}/admin/users/new" class="btn btn-primary"><i class="bi bi-person-plus me-1"></i>Add user</a>
        </div>

        <form class="card-de p-3 mb-3" method="get">
            <div class="row g-2">
                <div class="col-md-7"><input type="text" class="form-control" name="q" value="${fn:escapeXml(q)}" placeholder="Search by ID, name, username, email or phone"></div>
                <div class="col-md-3">
                    <select class="form-select" name="role">
                        <option value="">All roles</option>
                        <option value="CUSTOMER" ${role == 'CUSTOMER' ? 'selected' : ''}>Regular customers</option>
                        <option value="PREMIUM" ${role == 'PREMIUM' ? 'selected' : ''}>Premium members</option>
                        <option value="ADMIN" ${role == 'ADMIN' ? 'selected' : ''}>Administrators</option>
                    </select>
                </div>
                <div class="col-md-2 d-grid"><button class="btn btn-outline-primary" type="submit">Search</button></div>
            </div>
        </form>

        <div class="card-de overflow-hidden">
            <div class="table-responsive">
                <table class="table table-de table-hover">
                    <thead><tr><th>ID</th><th>Name</th><th>Username</th><th>Contact</th><th>Role</th><th>Joined</th><th class="text-end">Actions</th></tr></thead>
                    <tbody>
                    <c:if test="${empty users}"><tr><td colspan="7" class="text-center text-muted-de p-4">No users found.</td></tr></c:if>
                    <c:forEach items="${users}" var="u">
                        <tr>
                            <td class="fw-bold">${u.id}</td>
                            <td><span class="avatar me-2">${fn:escapeXml(u.initials)}</span><c:out value="${u.fullName}"/></td>
                            <td>@<c:out value="${u.username}"/></td>
                            <td class="small"><c:out value="${u.email}"/><br><c:out value="${u.phone}"/></td>
                            <td><span class="badge ${u.role == 'ADMIN' ? 'text-bg-dark' : (u.role == 'PREMIUM' ? 'text-bg-warning' : 'text-bg-primary')}">${u.roleDisplayName}</span></td>
                            <td class="small">${u.registeredDate}</td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/admin/users/${u.id}/edit" class="btn btn-sm btn-outline-primary" title="Edit"><i class="bi bi-pencil"></i></a>
                                <c:if test="${u.id != currentUser.id}">
                                    <form action="${ctx}/admin/users/${u.id}/delete" method="post" class="d-inline"
                                          data-confirm="Delete ${fn:escapeXml(u.fullName)}'s account?">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete"><i class="bi bi-trash"></i></button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
