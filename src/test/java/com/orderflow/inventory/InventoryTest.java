package com.orderflow.inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.orderflow.common.exception.InsufficientStockException;
import com.orderflow.product.Product;

public class InventoryTest {
    @Test
    void shouldRemoveStockWhenEnoughQuantityExists() {

        Product product = new Product(
                "TEST-001",
                "Test Product",
                "Test description",
                new BigDecimal("100.00"));

        Inventory inventory = new Inventory(product, 10);

        inventory.removeStock(4);

        assertEquals(6, inventory.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        Product product = new Product(
                "TEST-001",
                "Test Product",
                "Test description",
                new BigDecimal("100.00"));

        Inventory inventory = new Inventory(product, 3);

        assertThrows(
                InsufficientStockException.class,
                () -> inventory.removeStock(5));
    }

    @Test 
    void shouldReduceQuantityToZeroWhenRemovingAllStock(){
        Product product = new Product("Test-001", "test product", "my product", new BigDecimal("20.00"));
        Inventory inventory = new Inventory(product, 5);

        inventory.removeStock(5);

        assertEquals(0, inventory.getQuantity());

    }

    @Test 
    void shouldRejectZeroAmount(){
        Product product = new Product("Test-001", "test product", "my product", new BigDecimal("20.00"));
        Inventory inventory = new Inventory(product, 5);

         assertThrows(
                IllegalArgumentException.class,
                () -> inventory.removeStock(0));
    }

    @Test 
    void shouldThrowExceptionWhenRemovingNegativeStock(){
        Product product = new Product("Test-001", "test product", "my product", new BigDecimal("20.00"));
        Inventory inventory = new Inventory(product, 5);

         assertThrows(
                IllegalArgumentException.class,
                () -> inventory.removeStock(-2));
    }

}