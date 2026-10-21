package com.orderflow.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Order() {
    }

    public static Order create() {

        Order order = new Order();
        order.status = OrderStatus.CREATED;
        order.totalAmount = BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();
        order.createdAt = now;
        order.updatedAt = now;

        return order;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.assignToOrder(this);

        totalAmount = totalAmount.add(item.getLineTotal());
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();

    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
