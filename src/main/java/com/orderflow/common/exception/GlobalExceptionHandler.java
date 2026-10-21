package com.orderflow.common.exception;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.jdbc.Expectation;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleProductAlreadyExists(
            ProductAlreadyExistsException exception,
            HttpServletRequest request) {
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
            HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(
                        error.getField(),
                        error.getDefaultMessage()));

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
    public ResponseEntity<ApiError> handleProductNotFound(
            ProductNotFoundException exception,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "PRODUCT_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI(),
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleInventoryAlreadyExists(
            InventoryAlreadyExistsException exception,
            HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "INVENTORY_ALREADY_EXISTS",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ApiError> handleInvenotryNotFound(
            InventoryNotFoundException exception,
            HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "INVENTORY_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficientStock(
            InsufficientStockException exception,
            HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "INSUFFICIENT_STOCK",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException exception,
            HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "CONCURRENT_UPDATE",
                "Inventory was modified by another request. Please retry.",
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFound(
            OrderNotFoundException exception,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "ORDER_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ProductInactiveException.class)
    public ResponseEntity<ApiError> handleProductInactive(
            ProductInactiveException exception,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "PRODUCT_INACTIVE",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(DuplicateProductInOrderException.class)
    public ResponseEntity<ApiError> handleDuplicateProductInOrder(
            DuplicateProductInOrderException exception,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                "DUPLICATE_PRODUCT_IN_ORDER",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
     @ExceptionHandler(OrderAlreadyCancelledException.class)
    public ResponseEntity<ApiError> handleOrderAlreadyCancelled(
            OrderAlreadyCancelledException exception,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "ORDER_ALREADY_CANCELLED",
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

}
