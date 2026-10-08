package com.orderflow.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateProductRequest(
    @NotBlank 
    String name,

    String description,

    @NotNull 
    @Positive 
    BigDecimal price
) {
}
