package com.orderflow.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.orderflow.order.OrderStatus;

public record OrderResponse(
    Long id,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
    
) {
    
}
