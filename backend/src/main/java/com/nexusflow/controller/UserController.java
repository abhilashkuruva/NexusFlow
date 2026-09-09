package com.nexusflow.controller;

import com.nexusflow.dto.UserDTO;
import com.nexusflow.entity.User;
import com.nexusflow.service.UserService;
import com.nexusflow.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for User management and profile operations.
 * 
 * @author NexusFlow Team
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:4173"})
@Tag(name = "Users", description = "User management and profile APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get All Users", description = "Retrieve all active users")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false, defaultValue = "false") boolean all) {
        List<User> users = all ? userService.findAll() : userService.findActiveUsers();
        List<UserDTO> dtoList = users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Retrieve profile of authenticated user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        return userService.findByEmail(auth.getName())
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User by ID", description = "Retrieve user profile by ID")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User", description = "Update user details (name, email, role)")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            String firstName = (String) body.get("firstName");
            String lastName = (String) body.get("lastName");
            String email = (String) body.get("email");
            
            User.Role role = null;
            if (body.get("role") != null) {
                try {
                    role = User.Role.valueOf(body.get("role").toString().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            User updated = userService.updateUser(id, firstName, lastName, email, role);
            return ResponseEntity.ok(toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate User", description = "Soft delete / deactivate a user")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getIsActive(),
                user.getCreatedAt()
        );
    }
}
