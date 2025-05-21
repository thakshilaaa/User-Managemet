<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>500 - Internal Server Error</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"/>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">
                <h1 class="display-1">500</h1>
                <h2 class="mb-4">Internal Server Error</h2>
                <p class="mb-4">Sorry, something went wrong on our end. Please try again later.</p>
                <a href="${pageContext.request.contextPath}/cars" class="btn btn-primary">Go to Home</a>
            </div>
        </div>
    </div>
</body>
</html> 

