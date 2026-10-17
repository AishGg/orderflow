package com.orderflow.common.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long productId){
        super("Inventory not found for product " + productId);
    }
}
