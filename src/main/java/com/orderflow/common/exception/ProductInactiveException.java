package com.orderflow.common.exception;

public class ProductInactiveException extends RuntimeException{

    public ProductInactiveException(Long productId){
        super("Product with id " + productId + " is inactive");
    }
    
}
