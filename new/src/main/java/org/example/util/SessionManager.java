package org.example.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Simplified utility class for managing sessions
 */
public class SessionManager {
    private static final String USER_ROLE = "userRole";

    /**
     * Create a new session for authenticated user
     * @param request HTTP request
     * @param isAdmin Whether the user is admin
     */
    public static void createSession(HttpServletRequest request, boolean isAdmin) {
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_ROLE, isAdmin ? "ADMIN" : "USER");
    }

    /**
     * Check if current user is authenticated
     * @param request HTTP request
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(USER_ROLE) != null;
    }

    /**
     * Check if current user is admin
     * @param request HTTP request
     * @return true if admin, false otherwise
     */
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && "ADMIN".equals(session.getAttribute(USER_ROLE));
    }

    /**
     * Invalidate current session
     * @param request HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
} 