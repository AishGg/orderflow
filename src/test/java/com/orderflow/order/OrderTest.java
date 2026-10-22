package com.orderflow.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.orderflow.product.Product;

public class OrderTest {
    
    @Test 
    void shouldStartWithCreatedStatus(){
        Order order = Order.create();

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test 
    void shouldAddItemTotalWhenNewProductAdded(){
         
        Product product = new Product(
                "TEST-001",
                "Test Product",
                "Test description",
                new BigDecimal("100.00"));

        OrderItem orderItem = new OrderItem(product, 3);

        Order order = Order.create();


        order.addItem(orderItem);

        assertEquals(new BigDecimal("300.00"), order.getTotalAmount());
    }

    @Test 
    void shouldCalculaetTotalForMultipleItems(){

        Product product1 = new Product(
                "TEST-001",
                "Test Product",
                "Test description",
                new BigDecimal("50.00"));

        Product product2 = new Product(
                "TEST-002",
                "Test Product 2",
                "Test description 2",
                new BigDecimal("20.00"));

        OrderItem item1 = new OrderItem(product1, 3);
        OrderItem item2 = new OrderItem(product2, 2);

        Order order = Order.create();

        order.addItem(item2);
        order.addItem(item1);

        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("190.00"), order.getTotalAmount());

    }

    @Test 
    void shouleCancelOrderStatus(){
        Order order = Order.create();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
