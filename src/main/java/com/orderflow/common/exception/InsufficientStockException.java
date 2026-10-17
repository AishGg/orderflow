package com.orderflow.common.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(
        Long productId,
        int available,
        int requested
    ){
        super(
            "Insufficent stock for product " + productId +
            ". Available: " + available + 
            ". requested: " + requested
        );
    }
}
