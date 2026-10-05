package com.orderflow.common.exception;

public class ProductAlreadyExistsException extends RuntimeException{

    public ProductAlreadyExistsException(String sku){
        super("Product with SKU " + sku + " already exists");
    }
    
}
