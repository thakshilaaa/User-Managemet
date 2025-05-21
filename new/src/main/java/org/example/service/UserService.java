package org.example.service;

import org.example.datastructure.LinkedList;
import org.example.model.User;
import org.example.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing users using Singleton pattern
 */
public class UserService {
    private static UserService instance;
    private final LinkedList<User> users;
    private static final String USERS_FILE = "users.txt";
    private static final String DELIMITER = "||";

    private UserService() {
        users = new LinkedList<>();
        loadUsersFromFile();
        // Ensure there's always an admin user
        if (users.find(user -> user.isAdmin()) == null) {
            createUser("admin", "admin", "admin@system.com", true);
        }
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadUsersFromFile() {
        List<String> lines = FileUtil.readLines(USERS_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|\\|");
            if (parts.length >= 4) {
                String username = parts[0];
                String password = parts[1];
                String email = parts[2];
                boolean isAdmin = Boolean.parseBoolean(parts[3]);
                users.add(new User(username, password, email, isAdmin));
            }
        }
        System.out.println("UserService: Loaded " + users.size() + " users from file");
    }

    private void saveUsersToFile() {
        List<String> lines = new ArrayList<>();
        User[] allUsers = users.toArray();
        for (User user : allUsers) {
            String line = String.join(DELIMITER,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                String.valueOf(user.isAdmin())
            );
            lines.add(line);
        }
        FileUtil.writeLines(USERS_FILE, lines);
        System.out.println("UserService: Saved " + lines.size() + " users to file");
    }

    /**
     * Authenticate user
     * @param username Username
     * @param password Password
     * @return User if authenticated, null otherwise
     */
    public User authenticate(String username, String password) {
        User user = users.find(u -> u.getUsername().equals(username) && u.getPassword().equals(password));
        return user;
    }

    /**
     * Create new user
     * @param username Username
     * @param password Password
     * @param email Email
     * @param isAdmin Admin status
     * @return true if user created, false if username exists
     */
    public boolean createUser(String username, String password, String email, boolean isAdmin) {
        if (users.find(user -> user.getUsername().equals(username)) != null) {
            return false;
        }
        users.add(new User(username, password, email, isAdmin));
        saveUsersToFile();
        return true;
    }

    /**
     * Update user details
     * @param username Username to update
     * @param newPassword New password (null to keep current)
     * @param newEmail New email (null to keep current)
     * @param newAdminStatus New admin status (null to keep current)
     * @return true if user updated, false if not found
     */
    public boolean updateUser(String username, String newPassword, String newEmail, Boolean newAdminStatus) {
        User user = users.find(u -> u.getUsername().equals(username));
        if (user == null) {
            return false;
        }
        
        if (newPassword != null) user.setPassword(newPassword);
        if (newEmail != null) user.setEmail(newEmail);
        if (newAdminStatus != null) user.setAdmin(newAdminStatus);
        
        saveUsersToFile();
        return true;
    }

    /**
     * Delete user
     * @param username Username to delete
     * @return true if user deleted, false if not found
     */
    public boolean deleteUser(String username) {
        User user = users.find(u -> u.getUsername().equals(username));
        if (user == null) {
            return false;
        }
        
        // Prevent deleting the last admin user
        if (user.isAdmin()) {
            User[] allUsers = users.toArray();
            int adminCount = 0;
            for (User u : allUsers) {
                if (u.isAdmin()) adminCount++;
            }
            if (adminCount <= 1) {
                return false;
            }
        }
        
        boolean result = users.remove(user);
        if (result) {
            saveUsersToFile();
        }
        return result;
    }

    /**
     * Get all users
     * @return Array of all users
     */
    public User[] getAllUsers() {
        return users.toArray();
    }

    /**
     * Check if user exists
     * @param username Username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        return users.find(user -> user.getUsername().equals(username)) != null;
    }

    /**
     * Get a specific user by username
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    public User getUser(String username) {
        System.out.println("UserService: Getting user with username: " + username);
        User[] allUsers = getAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                System.out.println("UserService: Found user: " + username);
                return user;
            }
        }
        System.out.println("UserService: User not found: " + username);
        return null;
    }
} 