package com.nexusflow.controller;

import com.nexusflow.dto.AuthRequest;
import com.nexusflow.dto.AuthResponse;
import com.nexusflow.dto.RegisterRequest;
import com.nexusflow.entity.User;
import com.nexusflow.service.UserService;
import com.nexusflow.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:3001"
})
@Tag(
        name = "Authentication",
        description = "User authentication and registration APIs"
)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * LOGIN
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Authenticate user and return JWT token"
    )
    public ResponseEntity<?> login(
            @RequestBody AuthRequest authRequest
    ) {

        try {

            System.out.println(
                    "Login attempt: " + authRequest.getEmail()
            );

            User user = userService.authenticate(
                    authRequest.getEmail(),
                    authRequest.getPassword()
            );

            if (user == null) {

                System.out.println("Authentication failed");

                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid email or password");

                return ResponseEntity
                        .status(401)
                        .body(error);
            }

            System.out.println("Authentication successful");

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId()
            );

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put(
                    "message",
                    "Login failed: " + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body(error);
        }
    }

    /**
     * REGISTER
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register",
            description = "Create a new user account"
    )
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {

        try {

            if (userService.emailExists(
                    registerRequest.getEmail()
            )) {

                Map<String, String> error = new HashMap<>();
                error.put("message", "Email already exists");

                return ResponseEntity
                        .badRequest()
                        .body(error);
            }

            User.Role role = User.Role.ANALYST;

            if (
                    registerRequest.getRole() != null &&
                    !registerRequest.getRole().isBlank()
            ) {

                try {

                    role = User.Role.valueOf(
                            registerRequest
                                    .getRole()
                                    .toUpperCase()
                    );

                } catch (IllegalArgumentException e) {

                    Map<String, String> error =
                            new HashMap<>();

                    error.put(
                            "message",
                            "Invalid role. Use ADMIN, ANALYST, or MANAGER"
                    );

                    return ResponseEntity
                            .badRequest()
                            .body(error);
                }
            }

            User user = userService.registerUser(
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    role
            );

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId()
            );

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, String> error = new HashMap<>();

            error.put(
                    "message",
                    "Registration failed: " + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body(error);
        }
    }

    /**
     * VALIDATE TOKEN
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate Token")
    public ResponseEntity<?> validateToken(
            @RequestBody Map<String, String> tokenRequest
    ) {

        String token = tokenRequest.get("token");

        Map<String, Object> response = new HashMap<>();

        if (token == null || token.isEmpty()) {

            response.put("valid", false);

            return ResponseEntity.ok(response);
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean isValid = jwtUtil.validateToken(token);

        response.put("valid", isValid);

        if (isValid) {

            response.put(
                    "email",
                    jwtUtil.extractEmail(token)
            );

            response.put(
                    "userId",
                    jwtUtil.extractUserId(token)
            );
        }

        return ResponseEntity.ok(response);
    }

    /**
     * HEALTH CHECK
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {

        Map<String, String> response =
                new HashMap<>();

        response.put("status", "UP");

        response.put(
                "message",
                "NexusFlow Authentication Service is running"
        );

        return ResponseEntity.ok(response);
    }
}