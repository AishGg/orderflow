package com.orderflow.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockAdjustmentRequest(

    @NotNull 
    @Min(1)
    Integer quantity
) {
    
}
