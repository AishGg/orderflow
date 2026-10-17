package com.orderflow.inventory;

import java.time.LocalDateTime;

import com.orderflow.common.exception.InsufficientStockException;
import com.orderflow.product.Product;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Inventory() {
    }

    public Inventory(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void addStock(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        this.quantity  += amount;
        this.updatedAt = LocalDateTime.now();
    }

     public void removeStock(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if(this.quantity < amount){
            throw new InsufficientStockException(
                this.product.getId(),
                this.quantity,
                amount
            );
        }

        this.quantity -= amount;
        this.updatedAt = LocalDateTime.now();
    }
    

}
