package com.orderflow.inventory.dto;

import java.time.LocalDateTime;

public record InventoryResponse(

        Long id,
        Long productId,
        String sku,
        int quantity,
        long version,
        LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt

) {

}
