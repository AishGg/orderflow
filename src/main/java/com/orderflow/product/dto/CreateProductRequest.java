package com.orderflow.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
    @NotBlank 
    String sku,

    @NotBlank 
    String name,

    @NotBlank 
    String description,

    @NotNull 
    @Positive 
    BigDecimal price

) {
    
    
    
}
