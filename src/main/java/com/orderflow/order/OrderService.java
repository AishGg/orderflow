package com.orderflow.order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.common.exception.DuplicateProductInOrderException;
import com.orderflow.common.exception.InventoryNotFoundException;
import com.orderflow.common.exception.OrderAlreadyCancelledException;
import com.orderflow.common.exception.OrderNotFoundException;
import com.orderflow.common.exception.ProductInactiveException;
import com.orderflow.common.exception.ProductNotFoundException;
import com.orderflow.inventory.Inventory;
import com.orderflow.inventory.InventoryRepository;
import com.orderflow.order.dto.CreateOrderItemRequest;
import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderItemResponse;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.product.Product;
import com.orderflow.product.ProductRepository;
import com.orderflow.product.dto.PageResponse;

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
        Set<Long> productIds = new HashSet<>();
        Order order = Order.create();

       
        for (CreateOrderItemRequest itemRequest : request.items()) {

             if(!productIds.add(itemRequest.productId())){
                throw new DuplicateProductInOrderException(itemRequest.productId());
            }

            Product product = productRepository
                    .findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            if (!product.isActive()) {
                throw new ProductInactiveException(product.getId());
            }

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

    @Transactional
    public OrderResponse cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() == OrderStatus.CANCELLED){
            throw new OrderAlreadyCancelledException(orderId);
        }

        for(OrderItem item :  order.getItems()){
            Long productId = item.getProduct().getId();

            Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));

            inventory.addStock(item.getQuantity());
        }

        order.cancel();
        inventoryRepository.flush();
        orderRepository.flush();

        return toResponse(order);
    }

    @Transactional (readOnly = true)
    public OrderResponse getById(Long id){
        Order order = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));

        return toResponse(order);
    }

    @Transactional (readOnly = true)
    public PageResponse<OrderResponse> getAll(Pageable pageable){
        Page<OrderResponse> page = orderRepository.findAll(pageable)
        .map(this::toResponse);

        return PageResponse.from(page);
    }
}
