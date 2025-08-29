package com.terra.team.prural.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = Map.of(
            "message", e.getMessage(),
            "error", "VALIDATION_ERROR",
            "code", "RUNTIME_ERROR"
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> error = Map.of(
            "message", e.getMessage(),
            "error", "VALIDATION_ERROR",
            "code", "ILLEGAL_STATE_ERROR"
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> error = Map.of(
            "message", e.getMessage(),
            "error", "VALIDATION_ERROR",
            "code", "ILLEGAL_ARGUMENT_ERROR"
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        Map<String, String> error = Map.of(
            "message", "Error interno del servidor",
            "error", "INTERNAL_ERROR",
            "code", "GENERIC_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
