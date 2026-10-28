package com.orderflow.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    
}