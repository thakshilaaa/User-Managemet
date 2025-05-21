package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Car;
import org.example.service.CarService;
import org.example.util.SessionManager;
import org.example.util.FileUtil;

import java.io.IOException;

@WebServlet("/cars/*")
public class CarServlet extends HttpServlet {
    private final CarService carService = CarService.getInstance();

    @Override
    public void init() throws ServletException {
        super.init();
        // Verify data directory on startup
        System.out.println("\n=== Checking Data Directory on Startup ===");
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        System.out.println("Servlet context real path: " + getServletContext().getRealPath("/"));
        System.out.println("Data directory location: " + FileUtil.getDataDirectory());
        FileUtil.checkDataDirectory();
        System.out.println("=========================================\n");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("\n--- CarServlet: Starting GET request ---");
            System.out.println("CarServlet: Request URI: " + request.getRequestURI());
            System.out.println("CarServlet: Path Info: " + request.getPathInfo());
            System.out.println("CarServlet: Query String: " + request.getQueryString());
            
            // Check authentication
            System.out.println("CarServlet: Checking authentication...");
            if (!SessionManager.isAuthenticated(request)) {
                System.out.println("CarServlet: User not authenticated, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            System.out.println("CarServlet: User is authenticated");

            String pathInfo = request.getPathInfo();
            String searchTerm = request.getParameter("search");
            String sortOrder = request.getParameter("sort");

            // List or search cars
            if (pathInfo == null || pathInfo.equals("/")) {
                try {
                    Car[] cars;
                    if (searchTerm != null) {
                        System.out.println("CarServlet: Searching cars with term: " + searchTerm);
                        try {
                            cars = carService.searchCars(searchTerm.trim());
                        } catch (Exception e) {
                            System.err.println("CarServlet: Error during search: " + e.getMessage());
                            cars = new Car[0]; // Return empty array instead of null
                        }
                        // Set the search term even if search fails
                        request.setAttribute("searchTerm", searchTerm);
                    } else {
                        System.out.println("CarServlet: Getting all cars");
                        cars = carService.getAllCars();
                    }
                    
                    // Ensure cars is never null
                    if (cars == null) {
                        System.out.println("CarServlet: Converting null cars to empty array");
                        cars = new Car[0];
                    }

                    // Sort cars if requested
                    if (sortOrder != null && cars.length > 0) {
                        System.out.println("CarServlet: Sorting cars by price: " + sortOrder);
                        Car[] sortedCars = new Car[cars.length];
                        System.arraycopy(cars, 0, sortedCars, 0, cars.length);
                        
                        if (sortOrder.equals("price_asc")) {
                            java.util.Arrays.sort(sortedCars, (a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                        } else if (sortOrder.equals("price_desc")) {
                            java.util.Arrays.sort(sortedCars, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                        }
                        cars = sortedCars;
                    }
                    
                    System.out.println("CarServlet: Retrieved " + cars.length + " cars");
                    if (cars.length > 0) {
                        System.out.println("CarServlet: First car: " + cars[0]);
                    }
                    
                    request.setAttribute("cars", cars);
                    System.out.println("CarServlet: Forwarding to list.jsp");
                    request.getRequestDispatcher("/WEB-INF/jsp/cars/list.jsp").forward(request, response);
                    return;
                } catch (Exception e) {
                    System.err.println("CarServlet: Error while getting cars: " + e.getMessage());
                    e.printStackTrace();
                    // Instead of throwing exception, show empty results
                    request.setAttribute("cars", new Car[0]);
                    if (searchTerm != null) {
                        request.setAttribute("searchTerm", searchTerm);
                    }
                    request.getRequestDispatcher("/WEB-INF/jsp/cars/list.jsp").forward(request, response);
                    return;
                }
            }

            // Handle other paths
            try {
                if (pathInfo.equals("/new")) {
                    System.out.println("CarServlet: Showing new car form");
                    request.getRequestDispatcher("/WEB-INF/jsp/cars/form.jsp").forward(request, response);
                    return;
                }

                if (pathInfo.startsWith("/edit/")) {
                    // Show edit car form
                    String registrationNumber = pathInfo.substring(6);
                    System.out.println("CarServlet: Editing car with registration: " + registrationNumber);
                    
                    Car car = carService.getCar(registrationNumber);
                    System.out.println("CarServlet: Retrieved car: " + (car != null ? car.toString() : "null"));
                    
                    if (car == null) {
                        System.out.println("CarServlet: Car not found for editing");
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
                        return;
                    }
                    
                    request.setAttribute("car", car);
                    System.out.println("CarServlet: Forwarding to form.jsp");
                    request.getRequestDispatcher("/WEB-INF/jsp/cars/form.jsp").forward(request, response);
                    return;
                } else if (pathInfo.startsWith("/view/")) {
                    String registrationNumber = pathInfo.substring(6);
                    System.out.println("CarServlet: Viewing car with registration: " + registrationNumber);
                    Car car = carService.getCar(registrationNumber);
                    if (car == null) {
                        System.out.println("CarServlet: Car not found for viewing");
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    request.setAttribute("car", car);
                    request.getRequestDispatcher("/WEB-INF/jsp/cars/view.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                System.err.println("CarServlet: Error processing path " + pathInfo + ": " + e.getMessage());
                e.printStackTrace();
                throw new ServletException("Error processing request", e);
            }

            System.out.println("CarServlet: Invalid path requested: " + pathInfo);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            
        } catch (Exception e) {
            System.err.println("CarServlet: Unhandled error in doGet: " + e.getMessage());
            System.err.println("CarServlet: Error class: " + e.getClass().getName());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is authenticated
        if (!SessionManager.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        boolean isEdit = pathInfo != null && pathInfo.startsWith("/edit/");
        String registrationNumber = isEdit ? pathInfo.substring(6) : null;

        // Get form parameters
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String yearStr = request.getParameter("year");
        String specifications = request.getParameter("specifications");
        String priceStr = request.getParameter("price");
        
        // For new cars, get registration number from form
        if (!isEdit) {
            registrationNumber = request.getParameter("registrationNumber");
        }

        // Validate input
        if (make == null || make.trim().isEmpty() ||
            model == null || model.trim().isEmpty() ||
            yearStr == null || yearStr.trim().isEmpty() ||
            registrationNumber == null || registrationNumber.trim().isEmpty() ||
            priceStr == null || priceStr.trim().isEmpty()) {
            
            request.setAttribute("error", "All fields except specifications are required");
            if (isEdit) {
                Car car = carService.getCar(registrationNumber);
                request.setAttribute("car", car);
            }
            request.getRequestDispatcher("/WEB-INF/jsp/cars/form.jsp").forward(request, response);
            return;
        }

        int year;
        double price;
        try {
            year = Integer.parseInt(yearStr);
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                throw new NumberFormatException("Price cannot be negative");
            }
            if (year < 1900 || year > 2100) {
                throw new NumberFormatException("Year must be between 1900 and 2100");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid year or price format: " + e.getMessage());
            if (isEdit) {
                Car car = carService.getCar(registrationNumber);
                request.setAttribute("car", car);
            }
            request.getRequestDispatcher("/WEB-INF/jsp/cars/form.jsp").forward(request, response);
            return;
        }

        boolean success;
        if (isEdit) {
            // Update existing car
            success = carService.updateCar(registrationNumber, make, model, year, specifications, price);
            if (!success) {
                request.setAttribute("error", "Car not found with registration: " + registrationNumber);
                request.getRequestDispatcher("/WEB-INF/jsp/cars/form.jsp").forward(request, response);
                return;
            }
        } else {
            // Create new car
            success = carService.addCar(make, model, year, registrationNumber, specifications, price);
            if (!success) {
                request.setAttribute("error", "Registration number already exists");
                request.getRequestDispatcher("/WEB-INF/jsp/cars/form.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/cars");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("CarServlet: Processing PUT request");
        
        // Check if user is authenticated
        if (!SessionManager.isAuthenticated(request)) {
            System.out.println("CarServlet: User not authenticated");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/")) {
            System.out.println("CarServlet: Invalid path info");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        String registrationNumber = pathInfo.substring(1);
        System.out.println("CarServlet: Updating car with registration: " + registrationNumber);

        // Validate required fields
        String newMake = request.getParameter("make");
        String newModel = request.getParameter("model");
        String yearStr = request.getParameter("year");
        String newSpecifications = request.getParameter("specifications");
        String priceStr = request.getParameter("price");

        if (newMake == null || newMake.trim().isEmpty() ||
            newModel == null || newModel.trim().isEmpty() ||
            yearStr == null || yearStr.trim().isEmpty() ||
            priceStr == null || priceStr.trim().isEmpty()) {
            System.out.println("CarServlet: Missing required fields");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields except specifications are required");
            return;
        }

        // Parse and validate year
        int newYear;
        try {
            newYear = Integer.parseInt(yearStr);
            if (newYear < 1900 || newYear > 2100) {
                throw new NumberFormatException("Year must be between 1900 and 2100");
            }
        } catch (NumberFormatException e) {
            System.out.println("CarServlet: Invalid year format: " + yearStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid year format. " + e.getMessage());
            return;
        }

        // Parse and validate price
        double newPrice;
        try {
            newPrice = Double.parseDouble(priceStr);
            if (newPrice < 0) {
                throw new NumberFormatException("Price cannot be negative");
            }
        } catch (NumberFormatException e) {
            System.out.println("CarServlet: Invalid price format: " + priceStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid price format. " + e.getMessage());
            return;
        }

        // Update car
        try {
            if (carService.updateCar(registrationNumber, newMake, newModel, newYear, newSpecifications, newPrice)) {
                System.out.println("CarServlet: Car updated successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                System.out.println("CarServlet: Car not found with registration: " + registrationNumber);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found with registration: " + registrationNumber);
            }
        } catch (Exception e) {
            System.err.println("CarServlet: Error updating car: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating car: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is authenticated
        if (!SessionManager.isAuthenticated(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Registration number is required");
            return;
        }

        String registrationNumber = pathInfo.substring(1);
        // Delete car
        if (carService.deleteCar(registrationNumber)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
        }
    }
} 