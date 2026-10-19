package com.orderflow.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.common.exception.InventoryNotFoundException;
import com.orderflow.common.exception.ProductNotFoundException;
import com.orderflow.inventory.Inventory;
import com.orderflow.inventory.InventoryRepository;
import com.orderflow.order.dto.CreateOrderItemRequest;
import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderItemResponse;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.product.Product;
import com.orderflow.product.ProductRepository;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProductSku(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }


    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = Order.create();

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = productRepository
                    .findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
            .orElseThrow(() -> new InventoryNotFoundException(product.getId()));
            
            inventory.removeStock(itemRequest.quantity());

            OrderItem orderItem = new OrderItem(product, itemRequest.quantity());

            order.addItem(orderItem);
            
        }
        Order savedOrder = orderRepository.save(order);
        inventoryRepository.flush();
        orderRepository.flush();

        return toResponse(savedOrder);
    }
}
