<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"/>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark ">
    <a class="navbar-brand" href="#">
    <div>
        <a class="navbar-brand" href="${pageContext.request.contextPath}/cars">
            <img src="images\logo.png" alt="carfliplogo" height="50px" >
        </a>
    </div>
    </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="${pageContext.request.contextPath}/cars">View Cars</a>
                </li>
                <c:if test="${sessionScope.userRole == 'ADMIN'}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/users">Users</a>
                    </li>
                </c:if>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <span class="nav-link">Welcome, ${sessionScope.userRole == 'ADMIN' ? 'Admin' : 'User'}</span>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="btn" style="background-color: greenyellow; color: white; ">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav> 