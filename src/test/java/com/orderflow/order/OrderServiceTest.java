package com.orderflow.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.foreign.Linker.Option;
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

import com.orderflow.common.exception.DuplicateProductInOrderException;
import com.orderflow.common.exception.InsufficientStockException;
import com.orderflow.common.exception.InventoryNotFoundException;
import com.orderflow.common.exception.OrderAlreadyCancelledException;
import com.orderflow.common.exception.ProductNotFoundException;
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

    @Test 
    void shouldThrowWhenProductDoesNotExist(){
        
        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(99L, 2)
        ));

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> orderService.create(request));
        verify(orderRepository, never()).save(any(Order.class));
        verify(inventoryRepository, never())
            .findByProductId(anyLong());
        
    }

    @Test 
    void shouldThrowWhenStockIsInsufficient(){
         Product product = new Product(
            "TEST-001",
            "Test Product",
            "Test description",
            new BigDecimal("50.00")
        );

        Inventory inventory = new Inventory(product, 10);

        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(1L, 11)
        ));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        assertThrows(InsufficientStockException.class, () -> orderService.create(request));

        verify(orderRepository, never()).save(any(Order.class));

        assertEquals(10, inventory.getQuantity());
    }

    @Test 
    void shouldRestoreInventoryWhenOrderIsCancelled(){

        Product product = mock(Product.class);

        when(product.getId()).thenReturn(1L);
        when(product.getSku()).thenReturn("TEST-001");
        when(product.getName()).thenReturn("Test Product");
        when(product.getPrice()).thenReturn(new BigDecimal("50.00"));


        Inventory inventory = new Inventory(product, 7);

        Order order = Order.create();
        OrderItem item = new OrderItem(product, 3);
        order.addItem(item);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        OrderResponse response = orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELLED, response.status());
        assertEquals(10, inventory.getQuantity());

        verify(inventoryRepository).flush();
        verify(orderRepository).flush();
    }

    @Test 
    void shouldThrowWhenOrderIsAlreadyCancelled(){
        Order order = Order.create();
        order.cancel();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        assertThrows(OrderAlreadyCancelledException.class, () -> orderService.cancel(1L));

        verify(inventoryRepository, never()).findByProductId(anyLong());

        verify(orderRepository, never()).flush();
    }

    @Test
    void shouldThrowWhenInventoryDoesNotExist(){
        Product product = new Product(
            "TEST-001",
            "Test Product",
            "Test description",
            new BigDecimal("50.00")
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(1L, 2)
        ));

        assertThrows(InventoryNotFoundException.class, () -> orderService.create(request));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test 
    void shouldRejectDuplicateProductInOrder(){

        CreateOrderRequest request = new CreateOrderRequest(List.of(
            new CreateOrderItemRequest(1L, 2),
            new CreateOrderItemRequest(1L, 2)
        ));

        assertThrows(DuplicateProductInOrderException.class, () -> orderService.create(request));

        verify(orderRepository, never()).save(any(Order.class));

    }

}
