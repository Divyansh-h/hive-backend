package com.hive.common.exception;

import com.hive.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HiveException.class)
    public ResponseEntity<ApiResponse<Void>> handleHiveException(HiveException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getStatus(), ex.getMessage(), ex.getCode()),
                ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                ApiResponse.error(HttpStatus.BAD_REQUEST, message, "VALIDATION_ERROR"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "INTERNAL_ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
