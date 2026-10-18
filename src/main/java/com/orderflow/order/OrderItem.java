package com.orderflow.order;

import java.math.BigDecimal;

import com.orderflow.product.Product;

import jakarta.persistence.*;

@Entity 
@Table (name = "order_items")
public class OrderItem {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "order_id", nullable = false)
    private Order order;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "product_id", nullable = false)
    private Product product;

    @Column (name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

     @Column(
            name = "line_total",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal lineTotal;

    protected OrderItem() {
    }

     public OrderItem(Product product, int quantity) {
        this.product = product;
        this.productSku = product.getSku();
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;

        this.lineTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));
    }

    void assignToOrder(Order order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getProductSku() {
        return productSku;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    


}
