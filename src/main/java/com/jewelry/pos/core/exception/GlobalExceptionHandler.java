package com.jewelry.pos.web.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String msg(String key, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    // 1. Handle Validation Errors (e.g., Missing fields, negative numbers)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                msg("error.validationFailed.detail", "Validation failed for one or more fields.")
        );
        problem.setType(URI.create("errors/validation-failed"));
        problem.setTitle(msg("error.validationFailed.title", "Validation Error"));

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
        problem.setTitle(msg("error.operationFailed.title", "Operation Failed"));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // 3. Handle Permission Errors
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                msg("error.accessDenied.detail", "You do not have permission to perform this action.")
        );
        problem.setTitle(msg("error.accessDenied.title", "Access Denied"));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    // 4. Handle "Not Found" specifically (if you use IllegalArgumentException for missing items)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle(msg("error.resourceNotFound.title", "Resource Not Found"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}