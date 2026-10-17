package com.orderflow.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryRequest(

    @NotNull 
    @Min(0)
    Integer quantity
) {
    
}
