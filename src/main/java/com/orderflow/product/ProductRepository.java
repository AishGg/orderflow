package com.orderflow.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{

    Optional<Product> findBySku(String Sku);
    boolean existsBySku(String Sku);
}
    

