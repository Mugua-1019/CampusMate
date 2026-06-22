package com.campusmate.common.exception;

import com.campusmate.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Result.fail(400, exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Result<Void>> handleIllegalState(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail(500, exception.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Result<Void>> handleSecurity(SecurityException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.fail(403, exception.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Result<Void>> handleIOException(IOException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail(500, exception.getMessage()));
    }
}
