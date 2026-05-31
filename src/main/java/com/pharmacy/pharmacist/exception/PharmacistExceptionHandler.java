package com.pharmacy.pharmacist.exception;

import com.pharmacy.shared.storage.StorageFileNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class PharmacistExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex,
                                                                  HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorBody(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex,
                                                                     HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStorageFileNotFound(StorageFileNotFoundException ex,
                                                                        HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorBody(HttpStatus.NOT_FOUND, "Prescription file not found", request.getRequestURI()));
    }

    private Map<String, Object> buildErrorBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }
}
