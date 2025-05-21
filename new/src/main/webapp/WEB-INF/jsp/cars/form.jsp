<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty car ? 'Add New' : 'Edit'} Car - Car Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"/>
</head>
<body style="background-image: url('<%= request.getContextPath() %>/images/background.jpeg'); background-size: cover; background-position: center; background-repeat: no-repeat; width: 100%; min-height:100vh;">
    <jsp:include page="../common/header.jsp"/>
    
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h3>${empty car ? 'Add New' : 'Edit'} Car</h3>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                ${error}
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/cars${empty car ? '' : '/edit/'.concat(car.registrationNumber)}" 
                              method="post">
                            <div class="mb-3">
                                <label for="make" class="form-label">Make</label>
                                <input type="text" class="form-control" id="make" name="make" 
                                       value="${car.make}" required>
                            </div>
                            <div class="mb-3">
                                <label for="model" class="form-label">Model</label>
                                <input type="text" class="form-control" id="model" name="model" 
                                       value="${car.model}" required>
                            </div>
                            <div class="mb-3">
                                <label for="year" class="form-label">Year</label>
                                <input type="number" class="form-control" id="year" name="year" 
                                       value="${car.year}" required min="1900" max="2100">
                            </div>
                            <div class="mb-3">
                                <label for="registrationNumber" class="form-label">Registration Number</label>
                                <input type="text" class="form-control" id="registrationNumber" 
                                       name="registrationNumber" value="${car.registrationNumber}" 
                                       ${not empty car ? 'readonly' : ''} required>
                            </div>
                            <div class="mb-3">
                                <label for="specifications" class="form-label">Specifications</label>
                                <textarea class="form-control" id="specifications" name="specifications" 
                                          rows="3">${car.specifications}</textarea>
                            </div>
                            <div class="mb-3">
                                <label for="price" class="form-label">Price (Rs)</label>
                                <input type="number" class="form-control" id="price" name="price" 
                                       value="${car.price}" required min="0" step="0.01"
                                       onkeypress="return (event.charCode >= 48 && event.charCode <= 57) || event.charCode === 46"
                                       oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');">
                            </div>
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    ${empty car ? 'Add' : 'Update'} Car
                                </button>
                                <a href="${pageContext.request.contextPath}/cars" 
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