package com.W3yneRagsac.SnapShop.repository;

import com.W3yneRagsac.SnapShop.model.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProductId(Long id); // Change method name to match the field name
    Optional<ProductEntity> findByProductName(String name);
}

