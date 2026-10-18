package com.orderflow.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(

    @NotNull 
    Long productId,

    @NotNull 
    @Min(1)
    Integer quantity

) {
    
}
