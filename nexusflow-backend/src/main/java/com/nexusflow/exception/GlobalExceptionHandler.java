package com.nexusflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Resource Not Found Exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex
    ) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    /**
     * Validation Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {

                    String field =
                            ((FieldError) error).getField();

                    errors.put(
                            field,
                            error.getDefaultMessage()
                    );
                });

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Authentication Exception
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(
            AuthenticationException ex
    ) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }

    /**
     * Generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex
    ) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error: " + ex.getMessage()
        );
    }

    /**
     * Helper method
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message
    ) {

        return new ResponseEntity<>(

                new ErrorResponse(
                        status.value(),
                        message,
                        LocalDateTime.now()
                ),

                status
        );
    }

    // =====================================================
    // ERROR RESPONSE MODEL
    // =====================================================

    public static class ErrorResponse {

        private int status;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(
                int status,
                String message,
                LocalDateTime timestamp
        ) {

            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    // =====================================================
    // CUSTOM EXCEPTIONS
    // =====================================================

    public static class ResourceNotFoundException
            extends RuntimeException {

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class AuthenticationException
            extends RuntimeException {

        public AuthenticationException(String message) {
            super(message);
        }
    }
}