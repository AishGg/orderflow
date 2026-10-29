package com.orderflow.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.orderflow.common.exception.InsufficientStockException;
import com.orderflow.inventory.Inventory;
import com.orderflow.inventory.InventoryRepository;
import com.orderflow.order.Order;
import com.orderflow.order.OrderRepository;
import com.orderflow.order.OrderService;
import com.orderflow.order.dto.CreateOrderItemRequest;
import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.product.Product;
import com.orderflow.product.ProductRepository;
import com.orderflow.product.dto.ProductResponse;

@SpringBootTest
@Testcontainers
public class OrderFlowIntegrationTest {

    @Container 
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17")
                    .withDatabaseName("orderflow_test")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired 
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;


    @Test
    void shouldCreateOrderAndReduceInventory(){

        Product product = new Product("test", "test", "test", new BigDecimal("10.00"));
        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory(savedProduct, 10);
        inventoryRepository.save(inventory);

        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(savedProduct.getId(), 3)
        ));
        
        OrderResponse order = orderService.create(request);

        Order savedOrder = orderRepository.findById(order.id()).orElseThrow();
        assertNotNull(savedOrder);
        assertEquals(order.id(), savedOrder.getId());

        Inventory updatedInventory = inventoryRepository.findByProductId(savedProduct.getId()).orElseThrow();

        assertEquals(7, updatedInventory.getQuantity());


    }

    @Test 
    void shouldRollbackInventoryWhenOrderCreationFails(){
        Product product = new Product(
            "keyboard",
            "rollback-keyboard-001",
            "Test keyboard",
            new BigDecimal("10.00"));

        Product savedProduct = productRepository.save(product);

        Product product2 = new Product(
            "mouse",
            "rollback-mouse-001",
            "Test mouse",
            new BigDecimal("10.00"));
        
        Product savedProduct2 = productRepository.save(product2);

        Inventory inventory = new Inventory(savedProduct, 10);
        inventoryRepository.save(inventory);
        Inventory inventory2 = new Inventory(savedProduct2, 2);
        inventoryRepository.save(inventory2);

        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(savedProduct.getId(), 3),
            new CreateOrderItemRequest(savedProduct2.getId(), 5)
        ));

        long orderCountBefore = orderRepository.count();

        assertThrows(InsufficientStockException.class, () -> orderService.create(request));
        Inventory savedInventory = inventoryRepository.findByProductId(savedProduct.getId()).orElseThrow();
        assertEquals(10, savedInventory.getQuantity());
        Inventory savedInventory2 = inventoryRepository.findByProductId(savedProduct2.getId()).orElseThrow();
        assertEquals(2, savedInventory2.getQuantity());

        assertEquals(orderCountBefore, orderRepository.count());


    }
    
}