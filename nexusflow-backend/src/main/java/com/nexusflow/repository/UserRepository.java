package com.nexusflow.repository;

import com.nexusflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * Provides CRUD operations and custom query methods for
 * managing user data in the database.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Used for authentication during login.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users with a specific role.
     */
    List<User> findByRole(User.Role role);

    /**
     * Finds all active users.
     */
    List<User> findByIsActiveTrue();

    /**
     * Counts the total number of users.
     */
    long count();

    /**
     * Counts users by role.
     */
    long countByRole(User.Role role);

    /**
     * Finds users whose name contains the search term.
     */
    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %:searchTerm%")
    List<User> searchByName(String searchTerm);
}