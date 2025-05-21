<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty user ? 'Add New' : 'Edit'} User - Car Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"/>
</head>
<body style="background-image: url('<%= request.getContextPath() %>/images/background.jpeg'); background-size: cover; background-position: center; background-repeat: no-repeat; width: 100%; min-height:100vh;">
    <jsp:include page="../common/header.jsp"/>
    
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">
                        <h3>${empty user ? 'Add New' : 'Edit'} User</h3>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                ${error}
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/users${empty user ? '' : '/edit/'.concat(user.username)}" 
                              method="post">
                            <div class="mb-3">
                                <label for="username" class="form-label">Username</label>
                                <input type="text" class="form-control" id="username" name="username" 
                                       value="${user.username}" ${not empty user ? 'readonly' : ''} required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">
                                    Password ${not empty user ? '(leave blank to keep current)' : ''}
                                </label>
                                <input type="password" class="form-control" id="password" name="password"
                                       ${empty user ? 'required' : ''}>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       value="${user.email}" required>
                            </div>
                            <div class="mb-3">
                                <label for="role" class="form-label">Role</label>
                                <select class="form-select" id="role" name="role" required>
                                    <option value="USER" ${not user.admin ? 'selected' : ''}>User</option>
                                    <option value="ADMIN" ${user.admin ? 'selected' : ''}>Admin</option>
                                </select>
                            </div>
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    ${empty user ? 'Add' : 'Update'} User
                                </button>
                                <a href="${pageContext.request.contextPath}/users" 
                                   class="btn btn-secondary">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 