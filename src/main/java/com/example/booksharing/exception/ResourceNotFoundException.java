package com.example.booksharing.exception;

// ─────────────────────────────────────────────────────────────
//  Step D2 — Resource Not Found Exception
//  Thrown when a book / user / sharing record is missing.
//  Replaces silent null returns with a meaningful HTTP 404.
// ─────────────────────────────────────────────────────────────
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
