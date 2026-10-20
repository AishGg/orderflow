package com.orderflow.common.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id){
        super("Order with id " + id + " was not found");
    }
    
}
