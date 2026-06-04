package com.nexusflow.service;

import com.nexusflow.entity.User;
import com.nexusflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User-related business logic.
 *
 * Handles user authentication, registration, and management.
 *
 * @author NexusFlow Team
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Finds a user by their ID.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Finds a user by their email address.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds all users.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Finds all active users.
     */
    public List<User> findActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Registers a new user.
     */
    public User registerUser(
            String email,
            String password,
            String firstName,
            String lastName,
            User.Role role
    ) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setEmail(email);

        // BCrypt encode password
        user.setPassword(passwordEncoder.encode(password));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    /**
     * Authenticates a user with email and password.
     */
    public User authenticate(String email, String password) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            System.out.println("User not found");
            return null;
        }

        User user = userOpt.get();

        if (!user.getIsActive()) {
            System.out.println("User inactive");
            return null;
        }

        String storedPassword = user.getPassword();

        System.out.println("Login attempt for: " + email);

        // BCrypt password check
        boolean passwordMatches = false;

        try {

            passwordMatches =
                    passwordEncoder.matches(password, storedPassword);

        } catch (Exception e) {

            System.out.println("BCrypt match failed");
        }

        // Fallback for old plain-text passwords
        if (!passwordMatches) {

            if (password.equals(storedPassword)) {

                System.out.println(
                        "Plain text password matched. Upgrading to BCrypt..."
                );

                // Upgrade old plain-text password to BCrypt
                user.setPassword(passwordEncoder.encode(password));

                userRepository.save(user);

                return user;
            }

            System.out.println("Password mismatch");

            return null;
        }

        System.out.println("Authentication successful");

        return user;
    }

    /**
     * Checks if an email exists.
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Updates a user's information.
     */
    public User updateUser(
            Long id,
            String firstName,
            String lastName,
            String email,
            User.Role role
    ) {

        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (email != null && !email.equals(user.getEmail())) {

            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }

            user.setEmail(email);
        }

        if (firstName != null) {
            user.setFirstName(firstName);
        }

        if (lastName != null) {
            user.setLastName(lastName);
        }

        if (role != null) {
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    /**
     * Deactivates a user (soft delete).
     */
    public void deactivateUser(Long id) {

        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {

            User user = userOpt.get();

            user.setIsActive(false);

            userRepository.save(user);
        }
    }

    /**
     * Counts total users.
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * Counts users by role.
     */
    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * Creates a default admin user if none exists.
     */
    public void createDefaultAdminIfNotExists() {

        String adminEmail = "admin@nexusflow.com";

        if (!userRepository.existsByEmail(adminEmail)) {

            User admin = new User();

            admin.setEmail(adminEmail);

            // BCrypt encoded password
            admin.setPassword(
                    passwordEncoder.encode("admin123")
            );

            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);

            userRepository.save(admin);

            System.out.println(
                    "Default admin user created: " + adminEmail
            );

            System.out.println(
                    "Login with: admin@nexusflow.com / admin123"
            );
        }
    }
}