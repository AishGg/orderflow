package com.orderflow.common.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleProductAlreadyExists(
        ProductAlreadyExistsException exception,
        HttpServletRequest request
    ){
        ApiError error = new ApiError(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            "Product_Already_Exists",
            exception.getMessage(),
            request.getRequestURI(),
            null

        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ){
        Map<String, String> fieldErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                    fieldErrors.put(
                        error.getField(),
                        error.getDefaultMessage()
                    )  
        );

        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_GATEWAY.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "VALIDATION_FAILED",
            "Request Validation Failed",
            request.getRequestURI(),
            fieldErrors


        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public  ResponseEntity<ApiError> handleProductNotFound(
        ProductNotFoundException exception,
        HttpServletRequest request
    ){
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "PRODUCT_NOT_FOUND",
            exception.getMessage(),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
}
