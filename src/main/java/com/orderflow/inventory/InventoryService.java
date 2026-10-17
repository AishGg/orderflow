package com.orderflow.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.common.exception.InventoryAlreadyExistsException;
import com.orderflow.common.exception.InventoryNotFoundException;
import com.orderflow.common.exception.ProductNotFoundException;
import com.orderflow.inventory.dto.CreateInventoryRequest;
import com.orderflow.inventory.dto.InventoryResponse;
import com.orderflow.inventory.dto.StockAdjustmentRequest;
import com.orderflow.product.Product;
import com.orderflow.product.ProductRepository;

@Service
public class InventoryService {

    final ProductRepository productRepository;
    final InventoryRepository inventoryRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getSku(),
                inventory.getQuantity(),
                inventory.getVersion(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt());

    }

    @Transactional
    public InventoryResponse create(Long productId, CreateInventoryRequest request) {
        if (inventoryRepository.existsByProductId(productId)) {
            throw new InventoryAlreadyExistsException(productId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Inventory inventory = new Inventory(product, request.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);

    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
        return toResponse(inventory);
    }

     @Transactional 
    public InventoryResponse addStock(
        Long productId,
        StockAdjustmentRequest request
    ){
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new InventoryNotFoundException(productId));
        inventory.addStock(request.quantity());

        inventoryRepository.flush();

        return toResponse(inventory);
    }

    @Transactional 
    public InventoryResponse removeStock(
        Long productId,
        StockAdjustmentRequest request
    ){
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new InventoryNotFoundException(productId));
        inventory.removeStock(request.quantity());

        inventoryRepository.flush();

        return toResponse(inventory);
    }
}
