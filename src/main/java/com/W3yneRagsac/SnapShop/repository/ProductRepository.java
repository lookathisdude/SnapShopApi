package com.W3yneRagsac.SnapShop.repository;

import com.W3yneRagsac.SnapShop.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> getProductById(Long id);
    Optional<ProductEntity> getProductByName(String name);
}
