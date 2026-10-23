package com.orderflow.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderflow.inventory.Inventory;
import com.orderflow.inventory.InventoryRepository;
import com.orderflow.order.dto.CreateOrderItemRequest;
import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.product.Product;
import com.orderflow.product.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    
    @Mock 
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks 
    private OrderService orderService;

    // we can use InjectMocks on line 20 to automate the orderservice 
    // so we don't need the below function

    // @BeforeEach 
    // void setUp(){
    //     MockitoAnnotations.openMocks(this);

    //     orderService = new OrderService(
    //             productRepository,
    //             inventoryRepository,
    //             orderRepository
    //     );
    // }


    @Test 
    void shouldCreateOrderAndReduceInventory(){
        Product product = new Product(
            "TEST-001",
            "Test Product",
            "Test description",
            new BigDecimal("50.00")
        );

        Inventory inventory = new Inventory(product, 10);

        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(1L, 2)
        ));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(request);

        assertEquals(new BigDecimal("100.00"), response.totalAmount());
        assertEquals(8, inventory.getQuantity());

        verify(orderRepository).save(any(Order.class));
        verify(inventoryRepository).flush();
        verify(orderRepository).flush();

    }

}
