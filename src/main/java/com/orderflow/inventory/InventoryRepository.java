package com.orderflow.inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long id);

    boolean existsByProductId(Long id);
    
}
