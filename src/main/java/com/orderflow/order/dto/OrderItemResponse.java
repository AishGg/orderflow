package com.orderflow.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long productId,
    String sku,
    String name,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal lineTotal
) {
    
}
