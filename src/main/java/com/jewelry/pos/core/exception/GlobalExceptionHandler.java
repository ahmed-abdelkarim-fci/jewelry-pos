package com.jewelry.pos.web.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Validation Errors (e.g., Missing fields, negative numbers)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields."
        );
        problem.setType(URI.create("errors/validation-failed"));
        problem.setTitle("Validation Error");

        // Collect field-specific errors
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        problem.setProperty("fields", fieldErrors);
        return ResponseEntity.badRequest().body(problem);
    }

    // 2. Handle Logic Errors (e.g., "User already exists", "Product not found")
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleLogicErrors(IllegalStateException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setTitle("Operation Failed");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // 3. Handle Permission Errors
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action."
        );
        problem.setTitle("Access Denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    // 4. Handle "Not Found" specifically (if you use IllegalArgumentException for missing items)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}