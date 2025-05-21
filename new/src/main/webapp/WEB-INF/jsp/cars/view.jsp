<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Car - Car Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"/>
</head>
<body style="background-image: url('<%= request.getContextPath() %>/images/background.jpeg'); background-size: cover; background-position: center; background-repeat: no-repeat; width: 100%; min-height:100vh;">
    <jsp:include page="../common/header.jsp"/>
    
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h3>Car Details</h3>
                        <div>
                            <a href="${pageContext.request.contextPath}/cars/edit/${car.registrationNumber}" 
                               class="btn btn-warning">
                                <i class="bi bi-pencil"></i> Edit
                            </a>
                            <button type="button" class="btn btn-danger" 
                                    onclick="deleteCar('${car.registrationNumber}')">
                                <i class="bi bi-trash"></i> Delete
                            </button>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table">
                            <tr>
                                <th style="width: 30%">Make:</th>
                                <td>${car.make}</td>
                            </tr>
                            <tr>
                                <th>Model:</th>
                                <td>${car.model}</td>
                            </tr>
                            <tr>
                                <th>Year:</th>
                                <td>${car.year}</td>
                            </tr>
                            <tr>
                                <th>Registration Number:</th>
                                <td>${car.registrationNumber}</td>
                            </tr>
                            <tr>
                                <th>Price:</th>
                                <td>Rs ${car.price}</td>
                            </tr>
                            <tr>
                                <th>Specifications:</th>
                                <td style="white-space: pre-wrap;">${car.specifications}</td>
                            </tr>
                        </table>
                        
                        <div class="d-grid">
                            <a href="${pageContext.request.contextPath}/cars" 
                               class="btn btn-secondary">Back to List</a>
                        </div>
                    </div>
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
                        window.location.href = '${pageContext.request.contextPath}/cars';
                    } else {
                        alert('Failed to delete car');
                    }
                });
            }
        }
    </script>
</body>
</html> 