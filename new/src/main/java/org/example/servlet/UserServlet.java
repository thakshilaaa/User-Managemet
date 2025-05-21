package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.User;
import org.example.service.UserService;
import org.example.util.SessionManager;

import java.io.IOException;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is authenticated
        if (!SessionManager.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if user is admin
        if (!SessionManager.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        // Set cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String pathInfo = request.getPathInfo();
        System.out.println("UserServlet: Processing GET request with path: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all users - always get fresh data
                System.out.println("UserServlet: Getting fresh list of all users");
                User[] users = userService.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/jsp/users/list.jsp").forward(request, response);
                return;
            }

            if (pathInfo.equals("/new")) {
                // Show new user form
                request.getRequestDispatcher("/WEB-INF/jsp/users/form.jsp").forward(request, response);
                return;
            }

            if (pathInfo.startsWith("/edit/")) {
                String username = pathInfo.substring(6);
                System.out.println("UserServlet: Editing user: " + username);
                
                // Get the user to edit
                User userToEdit = userService.getUser(username);
                if (userToEdit == null) {
                    System.out.println("UserServlet: User not found: " + username);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }

                // Set the user in request attributes
                request.setAttribute("user", userToEdit);
                System.out.println("UserServlet: Found user to edit: " + userToEdit.getUsername() + ", Admin: " + userToEdit.isAdmin());
                request.getRequestDispatcher("/WEB-INF/jsp/users/form.jsp").forward(request, response);
                return;
            }

            // Invalid path
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            
        } catch (Exception e) {
            System.err.println("UserServlet: Error processing request: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Error processing request", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("UserServlet: Processing POST request");
            
            // Check if user is authenticated and admin
            if (!SessionManager.isAuthenticated(request)) {
                System.out.println("UserServlet: User not authenticated, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            if (!SessionManager.isAdmin(request)) {
                System.out.println("UserServlet: User not admin, sending forbidden");
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String pathInfo = request.getPathInfo();
            boolean isEdit = pathInfo != null && pathInfo.startsWith("/edit/");
            String username = isEdit ? pathInfo.substring(6) : request.getParameter("username");

            // Get form parameters
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String role = request.getParameter("role");
            
            System.out.println("UserServlet: Received parameters:");
            System.out.println("- username: " + username);
            System.out.println("- email: " + email);
            System.out.println("- role: " + role);
            System.out.println("- isEdit: " + isEdit);

            boolean isAdmin = "ADMIN".equals(role);
            System.out.println("UserServlet: Determined isAdmin=" + isAdmin);

            // Validate input for new users or required fields for edit
            if (!isEdit && (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty())) {
                System.out.println("UserServlet: Username and password required for new users");
                request.setAttribute("error", "Username and password are required for new users");
                request.getRequestDispatcher("/WEB-INF/jsp/users/form.jsp").forward(request, response);
                return;
            }

            if (email == null || email.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
                System.out.println("UserServlet: Missing required fields");
                request.setAttribute("error", "Email and role are required");
                if (isEdit) {
                    User user = userService.getUser(username);
                    request.setAttribute("user", user);
                }
                request.getRequestDispatcher("/WEB-INF/jsp/users/form.jsp").forward(request, response);
                return;
            }

            boolean success;
            if (isEdit) {
                // Update existing user
                System.out.println("UserServlet: Updating existing user: " + username);
                success = userService.updateUser(username, 
                    password != null && !password.trim().isEmpty() ? password : null,
                    email,
                    isAdmin);
                
                if (!success) {
                    System.out.println("UserServlet: User not found: " + username);
                    request.setAttribute("error", "User not found");
                    request.getRequestDispatcher("/WEB-INF/jsp/users/form.jsp").forward(request, response);
                    return;
                }
            } else {
                // Create new user
                System.out.println("UserServlet: Creating new user with isAdmin=" + isAdmin);
                success = userService.createUser(username, password, email, isAdmin);
                if (!success) {
                    System.out.println("UserServlet: Username already exists");
                    request.setAttribute("error", "Username already exists");
                    request.getRequestDispatcher("/WEB-INF/jsp/users/form.jsp").forward(request, response);
                    return;
                }
            }

            System.out.println("UserServlet: Operation successful, redirecting to users list");
            response.sendRedirect(request.getContextPath() + "/users");
            
        } catch (Exception e) {
            System.err.println("UserServlet: Error in doPost: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("UserServlet: Processing PUT request");
            
            // Check if user is authenticated and admin
            if (!SessionManager.isAuthenticated(request)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (!SessionManager.isAdmin(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Get username from path
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || !pathInfo.startsWith("/")) {
                System.out.println("UserServlet: Invalid path info");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
                return;
            }

            String username = pathInfo.substring(1);
            String newPassword = request.getParameter("password");
            String newEmail = request.getParameter("email");
            String role = request.getParameter("role");
            
            System.out.println("UserServlet: Updating user:");
            System.out.println("- username from path: " + username);
            System.out.println("- email: " + newEmail);
            System.out.println("- role: " + role);
            System.out.println("- password changed: " + (newPassword != null && !newPassword.trim().isEmpty()));

            if (username == null || username.trim().isEmpty()) {
                System.out.println("UserServlet: Username is missing");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required");
                return;
            }

            // Only set admin status if role parameter is present
            Boolean newAdminStatus = role != null ? "ADMIN".equals(role) : null;
            System.out.println("UserServlet: New admin status: " + newAdminStatus);

            // Update user
            if (userService.updateUser(username, 
                                    newPassword != null && !newPassword.trim().isEmpty() ? newPassword : null,
                                    newEmail,
                                    newAdminStatus)) {
                System.out.println("UserServlet: User updated successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                System.out.println("UserServlet: User not found: " + username);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            System.err.println("UserServlet: Error in doPut: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is authenticated and admin
        if (!SessionManager.isAuthenticated(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!SessionManager.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required");
            return;
        }

        String username = pathInfo.substring(1);
        // Delete user
        if (userService.deleteUser(username)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }
} 