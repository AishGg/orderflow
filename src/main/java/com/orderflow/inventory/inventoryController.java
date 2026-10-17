package com.orderflow.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.inventory.dto.CreateInventoryRequest;
import com.orderflow.inventory.dto.InventoryResponse;
import com.orderflow.inventory.dto.StockAdjustmentRequest;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("api/v1/inventory")
public class inventoryController {
    private final InventoryService inventoryService;

    public inventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @PostMapping ("/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(
        @PathVariable Long productId,
        @Valid @RequestBody CreateInventoryRequest request
    ) {
        return inventoryService.create(productId, request);
    }

    @GetMapping ("/{productId}/add")
    public InventoryResponse addStock(
        @PathVariable long productId,
        @Valid @RequestBody StockAdjustmentRequest request
    ){
        return inventoryService.addStock(productId, request);
    }

    @GetMapping ("/{productId}/remove")
    public InventoryResponse removeStock(
        @PathVariable long productId,
        @Valid @RequestBody StockAdjustmentRequest request
    ){
        return inventoryService.removeStock(productId, request);
    }
}
