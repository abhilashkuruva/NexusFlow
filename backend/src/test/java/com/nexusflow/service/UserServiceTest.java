package com.nexusflow.service;

import com.nexusflow.entity.User;
import com.nexusflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void testAuthenticateSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@nexusflow.com");
        user.setPassword("encodedPassword");
        user.setIsActive(true);
        user.setRole(User.Role.ADMIN);

        when(userRepository.findByEmail("admin@nexusflow.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);

        User authenticated = userService.authenticate("admin@nexusflow.com", "admin123");
        assertNotNull(authenticated);
        assertEquals("admin@nexusflow.com", authenticated.getEmail());
    }

    @Test
    @DisplayName("Should reject authentication with invalid password")
    void testAuthenticateInvalidPassword() {
        User user = new User();
        user.setEmail("admin@nexusflow.com");
        user.setPassword("encodedPassword");
        user.setIsActive(true);

        when(userRepository.findByEmail("admin@nexusflow.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        User authenticated = userService.authenticate("admin@nexusflow.com", "wrongPassword");
        assertNull(authenticated);
    }

    @Test
    @DisplayName("Should reject authentication for inactive user")
    void testAuthenticateInactiveUser() {
        User user = new User();
        user.setEmail("inactive@nexusflow.com");
        user.setPassword("encodedPassword");
        user.setIsActive(false);

        when(userRepository.findByEmail("inactive@nexusflow.com")).thenReturn(Optional.of(user));

        User authenticated = userService.authenticate("inactive@nexusflow.com", "anyPassword");
        assertNull(authenticated);
    }
}
