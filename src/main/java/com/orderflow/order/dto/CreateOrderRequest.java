package com.orderflow.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(
    
    @NotEmpty 
    List<@Valid CreateOrderItemRequest> items
) {
    
}
