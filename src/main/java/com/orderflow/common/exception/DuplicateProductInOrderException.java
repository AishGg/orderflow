package com.orderflow.common.exception;

public class DuplicateProductInOrderException extends RuntimeException{
    
    public DuplicateProductInOrderException(long productId){
        super("Product " + productId + " appears more than once in order");
    }
}
