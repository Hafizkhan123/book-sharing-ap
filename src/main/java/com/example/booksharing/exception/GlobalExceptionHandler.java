package com.example.booksharing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// ─────────────────────────────────────────────────────────────
//  Step D3 — Global Exception Handler
//
//  Catches ALL exceptions across every controller.
//  No more Whitelabel Error Page — every error returns clean JSON.
//
//  Handles:
//    • ResourceNotFoundException  → 404 Not Found
//    • MethodArgumentNotValidException → 400 Bad Request (validation)
//    • Exception (fallback)        → 500 Internal Server Error
// ─────────────────────────────────────────────────────────────
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── D2: Resource not found (book / user / share record) ──────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── D1: Validation failures (@NotBlank, @Email, etc.) ────────
    //  Returns a map of  { fieldName: "error message" }
    //  so the frontend knows EXACTLY which field failed.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.BAD_REQUEST.value());
        body.put("error",     "Validation Failed");
        body.put("details",   fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ── Fallback: any other unexpected exception ──────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong: " + ex.getMessage());
    }

    // ── Shared helper ─────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);

        return new ResponseEntity<>(body, status);
    }
}
