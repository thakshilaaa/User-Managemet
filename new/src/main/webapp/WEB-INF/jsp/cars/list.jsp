<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page errorPage="/WEB-INF/jsp/error.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cars - Car Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"/>
</head>
<body style="background-image: url('<%= request.getContextPath() %>/images/background.jpeg'); background-size: cover; background-position: center; background-repeat: no-repeat; width: 100%; min-height:100vh;">
    <jsp:include page="../common/header.jsp"/>
    
    <div class="container mt-4">
        <div class="row mb-3">
            <div class="col-md-6">
                <h2>View Cars</h2>
            </div>
            <div class="col-md-6 text-end">
                <a href="${pageContext.request.contextPath}/cars/new" class="btn" style="background-color: greenyellow; color: white;">
                    <i class="bi bi-plus-lg"></i> Add New Car
                </a>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/cars" method="get" class="mb-4">
                    <div class="input-group">
                        <input type="text" name="search" class="form-control" placeholder="Search by make or model" 
                               value="<c:out value="${searchTerm}"/>">
                        <button type="submit" class="btn btn-outline-secondary">
                            <i class="bi bi-search"></i> Search
                        </button>
                        <c:if test="${not empty searchTerm}">
                            <a href="${pageContext.request.contextPath}/cars" class="btn btn-outline-secondary">
                                <i class="bi bi-x-lg"></i> Clear Search
                            </a>
                        </c:if>
                    </div>
                </form>

                <div class="mb-3">
                    <div class="btn-group" role="group" aria-label="Sort options">
                        <a href="${pageContext.request.contextPath}/cars?sort=price_asc${not empty searchTerm ? '&search='.concat(searchTerm) : ''}" 
                           class="btn btn-outline-primary ${param.sort == 'price_asc' ? 'active' : ''}">
                            <i class="bi bi-sort-numeric-down"></i> Price (Low to High)
                        </a>
                        <a href="${pageContext.request.contextPath}/cars?sort=price_desc${not empty searchTerm ? '&search='.concat(searchTerm) : ''}" 
                           class="btn btn-outline-primary ${param.sort == 'price_desc' ? 'active' : ''}">
                            <i class="bi bi-sort-numeric-up"></i> Price (High to Low)
                        </a>
                    </div>
                </div>

                <div class="table-responsive">
                    <%-- Debug information --%>
                    <% 
                    System.out.println("list.jsp: Starting to process cars list");
                    Object carsAttr = request.getAttribute("cars");
                    if (carsAttr == null) {
                        System.out.println("list.jsp: cars attribute is null");
                    } else {
                        System.out.println("list.jsp: cars attribute type: " + carsAttr.getClass().getName());
                        if (carsAttr.getClass().isArray()) {
                            Object[] carsArray = (Object[]) carsAttr;
                            System.out.println("list.jsp: cars array length: " + carsArray.length);
                            if (carsArray.length > 0) {
                                System.out.println("list.jsp: first car type: " + carsArray[0].getClass().getName());
                            }
                        }
                    }
                    %>

                    <c:choose>
                        <c:when test="${cars == null}">
                            <div class="alert alert-warning">
                                Error loading cars. Please try again later.
                            </div>
                        </c:when>
                        <c:when test="${empty cars}">
                            <div class="alert alert-info">
                                <c:choose>
                                    <c:when test="${not empty searchTerm}">
                                        <h4 class="alert-heading">No Cars Found</h4>
                                        <p>No cars matching "<strong><c:out value="${searchTerm}"/></strong>" were found.</p>
                                        <hr>
                                        <p class="mb-0">
                                            Try searching with different terms or 
                                            <a href="${pageContext.request.contextPath}/cars" class="alert-link">view all cars</a>.
                                        </p>
                                    </c:when>
                                    <c:otherwise>
                                        No cars found. Click "Add New Car" to add one.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${not empty searchTerm}">
                                <div class="alert alert-success mb-3">
                                    Showing results for: "<strong><c:out value="${searchTerm}"/></strong>"
                                </div>
                            </c:if>
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Make</th>
                                        <th>Model</th>
                                        <th>Year</th>
                                        <th>Registration</th>
                                        <th>Price (Rs)</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${cars}" var="car">
                                        <tr>
                                            <td><c:out value="${car.make}"/></td>
                                            <td><c:out value="${car.model}"/></td>
                                            <td><c:out value="${car.year}"/></td>
                                            <td><c:out value="${car.registrationNumber}"/></td>
                                            <td>Rs <c:out value="${car.price}"/></td>
                                            <td>
                                                <div class="btn-group" role="group">
                                                    <a href="${pageContext.request.contextPath}/cars/view/${car.registrationNumber}" 
                                                       class="btn btn-sm btn-info">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/cars/edit/${car.registrationNumber}" 
                                                       class="btn btn-sm btn-warning">
                                                        <i class="bi bi-pencil"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-danger" 
                                                            onclick="deleteCar('${car.registrationNumber}')">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function deleteCar(registrationNumber) {
            if (confirm('Are you sure you want to delete this car?')) {
                fetch('${pageContext.request.contextPath}/cars/' + registrationNumber, {
                    method: 'DELETE'
                }).then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Failed to delete car');
                    }
                });
            }
        }
    </script>
</body>
</html> 